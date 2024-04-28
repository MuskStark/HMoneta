package fan.summer.hmoneta.database.entity.ipPool;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ip池使用情况
 */
@Entity
@Table(name = "ip_pool_used_detail")
@Getter
@Setter
public class IpPoolUsedDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "pool_id", nullable = false)
    private Long poolId;
    @Column(name = "server_name", nullable = false)
    private String serverName;
    @Column(name = "issued_ip", nullable = false)
    private String issuedIp;
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;
}
