package fan.summer.hmoneta.database.entity.acme;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Acme申请过程信息
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/2/25
 */
@Entity
@Data
@Table(name = "acme_challenge_Info")
public class AcmeChallengeInfoEntity {
    @Id
    private Long taskId;
    private String domain;

    private String certPublicKey;
    private String certPrivateKey;

    private String statusInfo;

    /**
     * 根据KeyPair存入
     * @param keyPair 待存入的KeyPair
     */
    public void saveKeyPair(KeyPair keyPair) {
        this.certPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        this.certPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] publicBytes = Base64.getDecoder().decode(this.certPublicKey);
        byte[] privateBytes = Base64.getDecoder().decode(this.certPrivateKey);
        return new KeyPair(keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes)), keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes)));
    }
}
