package fan.summer.hmoneta.common.enums.error;

/**
 * @author Phoebej
 * @date 2023/5/14
 */
public enum BusinessExceptionEnum {

    /**
     * 业务错误枚举类
     * 1.User模块错误枚举信息格式：USER_;code:1000-1099;
     * 2.ServerManager模块错误枚举信息格式:SM_;code:2000-2099;
     */
    USER_REG_INFO_ERROR(1000,"注册信息不全请重新输入"),
    USER_NAME_EXISTS_ERROR(1001,"用户名已存在请重新输入！"),
    USER_NOT_EXISTS(1002,"用户不存在"),
    USER_PASSWORD_ERROR(1003,"用户密码错误请重新输入"),
    USER_PASSWORD_DECRYPT_ERROR(1004,"用户密码解密失败"),
    IP_POOL_INFO_ERROR(2001,"Ip池创建信息不全请重新输入"),
    IP_POOL_INFO_NOT_MATCH_ERROR(2002,"Ip池信息不符合ip相关规范请检查"),
    IP_POOL_EXISTS_ERROR(2002,"该ip池已存在"),
    IP_POOL_NOT_EXISTS_ERROR(2003,"该ip池不存在"),
    IP_POOL_IP_ADDR_EXISTS_ERROR(2004,"该分Ip已存在"),
    IP_POOL_IP_FORMAT_ERROR(2005,"Ip地址格式错误"),
    IP_POOL_MAC_FORMAT_ERROR(2005,"MAC地址格式错误"),
    IP_POOL_IP_ADDR_REPEAT_ERROR(2006,"该IP池地址与同网段其他IP池地址存在重复"),
    SM_SERVER_EXISTS_ERROR(3001,"服务器已存在请重新输入"),
    SM_SERVER_NOT_EXISTS_ERROR(3002,"服务器不存在"),
    SM_SERVER_MAC_FORMAT_ERROR(3003,"MAC地址格式错误"),
    SM_SERVER_IP_FORMAT_ERROR(3004,"IP地址格式错误"),
    SM_SERVER_IP_MATCH_ERROR(3004,"IP地址不属于该IP池"),
    SM_SERVER_DELETE_ERROR(3005,"服务器信息删除失败"),
    WEB_MENUS_NOT_FIND_FATHER_NODE(4001,"未找到父菜单")
    ;

    BusinessExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

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
