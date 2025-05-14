package fan.summer.hmoneta.service.acme;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import fan.summer.hmoneta.common.context.LoginUserContext;
import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.acme.AcmeChallengeInfoEntity;
import fan.summer.hmoneta.database.entity.acme.AcmeUserInfoEntity;
import fan.summer.hmoneta.database.repository.acme.AcmeChallengeInfoRepository;
import fan.summer.hmoneta.database.repository.acme.AcmeUserInfoRepository;
import fan.summer.hmoneta.service.ddns.provider.DDNSProvider;
import fan.summer.hmoneta.service.ddns.provider.ProviderFactory;
import fan.summer.hmoneta.util.SnowFlakeUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ACME所有异步方法服务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/3/1
 */
@Service
@Slf4j
public class AcmeAsyncService {

    private final LinkedList<String> logList = new LinkedList<>();

    final int maxAttempts = 10;  // 最大尝试次数

    @Value("${hmoneta.acme.uri}")
    private String acmeUri;

    private final ProviderFactory providerFactory;

    private final AcmeChallengeInfoRepository acmeChallengeInfoRepository;
    private final AcmeUserInfoRepository acmeUserInfoRepository;

    @Autowired
    public AcmeAsyncService(ProviderFactory providerFactory, AcmeChallengeInfoRepository acmeChallengeInfoRepository, AcmeUserInfoRepository acmeUserInfoRepository) {
        this.providerFactory = providerFactory;
        this.acmeChallengeInfoRepository = acmeChallengeInfoRepository;
        this.acmeUserInfoRepository = acmeUserInfoRepository;
    }

    protected LinkedList<String> getLogInfo() {
        return this.logList;
    }

    protected byte[] packCertifications(String domain) throws IOException {
        String dirPath = "certs/" + domain;
        Path basePath = Paths.get(dirPath);
        if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {
            throw new BusinessException(BusinessExceptionEnum.CER_ERROR_FOLDER_NOT_EXIST);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            Files.walk(basePath)
                    .filter(Files::isRegularFile) // 只处理普通文件
                    .forEach(file -> {
                        try {
                            // 添加文件到 ZIP 中
                            ZipEntry zipEntry = new ZipEntry(basePath.relativize(file).toString());
                            zipOut.putNextEntry(zipEntry);
                            Files.copy(file, zipOut);
                            zipOut.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("文件打包失败", e);
                        }
                    });
        }
        return byteArrayOutputStream.toByteArray();
    }


