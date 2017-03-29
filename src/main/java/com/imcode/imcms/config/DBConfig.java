package com.imcode.imcms.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zemluk on 13.10.16.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.imcode.imcms.mapping.jpa", "com.imcode.imcms.imagearchive.entity"})

public class DBConfig {

    @Autowired
    private Environment env;

//    TODO just another way to obtain properties
/*@Value("${JdbcUrl}")
    private String jdbcUrl;*//*
*/

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(env.getProperty("JdbcDriver"));
        basicDataSource.setUrl(env.getProperty("JdbcUrl"));
        basicDataSource.setUsername(env.getProperty("User"));
        basicDataSource.setPassword(env.getProperty("Password"));
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setValidationQuery("select 1");
        basicDataSource.setDefaultAutoCommit(false);
        basicDataSource.setMaxTotal(20);
        basicDataSource.setMaxTotal(Integer.parseInt(env.getProperty("MaxConnectionCount")));
        return basicDataSource;
    }

    //    Wasn't in previous config
    @Bean
    public BasicDataSource dataSourceWithAutoCommit() {
        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setDriverClassName(env.getProperty("JdbcDriver"));
        basicDataSource.setUrl(env.getProperty("JdbcUrl"));
        basicDataSource.setUsername(env.getProperty("User"));
        basicDataSource.setPassword(env.getProperty("Password"));
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setValidationQuery("select 1");
        basicDataSource.setDefaultAutoCommit(false);
        basicDataSource.setMaxTotal(20);
        basicDataSource.setMaxTotal(Integer.parseInt(env.getProperty("MaxConnectionCount")));
        basicDataSource.setDefaultAutoCommit(true);

        return basicDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setJpaDialect(new HibernateJpaDialect());
        entityManagerFactory.setPackagesToScan("com.imcode.imcms.imagearchive", "com.imcode.imcms.mapping.jpa");
        entityManagerFactory.setPersistenceUnitName("com.imcode.imcms");
        entityManagerFactory.setJpaPropertyMap(hibernateJpaProperties());
        return entityManagerFactory;
    }

    private Map<String, ?> hibernateJpaProperties() {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.use_sql_comments", "true");
        properties.put("hibernate.show_sql", "false");

        //TODO: Some additional properties for hibernate
//        properties.put("hibernate.hbm2ddl.auto", "create");
//        properties.put("hibernate.show_sql", "false");
//        properties.put("hibernate.format_sql", "false");
//        properties.put("hibernate.hbm2ddl.import_files", "insert-data.sql");
//        properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

//        properties.put("hibernate.c3p0.min_size", "2");
//        properties.put("hibernate.c3p0.max_size", "5");
//        properties.put("hibernate.c3p0.timeout", "300"); // 5mins

        return properties;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        final JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf);
        return jpaTransactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
