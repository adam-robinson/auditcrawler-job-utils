package com.searchmetrics.auditcrawler;

import com.searchmetrics.auditcrawler.api.rest.JobUtilsResource;
import io.dropwizard.Application;
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
        final JobUtilsResource jobUtilsResource =
                applicationContext.getBean(JobUtilsResource.class);

        environment.jersey().register(jobUtilsResource);

    }
}
