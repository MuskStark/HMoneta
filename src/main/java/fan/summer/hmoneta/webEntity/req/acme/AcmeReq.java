package fan.summer.hmoneta.webEntity.req.acme;

import lombok.Data;

/**
 * 申请指定域名请求类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/5/11
 */
@Data
public class AcmeReq {
    private String domain;
    private String dnsProvider;
}
