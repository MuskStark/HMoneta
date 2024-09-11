package fan.summer.hmoneta.common.enums.error;

/**
 * @author Phoebej
 * @date 2023/5/13
 */

public enum ServiceErrorEnum  {

    SERVICE_ERROR(80000,"服务器异常");

    private int code;
    private String message;

    ServiceErrorEnum(int code, String message) {
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
