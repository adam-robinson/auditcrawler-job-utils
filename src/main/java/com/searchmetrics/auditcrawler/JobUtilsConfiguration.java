package com.searchmetrics.auditcrawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jcraft.jsch.JSch;
import com.searchmetrics.auditcrawler.api.rest.JobUtilsResource;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;


/**
 * Created by arobinson on 3/20/17.
 */
@org.springframework.context.annotation.Configuration
@ComponentScan("com.searchmetrics.auditcrawler.dao")
@EnableJpaRepositories("com.searchmetrics.auditcrawler.dao")
@PropertySource(value = "classpath:application.properties")
public class JobUtilsConfiguration extends Configuration {
    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Bean
    public JobUtilsResource jobUtilsResource() {
        return new JobUtilsResource();
    }

    @Bean
    public DataSource dataSource() {
        JSch jsch = new JSch();

        BasicDataSource basicDataSource = new BasicDataSource();
        return basicDataSource;
    }
}
