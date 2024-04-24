package fan.summer.hmoneta.common.enums;

public enum WebControllerExceptionEnum {
    WEB_IP_POOL_REQ_EMPTY(1001,"IP池请求不能为空"),
    ;
    private int code;
    private String message;

    WebControllerExceptionEnum(int code, String message) {
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
