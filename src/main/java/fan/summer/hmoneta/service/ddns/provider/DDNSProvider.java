package fan.summer.hmoneta.service.ddns.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 所有DDNS服务商的父类，均需重新实现modifyDdns
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/30
 */
public abstract class DDNSProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 执行DDNS操作，用于动态域名解析
     * 该方法通过检查当前域名解析情况，并根据新的IP地址进行解析更新
     * 采用最终类修饰，确保该方法不会被子类覆盖
     *
     * @param domain    域名，例如example.com
     * @param subDomain 子域名，例如www，代表www.example.com
     * @param ip        新的IP地址，用于更新域名解析
     */
    public final boolean DDNSOperation(String domain, String subDomain, String ip) {
        logStart();
        Map<String, Object> result = dnsCheck(domain, subDomain);
        boolean status = modifyDdns(result, domain, subDomain, ip);
        logEnd();
        return status;

    }

    protected abstract Map<String, Object> dnsCheck(String domain, String subDomain);

    protected abstract boolean modifyDdns(Map<String, Object> dnsCheckResult, String domain, String subDomain, String ip);

    // 日志记录方法
    private void logStart() {
        logger.info("Starting operation for {}", this.getClass().getSimpleName());
    }

    private void logEnd() {
        logger.info("Finished operation for {}", this.getClass().getSimpleName());
    }

    // 用于记录关键信息的方法
    protected void logInfo(String message) {
        logger.info("[{}] {}", this.getClass().getSimpleName(), message);
    }

    // 用于记录警告信息的方法
    protected void logWarning(String message) {
        logger.warn("[{}] {}", this.getClass().getSimpleName(), message);
    }

    // 用于记录错误信息的方法
    protected void logError(String message, Throwable throwable) {
        logger.error("[{}] {}", this.getClass().getSimpleName(), message, throwable);
    }

    protected abstract boolean deleteDdns(String domain, String subDomain);
}
