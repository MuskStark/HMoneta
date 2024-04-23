package fan.summer.hmoneta.database.repository;


import fan.summer.hmoneta.database.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Find
    User findByUserName(String userName);

}
