package fan.summer.hmoneta.database.entity.ddns;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 域名DDNS记录表
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/1/14
 */
@Entity
@Data
@Table(name = "ddns_recorder_info")
public class DDNSRecorderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "sub_domain", length = 100, nullable = false)
    private String subDomain;
    @Column(name = "domain", length = 500, nullable = false)
    private String domain;
    @Column(name = "provider_name", length = 100, nullable = false)
    private String providerName;

}
