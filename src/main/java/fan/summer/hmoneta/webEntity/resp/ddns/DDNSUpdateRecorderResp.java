package fan.summer.hmoneta.webEntity.resp.ddns;

import lombok.Data;

@Data
public class DDNSUpdateRecorderResp {
    private String subDomain;
    private String domain;
    private String ip;
    private String providerName;
    private String recorderId;
    private Integer status;
}
