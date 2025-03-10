package fan.summer.hmoneta.database.entity.acme;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Acme申请过程信息
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/2/25
 */
@Entity
@Data
@Table(name = "acme_challenge_Info")
public class AcmeChallengeInfoEntity {
    @Id
    private Long taskId;
    private String domain;
    private String domainKeyPair;
    private String certKeyPair;
    private String statusInfo;
}
