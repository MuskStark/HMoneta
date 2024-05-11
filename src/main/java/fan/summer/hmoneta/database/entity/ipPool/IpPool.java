package fan.summer.hmoneta.database.entity.ipPool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "ip_pool")
@Getter
@Setter
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
