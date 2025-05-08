package fan.summer.hmoneta.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.user.User;
import fan.summer.hmoneta.service.UserService;
import fan.summer.hmoneta.util.JwtUtil;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.user.UserLoginReq;
import fan.summer.hmoneta.webEntity.req.user.UserRegReq;
import fan.summer.hmoneta.webEntity.req.user.UserUpdateReq;
import fan.summer.hmoneta.webEntity.resp.user.UserResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * HMoneta用户服务API
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/23
 */
@RestController
@RequestMapping("/hm")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取公钥的接口。
     *
     * @return ApiRestResponse<String> 包含公钥的响应对象。成功时，response的数据部分为公钥字符串。
     */
    @GetMapping("/publicKey")
    public ApiRestResponse<String> getPublicKey() {
        return ApiRestResponse.success(userService.issuePublicKey());
    }

    /**
     * 用户注册
     *
     * @param req 用户注册请求信息
     * @return 注册成功返回用户信息的响应
     */
    @PostMapping("/register")
    public ApiRestResponse<UserResp> register(@RequestBody UserRegReq req) {
        if(ObjUtil.isEmpty(req.getEmail()) | ObjUtil.isEmpty(req.getUserName()) | ObjUtil.isEmpty(req.getPassword())){
            throw new BusinessException(BusinessExceptionEnum.USER_REG_INFO_ERROR);
        };
        User user = userService.addUser(req);
        return ApiRestResponse.success(BeanUtil.copyProperties(user, UserResp.class));

    }

    /**
     * 更新用户信息。
     *
     * @param req 用户更新请求对象，包含需要更新的用户信息。
     * @return 返回一个API响应对象，其中包含更新成功的用户信息。
     */
    public ApiRestResponse<UserResp> updateUser(@RequestBody UserUpdateReq req){
        userService.updateUser(req);
        return ApiRestResponse.success(BeanUtil.copyProperties(req, UserResp.class));
    }

    /**
     * 用户登录接口。
     *
     * @param req 包含用户名和密码的用户登录请求对象。
     * @return 返回一个API响应对象，其中包含登录成功的用户信息。
     */
    @PostMapping("/login")
    public ApiRestResponse<UserResp> login(@RequestBody UserLoginReq req){
        User loginUser = userService.login(req.getUserName(), req.getPassword());
        UserResp userResp = BeanUtil.copyProperties(loginUser, UserResp.class);
        // 临时解决token未更新签发的问题
        userResp.setToken(null);
        userResp.setToken(JwtUtil.createTokenForObject(userResp));
        return ApiRestResponse.success(userResp);
    }
}
