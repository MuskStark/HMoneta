package fan.summer.hmoneta.database.entity.agent;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/13
 */
@Entity
@Table(name = "agent_info")
@Getter
@Setter
public class AgentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agentId;
    @Column(name = "server_id", nullable = false)
    private Long serverId;
    @Column(name = "server_ip", nullable = false)
    private String serverIp;
    @Column(name = "is_alive", nullable = false)
    private Boolean isAlive;
    @Column(name = "is_issue_config", nullable = false)
    private Boolean isIssueConfig;

}
