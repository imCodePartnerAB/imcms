package com.imcode.imcms.mapping.jpa;

import com.imcode.imcms.util.Cells;
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
@EnableJpaRepositories(basePackageClasses = com.imcode.imcms.mapping.jpa.JpaConfiguration.class)
@ComponentScan(basePackageClasses = com.imcode.imcms.mapping.jpa.JpaConfiguration.class)
public class JpaConfiguration {

    @Bean
    public JpaTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory() {
        return Cells.updateAndGet(new LocalEntityManagerFactoryBean(), b -> b.setPersistenceUnitName("com.imcode.imcms"));
    }
}
