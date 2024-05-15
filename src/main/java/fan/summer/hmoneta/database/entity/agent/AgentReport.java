package fan.summer.hmoneta.database.entity.agent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/15
 */
@Entity
@Table(name = "agent_report")
@Getter
@Setter
public class AgentReport {
    @Id
    private Long agentId;
    @Column(name = "cpu_name")
    private String cpuName;
    @Column(name = "cpu_core_num")
    private Integer cpuCoreNum;
    @Column(name = "cup_avg_load")
    private double cupAvgLoad;
    @Column(name = "total_memory")
    private long totalMemory;
    @Column(name = "free_memory")
    private long freeMemory;
    @Column(name = "total_disk")
    private long totalDisk;
    @Column(name = "free_disk")
    private String freeDisk;
}
