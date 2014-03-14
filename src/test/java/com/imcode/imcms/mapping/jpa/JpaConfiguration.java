package com.imcode.imcms.mapping.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.imcode.imcms.mapping.jpa")
@ComponentScan(basePackages = {"com.imcode.imcms.mapping.jpa"})
public class JpaConfiguration {

    @Bean
    public JpaTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory() {
        return new LocalEntityManagerFactoryBean();
    }
}