    @Async
    @Transactional(rollbackOn = Exception.class)
    protected void useDnsChallengeGetCertification(String domain, String providerName, AcmeChallengeInfoEntity info) {
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        String email = LoginUserContext.getMember().getEmail();
        if (ObjectUtil.isNotEmpty(acmeChallengeInfoRepository.findByDomain(domain))) {
            acmeChallengeInfoRepository.deleteByDomain(domain);
        }
        AcmeChallengeInfoEntity dataBaseInfo = new AcmeChallengeInfoEntity();
        dataBaseInfo.setUserId(LoginUserContext.getId());
        dataBaseInfo.setDomain(domain);
        dataBaseInfo.setTaskId(info.getTaskId());
        dataBaseInfo.setStatusInfo("0");
        saveRunningLog(String.format("[ACME-Task:%s]开始为%s申请证书", info.getTaskId(), domain), "info");
        acmeChallengeInfoRepository.save(dataBaseInfo);
        try {
            KeyPair keyPair;
            Session session = new Session(acmeUri);
            List<AcmeUserInfoEntity> byUserEmail = acmeUserInfoRepository.findByUserEmail(email);
            if (ObjectUtil.isNotEmpty(byUserEmail)) {
                saveRunningLog(String.format("[ACME-Task:%s]>>>>>>>>无需创建ACME账号", info.getTaskId()), "info");
                keyPair = byUserEmail.getFirst().generateKeyPair();
            } else {
                String accountEmail = "mailto:" + email;
                saveRunningLog(String.format("[ACME-Task:%s]>>>>>>>>创建ACME账号", info.getTaskId()), "info");
                keyPair = KeyPairUtils.createKeyPair(2048);
                Account account = new AccountBuilder()
                        .addContact(accountEmail)
                        .agreeToTermsOfService()
                        .useKeyPair(keyPair)
                        .create(session);
                if (ObjectUtil.isNotEmpty(account)) {
                    saveRunningLog(String.format("[ACME-Task:%s]>>>>>>>>成功创建ACME账号", info.getTaskId()), "info");
                    AcmeUserInfoEntity acmeUserInfoEntity = new AcmeUserInfoEntity();
                    acmeUserInfoEntity.setUserId(SnowFlakeUtil.getSnowFlakeNextId());
                    acmeUserInfoEntity.setUserEmail(email);
                    acmeUserInfoEntity.saveKeyPair(keyPair);
                    acmeUserInfoRepository.save(acmeUserInfoEntity);
                } else {
                    throw new RuntimeException("[ACME-Task:" + info.getTaskId() + "]创建账户失败");
                }
            }
            saveRunningLog(String.format("[ACME-Task:%s]1.登录ACME提供商", info.getTaskId()), "info");
            // Login
            saveRunningLog(String.format("[ACME-Task:%s]开始登录", info.getTaskId()), "info");
            Login login = new AccountBuilder().onlyExisting().agreeToTermsOfService().useKeyPair(keyPair).createLogin(session);
            // 发起订单
            saveRunningLog(String.format("[ACME-Task:%s]创建订单", info.getTaskId()), "info");
            Order order = login.newOrder().domain(domain).create();
            Order bindOrder = login.bindOrder(order.getLocation());
            Authorization authorization = bindOrder.getAuthorizations().getFirst();
            saveRunningLog(String.format("[ACME-Task:%s]发起DNS-01挑战", info.getTaskId()), "info");
            authorization.findChallenge(Dns01Challenge.class).ifPresentOrElse(challenge -> {
                String digest = challenge.getDigest();
                saveRunningLog(String.format("[ACME-Task:%s]获取到DNS挑战内容:%s", info.getTaskId(), digest), "info");
                saveRunningLog(String.format("[ACME-Task:%s]修改DNS", info.getTaskId()), "info");
                DDNSProvider ddnsProvider = providerFactory.generatorProvider(DDNSProvidersSelectEnum.valueOf(providerName));
                String subDomain = domain.substring(0, domain.indexOf('.'));
                String mainDomain = domain.substring(domain.indexOf('.') + 1);
                boolean status = ddnsProvider.modifyDdns(mainDomain, "_acme-challenge." + subDomain, digest, "TXT");
                saveRunningLog(String.format("[ACME-Task:%s]DNS修改状态:%s", info.getTaskId(), status), "info");
                if (status & waitForDnsPropagation("_acme-challenge." + domain, digest)) {
                    try {
                        saveRunningLog(String.format("[ACME-Task:%s]开启挑战", info.getTaskId()), "info");
                        challenge.trigger();
                        saveRunningLog(String.format("[ACME-Task:%s]挑战结果验证", info.getTaskId()), "info");
                        while (!EnumSet.of(Status.VALID, Status.INVALID).contains(authorization.getStatus())) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(5000);
                                saveRunningLog(String.format("[ACME-Task:%s]再次验证", info.getTaskId()), "info");
                                authorization.fetch();
                            } catch (InterruptedException ignored) {
                                saveRunningLog(String.format("[ACME-Task:%s]线程意外中断:%s", info.getTaskId(), ignored.getMessage()), "error");
                                break;
                            } catch (AcmeException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        saveRunningLog(String.format("[ACME-Task:%s]挑战结果:%s", info.getTaskId(), authorization.getStatus()), "info");
                        if (authorization.getStatus() == Status.VALID) {
                            saveRunningLog(String.format("[ACME-Task:%s]验证通过", info.getTaskId()), "info");
                            removeTxtDnsInfo(ddnsProvider, mainDomain, subDomain);
                            // 获取证书
                            try {
                                saveRunningLog(String.format("[ACME-Task:%s]获取证书", info.getTaskId()), "info");
                                KeyPair cerKeyPair = KeyPairUtils.createKeyPair(2048);
                                order.execute(cerKeyPair);
                                while (!EnumSet.of(Status.VALID, Status.INVALID).contains(order.getStatus())) {
                                    TimeUnit.MILLISECONDS.sleep(5000);
                                    saveRunningLog(String.format("[ACME-Task:%s]继续验证证书签发状态", info.getTaskId()), "info");
                                    order.fetch();
                                }
                                if (order.getStatus() == Status.VALID) {
                                    saveRunningLog(String.format("[ACME-Task:%s]订单确认完成，开始下载证书", info.getTaskId()), "info");
                                    Certificate cert = order.getCertificate();
                                    X509Certificate certificate = cert.getCertificate();
                                    List<X509Certificate> chain = cert.getCertificateChain();
                                    saveCertificateFiles(cerKeyPair, cert, domain, info.getTaskId());
                                    dataBaseInfo.saveKeyPair(keyPair);
                                    dataBaseInfo.setCertApplyTime(DateTime.now());
                                    dataBaseInfo.setStatusInfo("1");
                                } else {
                                    saveRunningLog(String.format("[ACME-Task:%s]订单确认失败", info.getTaskId()), "error");
                                    dataBaseInfo.setStatusInfo("-1");
                                }
                            } catch (AcmeException | InterruptedException | CertificateEncodingException |
                                     IOException e) {
                                dataBaseInfo.setStatusInfo("-1");
                                throw new RuntimeException(e);
                            }
                        } else {
                            removeTxtDnsInfo(ddnsProvider, mainDomain, subDomain);
                            saveRunningLog(String.format("[ACME-Task:%s]验证未通过", info.getTaskId()), "error");
                            dataBaseInfo.setStatusInfo("-1");
                        }
                    } catch (AcmeException e) {
                        removeTxtDnsInfo(ddnsProvider, mainDomain, subDomain);
                        dataBaseInfo.setStatusInfo("-1");
                        throw new RuntimeException(e);
                    }
                } else {
                    saveRunningLog(String.format("[ACME-Task:%s]未通过DNS记录验证", info.getTaskId()), "error");
                    dataBaseInfo.setStatusInfo("-1");
                    if (status) {
                        removeTxtDnsInfo(ddnsProvider, mainDomain, subDomain);
                    }
                }
            }, () -> {
                saveRunningLog(String.format("[ACME-Task:%s]未正常获取到Dns01Challenge对象", info.getTaskId()), "error");
                dataBaseInfo.setStatusInfo("-1");
            });
        } catch (AcmeException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            dataBaseInfo.setStatusInfo("-1");
            throw new RuntimeException(e);
        } finally {
            if (ObjectUtil.isNotEmpty(dataBaseInfo)) {
                if (ObjectUtil.isEmpty(dataBaseInfo.getStatusInfo())) {
                    dataBaseInfo.setStatusInfo("-1");
                }
                acmeChallengeInfoRepository.save(dataBaseInfo);
            }
            saveRunningLog(String.format("[ACME-Task:%s]结束证书申请服务", info.getTaskId()), "info");
        }
    }

