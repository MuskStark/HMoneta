package fan.summer.hmoneta.database.repository;



import fan.summer.hmoneta.database.entity.ipPool.IpPoolUsedDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpPoolUsedDetailRepository extends JpaRepository<IpPoolUsedDetail, Long> {

    //Find
    List<IpPoolUsedDetail> findByPoolId(Long poolId);

    @Query(value = """
            select p.issuedIp from IpPoolUsedDetail p where  p.poolId= ?1""")
    List<String> findIssuedIpByPoolId(Long poolId);

    // Delete
    void deleteAllByPoolId(Long poolId);

    @Modifying
    @Query(value = """
            delete from IpPoolUsedDetail p where p.poolId= ?1 and p.issuedIp= ?2""")
    void deleteByAddrAndPoolId(String addr, Long poolId);

    //Other
    @Query(value = """ 
    select count(*) from IpPoolUsedDetail p where p.isUsed=true and p.poolId=?1
    """)
    int countUsedNumByPoolId(Long poolId);

}
