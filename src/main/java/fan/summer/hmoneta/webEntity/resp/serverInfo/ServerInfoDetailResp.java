package fan.summer.hmoneta.webEntity.resp.serverInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * HMoneta-服务器信息详情响应实体类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/25
 */
@Getter
@Setter
public class ServerInfoDetailResp {

    private Long id;
    private Long poolId;
    private String serverName;
    private String serverMacAddr;
    private String serverIpAddr;
    private String serverPort;
    private Boolean isAlive;

}
