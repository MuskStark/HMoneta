package fan.summer.hmoneta.common.enums;

public enum WebControllerExceptionEnum {
    WEB_IP_POOL_REQ_EMPTY(1001,"IP池请求不能为空"),
    WEB_IP_POOL_REQ_ID_EMPTY(1002,"IP池请求ID不能为空"),
    WEB_IP_FORMAT_ERROR(1003,"IP池地址信息格式错误"),
    WEB_IP_NOT_IN_SAME_NETWORK(1004,"IP池地址不在同一个网络段"),
    WEB_SM_REQ_EMPTY(1005,"服务器请求不能为空"),
    WEB_SM_REQ_MAC_FORMAT_ERROR(1006,"服务器MAC地址格式错误"),
    WEB_SM_REQ_IP_FORMAT_ERROR(1007,"服务器IP地址格式错误"),
    WEB_SM_REQ_IP_IS_NOT_IN_POOL(1008,"服务器IP地址不在IP池中"),
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
