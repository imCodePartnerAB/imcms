package com.imcode.imcms.test;

import com.imcode.imcms.config.ApplicationConfig;
import com.imcode.imcms.config.DBConfig;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.Properties;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Configuration
@PropertySource(value = "classpath:server.properties", name = "imcms.properties")
@Import({
        DBConfig.class,
        ApplicationConfig.class
})
@ComponentScan({
        "com.imcode.imcms.mapping",
        "com.imcode.imcms.imagearchive",
        "imcode.util",
        "com.imcode.imcms.service",
        "com.imcode.imcms.document.text"
})
public class TestConfig {

    private final AbstractEnvironment env;

    @Autowired
    public TestConfig(AbstractEnvironment env) {
        this.env = env;
    }

    //    Required to be able to access properties file from environment at other configs
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

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

    @Bean
    public Properties imcmsProperties() {
        return (Properties) env.getPropertySources().get("imcms.properties").getSource();
    }

    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }

    @Autowired
    @Bean
    public MockMvc mockMvc(WebApplicationContext wac) {
        return webAppContextSetup(wac).build();
    }
}
