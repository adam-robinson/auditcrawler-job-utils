package com.searchmetrics.auditcrawler.dto;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.searchmetrics.auditcrawler.JobUtilsConfiguration;
import com.searchmetrics.auditcrawler.dao.CrawlerJobsRepository;
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

/**
 * Created by arobinson on 3/21/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JobUtilsConfiguration.class)
@PropertySource(value = "classpath:application.properties")
public class CrawlerJobIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerJobIT.class);

    @Autowired
    private CrawlerJobsRepository crawlerJobsRepository;

    @Autowired
    private Environment env;

    @Before
    public void doStuff() throws JSchException {
        JSch jSch = new JSch();
        jSch.addIdentity(
            env.getProperty("privateKey.path"),
            env.getProperty("publicKey.path"),
            env.getProperty("key.passcode").getBytes()
        );
        Session session =
            jSch.getSession(env.getProperty("ssh.user"), env.getProperty("ssh.host"), Integer.valueOf(env.getProperty("ssh.port")));
        session.setConfig("StrictHostKeyChecking", "no");

        final int assignedPort =
            session.setPortForwardingL(
                Integer.valueOf(env.getProperty("forwarding.port")),
                env.getProperty("tunnel.host"),
                Integer.valueOf(env.getProperty("tunnel.port"))
            );
    }

    @Test
    public void testConnection() {
        CrawlerJob crawlerJob = crawlerJobsRepository.findById(453373446L);
        Assert.assertNotNull("Got a CrawlerJob object :)", crawlerJob);
    }

}
