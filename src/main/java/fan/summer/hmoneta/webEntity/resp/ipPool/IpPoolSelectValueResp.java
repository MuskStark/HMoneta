package fan.summer.hmoneta.webEntity.resp.ipPool;

import lombok.Getter;
import lombok.Setter;

/**
 * 前端IpPool相关Select组件响应类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/25
 */
@Getter
@Setter
public class IpPoolSelectValueResp {
    // IpPoolId
    private Long value;
    // IpPoolName
    private String label;
}
