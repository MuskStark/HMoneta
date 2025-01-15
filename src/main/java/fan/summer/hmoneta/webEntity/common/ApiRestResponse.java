package fan.summer.hmoneta.webEntity.common;


import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.enums.error.ServiceErrorEnum;

/**
 * Api接口数据返回对象
 * @author Phoebej
 * @date 2023/3/1
 * @since v1.0.0
 */
public class ApiRestResponse<T> {

    private Integer code;
    private String message;
    private boolean status = true;
    private T data;

    private static final int SUCCESS_CODE = 200;
    private static final String SUCCESS_MESSAGE = "success";
    private static final boolean SUCCESS_STATUS = true;

    public ApiRestResponse() {
        this(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public ApiRestResponse(Integer code, String message, T data) {
        this.code = SUCCESS_CODE;
        this.message = SUCCESS_MESSAGE;
        this.data = data;
    }

    public ApiRestResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 响应成功处理不带处理结果
     * @return
     * @param <T>
     */
    public static <T> ApiRestResponse<T> success(){
        return new ApiRestResponse<>();
    }
    /**
     * 响应成功处理带处理结果
     * @return
     * @param <T>
     */
    public static <T> ApiRestResponse<T> success(T result){
        ApiRestResponse<T> objectApiRestResponse = new ApiRestResponse<>();
        objectApiRestResponse.setData(result);
        return objectApiRestResponse;
    }
    /**
     * 响应处理失败
     * @return
     * @param code 错误代码
     * @param msg 错误信息
     */
    public static <T> ApiRestResponse<T> error(Integer code,String msg){
        ApiRestResponse<T> objectApiRestResponse = new ApiRestResponse<>(code, msg);
        objectApiRestResponse.setStatus(false);
        return objectApiRestResponse;
    }

    /**
     * 响应处理失败
     * @param es 枚举类中定义的属性
     * @return
     * @param <T>
     */
    public static <T> ApiRestResponse<T> error(ServiceErrorEnum es){
        ApiRestResponse<T> apiRestResponse = new ApiRestResponse<>(es.getCode(), es.getMessage());
        apiRestResponse.setStatus(false);
        return apiRestResponse;
    }

    public static <T> ApiRestResponse<T> error(BusinessExceptionEnum es){
        ApiRestResponse<T> apiRestResponse = new ApiRestResponse<>(es.getCode(), es.getMessage());
        apiRestResponse.setStatus(false);
        return apiRestResponse;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
