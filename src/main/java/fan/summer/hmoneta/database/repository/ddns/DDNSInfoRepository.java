package fan.summer.hmoneta.database.repository.ddns;

import fan.summer.hmoneta.database.entity.ddns.DDNSInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DDNSInfoRepository extends JpaRepository<DDNSInfo,Long> {

}
