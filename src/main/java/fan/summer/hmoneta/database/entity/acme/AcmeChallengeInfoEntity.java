package fan.summer.hmoneta.database.entity.acme;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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

    @Lob
    @Column(columnDefinition = "bytea", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] certPublicKey;
    @Lob
    @Column(columnDefinition = "bytea", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] certPrivateKey;

    private String statusInfo;

    /**
     * 根据KeyPair存入
     *
     * @param keyPair 待存入的KeyPair
     */
    public void saveKeyPair(KeyPair keyPair) {
        this.certPublicKey = keyPair.getPublic().getEncoded();
        this.certPrivateKey = keyPair.getPrivate().getEncoded();
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return new KeyPair(keyFactory.generatePublic(new X509EncodedKeySpec(this.certPublicKey)), keyFactory.generatePrivate(new PKCS8EncodedKeySpec(this.certPrivateKey)));
    }
}
