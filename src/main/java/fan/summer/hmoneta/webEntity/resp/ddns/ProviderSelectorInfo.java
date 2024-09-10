package fan.summer.hmoneta.webEntity.resp.ddns;

import lombok.Getter;
import lombok.Setter;

/**
 * Provider前端Selector信息
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/9/10
 */
@Getter
@Setter
public class ProviderSelectorInfo {

    private String value;
    private String label;

    public ProviderSelectorInfo(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
