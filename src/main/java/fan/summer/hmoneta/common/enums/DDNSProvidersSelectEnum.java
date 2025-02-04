package fan.summer.hmoneta.common.enums;

import lombok.Getter;

@Getter
public enum DDNSProvidersSelectEnum {
    TencentCloud("TencentCloud", "腾讯云");

    private String name;
    private String label;

    DDNSProvidersSelectEnum(String name, String label) {
        this.name = name;
        this.label = label;
    }
}
