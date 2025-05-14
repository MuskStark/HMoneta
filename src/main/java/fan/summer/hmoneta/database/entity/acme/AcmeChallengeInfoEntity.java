package fan.summer.hmoneta.database.entity.acme;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Objects;

/**
 * Acme申请过程信息
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/2/25
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "acme_challenge_Info")
public class AcmeChallengeInfoEntity {
    @Id
    private Long taskId;
    private Long userId;
    private String domain;
    private byte[] certPublicKey;
    private byte[] certPrivateKey;
    private String statusInfo;
    private Date certApplyTime;

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

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        AcmeChallengeInfoEntity that = (AcmeChallengeInfoEntity) o;
        return getTaskId() != null && Objects.equals(getTaskId(), that.getTaskId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
