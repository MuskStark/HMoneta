package fan.summer.hmoneta.database.repository.ddns;

import fan.summer.hmoneta.database.entity.ddns.DDNSProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DDNSProviderRepository extends JpaRepository<DDNSProviderEntity, Long> {
    DDNSProviderEntity findByProviderName(String providerName);

}
