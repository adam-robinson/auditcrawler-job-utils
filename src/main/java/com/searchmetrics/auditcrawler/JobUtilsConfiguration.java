package com.searchmetrics.auditcrawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.searchmetrics.auditcrawler.dao.CrawlerJobAggregationDAO;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by arobinson on 3/20/17.
 */
@org.springframework.context.annotation.Configuration
@EnableTransactionManagement
@ComponentScan("com.searchmetrics.auditcrawler.dao")
@EnableJpaRepositories(basePackages = "com.searchmetrics.auditcrawler.dao")
@PropertySource(value = "classpath:application.properties")
public class JobUtilsConfiguration extends Configuration {
    @Autowired
    Environment env;

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Bean
    public CrawlerJobAggregationDAO crawlerJobAggregationDAO() throws SQLException {
        return new CrawlerJobAggregationDAO(dataSource().getConnection());
    }

    @Bean
    public DataSource dataSource() {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(env.getProperty("spring.datasource.url"));
        mysqlDataSource.setUser(env.getProperty("spring.datasource.username"));
        mysqlDataSource.setPassword(env.getProperty("spring.datasource.password"));

        return mysqlDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(
                new String[] { "com.searchmetrics.auditcrawler.dto" });

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabase(Database.MYSQL);
        em.setJpaVendorAdapter(vendorAdapter);
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.connection.driver_class",
                env.getProperty("spring.datasource.driver-class-name"));
        properties.put("hibernate.cache.use_second_level_cache", false);
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect",
                env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.put("hibernate.implicit_naming_strategy",
                env.getProperty("spring.jpa.hibernate.naming-strategy"));
        properties.put("hibernate.connection.max_allowed_packet",
                env.getProperty("spring.jpa.hibernate.max_allowed_packet"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
