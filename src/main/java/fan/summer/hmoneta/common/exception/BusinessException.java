package fan.summer.hmoneta.common.exception;


import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;

/**
 * @author Phoebej
 * @date 2023/5/14
 */

public class BusinessException extends RuntimeException{
    private BusinessExceptionEnum e;

    public BusinessException(BusinessExceptionEnum e) {
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

    public void setE(BusinessExceptionEnum e) {
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
