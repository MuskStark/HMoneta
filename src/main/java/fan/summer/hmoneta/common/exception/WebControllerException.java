package fan.summer.hmoneta.common.exception;

import fan.summer.hmoneta.common.enums.BusinessExceptionEnum;
import fan.summer.hmoneta.common.enums.WebControllerExceptionEnum;

/**
 * Controller 层异常
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/24
 */
public class WebControllerException extends RuntimeException {
    private WebControllerExceptionEnum e;

    public WebControllerException(WebControllerExceptionEnum e) {
        this.e = e;
    }

    public WebControllerExceptionEnum getE() {
        return e;
    }

    public void setE(WebControllerExceptionEnum e) {
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
