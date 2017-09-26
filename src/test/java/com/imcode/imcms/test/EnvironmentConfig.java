package com.imcode.imcms.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import javax.inject.Inject;

@Configuration
@PropertySource("classpath:server.properties")
public class EnvironmentConfig {

    @Inject
    public void setEnv(StandardEnvironment env) {
        env.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
    }
}
