package com.searchmetrics.auditcrawler.dao;

import com.searchmetrics.auditcrawler.dto.CrawlerJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface CrawlerJobsRepository extends CrudRepository<CrawlerJob, Long> {
    public CrawlerJob findById(Long crawlId);
}
