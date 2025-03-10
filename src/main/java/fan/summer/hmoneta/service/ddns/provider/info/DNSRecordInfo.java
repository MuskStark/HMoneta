package fan.summer.hmoneta.service.ddns.provider.info;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用于记录所查询的Dns信息
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/3/10
 */
@Data
@AllArgsConstructor
public class DNSRecordInfo {
    private String Type;
    private long RecordId;
    private String Value;

    public DNSRecordInfo(){

    }
}
