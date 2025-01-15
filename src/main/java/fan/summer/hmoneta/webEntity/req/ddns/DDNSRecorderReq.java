package fan.summer.hmoneta.webEntity.req.ddns;

import lombok.Data;

/**
 * 需DDNS域名请求体
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/1/15
 */
@Data
public class DDNSRecorderReq {
    private String subDomain;
    private String domain;
    private String providerName;
}
