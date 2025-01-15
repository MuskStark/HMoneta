package fan.summer.hmoneta.database.repository.ddns;

import fan.summer.hmoneta.database.entity.ddns.DDNSRecorderEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DDNSRecorderRepository extends JpaRepository<DDNSRecorderEntity, Long> {
    DDNSRecorderEntity findBySubDomainAndDomain(String subDomain, String domain);

}
