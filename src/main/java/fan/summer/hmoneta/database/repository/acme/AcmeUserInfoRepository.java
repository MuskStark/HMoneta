package fan.summer.hmoneta.database.repository.acme;

import fan.summer.hmoneta.database.entity.acme.AcmeUserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcmeUserInfoRepository extends JpaRepository<AcmeUserInfoEntity, Long> {
    List<AcmeUserInfoEntity> findByUserEmail(String userEmail);
}
