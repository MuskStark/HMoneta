package fan.summer.hmoneta.webEntity.req.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Phoebej
 * @date 2023/5/23
 */
@Data
public class UserLoginReq {

        @NotBlank(message = "用户名不能为空")
        private String userName;

        @NotBlank(message = "密码不能为空")
        private String password;

}
