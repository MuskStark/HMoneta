package fan.summer.hmoneta.webEntity.resp.ddns;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Data
public class DDNSUpdateRecorderResp {
    private String subDomain;
    private String domain;
    private String ip;
    private String providerName;
    private Integer status;
}
