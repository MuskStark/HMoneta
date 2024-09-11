package fan.summer.hmoneta.common.enums;

public enum DDNSProvidersSelectEnum {
    TencentCloud("TencentCloud", "腾讯云");

    private String name;
    private String label;

    DDNSProvidersSelectEnum(String name, String label) {
        this.name = name;
        this.label = label;
    }
    public String getName() {
        return name;
    }
    public String getLabel() {
        return label;
    }
}
