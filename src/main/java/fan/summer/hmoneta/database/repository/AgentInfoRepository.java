package fan.summer.hmoneta.database.repository;

import fan.summer.hmoneta.database.entity.agent.AgentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface AgentInfoRepository extends JpaRepository<AgentInfo, Long> {

    @Query(value = """
    select i.serverId from AgentInfo i""")
    Set<Long> findAllServerId();

    AgentInfo findByServerId(Long serverId);

    AgentInfo findByAgentId(Long agentId);

}
