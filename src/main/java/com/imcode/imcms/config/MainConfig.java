package com.imcode.imcms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.Properties;

@Configuration
//TODO: Check properties for  system-properties-mode="NEVER" option
@PropertySource(value = "/WEB-INF/conf/server.properties", name = "imcms.properties")
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
public class MainConfig {

    private final StandardServletEnvironment env;

    @Autowired
    public MainConfig(StandardServletEnvironment env) {
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
