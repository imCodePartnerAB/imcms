package com.imcode.imcms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.StandardEnvironment;

import java.util.Properties;

@Configuration
@PropertySource(value = {
        "/WEB-INF/conf/server.properties",
        "classpath:test.server.properties"},
        name = "imcms.properties", ignoreResourceNotFound = true)
@Import({
        DBConfig.class,
        ApplicationConfig.class,
        MappingConfig.class
})
@ComponentScan({
        "com.imcode.imcms.domain",
        "imcode.util"
})
public class MainConfig {

    private final StandardEnvironment env;

    @Autowired
    public MainConfig(StandardEnvironment env) {
        this.env = env;
    }

    //    Required to be able to access properties file from environment at other configs
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Properties imcmsProperties() {
        return (Properties) env.getPropertySources().get("imcms.properties").getSource();
    }
}
