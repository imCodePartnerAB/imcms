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
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.imcode.imcms.mapping.jpa",
        "com.imcode.imcms.imagearchive.entity"
})
public class DBConfig {

    private final Properties imcmsProperties;

    @Autowired
    public DBConfig(Properties imcmsProperties) {
        this.imcmsProperties = imcmsProperties;
    }

    @Bean
    public BasicDataSource dataSource() {
        final BasicDataSource basicDataSource = new BasicDataSource();
        setImcmsDataSourceProperties(basicDataSource);
        return basicDataSource;
    }

    //    Wasn't in previous config
    @Bean
    public BasicDataSource dataSourceWithAutoCommit() {
        final BasicDataSource basicDataSource = new BasicDataSource();

        setImcmsDataSourceProperties(basicDataSource);
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
        entityManagerFactory.setJpaPropertyMap(createHibernateJpaProperties());
        return entityManagerFactory;
    }

    private Map<String, String> createHibernateJpaProperties() {
        final HashMap<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.use_sql_comments", "true");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.hbm2ddl.auto", imcmsProperties.getProperty("hbm2ddl.auto"));

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

    private void setImcmsDataSourceProperties(BasicDataSource basicDataSource) {
        basicDataSource.setDriverClassName(imcmsProperties.getProperty("JdbcDriver"));
        basicDataSource.setUrl(imcmsProperties.getProperty("JdbcUrl"));
        basicDataSource.setUsername(imcmsProperties.getProperty("User"));
        basicDataSource.setPassword(imcmsProperties.getProperty("Password"));
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setValidationQuery("select 1");
        basicDataSource.setDefaultAutoCommit(false);
        basicDataSource.setMaxTotal(20);
        basicDataSource.setMaxTotal(Integer.parseInt(imcmsProperties.getProperty("MaxConnectionCount")));
    }
}
