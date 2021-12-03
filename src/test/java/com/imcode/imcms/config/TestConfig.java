package com.imcode.imcms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentStoredFields;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.apache.solr.common.SolrDocument;
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
import java.util.function.Function;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Configuration
@Import(MainConfig.class)
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
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        return mapper;
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

    @Bean
    public Function<TextDocumentDTO, DocumentStoredFieldsDTO> textDocumentDTOtoDocumentStoredFieldsDTO() {
        return textDocument -> {
            final String langCode = Imcms.getUser().getLanguage();

            SolrDocument solrDocument = new SolrDocument();
            solrDocument.put(DocumentIndex.FIELD__META_ID, textDocument.getId());
            solrDocument.put(DocumentIndex.FIELD__META_HEADLINE + "_" + langCode,
                    textDocument.getCommonContents().get(0).getHeadline());

            solrDocument.put(DocumentIndex.FIELD__DOC_TYPE_ID, textDocument.getType().ordinal());
            solrDocument.put(DocumentIndex.FIELD__ALIAS, textDocument.getAlias());
            solrDocument.put(DocumentIndex.FIELD__STATUS, textDocument.getPublicationStatus().ordinal());
            solrDocument.put(DocumentIndex.FIELD__VERSION_NO, textDocument.getCurrentVersion().getId());
            solrDocument.put(DocumentIndex.FIELD__LANGUAGE_CODE, null); // I hope we don't need this in tests...
            solrDocument.put(DocumentIndex.FIELD__CREATED_DATETIME, textDocument.getCreated().getFormattedDate());
            solrDocument.put(DocumentIndex.FIELD__MODIFIED_DATETIME, textDocument.getModified().getFormattedDate());
            solrDocument.put(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, textDocument.getPublished().getFormattedDate());
            solrDocument.put(DocumentIndex.FIELD__ARCHIVED_DATETIME, textDocument.getArchived().getFormattedDate());
            solrDocument.put(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, textDocument.getPublicationEnd().getFormattedDate());
            DocumentStoredFields from = new DocumentStoredFields(solrDocument);

            return new DocumentStoredFieldsDTO(from);
        };
    }

    @PreDestroy
    private void destroy() throws IOException {
        assertTrue(FileUtility.forceDelete(defaultTestSolrFolder.getParentFile()));
    }

}
