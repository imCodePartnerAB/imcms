package com.imcode.imcms.test;

import com.imcode.imcms.config.WebConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Configuration
@Import(WebConfig.class)
public class WebTestConfig {

    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }

    @Bean
    public MockMvc mockMvc(WebApplicationContext wac) {
        return webAppContextSetup(wac).build();
    }

}
