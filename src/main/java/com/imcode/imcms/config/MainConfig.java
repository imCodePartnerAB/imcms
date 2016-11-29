package com.imcode.imcms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by zemluk on 13.10.16.
 */
@Configuration
@Import({DBConfig.class, WebConfig.class, ApplicationConfig.class})
//@Import({ WebConfig.class})

//@Import({WebConfig.class, AppConfig.class})
@ComponentScan({"com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.api.linker", "imcode.util", "com.imcode.imcms.servlet.apis", "com.imcode.imcms.config"})
//@ComponentScan(basePackages = {"com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.api.linker", "imcode.util", "com.imcode.imcms.servlet.apis"},excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "com.imcode.imcms.mapping.jpa.*") )
//@ComponentScan(basePackages = {"com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.api.linker", "imcode.util", "com.imcode.imcms.servlet.apis"},excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "com.imcode.imcms.mapping.jpa") )
//@PropertySource("/WEB-INF/conf/server.properties" )
@PropertySources({@PropertySource(value = "/WEB-INF/conf/server.properties", ignoreResourceNotFound = true)})
//@Import({ AppConfig.class, DBConfig.class, WebConfig.class})
//@Import({ WebConfig.class, ApplicationConfig.class})
public class MainConfig {

    @Autowired
    private Environment env;

    //    Required to be able to access properties file from environment at other configs
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
