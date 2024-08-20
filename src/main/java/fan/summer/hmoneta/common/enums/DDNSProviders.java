package fan.summer.hmoneta.common.enums;

public enum DDNSProviders {
    TencentCloud("tencent");

    private String name;

    DDNSProviders(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
