package fan.summer.hmoneta.common.handler;




import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.enums.error.DataBaseErrorEnum;
import fan.summer.hmoneta.common.enums.error.ServiceErrorEnum;
import fan.summer.hmoneta.common.enums.error.WebControllerExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.common.exception.DataBaseException;
import fan.summer.hmoneta.common.exception.WebControllerException;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理、数据预处理等
 * @author phoebej
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 处理系统异常的控制器方法。
     * 当Controller层发生异常时，此方法将被调用，用于生成一个包含错误信息的响应体。
     *
     * @param e 异常对象，捕获到的任何异常都将作为参数传递给这个方法。
     * @return 返回一个包含错误信息的ApiRestResponse对象。其中，错误码使用ServiceErrorEnum.SERVICE_ERROR。
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiRestResponse<ServiceErrorEnum> exceptionHandler(Exception e)  {
        LOG.error("系统异常：", e);
        return ApiRestResponse.error(ServiceErrorEnum.SERVICE_ERROR);
    }

    /**
     * 处理Web控制器中抛出的特定异常。
     *
     * @param e 抛出的WebControllerException异常实例，封装了具体的错误信息。
     * @return 返回一个ApiRestResponse对象，其中包含错误代码和错误消息。
     */
    @ExceptionHandler(value = WebControllerException.class)
    @ResponseBody
    public ApiRestResponse<WebControllerExceptionEnum> exceptionHandler(WebControllerException e)  {
        LOG.error("WEB请求异常：{}", e.getE().getMessage());
        return ApiRestResponse.error(e.getE().getCode(),e.getE().getMessage());
    }

    /**
     * 处理业务异常的异常处理器。
     * 当发生业务异常时，将异常信息封装成ApiRestResponse对象返回给前端。
     *
     * @param e 抛出的业务异常对象，包含具体的错误代码和错误信息。
     * @return 返回一个包含错误代码和错误信息的ApiRestResponse对象。
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ApiRestResponse<BusinessExceptionEnum> exceptionHandler(BusinessException e)  {
        LOG.error("业务异常：{}", e.getE().getMessage());
        return ApiRestResponse.error(e.getE().getCode(),e.getE().getMessage());
    }

    /**
     * 处理数据库异常的异常处理器。
     * 当发生数据库异常时，此处理器将捕获异常，并返回一个包含错误信息的API响应。
     *
     * @param e 数据库异常对象，封装了具体的数据库错误信息。
     * @return 返回一个表示错误的ApiRestResponse对象，其中包含了数据库错误的代码和消息。
     */
    @ExceptionHandler(value = DataBaseException.class)
    @ResponseBody
    public ApiRestResponse<DataBaseErrorEnum> exceptionHandler(DataBaseException e)  {
        LOG.error("数据库异常：{}", e.getE().getMessage());
        return ApiRestResponse.error(e.getE().getCode(),e.getE().getMessage());
    }

    /**
     * 统一处理数据绑定异常。
     * 当前端请求参数与后端接收参数不匹配时，会抛出数据绑定异常（BindException）。
     * 此方法捕获此类异常，并返回一个包含错误信息的响应体。
     *
     * @param e 绑定异常对象，包含具体的错误信息。
     * @return 返回一个表示错误信息的ApiRestResponse对象，其中错误码为9999，错误信息为异常中包含的错误消息。
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ApiRestResponse<ServiceErrorEnum> exceptionHandler(BindException e)  {
        LOG.error("校验异常：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ApiRestResponse.error(9999,e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }


}