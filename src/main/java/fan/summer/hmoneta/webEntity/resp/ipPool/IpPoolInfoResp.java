package fan.summer.hmoneta.webEntity.resp.ipPool;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * Ip资源池前端响应类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/24
 */
@Getter
@Setter
public class IpPoolInfoResp {
    private Long poolId;
    private String poolName;
    private String networkAddress;
    private String mask;
    private String startAddr;
    private String endAddr;
    private Integer available;
    private Integer inUse;
    private Integer remain;
}
