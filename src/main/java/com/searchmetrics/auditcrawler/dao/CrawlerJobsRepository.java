package com.searchmetrics.auditcrawler.dao;

import com.searchmetrics.auditcrawler.dto.CrawlerJob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Repository
public interface CrawlerJobsRepository extends CrudRepository<CrawlerJob, Long> {

    @Query(value = "SELECT * FROM jobs WHERE status='proc'",
            nativeQuery = true)
    public Iterable<CrawlerJob> getProcessingJobs();
}
