package com.searchmetrics.auditcrawler.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;

/**
 *
 */
@Entity
@Table( name = "jobs")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CrawlerJob implements Serializable {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CrawlerJob.class);

    public enum MaxPages {
        ZERO(0), FIFTY(50),
        ONE_HUNDRED(100), FIVE_HUNDRED(500),
        ONE_THOUSAND(1000), TWO_THOUSAND(2000), TWO_THOUSAND_FIVE_HUNDRED(2500), FIVE_THOUSAND(5000),
        TEN_THOUSAND(1000), TWENTY_THOUSAND(20000), FIFTY_THOUSAND(50000),
        ONE_HUNDRED_THOUSAND(100000), FIVE_HUNDRED_THOUSAND(500000),
        ONE_MILLION(1000000), TWO_MILLION(2000000);

        private static final Map<Integer, MaxPages> MAX_PAGES_MAP;

        static {
            MAX_PAGES_MAP = new HashMap<>();

            for (MaxPages mp : MaxPages.values()) {
                MAX_PAGES_MAP.put(mp.integerValue, mp);
            }
        }

        private final Integer integerValue;

        MaxPages(final Integer intValue) {
            this.integerValue = intValue;
        }

        public Integer getIntegerValue() {
            return integerValue;
        }

        public static MaxPages getMaxPagesFor(Integer integer) {
            final MaxPages maxPages = MAX_PAGES_MAP.getOrDefault(integer, null);
            checkNotNull(maxPages, "[{}] is not mapped to a valid MaxPages value!", integer);
            return maxPages;
        }

        public static Integer getIntegerValue(MaxPages maxPages) {
            return maxPages.integerValue;
        }

        public static String getName(MaxPages maxPages) {
            return maxPages.name();
        }
    };

    enum Priority {ZERO, SO, RECRAWL, SSO, HIGHEST;

        public static Priority getPriority(Integer priority) {
            if (priority > 10)
                return ZERO;
            else if (priority > 127)
                return SO;
            else if (priority > 150)
                return RECRAWL;
            else if (priority == 150)
                return SSO;
            else
                return HIGHEST;
        }

        public static Integer getIntegerValue(Priority priority) {
            switch (priority) {
                case SO:
                    return 10;
                case RECRAWL:
                    return  127;
                case SSO:
                    return 150;
                case HIGHEST:
                    return 151;
                default:
                    return 0;
            }
        }
    };

    enum Status {BLOCK, DATA, DONE, ERROR, NEW, PROCESSING, STOPPED, TIMEOUT};

    enum Type {AUDIT, KEYWORD, URL_LIST};

    @Id
    private Long id;
    @Column
    private String url;
    @Column(name = "max_pages")
    private Integer maxPages;
    @Column(name = "type")
    private String crawlType;
    @Column(name = "keywjson")
    private String keywordJSON;
    @Column(name = "deepjson")
    private String ssoAuditJSON;
    @Column(name = "crawler")
    private Integer crawlerNode;
    @Column(name = "crawler_pid")
    private Integer crawlerPid;
    @Column
    private LocalDateTime createDate;
    @Column(name = "callback")
    private String callback;
    @Column(name = "sm_url_id")
    private Boolean useSMUrlIds;


    @Column(name = "prio")
    private Integer priority;
    @Column(name = "real_pages")
    private Integer realPages = 0;
    @Column(name = "err_count")
    private Integer errorCount = 0;
    @Column(name = "status")
    private String crawlStatus = Status.NEW.name().toLowerCase();
    @Column
    private Integer retries = 0;
    @Column
    private LocalDateTime lastCrawl;
    @Column
    private LocalDateTime jobStopSent;
    @Column
    private LocalDateTime jobDone;

    public CrawlerJob(){}

    @JsonCreator
    public CrawlerJob(
        @JsonProperty("id") final Long id,
        @JsonProperty("url") final String url,
        @JsonProperty("prio") final Integer priority,
        @JsonProperty("max_pages") final Integer maxPages,
        @JsonProperty("type") final String crawlType,
        @JsonProperty("real_pages") final Integer realPages,
        @JsonProperty("keywjson") final String keywordJSON,
        @JsonProperty("deepjson") final String ssoAuditJSON,
        @JsonProperty("err_count") final Integer errorCount,
        @JsonProperty("status") final String status,
        @JsonProperty("crawler") final Integer crawlerNode,
        @JsonProperty("crawler_pid") final Integer crawlerPid,
        @JsonProperty("createDate") final LocalDateTime createDate,
        @JsonProperty("lastCrawl") final LocalDateTime lastCrawl,
        @JsonProperty("jobStopSent") final LocalDateTime jobStopSent,
        @JsonProperty("jobDone") final LocalDateTime jobDone,
        @JsonProperty("callback") final String callback,
        @JsonProperty("sm_url_id") final Integer smUrlId) throws ParseException {

        this.id = checkNotNull(id);
        this.url = checkNotNull(url);
        this.maxPages = null != MaxPages.getMaxPagesFor(maxPages) ? maxPages : 0;
        this.crawlType = checkNotNull(isValidEnum(Type.class, crawlType) ? crawlType : Type.AUDIT.name());
        this.keywordJSON = keywordJSON;
        this.ssoAuditJSON = ssoAuditJSON;
        this.crawlerNode = checkNotNull(crawlerNode);
        this.crawlerPid = checkNotNull(crawlerPid);
        this.createDate = createDate;
        this.lastCrawl = lastCrawl;
        this.jobStopSent = jobStopSent;
        this.jobDone = jobDone;
        this.callback = checkNotNull(callback);

        this.priority = null != priority && null != Priority.getPriority(priority) ? priority : 0;
        this.useSMUrlIds = null != smUrlId ? true : (crawlType.equals(Type.URL_LIST) ? false : true);
    }


    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("prio")
    public Integer getPriority() {
        return priority;
    }

    @JsonProperty("max_pages")
    public Integer getMaxPages() {
        return maxPages;
    }

    @JsonProperty("type")
    public String getCrawlType() {
        return crawlType;
    }

    @JsonIgnore
