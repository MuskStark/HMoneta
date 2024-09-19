package fan.summer.hmoneta.database.entity.ddns;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于存储DDNS提供商秘钥
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/31
 */
@Entity
@Table(name = "ddns_info")
@Getter
@Setter
public class DDNSInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ddnsId;
    @Column(name = "access_key_id", nullable = false)
    private String accessKeyId;
    @Column(name = "access_key_secret", nullable = false)
    private String accessKeySecret;
    @Column(name = "provider_name", nullable = false)
    private String providerName;

}
