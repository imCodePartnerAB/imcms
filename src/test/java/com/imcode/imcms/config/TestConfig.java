package com.imcode.imcms.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MainConfig.class})
@ComponentScan({
        "com.imcode.imcms.util.datainitializer"
})
public class TestConfig {
}