//    @JsonProperty("keywjson")
    public String getKeywordJSON() {
        return keywordJSON;
    }

    @JsonIgnore
//    @JsonProperty("deepjson")
    public String getSsoAuditJSON() {
        return ssoAuditJSON;
    }

    @JsonProperty("crawler")
    public Integer getCrawlerNode() {
        return crawlerNode;
    }

    @JsonProperty("crawler_pid")
    public Integer getCrawlerPid() {
        return crawlerPid;
    }

    @JsonProperty("createDate")
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    @JsonProperty("callback")
    public String getCallback() {
        return callback;
    }

    @JsonProperty("sm_url_id")
    public Boolean getUseSMUrlIds() {
        return useSMUrlIds;
    }

    @JsonProperty("real_pages")
    public Integer getRealPages() {
        return realPages;
    }

    @JsonProperty("err_count")
    public Integer getErrorCount() {
        return errorCount;
    }

    @JsonProperty("status")
    public String getCrawlStatus() {
        return crawlStatus;
    }

    @JsonProperty("retries")
    public Integer getRetries() {
        return retries;
    }

    @JsonProperty("lastCrawl")
    public LocalDateTime getLastCrawl() {
        return lastCrawl;
    }

    @JsonProperty("jobStopSent")
    public LocalDateTime getJobStopSent() {
        return jobStopSent;
    }

    @JsonProperty("jobDone")
    public LocalDateTime getJobDone() {
        return jobDone;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("max_pages")
    public void setMaxPages(Integer maxPages) {
        this.maxPages = maxPages;
    }

    @JsonProperty("type")
    public void setCrawlType(String crawlType) {
        this.crawlType = crawlType;
    }

    @JsonProperty("keywjson")
    public void setKeywordJSON(String keywordJSON) {
        this.keywordJSON = keywordJSON;
    }

    @JsonProperty("deepjson")
    public void setSsoAuditJSON(String ssoAuditJSON) {
        this.ssoAuditJSON = ssoAuditJSON;
    }

    @JsonProperty("crawler")
    public void setCrawlerNode(Integer crawlerNode) {
        this.crawlerNode = crawlerNode;
    }

    @JsonProperty("crawler_pid")
    public void setCrawlerPid(Integer crawlerPid) {
        this.crawlerPid = crawlerPid;
    }

    @JsonProperty("createDate")
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @JsonProperty("callback")
    public void setCallback(String callback) {
        this.callback = callback;
    }

    @JsonProperty("sm_url_id")
    public void setUseSMUrlIds(Boolean useSMUrlIds) {
        this.useSMUrlIds = useSMUrlIds;
    }

    @JsonProperty("status")
    public void setCrawlStatus(String crawlStatus) {
        this.crawlStatus = crawlStatus;
    }

    @JsonProperty("prio")
    public void setPriority(Integer priority) { this.priority = priority; }

    @JsonProperty("real_pages")
    public void setRealPages(Integer realPages) {
        this.realPages = realPages;
    }

    @JsonProperty("err_count")
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    @JsonProperty("status")
    public void setCrawlStatus(Status crawlStatus) {
        this.crawlStatus = crawlStatus.name();
    }

    @JsonProperty("retries")
    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    @JsonProperty("lastCrawl")
    public void setLastCrawl(LocalDateTime lastCrawl) {
        this.lastCrawl = lastCrawl;
    }

    @JsonProperty("jobStopSent")
    public void setJobStopSent(LocalDateTime jobStopSent) {
        this.jobStopSent = jobStopSent;
    }

    @JsonProperty("jobDone")
    public void setJobDone(LocalDateTime jobDone) {
        this.jobDone = jobDone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (! (o instanceof CrawlerJob)) return false;

        CrawlerJob that = (CrawlerJob) o;

        if (! id.equals(that.id)) return false;
        if (! url.equals(that.url)) return false;
        if (priority != that.priority) return false;
        if (maxPages != that.maxPages) return false;
        if (crawlType != that.crawlType) return false;
        if (keywordJSON != null ? ! keywordJSON.equals(that.keywordJSON) : that.keywordJSON != null) return false;
        if (ssoAuditJSON != null ? ! ssoAuditJSON.equals(that.ssoAuditJSON) : that.ssoAuditJSON != null) return false;
        if (! crawlerNode.equals(that.crawlerNode)) return false;
        if (! crawlerPid.equals(that.crawlerPid)) return false;
        if (! createDate.equals(that.createDate)) return false;
        if (! callback.equals(that.callback)) return false;
        if (! useSMUrlIds.equals(that.useSMUrlIds)) return false;
        if (! realPages.equals(that.realPages)) return false;
        if (! errorCount.equals(that.errorCount)) return false;
        if (crawlStatus != that.crawlStatus) return false;
        if (! retries.equals(that.retries)) return false;
        if (lastCrawl != null ? ! lastCrawl.equals(that.lastCrawl) : that.lastCrawl != null) return false;
        if (jobStopSent != null ? ! jobStopSent.equals(that.jobStopSent) : that.jobStopSent != null) return false;
        return jobDone != null ? jobDone.equals(that.jobDone) : that.jobDone == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + priority.hashCode();
        result = 31 * result + maxPages.hashCode();
        result = 31 * result + crawlType.hashCode();
        result = 31 * result + (keywordJSON != null ? keywordJSON.hashCode() : 0);
        result = 31 * result + (ssoAuditJSON != null ? ssoAuditJSON.hashCode() : 0);
        result = 31 * result + crawlerNode.hashCode();
        result = 31 * result + crawlerPid.hashCode();
        result = 31 * result + createDate.hashCode();
        result = 31 * result + callback.hashCode();
        result = 31 * result + useSMUrlIds.hashCode();
        result = 31 * result + realPages.hashCode();
        result = 31 * result + errorCount.hashCode();
        result = 31 * result + (crawlStatus != null ? crawlStatus.hashCode() : 0);
        result = 31 * result + retries.hashCode();
        result = 31 * result + (lastCrawl != null ? lastCrawl.hashCode() : 0);
        result = 31 * result + (jobStopSent != null ? jobStopSent.hashCode() : 0);
        result = 31 * result + (jobDone != null ? jobDone.hashCode() : 0);
        return result;
    }
}
