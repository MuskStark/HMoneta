package fan.summer.hmoneta.webEntity.resp.ddns;

import lombok.Getter;
import lombok.Setter;

/**
 * DDNS 供应商信息返回类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/8/25
 */
@Getter
@Setter
public class ProviderInfoResp {
    private String providerName;
    private String accessKeyId;

}
