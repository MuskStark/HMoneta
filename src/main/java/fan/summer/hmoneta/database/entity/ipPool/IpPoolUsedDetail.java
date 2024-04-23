package fan.summer.hmoneta.database.entity.ipPool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "pool_id", nullable = false)
    private Long poolId;
    @Column(name = "server_name", nullable = false)
    private String serverName;
    @Column(name = "issued_ip", nullable = false)
    private String issuedIp;
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;
}
