package fan.summer.hmoneta.database.entity.ipPool;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ip_pool")
@Data
public class IpPool {

    @Id
    private Long poolId;
    @Column(name = "pool_name", nullable = false)
    private String poolName;
    @Column(name = "network_address", nullable = false)
    private String networkAddress;
    @Column(name = "mask", nullable = false)
    private String mask; // /24
    @Column(name = "start_addr", nullable = false)
    private String startAddr;
    @Column(name = "end_addr", nullable = false)
    private String endAddr;
    @Column(name = "available", nullable = false)
    private Integer available;
    @Column(name = "in_use", nullable = false)
    private Integer inUse;
    @Column(name = "remain", nullable = false)
    private Integer remain;

}
