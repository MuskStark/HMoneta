package fan.summer.hmoneta.service.acme;

import cn.hutool.core.util.ObjectUtil;
import fan.summer.hmoneta.database.entity.acme.AcmeChallengeInfoEntity;
import fan.summer.hmoneta.database.entity.ddns.DDNSRecorderEntity;
import fan.summer.hmoneta.service.ddns.DDNSService;
import fan.summer.hmoneta.util.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 提供ACME相关服务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/2/6
 */
@Service
@Slf4j
public class AcmeService {

    private final DDNSService ddnsService;
    private final AcmeAsyncService acmeAsyncService;


    @Autowired
    public AcmeService(DDNSService ddnsService, AcmeAsyncService acmeAsyncService) {
        this.ddnsService = ddnsService;
        this.acmeAsyncService = acmeAsyncService;
    }

    public Long applyCertificate(String domain, String providerName) {
        Long taskId = SnowFlakeUtil.getSnowFlakeNextId();
        AcmeChallengeInfoEntity info = new AcmeChallengeInfoEntity();
        info.setTaskId(taskId);
        info.setDomain(domain);
        if (ObjectUtil.isEmpty(providerName)) {
            // 通过DDNS记录查询Provider
            String subDomain = domain.substring(0, domain.indexOf('.'));
            String mainDomain = domain.substring(domain.indexOf('.') + 1);
            DDNSRecorderEntity ddnsRecorderEntity = ddnsService.queryRecordBySubDomainAndDomain(subDomain, mainDomain);
            if (ObjectUtil.isEmpty(ddnsRecorderEntity)) {
                throw new RuntimeException("未正确提供供应商无法申请证书");
            }
            providerName = ddnsRecorderEntity.getProviderName();
        }
        acmeAsyncService.useDnsChallengeGetCertification(domain, providerName, info);
        return taskId;
    }


}
