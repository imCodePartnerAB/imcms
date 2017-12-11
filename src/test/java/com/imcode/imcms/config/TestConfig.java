package com.imcode.imcms.config;

import imcode.server.Config;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.IOException;

@Configuration
@Import({MainConfig.class})
@ComponentScan({
        "com.imcode.imcms.components.datainitializer"
})
public class TestConfig {

    @Bean
    public Config config(Config config,
                         @Value("WEB-INF/test-solr") File defaultTestSolrFolder,
                         @Value("WEB-INF/solr") File defaultSolrFolder) {

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

}
