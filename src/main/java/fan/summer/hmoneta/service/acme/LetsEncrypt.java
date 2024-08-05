package fan.summer.hmoneta.service.acme;

import fan.summer.hmoneta.service.ddns.DDNSService;
import okhttp3.Challenge;
import org.apache.tomcat.util.http.parser.Authorization;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;

/**
 * 通过LetsEncrypt获取SSL证书
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/8/3
 */
@Service
public class LetsEncrypt {

    private final DDNSService ddnsService;
    private static final String BASE_DIR = "certificates";
    private static final String USER_KEY_FILE = "user.key";
    private static final String DOMAIN_KEY_FILE = "domain.key";
    private static final String DOMAIN_CSR_FILE = "domain.csr";
    private static final String DOMAIN_CHAIN_FILE = "domain-chain.crt";

    @Autowired
    public  LetsEncrypt(DDNSService ddnsService){
        this.ddnsService = ddnsService;
    }
    public void SSLCertificateFetcher(String domain){
        // 创建域名特定的目录
        Path domainDir = createDomainDirectory(domain);

        // 1. 创建或加载账户
        KeyPair userKeyPair = loadOrCreateUserKeyPair(domainDir);
        Session session = new Session("acme://letsencrypt.org/staging");
        Account account = new AccountBuilder()
                .agreeToTermsOfService()
                .useKeyPair(userKeyPair)
                .create(session);

        // 2. 创建订单
        Order order = account.newOrder().domains(domain).create();

        // 3. 执行验证
        for (Authorization auth : order.getAuthorizations()) {
            Challenge challenge = dnsChallenge(auth);
            if (challenge != null) {
                completeChallenge(challenge, domain);
            }
        }

        // 4. 完成订单
        KeyPair domainKeyPair = loadOrCreateDomainKeyPair(domainDir);
        CSR csr = new CSRBuilder()
                .addDomain(domain)
                .sign(domainKeyPair);
        Path csrFile = domainDir.resolve(DOMAIN_CSR_FILE);
        try (FileWriter fw = new FileWriter(csrFile.toFile())) {
            CSRWriter csrWriter = new CSRWriter(fw);
            csrWriter.write(csr);
        }

        order.execute(csr);

        // 5. 下载证书
        Certificate certificate = order.getCertificate();
        Path chainFile = domainDir.resolve(DOMAIN_CHAIN_FILE);
        try (FileWriter fw = new FileWriter(chainFile.toFile())) {
            certificate.writeCertificate(fw);
        }

        System.out.println("Certificate successfully created for " + domain + "!");

    }

    private static Path createDomainDirectory(String domain) throws IOException {
        Path domainDir = Paths.get(BASE_DIR, domain);
        Files.createDirectories(domainDir);
        return domainDir;
    }

    private static KeyPair loadOrCreateUserKeyPair(Path domainDir) throws IOException {
        Path userKeyFile = domainDir.resolve(USER_KEY_FILE);
        if (Files.exists(userKeyFile)) {
            try (FileReader fr = new FileReader(userKeyFile.toFile())) {
                return KeyPairUtils.readKeyPair(fr);
            }
        } else {
            KeyPair userKeyPair = KeyPairUtils.createKeyPair(2048);
            try (FileWriter fw = new FileWriter(userKeyFile.toFile())) {
                KeyPairUtils.writeKeyPair(userKeyPair, fw);
            }
            return userKeyPair;
        }
    }

    private static KeyPair loadOrCreateDomainKeyPair(Path domainDir) throws IOException {
        Path domainKeyFile = domainDir.resolve(DOMAIN_KEY_FILE);
        if (Files.exists(domainKeyFile)) {
            try (FileReader fr = new FileReader(domainKeyFile.toFile())) {
                return KeyPairUtils.readKeyPair(fr);
            }
        } else {
            KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
            try (FileWriter fw = new FileWriter(domainKeyFile.toFile())) {
                KeyPairUtils.writeKeyPair(domainKeyPair, fw);
            }
            return domainKeyPair;
        }
    }

    private static Challenge dnsChallenge(Authorization auth) throws AcmeException {
        return auth.findChallenge(Dns01Challenge.TYPE);
    }

    private static void completeChallenge(Challenge challenge, String domain) throws AcmeException {
        if (challenge instanceof Dns01Challenge) {
            Dns01Challenge dns01Challenge = (Dns01Challenge) challenge;
            String recordName = "_acme-challenge." + domain;
            String recordValue = dns01Challenge.getDigest();

            updateDNSRecord(recordName, recordValue);

            try {
                System.out.println("Waiting for DNS propagation...");
                Thread.sleep(60000); // 等待60秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            challenge.trigger();

            while (challenge.getStatus() == Status.PENDING) {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                challenge.update();
            }
        }
    }
}
