package fan.summer.hmoneta.service.ddns.provider;

/**
 * 所有DDNS服务商的父类，均需重新实现modifyDdns
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/30
 */
public abstract class DDNSProvider {

    public abstract boolean modifyDdns(String domain, String subDomain, String ip);

}
