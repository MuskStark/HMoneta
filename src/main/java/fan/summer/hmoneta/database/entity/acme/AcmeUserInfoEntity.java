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
 * Acme账号信息
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/3/11
 */
@Data
@Entity
@Table(name = "acme_user_Info")
public class AcmeUserInfoEntity {
    @Id
    private Long userId;
    private String userEmail;

    @Lob
    @Column(columnDefinition = "bytea", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY) // 强制 Hibernate 使用二进制类型
    private byte[] publicKey;

    @Lob
    @Column(columnDefinition = "bytea", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY) // 强制 Hibernate 使用二进制类型
    private byte[] privateKey;

    /**
     * 根据KeyPair存入
     *
     * @param keyPair 待存入的KeyPair
     */
    public void saveKeyPair(KeyPair keyPair) {
        this.publicKey = keyPair.getPublic().getEncoded();
        this.privateKey = keyPair.getPrivate().getEncoded();
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return new KeyPair(keyFactory.generatePublic(new X509EncodedKeySpec(this.publicKey)), keyFactory.generatePrivate(new PKCS8EncodedKeySpec(this.privateKey)));
    }

}
