package fan.summer.hmoneta.service.ddns.provider;

/**
 * 腾讯云DDNS实现
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/30
 */
public class Tencent extends DDNSProvider {

    private String accessKeyId;
    private String accessKeySecret;

    public Tencent(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public Tencent(){}

    @Override
    public boolean modifyDdns() {
        return super.modifyDdns();
    }
}
