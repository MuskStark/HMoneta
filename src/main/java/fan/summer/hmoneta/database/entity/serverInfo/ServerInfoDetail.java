package fan.summer.hmoneta.database.entity.serverInfo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "server_info_detail")
@Getter
@Setter
public class ServerInfoDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pool_id", nullable = false)
    private Long poolId;
    @Column(name = "server_name", nullable = false)
    private String serverName;
    @Column(name = "server_mac_addr", nullable = false)
    private String serverMacAddr;
    @Column(name = "server_ip_addr", nullable = false)
    private String serverIpAddr;
    @Column(name = "server_port", nullable = false)
    private String serverPort;
    @Column(name = "is_alive", nullable = false)
    private Boolean isAlive;
}
