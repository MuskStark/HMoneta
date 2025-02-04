package fan.summer.hmoneta.service.ddns.provider;

import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.ddns.DDNSProviderEntity;
import fan.summer.hmoneta.service.ddns.provider.providers.Tencent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取指定DDNS供应商实例
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/1/25
 */
@Component
public class ProviderFactory {

    private final ProviderService providerService;

    @Autowired
    public ProviderFactory(ProviderService service) {
        this.providerService = service;
    }

    /**
     * Generates a DDNS provider instance based on the selected enumeration.
     *
     * @param providersEnum The enumeration representing the chosen DDNS provider. Must not be null.
     *                      Currently, only {@link DDNSProvidersSelectEnum#TencentCloud} is supported.
     * @return An instance of a DDNS provider subclass corresponding to the provided selection.
     * For {@link DDNSProvidersSelectEnum#TencentCloud}, an instance of {@link Tencent} is returned.
     * @throws BusinessException If the specified DDNS provider is not supported, with the error code
     *                           {@link BusinessExceptionEnum#DDNS_PROVIDER_NOT_SUPPORT}.
     */
    public DDNSProvider generatorProvider(DDNSProvidersSelectEnum providersEnum) {
        if (providersEnum == DDNSProvidersSelectEnum.TencentCloud) {
            DDNSProviderEntity providerBaseInfo = providerService.getProviderBaseInfo(providersEnum.getName());
            return new Tencent(providerBaseInfo.getAccessKeyId(), providerBaseInfo.getAccessKeySecret());
        } else throw new BusinessException(BusinessExceptionEnum.DDNS_PROVIDER_NOT_SUPPORT);
    }
}
