package fan.summer.hmoneta.service.ddns.provider;

import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.database.entity.ddns.DDNSProviderEntity;
import fan.summer.hmoneta.service.ddns.provider.providers.Tencent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类的详细说明
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

    public DDNSProvider generatorProvider(String providerName) {

        if (DDNSProvidersSelectEnum.valueOf(providerName) == DDNSProvidersSelectEnum.TencentCloud) {
            DDNSProviderEntity providerEntity = providerService.getProviderEntity(providerName);
            return new Tencent(providerEntity.getAccessKeyId(), providerEntity.getAccessKeySecret());
        } else throw new RuntimeException("不支持的DDNS服务商");
    }
}
