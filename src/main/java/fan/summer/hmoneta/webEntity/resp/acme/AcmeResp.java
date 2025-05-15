package fan.summer.hmoneta.webEntity.resp.acme;

import lombok.Data;

/**
 * 证书申请状态长训响应类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/5/15
 */
@Data
public class AcmeResp {
    private String domain;
    private String statusInfo;
}
