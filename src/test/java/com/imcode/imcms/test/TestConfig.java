package com.imcode.imcms.test;

import com.imcode.imcms.config.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.StandardEnvironment;

import java.util.Properties;

@Configuration
@PropertySource(value = "classpath:server.properties", name = "test.properties")
@Import({DBConfig.class})
@ComponentScan({
        "com.imcode.imcms.mapping",
        "com.imcode.imcms.imagearchive",
        "imcode.util",
        "com.imcode.imcms.service",
        "com.imcode.imcms.document.text"
})
public class TestConfig {

    private final StandardEnvironment env;

    @Autowired
    public TestConfig(StandardEnvironment env) {
        this.env = env;
    }

    //    Required to be able to access properties file from environment at other configs
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Properties imcmsProperties() {
        return (Properties) env.getPropertySources().get("test.properties").getSource();
    }

}
