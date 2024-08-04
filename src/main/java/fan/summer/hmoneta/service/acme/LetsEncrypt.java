package fan.summer.hmoneta.service.acme;

import fan.summer.hmoneta.service.ddns.DDNSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/8/3
 */
@Service
public class LetsEncrypt {

    private DDNSService ddnsService;

    @Autowired
    public  LetsEncrypt(DDNSService ddnsService){
        this.ddnsService = ddnsService;
    }
    public void SSLCertificateFetcher(){

    }
}
