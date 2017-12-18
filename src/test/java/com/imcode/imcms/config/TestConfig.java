package com.imcode.imcms.config;

import imcode.server.Config;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

@Configuration
@Import({MainConfig.class})
@ComponentScan({
        "com.imcode.imcms.components.datainitializer"
})
public class TestConfig {

    @Value("WEB-INF/test-solr")
    private File defaultTestSolrFolder;

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
