package fan.summer.hmoneta.service.acme;

import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.database.entity.acme.AcmeChallengeInfoEntity;
import fan.summer.hmoneta.database.repository.acme.AcmeChallengeInfoRepository;
import fan.summer.hmoneta.service.ddns.provider.DDNSProvider;
import fan.summer.hmoneta.service.ddns.provider.ProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;


import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    final int maxAttempts = 10;  // 最大尝试次数

    @Value("${hmoneta.acme.uri}")
    private String acmeUri;

    private final ProviderFactory providerFactory;

    private final AcmeChallengeInfoRepository acmeChallengeInfoRepository;

    @Autowired
    public AcmeAsyncService(ProviderFactory providerFactory, AcmeChallengeInfoRepository acmeChallengeInfoRepository) {
        this.providerFactory = providerFactory;
        this.acmeChallengeInfoRepository = acmeChallengeInfoRepository;
    }

    @Async
    protected void useDnsChallengeGetCertification(String domain, String providerName, AcmeChallengeInfoEntity info) {
        log.info("订单号:{}-开始为{}申请证书", info.getTaskId(), domain);
        try {
            log.info("登录ACME提供商");
            Session session = new Session(acmeUri);
            KeyPair keyPair = KeyPairUtils.createKeyPair(2048);
            // Create Account
            log.info("创建账号");
            Account account = new AccountBuilder()
                    .addContact("mailto:gitmain@outlook.sg")
                    .agreeToTermsOfService()
                    .useKeyPair(keyPair)
                    .create(session);
            // 保存账户信息
            // Login
            log.info("开始登录");
            Login login = new AccountBuilder().onlyExisting().agreeToTermsOfService().useKeyPair(keyPair).createLogin(session);
            // 发起订单
            log.info("创建订单");
            Order order = login.newOrder().domain(domain).create();
            Order bindOrder = login.bindOrder(order.getLocation());
            Authorization authorization = bindOrder.getAuthorizations().getFirst();
            log.info("发起DNS-01挑战");
            authorization.findChallenge(Dns01Challenge.class).ifPresentOrElse(challenge -> {
                String digest = challenge.getDigest();
                log.info("获取到DNS挑战内容:{}", digest);
                log.info("修改DNS");
                DDNSProvider ddnsProvider = providerFactory.generatorProvider(DDNSProvidersSelectEnum.valueOf(providerName));
                String subDomain = domain.substring(0, domain.indexOf('.'));
                String mainDomain = domain.substring(domain.indexOf('.') + 1);
                boolean status = ddnsProvider.modifyDdns(mainDomain, "_acme-challenge." + subDomain, digest, "TXT");
                log.info("DNS修改状态:{}", status);
                if (status & waitForDnsPropagation("_acme-challenge." + domain, digest)) {
                    try {
                        log.info("开启挑战");
                        challenge.trigger();
                        log.info("挑战结果验证");
                        while (!EnumSet.of(Status.VALID, Status.INVALID).contains(authorization.getStatus())) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(5000);
                                log.info("再次验证");
                                authorization.fetch();
                            } catch (InterruptedException ignored) {
                                log.error("线程意外中断:{}", ignored.getMessage());
                                break;
                            } catch (AcmeException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        log.info("挑战结果:{}", authorization.getStatus());
                        if (authorization.getStatus() == Status.VALID) {
                            log.info("验证通过");
                            removeTxtDnsInfo(ddnsProvider, mainDomain, subDomain);
                            // 获取证书
                            try {
                                log.info("获取证书");
                                KeyPair cerKeyPair = KeyPairUtils.createKeyPair(2048);
                                order.execute(cerKeyPair);
                                while (!EnumSet.of(Status.VALID, Status.INVALID).contains(order.getStatus())) {
                                    TimeUnit.MILLISECONDS.sleep(5000);
                                    log.info("继续验证证书签发状态");
                                    order.fetch();
                                }
                                if (order.getStatus() == Status.VALID) {
                                    log.info("订单确认完成，开始下载证书");
                                    Certificate cert = order.getCertificate();
                                    X509Certificate certificate = cert.getCertificate();
                                    List<X509Certificate> chain = cert.getCertificateChain();
                                } else {
                                    log.error("订单确认失败");
                                }
                            } catch (AcmeException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            removeTxtDnsInfo(ddnsProvider, mainDomain, subDomain);
                            log.error("验证未通过");
                        }
                    } catch (AcmeException e) {
                        removeTxtDnsInfo(ddnsProvider, mainDomain, subDomain);
                        throw new RuntimeException(e);
                    }
                }else {
                    log.error("未通过DNS记录验证");
                    if(status){
                        removeTxtDnsInfo(ddnsProvider, mainDomain, subDomain);
                    }
                }
            }, () -> log.error("未正常获取到Dns01Challenge对象"));
        } catch (AcmeException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean waitForDnsPropagation(String domain, String expectedTxtRecord) {
        log.info("开始验证域名{}的TXT解析是否生效", domain);
        try {
            Lookup lookup = new Lookup(domain, Type.TXT);
            for (int attempt = 1; maxAttempts >= attempt; attempt++) {
                lookup.run();
                if (lookup.getResult() == Lookup.SUCCESSFUL) {
                    log.info("DNS记录查询成功，开始验证");
                    Record[] answers = lookup.getAnswers();
                    for (Record record : answers) {
                        if(record instanceof TXTRecord txtRecord) {
                            for (String txt : txtRecord.getStrings()) {
                                if (txt.equals(expectedTxtRecord)) {
                                    log.info("通过解析验证，DNS记录正确，开始下一步");
                                    return true;
                                }
                            }
                        }
                    }
                    log.info("未匹配上指定的DNS记录，5秒后重新尝试。第{}次尝试", attempt);
                    TimeUnit.MILLISECONDS.sleep(5000);
                } else {
                    log.info("DNS记录查询未成功，5秒后重新尝试。第{}次尝试", attempt);
                    TimeUnit.MILLISECONDS.sleep(5000);
                    if(attempt == maxAttempts) {
                        return false;}
                }
            }
        } catch (TextParseException | InterruptedException e) {
            log.error(e.toString());
            return false;
        }
        return false;
    }

    private void removeTxtDnsInfo(DDNSProvider ddnsProvider, String domain, String subDomain){
        log.info("开始清理用于验证的DNS记录");
        try {
            ddnsProvider.deleteDdns(domain, "_acme-challenge." + subDomain, "TXT");
        }catch (Exception e){
            log.error(e.toString());
        }finally {
            log.info("结束清理");
        }
    }

}
