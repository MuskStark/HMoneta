package fan.summer.hmoneta.common.enums.error;

public enum DDNSServerErrorEnum {
    PROVIDER_LIST_EMPTY(5001, "未录入DDNS供应商");

    private int code;
    private String message;

    DDNSServerErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
