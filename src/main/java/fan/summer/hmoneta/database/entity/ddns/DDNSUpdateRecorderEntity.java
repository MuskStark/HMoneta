package fan.summer.hmoneta.database.entity.ddns;

import jakarta.persistence.*;
import lombok.Data;

/**
 * DDNS解析记录实体
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/11/23
 */
@Entity
@Data
@Table(name = "ddns_update_recorder")
public class DDNSUpdateRecorderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "recorder_id", nullable = false)
    private Long recorderId;
    @Column(name = "sub_domain", length = 100, nullable = false)
    private String subDomain;
    @Column(name = "domain", length = 500, nullable = false)
    private String domain;
    @Column(name = "ip", length = 100, nullable = false)
    private String ip;
    @Column(name = "provider_name", length = 100, nullable = false)
    private String providerName;
    @Column(name = "status", nullable = false)
    private Boolean status;

}
