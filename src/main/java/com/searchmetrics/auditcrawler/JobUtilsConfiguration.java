package com.searchmetrics.auditcrawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.searchmetrics.auditcrawler.api.rest.JobUtilsResource;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by arobinson on 3/20/17.
 */
@org.springframework.context.annotation.Configuration
@ComponentScan("com.searchmetrics.auditcrawler.dao")
//@PropertySource(name = "appProps", value = "classpath:application.properties")
public class JobUtilsConfiguration extends Configuration {
    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Bean
    public JobUtilsResource jobUtilsResource() {
        return new JobUtilsResource();
    }
}
