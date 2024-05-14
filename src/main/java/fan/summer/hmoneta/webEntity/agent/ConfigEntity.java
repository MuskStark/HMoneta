package fan.summer.hmoneta.webEntity.agent;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * Agent服务器下发配置信息
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/12
 */
@Getter
@Setter
public class ConfigEntity {
    private String reportUrl;
    private Long agentId;
}
