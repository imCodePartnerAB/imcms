package com.imcode.imcms.config;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zemluk on 13.10.16.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.imcode.imcms.mapping.jpa")
@EnableTransactionManagement
public class DBConfig {

    @Autowired
    private Environment env;


    //    TODO just another way to obtain properties
    /*@Value("${JdbcUrl}")
    private String jdbcUrl;*/

//    TODO: Maybe type should be changed to DataSource instead of BasicDataSource
    @Bean(destroyMethod = "close")
    public BasicDataSource basicDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("JdbcDriver"));
        dataSource.setUrl(env.getProperty("JdbcUrl"));
        dataSource.setUsername(env.getProperty("User"));
        dataSource.setPassword(env.getProperty("Password"));
        dataSource.setTestOnBorrow(true);
        dataSource.setValidationQuery("select 1");
        dataSource.setDefaultAutoCommit(false);
        dataSource.setMaxTotal(20);
        dataSource.setMaxTotal(Integer.parseInt(env.getProperty("MaxConnectionCount")));
        return dataSource;
    }


    @Bean
    public DefaultPersistenceUnitManager defaultPersistenceUnitManager() {
        Map<String, DataSource> dataSources = new HashedMap();
        dataSources.put("localDataSource", basicDataSource());
        dataSources.put("remoteDataSource", basicDataSource());

        DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
        persistenceUnitManager.setPersistenceXmlLocations("classpath:META-INF/persistence.xml");
        persistenceUnitManager.setDataSources(dataSources);
        persistenceUnitManager.setDefaultDataSource(basicDataSource());
        return persistenceUnitManager;
    }


    @Bean
    public EntityManagerFactory entityManagerFactory() {

//TODO: is all hibernate config should be placed here?
//TODO: Find a way move that config to annotations
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.imcode.imcms.mapping.jpa");
        factory.setDataSource(basicDataSource());
        factory.setPersistenceUnitName("com.imcode.imcms");
        factory.setPersistenceUnitManager(defaultPersistenceUnitManager());
        factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        factory.setJpaProperties(props);
//TODO: Is that needed???
        factory.afterPropertiesSet();
        return factory.getObject();
    }


    //TODO: Possibly not required if @EnableTransactionManagement exists
    @Bean
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory());
        return jpaTransactionManager;
    }

}
