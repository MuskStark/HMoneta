package fan.summer.hmoneta.database.repository;

import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpPoolRepository extends JpaRepository<IpPool, Long> {

    //Find
    IpPool findByPoolId(Long poolId);

    IpPool findByPoolName(String poolName);

    List<IpPool> findAllByNetworkAddress(String networkAddress);

    @Query(value = """
           select i.poolName from IpPool i""")
    List<String> findAllPoolName();
}