    private boolean waitForDnsPropagation(String domain, String expectedTxtRecord) {
        log.info("开始验证域名{}的TXT解析是否生效", domain);
        try {
            Lookup lookup = new Lookup(domain, Type.TXT);
            for (int attempt = 1; this.maxAttempts >= attempt; attempt++) {
                lookup.run();
                if (lookup.getResult() == Lookup.SUCCESSFUL) {
                    log.info("DNS记录查询成功，开始验证");
                    Record[] answers = lookup.getAnswers();
                    for (Record record : answers) {
                        if (record instanceof TXTRecord txtRecord) {
                            for (String txt : txtRecord.getStrings()) {
                                if (txt.equals(expectedTxtRecord)) {
                                    log.info("通过解析验证，DNS记录正确，开始下一步");
                                    return true;
                                }
                            }
                        }
                    }
                    log.info("未匹配上指定的DNS记录，10秒后重新尝试。第{}次尝试", attempt);
                    TimeUnit.MILLISECONDS.sleep(10000);
                } else {
                    log.info("DNS记录查询未成功，10秒后重新尝试。第{}次尝试", attempt);
                    TimeUnit.MILLISECONDS.sleep(10000);
                    if (attempt == maxAttempts) {
                        return false;
                    }
                }
            }
        } catch (TextParseException | InterruptedException e) {
            log.error(e.toString());
            return false;
        }
        return false;
    }

    private void removeTxtDnsInfo(DDNSProvider ddnsProvider, String domain, String subDomain) {
        log.info("开始清理用于验证的DNS记录");
        try {
            ddnsProvider.deleteDdns(domain, "_acme-challenge." + subDomain, "TXT");
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            log.info("结束清理");
        }
    }

