package fan.summer.hmoneta.service.ddns.provider;

import fan.summer.hmoneta.service.ddns.provider.info.DNSRecordInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
     * 用于检查指定域名及子域名的DNS状态。
     *
     * @param domain    主域名字符串，例如："example.com"。
     * @param subDomain 子域名字符串，例如："www"，用于构成 "www.example.com"。
     * @return 返回一个映射集，包含关于DNS检查结果的键值对。
     * 具体的键和值依赖于实现，可能包括解析状态、TTL、记录类型等信息。
     */
    public abstract List<DNSRecordInfo> dnsCheck(String domain, String subDomain);


    /**
     * 修改动态DNS（DDNS）服务中的DNS记录，将指定的IP地址与给定的域名及子域名关联。
     *
     * @param domain    主域名，DDNS更改所适用的域名（例如："example.com"）。
     * @param subDomain 主域名下的子域名，需要更新的部分（例如："www" 用于 "www.example.com"）。
     * @param ip        为指定的域名和子域名组合设置的新IP地址。
     * @return 如果修改成功返回 {@code true}，否则返回 {@code false}。
     */
    public abstract boolean modifyDdns(String domain, String subDomain, String value, String recordType);

    /**
     * 删除指定域名和子域名的动态DNS（DDNS）记录。
     *
     * @param domain    主域名，即要删除DDNS记录的域名部分，例如："example.com"。
     * @param subDomain 子域名，指主域名下的特定子域名部分，例如："www" 对应于 "www.example.com"。
     */
    public abstract void deleteDdns(String domain, String subDomain, String recordType);

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

}
