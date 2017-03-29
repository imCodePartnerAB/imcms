package com.imcode.imcms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * Created by zemluk on 13.10.16.
 */
@Configuration
@PropertySources({@PropertySource(value = "/WEB-INF/conf/server.properties", ignoreResourceNotFound = true)})
@Import({DBConfig.class, WebConfig.class, ApplicationConfig.class})
@ComponentScan({"com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.api.linker", "imcode.util", "com.imcode.imcms.servlet.apis"})
//@ComponentScan(basePackages = {"com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.api.linker", "imcode.util", "com.imcode.imcms.servlet.apis"},excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "com.imcode.imcms.mapping.jpa*") )
public class MainConfig {

    @Autowired
    private Environment env;

    //    Required to be able to access properties file from environment at other configs
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
