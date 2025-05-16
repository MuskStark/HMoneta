package fan.summer.hmoneta.service.acme;

import cn.hutool.core.util.ObjectUtil;
import fan.summer.hmoneta.common.context.LoginUserContext;
import fan.summer.hmoneta.database.entity.acme.AcmeChallengeInfoEntity;
import fan.summer.hmoneta.database.entity.ddns.DDNSRecorderEntity;
import fan.summer.hmoneta.database.repository.acme.AcmeChallengeInfoRepository;
import fan.summer.hmoneta.service.ddns.DDNSService;
import fan.summer.hmoneta.util.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
    private final AcmeChallengeInfoRepository acmeChallengeInfoRepository;


    @Autowired
    public AcmeService(DDNSService ddnsService, AcmeAsyncService acmeAsyncService, AcmeChallengeInfoRepository repository) {
        this.ddnsService = ddnsService;
        this.acmeAsyncService = acmeAsyncService;
        this.acmeChallengeInfoRepository = repository;
    }

    public AcmeChallengeInfoEntity queryCertApplyStatus(Long taskId) {
        AcmeChallengeInfoEntity byUserIdAndTaskId = acmeChallengeInfoRepository.findByUserIdAndTaskId(LoginUserContext.getId(), taskId);
        if (ObjectUtil.isNotEmpty(byUserIdAndTaskId)) {
            return byUserIdAndTaskId;
        } else {
            return null;
        }
    }

    public List<AcmeChallengeInfoEntity> queryCertApplyByUserId() {
        return acmeChallengeInfoRepository.findAllByUserId(LoginUserContext.getId());
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

    public List<String> getLogList(Long taskId) {
        if (ObjectUtil.isEmpty(taskId)) {
            return acmeAsyncService.getLogList();
        } else {
            return acmeAsyncService.getLogList(taskId);
        }
    }

    public byte[] getCert(String domain) throws IOException {
        return acmeAsyncService.packCertifications(domain);
    }


}
