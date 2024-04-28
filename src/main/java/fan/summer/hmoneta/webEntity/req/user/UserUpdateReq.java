package fan.summer.hmoneta.webEntity.req.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息更新请求实体类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/28
 */
@Getter
@Setter
public class UserUpdateReq {
    @NotBlank(message = "用户名不能为空")
    private String userName;
    @NotBlank(message = "邮箱不能为空")
    private String email;
    @NotBlank(message = "老密码不能为空")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
