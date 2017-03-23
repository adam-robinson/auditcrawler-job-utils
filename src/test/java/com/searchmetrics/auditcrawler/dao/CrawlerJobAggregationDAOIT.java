package com.searchmetrics.auditcrawler.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchmetrics.auditcrawler.JobUtilsConfiguration;
import com.searchmetrics.auditcrawler.dto.CrawlerJobIT;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by arobinson on 3/23/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JobUtilsConfiguration.class)
@PropertySource(value = "classpath:application.properties")
public class CrawlerJobAggregationDAOIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerJobIT.class);

    @Autowired
    private Environment env;

    private CrawlerJobAggregationDAO dao;

    @Before
    public void setup() throws SQLException {
        dao = new CrawlerJobAggregationDAO(
                DriverManager.getConnection(
                        env.getProperty("spring.datasource.url"),
                        env.getProperty("spring.datasource.username"),
                        env.getProperty("spring.datasource.password")
                )
        );
    }

    @Test
    public void test_getProcessingJobsByDate() throws SQLException, JsonProcessingException {
        List<CrawlerJobAggregationDAO.ByDateJobRecord> list = dao.getProcessingJobsByDate();

        Assert.assertNotNull("Obtained non-null result", list);

        LOGGER.debug("Result: {}", new ObjectMapper().writeValueAsString(list));
    }
}