    private void saveCertificateFiles(KeyPair keyPair, Certificate cert, String domain, Long taskId) throws IOException, CertificateEncodingException {
        saveRunningLog(String.format("开始保存证书文件，域名: %s, 任务ID: %s", domain, taskId), "info");
//        log.info("开始保存证书文件，域名: {}, 任务ID: {}", domain, taskId);

        // 创建证书存储目录
        String certPath = "certs/" + domain;
        File certDir = new File(certPath);
        // 删除已存在目录并重新创建
        boolean successMkDir = false;
        if (certDir.exists()) {
            if (deleteRecursively(certDir)) {
                successMkDir = certDir.mkdirs();
            }
        } else {
            successMkDir = certDir.mkdirs();
        }
        if (!successMkDir) {
            throw new IOException("无法创建证书目录: " + certPath);
        }
        // 保存私钥 (KEY文件)
        try (FileWriter fw = new FileWriter(new File(certDir, domain + ".key"))) {
            KeyPairUtils.writeKeyPair(keyPair, fw);
            saveRunningLog(String.format("私钥文件已保存: %s", domain + ".key"), "info");
//            log.info("私钥文件已保存: {}", domain + ".key");
        }

        X509Certificate certificate = cert.getCertificate();
        List<X509Certificate> chain = cert.getCertificateChain();

        // 保存证书 (CRT文件)
        try (FileOutputStream fos = new FileOutputStream(new File(certDir, domain + ".crt"))) {
            writeCertificate(certificate, fos);
            saveRunningLog(String.format("证书文件已保存: %s", domain + ".crt"), "info");
//            log.info("证书文件已保存: {}", domain + ".crt");
        }

        // 保存完整证书链 (PEM文件)
        try (FileOutputStream fos = new FileOutputStream(new File(certDir, domain + ".pem"))) {
            writeCertificate(certificate, fos);

            // 添加中间证书
            for (int i = 1; i < chain.size(); i++) {
                fos.write('\n');
                writeCertificate(chain.get(i), fos);
            }
            saveRunningLog(String.format("完整证书链文件已保存: %s", domain + ".pem"), "info");
//            log.info("完整证书链文件已保存: {}", domain + ".pem");
        }

        // 保存完整证书链和私钥 (FULLCHAIN文件) - 修复后的版本
        File fullchainFile = new File(certDir, domain + ".fullchain.pem");

        // 先将私钥写入字符串
        StringWriter keyWriter = new StringWriter();
        KeyPairUtils.writeKeyPair(keyPair, keyWriter);
        String privateKeyPem = keyWriter.toString();

        // 使用单个输出流写入所有内容
        try (FileOutputStream fos = new FileOutputStream(fullchainFile)) {
            // 写入私钥
            fos.write(privateKeyPem.getBytes(StandardCharsets.UTF_8));
            fos.write('\n');

            // 写入证书
            writeCertificate(certificate, fos);

            // 写入证书链
            for (int i = 1; i < chain.size(); i++) {
                fos.write('\n');
                writeCertificate(chain.get(i), fos);
            }
            saveRunningLog(String.format("完整证书链和私钥文件已保存: %s", domain + ".fullchain.pem"), "info");
//            log.info("完整证书链和私钥文件已保存: {}", domain + ".fullchain.pem");
        }
        saveRunningLog("所有证书文件保存完成", "info");
//        log.info("所有证书文件保存完成");
    }

    private void writeCertificate(X509Certificate certificate, OutputStream out) throws IOException, CertificateEncodingException {
        byte[] encoded = certificate.getEncoded();
        String encoded64 = Base64.getEncoder().encodeToString(encoded);

        // 按照PEM格式写入
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.US_ASCII), false);
        writer.println("-----BEGIN CERTIFICATE-----");

        // 每64个字符一行
        for (int i = 0; i < encoded64.length(); i += 64) {
            writer.println(encoded64.substring(i, Math.min(i + 64, encoded64.length())));
        }

        writer.println("-----END CERTIFICATE-----");
        writer.flush();
        // 注意：这里不关闭writer，因为我们使用的是外部传入的OutputStream
    }

    private void saveRunningLog(String logInfo, String logLeve) {
        if (this.logList.size() == 50) {
            this.logList.poll();
        }
        this.logList.add(logInfo);
        switch (logInfo) {
            case "warn" -> log.warn(logInfo);
            case "error" -> log.error(logInfo);
            default -> log.info(logInfo);
        }

    }

    private static boolean deleteRecursively(File file) {
        // 如果是文件或空文件夹，直接删除
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.isFile() || Objects.requireNonNull(file.list()).length == 0) {
            return file.delete();
        }

        // 如果是文件夹，先删除其内容
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                if (!deleteRecursively(subFile)) {
                    return false; // 删除失败时停止
                }
            }
        }

        // 最后删除文件夹本身
        return file.delete();
    }


}
