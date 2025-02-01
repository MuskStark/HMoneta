package fan.summer.hmoneta.database.repository.ddns;

import fan.summer.hmoneta.database.entity.ddns.DDNSUpdateRecorderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DDNSUpdateRecorderRepository extends JpaRepository<DDNSUpdateRecorderEntity, Long> {
    DDNSUpdateRecorderEntity findByDomainAndSubDomain(String domain, String subDomain);

    List<DDNSUpdateRecorderEntity> findAllByProviderName(String providerName);

    Optional<DDNSUpdateRecorderEntity> findByRecorderId(Long recorderId);

    void deleteByRecorderId(Long recorderId);
}
