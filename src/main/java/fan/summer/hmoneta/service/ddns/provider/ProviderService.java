package fan.summer.hmoneta.service.ddns.provider;

import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.ddns.DDNSProviderEntity;
import fan.summer.hmoneta.database.repository.ddns.DDNSProviderRepository;
import fan.summer.hmoneta.webEntity.resp.ddns.ProviderInfoResp;
import fan.summer.hmoneta.webEntity.resp.ddns.ProviderSelectorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * DDNS供应商相关服务
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

    /**
     * Modifies an existing DDNS provider entity in the system.
     * <p>
     * This method updates the DDNS provider information based on the provided {@link DDNSProviderEntity}.
     * If a provider with the same name already exists, it will update the existing record by setting its ddnsId.
     * Otherwise, it saves the new provider entity.
     *
     * @param ddnsProviderEntity The DDNS provider entity containing the updated information.
     *                           Must not be null.
     * @throws RuntimeException if the input DDNSProviderEntity is null.
     */
    public void modifyDdnsProvider(DDNSProviderEntity ddnsProviderEntity) {
        if (ddnsProviderEntity == null) throw new RuntimeException("DDNS供应商信息不能为空");
        DDNSProviderEntity byProviderName = providerRepository.findByProviderName(ddnsProviderEntity.getProviderName());
        if (byProviderName != null) ddnsProviderEntity.setDdnsId(byProviderName.getDdnsId());
        providerRepository.save(ddnsProviderEntity);
    }


    /**
     * Retrieves all DDNS provider information from the repository.
     *
     * @return A list of {@link ProviderInfoResp} containing details of each DDNS provider,
     * or null if no providers are found.
     */
    public List<ProviderInfoResp> queryAllDDNSProvider() {
        List<DDNSProviderEntity> allProviders = providerRepository.findAll();
        if (allProviders.isEmpty()) return null;
        else {
            List<ProviderInfoResp> providerInfos = new ArrayList<>();
            for (DDNSProviderEntity ddnsProviderEntity : allProviders) {
                ProviderInfoResp providerInfoResp = new ProviderInfoResp();
                providerInfoResp.setProviderName(ddnsProviderEntity.getProviderName());
                providerInfoResp.setAccessKeyId(ddnsProviderEntity.getAccessKeyId());
                providerInfos.add(providerInfoResp);
            }
            return providerInfos;
        }
    }

    /**
     * Retrieves a list of provider selector information.
     * <p>
     * This method populates a list of {@link ProviderSelectorInfo} objects using the available
     * {@link DDNSProvidersSelectEnum} enumeration values. Each entry consists of a 'name' and a 'label',
     * mirroring the properties from the enumeration.
     *
     * @return A list of {@link ProviderSelectorInfo} instances, each representing a selectable option
     * for DDNS providers with their respective names and labels.
     */
    public List<ProviderSelectorInfo> getProviderSelectorInfo() {
        List<ProviderSelectorInfo> selectors = new ArrayList<>();
        DDNSProvidersSelectEnum[] values = DDNSProvidersSelectEnum.values();
        for (DDNSProvidersSelectEnum value : values)
            selectors.add(new ProviderSelectorInfo(value.getName(), value.getLabel()));
        return selectors;
    }

    /**
     * Retrieves a DDNS provider entity by its name.
     *
     * @param providerName The name of the DDNS provider to retrieve. Must not be null or empty.
     * @return The {@link DDNSProviderEntity} associated with the given provider name.
     * @throws BusinessException if no provider entity is found for the specified name,
     *                           with the error code {@link BusinessExceptionEnum#DDNS_PROVIDER_LIST_EMPTY}.
     */
    public DDNSProviderEntity getProviderEntity(String providerName) throws BusinessException {
        DDNSProviderEntity byProviderName = providerRepository.findByProviderName(providerName);
        if (ObjUtil.isEmpty(byProviderName))
            throw new BusinessException(BusinessExceptionEnum.DDNS_PROVIDER_LIST_EMPTY);
        else return byProviderName;
    }
}
