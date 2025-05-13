package fan.summer.hmoneta.database.repository.acme;


import fan.summer.hmoneta.database.entity.acme.AcmeChallengeInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcmeChallengeInfoRepository extends JpaRepository<AcmeChallengeInfoEntity, Long> {

    AcmeChallengeInfoEntity findByUserIdAndTaskId(Long userId, Long taskId);

    List<AcmeChallengeInfoEntity> findByDomain(String domain);

    void deleteByDomain(String domain);

    List<AcmeChallengeInfoEntity> findAllByUserId(Long userId);
}
