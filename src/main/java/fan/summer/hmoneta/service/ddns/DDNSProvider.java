package fan.summer.hmoneta.service.ddns;

/**
 * 所有DDNS服务商的父类，均需重新实现modifyDdns
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/30
 */
public class DDNSProvider {
    public boolean modifyDdns(){
        return true;
    }

}
