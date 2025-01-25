package fan.summer.hmoneta.service.ddns.provider;

import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.ddns.DDNSProviderEntity;
import fan.summer.hmoneta.database.repository.ddns.DDNSProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/1/25
 */
@Service
public class ProviderService {

    private final DDNSProviderRepository providerRepository;

    @Autowired
    public ProviderService(DDNSProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public DDNSProviderEntity getProviderEntity(String providerName) throws BusinessException {
        DDNSProviderEntity byProviderName = providerRepository.findByProviderName(providerName);
        if (ObjUtil.isEmpty(byProviderName))
            throw new BusinessException(BusinessExceptionEnum.DDNS_PROVIDER_LIST_EMPTY);
        else return byProviderName;
    }
}
