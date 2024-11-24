package fan.summer.hmoneta.database.repository.ddns;

import fan.summer.hmoneta.database.entity.ddns.DDNSUpdateRecorderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DDNSUpdateRecorderRepository extends JpaRepository<DDNSUpdateRecorderEntity, Long> {
    DDNSUpdateRecorderEntity findByDomainAndSubDomain(String domain, String subDomain);

}
