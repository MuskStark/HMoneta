package fan.summer.hmoneta.database.repository;


import fan.summer.hmoneta.database.entity.serverInfo.ServerInfoDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServerInfoDetailRepository extends JpaRepository<ServerInfoDetail, Long> {
    //Find
    List<ServerInfoDetail> findAllByPoolId(Long poolId);
    ServerInfoDetail findByServerName(String serverName);

    //Delete
    void deleteByServerName(String serverName);

    void deleteAllByPoolId(Long poolId);
}
