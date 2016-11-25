package com.imcode.imcms.test;

import com.imcode.imcms.util.Value;
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
public class DataSourceConfig {

    @Inject
    private Environment env;

    @Bean
    public DataSource dataSource() {
        return Value.with(dataSourcePrototype(), ds -> ds.setUrl(env.getRequiredProperty("JdbcUrl")));
    }

    @Bean
    public DataSource dataSourceWithoutDbName() {
        return Value.with(dataSourcePrototype(), ds -> ds.setUrl(env.getRequiredProperty("JdbcUrlWithoutDBName")));
    }

    @Bean
    public String databaseName() {
        return env.getRequiredProperty("DBName");
    }

    @Scope("prototype")
    @Bean(destroyMethod = "close")
    public BasicDataSource dataSourcePrototype() {
        return Value.with(new BasicDataSource(), ds -> {
            ds.setDriverClassName(env.getRequiredProperty("JdbcDriver"));
            ds.setUsername(env.getRequiredProperty("User"));
            ds.setPassword(env.getRequiredProperty("Password"));
            ds.setTestOnBorrow(true);
            ds.setValidationQuery("select 1");
            ds.setMaxTotal(1);
        });
    }
}
