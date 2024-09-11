package fan.summer.hmoneta.common.enums.error;

/**
 * 数据库通用异常
 */
public enum DataBaseErrorEnum {
    DATABASE_SEARCH_ERROR(40001,"数据库查询失败")
    ,DATABASE_INSERT_ERROR(40002,"数据库插入失败")
    ,DATABASE_DELETE_ERROR(40002,"数据库删除失败")
    ;

    DataBaseErrorEnum(int code, String message) {
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
