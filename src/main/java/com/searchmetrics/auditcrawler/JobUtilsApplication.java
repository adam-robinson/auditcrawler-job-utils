package com.searchmetrics.auditcrawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.searchmetrics.auditcrawler.api.rest.JobUtilsResource;
import com.searchmetrics.auditcrawler.dao.CrawlerJobAggregationDAO;
import com.searchmetrics.auditcrawler.dao.CrawlerJobsRepository;
import io.dropwizard.Application;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by arobinson on 3/20/17.
 */
public class JobUtilsApplication extends Application<JobUtilsConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobUtilsApplication.class);

    public static void main(String[] args) throws Exception {
        new JobUtilsApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<JobUtilsConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<JobUtilsConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(JobUtilsConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(
            final JobUtilsConfiguration jobUtilsConfiguration,
            final Environment environment) throws Exception {

        final AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(
                        JobUtilsConfiguration.class
                );

        final CrawlerJobsRepository crawlerJobsRepository =
                applicationContext.getBean(CrawlerJobsRepository.class);
        final CrawlerJobAggregationDAO crawlerJobAggregationDAO =
                applicationContext.getBean(CrawlerJobAggregationDAO.class);
        final JobUtilsResource jobUtilsResource =
                new JobUtilsResource(crawlerJobAggregationDAO, crawlerJobsRepository);

        final ObjectMapper objectMapper = Jackson.newObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        environment.jersey().register(new JacksonMessageBodyProvider(objectMapper));
        environment.jersey().register(jobUtilsResource);

    }
}
