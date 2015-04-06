package com.imcode.imcms.mapping.jpa;

import com.google.common.base.Preconditions;
import com.imcode.imcms.util.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = com.imcode.imcms.mapping.jpa.JpaConfiguration.class)
@PropertySource({ "/WEB-INF/conf/server.properties" })
@ComponentScan(basePackageClasses = com.imcode.imcms.mapping.jpa.JpaConfiguration.class)
public class JpaConfiguration {


    @Autowired
    private Environment env;

    public JpaConfiguration() {
        super();
    }

    // beans

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
       // em.setPersistenceUnitName("com.imcode.imcms");
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.imcode.imcms.mapping", "com.imcode.imcms.addon.imagearchive", "com.imcode.imcms.mapping.jpa");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Preconditions.checkNotNull(env.getProperty("db-driver")));
        dataSource.setUrl(Preconditions.checkNotNull(env.getProperty("db-url")));
        dataSource.setUsername(Preconditions.checkNotNull(env.getProperty("db-user")));
        dataSource.setPassword(Preconditions.checkNotNull(env.getProperty("db-pass")));

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }


}
