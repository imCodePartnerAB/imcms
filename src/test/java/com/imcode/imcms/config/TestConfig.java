package com.imcode.imcms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import imcode.server.Config;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Configuration
@Import({MainConfig.class, WebConfig.class})
@ComponentScan({
        "com.imcode.imcms.components.datainitializer"
})
@WebAppConfiguration
public class TestConfig {

    @Value("WEB-INF/test-solr")
    private File defaultTestSolrFolder;

    @Bean
    public MockMvc mockMvc(WebApplicationContext wac) {
        return webAppContextSetup(wac).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Config config(Config config, @Value("WEB-INF/solr") File defaultSolrFolder) {

        final String solrHome = defaultTestSolrFolder.getAbsolutePath();
        config.setSolrHome(solrHome);

        if (defaultTestSolrFolder.mkdirs()) {
            try {
                FileUtils.copyDirectory(defaultSolrFolder, defaultTestSolrFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return config;
    }

    @PreDestroy
    private void destroy() throws IOException {
        assertTrue(FileUtility.forceDelete(defaultTestSolrFolder));
    }

}
