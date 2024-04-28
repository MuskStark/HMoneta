package fan.summer.hmoneta.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.user.User;
import fan.summer.hmoneta.database.repository.UserRepository;
import fan.summer.hmoneta.util.EncryptionUtil;
import fan.summer.hmoneta.util.JwtUtil;
import fan.summer.hmoneta.util.Md5Unit;
import fan.summer.hmoneta.webEntity.req.user.UserRegReq;
import fan.summer.hmoneta.webEntity.req.user.UserUpdateReq;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;

/**
 * HMoneta用户服务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/23
 */
@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final Map<String, String> keyPair;


    @Resource
    private UserRepository userRepository;


    public UserService() {
        try {
            keyPair = EncryptionUtil.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 在应用程序启动后，初始化一个管理员用户。
     * 如果数据库中尚未存在用户，则创建一个用户名为"admin"的用户，并设置随机密码。
     * -@PostConstruct注解，确保在该Bean的依赖注入完成后执行一次。
     */
    @PostConstruct
    public void initAdminUser() {
        LOG.info(">>>>>>>>>>>start init admin user");
        List<User> all = userRepository.findAll();
        if (all.isEmpty()) {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+~`|}{[]:;?><,./-=";
            Random random = new SecureRandom();
            StringJoiner password = new StringJoiner("");

            for (int i = 0; i < 10; i++) {
                int index = random.nextInt(characters.length());
                password.add(characters.charAt(index) + "");
            }
            String finalPassword = password.toString();
            int salt = Md5Unit.saltGenerator();
            User user = new User();
            user.setUserName("admin");
            user.setPassword(Md5Unit.md5Digest(finalPassword, salt));
            user.setEmail("homelab@summer.fan");
            user.setSalts(salt);
            user.setCreateTime(DateTime.now());
            user.setUpdateTime(DateTime.now());
            user.setToken(JwtUtil.createTokenForObject(user));
            userRepository.save(user);
            LOG.info("admin user init success,userName:admin;password:{}", finalPassword);
        } else {
            LOG.info("already have admin user");
        }
        LOG.info(">>>>>>>>>>>end init admin user");
    }

    /**
     * 发行公钥
     * 获取生成的密钥对中的公钥部分。
     *
     * @return 返回一个字符串形式的公钥。
     */
    public String issuePublicKey() {
        return keyPair.get("publicKey");
    }

    /**
     * 添加用户
     *
     * @param req 用户注册请求对象，包含用户注册信息
     * @return 添加成功的用户对象
     * @throws BusinessException 如果用户名称已存在，则抛出业务异常
     */
    public User addUser(UserRegReq req) {
        User byUserName = userRepository.findByUserName(req.getUserName());
        if (ObjUtil.isEmpty(byUserName)) {
            DateTime now = DateTime.now();
            User user = BeanUtil.copyProperties(req, User.class);
            int salts = Md5Unit.saltGenerator();
            String passMd5 = Md5Unit.md5Digest(user.getPassword(), salts);
            user.setSalts(salts);
            user.setPassword(passMd5);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            user.setToken(JwtUtil.createTokenForObject(user));
            return userRepository.save(user);
        } else {
            throw new BusinessException(BusinessExceptionEnum.USER_NAME_EXISTS_ERROR);
        }
    }

    /**
     * 更新用户密码。
     *
     * @param req 包含用户名、旧密码和新密码的请求对象。
     * @return 更新后的用户对象。
     * @throws BusinessException 如果用户不存在、旧密码错误或密码解密过程出错。
     */
    public User updateUser(UserUpdateReq req) {
        String newPassword;
        User userDb = userRepository.findByUserName(req.getUserName());
        if (ObjUtil.isEmpty(userDb)) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_EXISTS);
        }
        try {
            String relPassword = new String(EncryptionUtil.decrypt(keyPair.get("privateKey"), req.getOldPassword()));
            if (!Md5Unit.md5Digest(relPassword, userDb.getSalts()).equals(userDb.getPassword())) {
                throw new BusinessException(BusinessExceptionEnum.USER_PASSWORD_ERROR);
            }
            newPassword = new String(EncryptionUtil.decrypt(keyPair.get("publicKey"), req.getNewPassword()));
        } catch (BusinessException e) {
            throw new BusinessException(BusinessExceptionEnum.USER_PASSWORD_ERROR);
        } catch (Exception e) {
            throw new BusinessException(BusinessExceptionEnum.USER_PASSWORD_DECRYPT_ERROR);
        }
        userDb.setPassword(Md5Unit.md5Digest(newPassword, userDb.getSalts()));
        userDb.setUpdateTime(DateTime.now());
        return userRepository.save(userDb);
    }

    /**
     * 用户登录功能实现。
     *
     * @param username 用户名。
     * @param password 密码（明文）。
     * @return 登录成功返回用户对象。
     * @throws BusinessException 当用户不存在、密码错误或解密过程出错时抛出业务异常。
     */
    public User login(String username, String password) {
        try {
            User byUserName = userRepository.findByUserName(username);
            if (ObjUtil.isEmpty(byUserName)) {
                throw new BusinessException(BusinessExceptionEnum.USER_NOT_EXISTS);
            } else {
                String relPassword = new String(EncryptionUtil.decrypt(keyPair.get("privateKey"), password));
                if (Md5Unit.md5Digest(relPassword, byUserName.getSalts()).equals(byUserName.getPassword())) {
                    return byUserName;
                } else {
                    throw new BusinessException(BusinessExceptionEnum.USER_PASSWORD_ERROR);
                }
            }
        } catch (BusinessException e) {
            throw new BusinessException(BusinessExceptionEnum.USER_PASSWORD_ERROR);
        } catch (Exception e) {
            throw new BusinessException(BusinessExceptionEnum.USER_PASSWORD_DECRYPT_ERROR);
        }
    }


}
