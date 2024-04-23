package fan.summer.hmoneta.database.entity.user;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(name = "user_name", length = 100, nullable = false)
    private String userName;
    @Column(name = "password", length = 5000, nullable = false)
    private String password;
    @Column(name = "salts", nullable = false)
    private Integer salts;
    @Column(name = "token", length = 5000, nullable = false)
    private String token;
    @Column(name = "email", length = 5000, nullable = false)
    private String email;
    @Column(name = "create_time", nullable = false)
    private Date createTime;
    @Column(name = "update_time", nullable = false)
    private Date updateTime;




}
