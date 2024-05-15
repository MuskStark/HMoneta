package fan.summer.hmoneta.database.repository;

import fan.summer.hmoneta.database.entity.agent.AgentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentReportRepository extends JpaRepository<AgentReport,Long> {

    AgentReport findByAgentId(Long agentId);
}
