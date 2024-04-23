package fan.summer.hmoneta.webEntity.common;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Phoebej
 * @date 2023/5/21
 */

@Data
public class PageConfigReq {

    @NotNull(message = "page 不能为空")
    private Integer page;

    @NotNull(message = "size 不能为空")
    @Max(value = 100, message = "每页最大条数100")
    private Integer size;

}
