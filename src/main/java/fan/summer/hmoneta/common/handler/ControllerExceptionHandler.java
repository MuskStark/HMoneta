package fan.summer.hmoneta.common.handler;



import fan.summer.homelab.common.enums.BusinessExceptionEnum;
import fan.summer.homelab.common.enums.DataBaseErrorEnum;
import fan.summer.homelab.common.enums.ServiceErrorEnum;
import fan.summer.homelab.common.exception.BusinessException;
import fan.summer.homelab.common.exception.DataBaseException;
import fan.summer.homelab.apiEntity.common.ApiRestResponse;
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
     * 所有异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiRestResponse<ServiceErrorEnum> exceptionHandler(Exception e)  {
        LOG.error("系统异常：", e);
        return ApiRestResponse.error(ServiceErrorEnum.SERVICE_ERROR);
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ApiRestResponse<BusinessExceptionEnum> exceptionHandler(BusinessException e)  {
        LOG.error("业务异常：{}", e.getE().getMessage());
        return ApiRestResponse.error(e.getE().getCode(),e.getE().getMessage());
    }

    @ExceptionHandler(value = DataBaseException.class)
    @ResponseBody
    public ApiRestResponse<DataBaseErrorEnum> exceptionHandler(DataBaseException e)  {
        LOG.error("数据库异常：{}", e.getE().getMessage());
        return ApiRestResponse.error(e.getE().getCode(),e.getE().getMessage());
    }

    /**
     * 所有异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ApiRestResponse<ServiceErrorEnum> exceptionHandler(BindException e)  {
        LOG.error("校验异常：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ApiRestResponse.error(9999,e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }


}