package fan.summer.hmoneta.service.acme;

import cn.hutool.core.util.ObjectUtil;
import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.database.entity.ddns.DDNSRecorderEntity;
import fan.summer.hmoneta.service.ddns.DDNSService;
import fan.summer.hmoneta.service.ddns.provider.DDNSProvider;
import fan.summer.hmoneta.service.ddns.provider.ProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 提供ACME相关服务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/2/6
 */
@Service
@Slf4j
public class AcmeService {

    @Value("${hmoneta.acme.uri}")
    private String acmeUri;

    private final ProviderFactory providerFactory;
    private final DDNSService ddnsService;

    @Autowired
    public AcmeService(ProviderFactory providerFactory, DDNSService ddnsService) {
        this.providerFactory = providerFactory;
        this.ddnsService = ddnsService;
    }

    /**
     * Uses a DNS challenge to obtain a certificate for the specified domain.
     *
     * @param domain       The domain for which to obtain the certificate.
     * @param providerName The name of the DDNS provider. If not provided, it will be fetched from the DDNS records.
     */
    public void useDnsChallengeGetCertification(String domain, String providerName) {
        log.info("开始为{}申请证书", domain);
        String provider;
        String subDomain = domain.substring(0, domain.indexOf('.'));
        String mainDomain = domain.substring(domain.indexOf('.') + 1);
        if (ObjectUtil.isEmpty(providerName)) {
            // 通过DDNS记录查询Provider
            DDNSRecorderEntity ddnsRecorderEntity = ddnsService.queryRecordBySubDomainAndDomain(subDomain, mainDomain);
            if (ObjectUtil.isEmpty(ddnsRecorderEntity)) {
                throw new RuntimeException("未正确提供供应商无法申请证书");
            }
            provider = ddnsRecorderEntity.getProviderName();
        } else {
            provider = providerName;
        }
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
                DDNSProvider ddnsProvider = providerFactory.generatorProvider(DDNSProvidersSelectEnum.valueOf(provider));
                boolean status = ddnsProvider.modifyDdns(mainDomain, subDomain, digest, "TXT");
                log.info("DNS修改状态:{}", status);
                // 验证DNS信息是否正确
                if (status) {
                    // 等待DNS生效
                    try {
                        log.info("开启挑战");
                        challenge.trigger();
                        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                        CompletableFuture<Status> pollFuture = CompletableFuture.supplyAsync(() -> {
                            // 验证
                            log.info("挑战结果验证");
                            while (!EnumSet.of(Status.VALID, Status.INVALID).contains(authorization.getStatus())) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(3000);
                                    log.info("再次验证");
                                    authorization.fetch();
                                } catch (InterruptedException ignored) {
                                    log.error("线程意外中断:{}", ignored.getMessage());
                                    Thread.currentThread().interrupt();
                                    break;
                                } catch (AcmeException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            log.info("挑战结果:{}", authorization.getStatus());
                            return authorization.getStatus();
                        }, scheduledExecutorService);
                        pollFuture.thenAccept(dnsCheckstatus -> {
                            if (dnsCheckstatus == Status.VALID) {
                                log.info("验证通过");
                                // 获取证书
                                try {
                                    log.info("获取证书");
                                    order.execute(keyPair);
                                    while (!EnumSet.of(Status.VALID, Status.INVALID).contains(order.getStatus())) {
                                        TimeUnit.MILLISECONDS.sleep(3000);
                                        log.info("继续验证证书签发状态");
                                        order.fetch();
                                    }
                                    if (order.getStatus() == Status.VALID) {
                                        log.info("订单确认完成，开始下载证书");
                                        Certificate certificate = order.getCertificate();
                                    } else {
                                        log.error("订单确认失败");
                                    }
                                } catch (AcmeException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                            } else {
                                log.error("验证未通过");
                            }
                        });


                    } catch (AcmeException e) {
                        throw new RuntimeException(e);
                    }
                }

            }, () -> log.error("未正常获取到Dns01Challenge对象"));


        } catch (AcmeException e) {
            log.error(e.getMessage(), new RuntimeException(e));
        }
    }


}
