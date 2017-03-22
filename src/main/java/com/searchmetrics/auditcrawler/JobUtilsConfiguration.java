package com.searchmetrics.auditcrawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.searchmetrics.auditcrawler.api.rest.JobUtilsResource;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;


/**
 * Created by arobinson on 3/20/17.
 */
@org.springframework.context.annotation.Configuration
@ComponentScan("com.searchmetrics.auditcrawler.dao")
@EnableJpaRepositories(basePackages = "com.searchmetrics.auditcrawler.dao")
@PropertySource(value = "classpath:application.properties")
public class JobUtilsConfiguration extends Configuration {
    @Autowired
    Environment env;

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Bean
    public JobUtilsResource jobUtilsResource() {
        return new JobUtilsResource();
    }

    @Bean
    public DataSource dataSource() {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(env.getProperty("spring.datasource.url"));
        mysqlDataSource.setUser(env.getProperty("spring.datasource.username"));
        mysqlDataSource.setPassword(env.getProperty("spring.datasource.password"));

        return mysqlDataSource;
    }
}
