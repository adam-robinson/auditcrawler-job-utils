package com.searchmetrics.auditcrawler.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;

/**
 *
 */
@Entity
@Table( name = "jobs")
public class CrawlerJob {
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
    private final Long crawlId;
    @Column
    private final String url;
    @Column(name = "max_pages")
    private final Integer maxPages;
    @Column(name = "type")
    private final String crawlType;
    @Column(name = "keywjson")
    private final Optional<KeywordJSON> keywordJSON;
    @Column(name = "deepjson")
    private final Optional<SSOAuditJSON> ssoAuditJSON;
    @Column(name = "crawler")
    private final Integer crawlerNode;
    @Column(name = "crawler_pid")
    private final Integer crawlerPid;
    @Column
    private final DateTime createDate;
    @Column(name = "callback")
    private final URL callbackURL;
    @Column(name = "sm_url_id")
    private final Optional<Boolean> useSMUrlIds;


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
    private Optional<DateTime> lastCrawl = Optional.empty();
    @Column
    private Optional<Boolean> jobStopSent = Optional.empty();
    @Column
    private Optional<Boolean> jobDone = Optional.empty();

    @JsonCreator
    public CrawlerJob(
        @JsonProperty("id") final Long crawlId,
        @JsonProperty("url") final String url,
        @JsonProperty("prio") final Integer priority,
        @JsonProperty("max_pages") final Integer maxPages,
        @JsonProperty("type") final String crawlType,
        @JsonProperty("real_pages") final Integer realPages,
        @JsonProperty("keywjson") final Optional<KeywordJSON> keywordJSON,
        @JsonProperty("deepjson") final Optional<SSOAuditJSON> ssoAuditJSON,
        @JsonProperty("err_count") final Integer errorCount,
        @JsonProperty("status") final String status,
        @JsonProperty("crawler") final Integer crawlerNode,
        @JsonProperty("crawler_pid") final Integer crawlerPid,
        @JsonProperty("createDate") final DateTime createDate,
        @JsonProperty("lastCrawl") final String lastCrawl,
        @JsonProperty("callback") final URL callbackURL,
        @JsonProperty("sm_url_id") final Integer smUrlId) {

        this.crawlId = checkNotNull(crawlId);
        this.url = checkNotNull(url);
        this.maxPages = null != MaxPages.getMaxPagesFor(maxPages) ? maxPages : 0;
        this.crawlType = checkNotNull(isValidEnum(Type.class, crawlType) ? crawlType : Type.AUDIT.name());
        this.keywordJSON = keywordJSON.isPresent() ? keywordJSON : Optional.empty();
        this.ssoAuditJSON = ssoAuditJSON.isPresent() ? ssoAuditJSON : Optional.empty();
        this.crawlerNode = checkNotNull(crawlerNode);
        this.crawlerPid = checkNotNull(crawlerPid);
        this.createDate = checkNotNull(createDate);
        this.callbackURL = checkNotNull(callbackURL);

        this.priority = null != priority && null != Priority.getPriority(priority) ? priority : 0;
        this.useSMUrlIds = Optional.of(null != smUrlId ? true : crawlType.equals(Type.URL_LIST) ? false : true);
    }


    @JsonProperty("id")
    public Long getCrawlId() {
        return crawlId;
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

    @JsonProperty("keywjson")
    public Optional<KeywordJSON> getKeywordJSON() {
        return keywordJSON;
    }

    @JsonProperty("deepjson")
    public Optional<SSOAuditJSON> getSsoAuditJSON() {
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

    @JsonProperty("keywjson")
    public DateTime getCreateDate() {
        return createDate;
    }

    @JsonProperty("callback")
    public URL getCallbackURL() {
        return callbackURL;
    }

    @JsonProperty("sm_url_id")
    public Optional<Boolean> getUseSMUrlIds() {
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
    public Optional<DateTime> getLastCrawl() {
        return lastCrawl;
    }

    @JsonProperty("jobStopSent")
    public Optional<Boolean> getJobStopSent() {
        return jobStopSent;
    }

    @JsonProperty("jobDone")
    public Optional<Boolean> getJobDone() {
        return jobDone;
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
    public void setLastCrawl(Optional<DateTime> lastCrawl) {
        this.lastCrawl = lastCrawl;
    }

    @JsonProperty("jobStopSent")
    public void setJobStopSent(Optional<Boolean> jobStopSent) {
        this.jobStopSent = jobStopSent;
    }

    @JsonProperty("jobDone")
    public void setJobDone(Optional<Boolean> jobDone) {
        this.jobDone = jobDone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (! (o instanceof CrawlerJob)) return false;

        CrawlerJob that = (CrawlerJob) o;

        if (! crawlId.equals(that.crawlId)) return false;
        if (! url.equals(that.url)) return false;
        if (priority != that.priority) return false;
        if (maxPages != that.maxPages) return false;
        if (crawlType != that.crawlType) return false;
        if (keywordJSON != null ? ! keywordJSON.equals(that.keywordJSON) : that.keywordJSON != null) return false;
        if (ssoAuditJSON != null ? ! ssoAuditJSON.equals(that.ssoAuditJSON) : that.ssoAuditJSON != null) return false;
        if (! crawlerNode.equals(that.crawlerNode)) return false;
        if (! crawlerPid.equals(that.crawlerPid)) return false;
        if (! createDate.equals(that.createDate)) return false;
        if (! callbackURL.equals(that.callbackURL)) return false;
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
        int result = crawlId.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + priority.hashCode();
        result = 31 * result + maxPages.hashCode();
        result = 31 * result + crawlType.hashCode();
        result = 31 * result + (keywordJSON != null ? keywordJSON.hashCode() : 0);
        result = 31 * result + (ssoAuditJSON != null ? ssoAuditJSON.hashCode() : 0);
        result = 31 * result + crawlerNode.hashCode();
        result = 31 * result + crawlerPid.hashCode();
        result = 31 * result + createDate.hashCode();
        result = 31 * result + callbackURL.hashCode();
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
