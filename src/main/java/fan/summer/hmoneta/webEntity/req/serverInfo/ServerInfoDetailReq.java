package fan.summer.hmoneta.webEntity.req.serverInfo;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * HMoneta-服务器信息详情请求实体类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/25
 */
@Getter
@Setter
public class ServerInfoDetailReq {

    private Long id;
    @NotBlank(message = "服务器归属地址池不能为空")
    private Long poolId;
    @NotBlank(message = "服务器名称不能为空")
    private String serverName;
    @NotBlank(message = "服务器Mac地址不能为空")
    private String serverMacAddr;
    @NotBlank(message = "服务器Ip地址不能为空")
    private String serverIpAddr;
    @NotBlank(message = "服务器端口不能为空")
    private String serverPort;
    private boolean create;

}
