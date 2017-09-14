package com.imcode.imcms.test;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
@Import(EnvironmentConfig.class)
public class TestConfig {

    @Inject
    private Environment env;

    @Scope("prototype")
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();

        ds.setDriverClassName(env.getRequiredProperty("JdbcDriver"));
        ds.setUsername(env.getRequiredProperty("User"));
        ds.setPassword(env.getRequiredProperty("Password"));
        ds.setTestOnBorrow(true);
        ds.setValidationQuery("select 1");
        ds.setMaxTotal(1);

        return ds;
    }
}
