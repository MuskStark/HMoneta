package fan.summer.hmoneta.webEntity.resp.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息请求响应类
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/23
 */
@Data
public class UserResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private String token;
}
