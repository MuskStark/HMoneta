package fan.summer.hmoneta.webEntity.req.ipPool;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Ip资源池前端请求类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/24
 */
@Getter
@Setter
public class IpPoolModifyReq {
    private Long poolId;
    @NotNull(message = "组名称不能为空")
    private String poolName;
    @NotNull(message = "IP不能为空")
    private String networkAddress;
    @NotNull(message = "掩码不能为空")
    private String mask;
    private String startAddr;
    private String endAddr;
    private boolean create;
}
