package fan.summer.hmoneta.webEntity.agent;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统详细信息类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/12
 */
@Getter
@Setter
public class SystemInfoEntity {
    private Long agentId;
    private String cpuName;
    private double[] cupLoad;
    private long totalMemory;
    private long freeMemory;
    private long totalDisk;
    private String freeDisk;
}