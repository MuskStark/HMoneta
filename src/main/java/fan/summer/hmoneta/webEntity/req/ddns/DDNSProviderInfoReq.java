package fan.summer.hmoneta.webEntity.req.ddns;

import jakarta.persistence.Column;
import lombok.Data;

/**
 * 供应商信息系统请求类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/9/19
 */
@Data
public class DDNSProviderInfoReq {
    private String accessKeyId;
    private String accessKeySecret;
    private String providerName;
}
