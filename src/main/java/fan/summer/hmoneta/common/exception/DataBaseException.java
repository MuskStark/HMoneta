package fan.summer.hmoneta.common.exception;

import fan.summer.homelab.common.enums.DataBaseErrorEnum;

/**
 * 数据库异常
 * @author Phoebej
 * @date 2023/8/07
 */

public class DataBaseException extends RuntimeException{
    private DataBaseErrorEnum e;

    public DataBaseException(DataBaseErrorEnum e) {
        this.e = e;
    }

    public DataBaseErrorEnum getE() {
        return e;
    }

    public void setE(DataBaseErrorEnum e) {
        this.e = e;
    }

    /**
     * 屏蔽堆栈信息
     * @return 当前类
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
