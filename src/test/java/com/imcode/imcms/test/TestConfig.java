package com.imcode.imcms.test;

import com.imcode.imcms.api.linker.LinkService;
import com.imcode.imcms.api.linker.StringLink;
import com.imcode.imcms.config.ApplicationConfig;
import com.imcode.imcms.config.DBConfig;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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
        "com.imcode.imcms.api.linker",
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

    @Autowired
    @Bean
    public LinkService linkService(ServletContext servletContext) {
        return new LinkService(servletContext) {
            @Override
            public void initializeLinksMap(String realPathToJSON) throws IOException {
                // do nothing!!!1
            }

            @Override
            public String get(String... args) {
                return "";
            }

            @Override
            public String forward(String... args) {
                return "forward:";
            }

            @Override
            public String redirect(String... args) {
                return "redirect:";
            }

            @Override
            public List<StringLink> getJSON() {
                return Collections.emptyList();
            }
        };
    }
}
