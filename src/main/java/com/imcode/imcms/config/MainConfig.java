package com.imcode.imcms.config;

import com.imcode.db.Database;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.api.MailService;
import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.domain.service.api.FileDocumentService;
import com.imcode.imcms.domain.service.api.TextDocumentService;
import com.imcode.imcms.domain.service.api.UrlDocumentService;
import com.imcode.imcms.mapping.DocumentLanguageMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.server.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexFactory;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.server.document.index.service.impl.DocumentIndexServiceOps;
import imcode.util.io.FileUtility;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.StandardEnvironment;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Properties;

@Configuration
@PropertySource(value = {
        "/WEB-INF/conf/server.properties",
        "classpath:test.server.properties"},
        name = "imcms.properties", ignoreResourceNotFound = true)
@Import({
        DBConfig.class,
        ApplicationConfig.class,
        MappingConfig.class
})
@ComponentScan({
        "com.imcode.imcms.domain",
        "com.imcode.imcms.mapping",
        "imcode.util",
        "imcode.server",
        "com.imcode.imcms.db"
})
class MainConfig {

    private static final Logger LOG = Logger.getLogger(MainConfig.class);

    //    Required to be able to access properties file from environment at other configs
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Properties imcmsProperties(StandardEnvironment env, @Value("WEB-INF/solr") File defaultSolrFolder) {
        final Properties imcmsProperties = (Properties) env.getPropertySources().get("imcms.properties").getSource();
        final String solrHome = defaultSolrFolder.getAbsolutePath();
        imcmsProperties.setProperty("SolrHome", solrHome);
        return imcmsProperties;
    }

    @Bean
    public LocalizedMessageProvider createLocalizedMessageProvider() {
        return new CachingLocalizedMessageProvider(new ImcmsPrefsLocalizedMessageProvider());
    }

    @Bean
    public DocumentLanguages createDocumentLanguages(DocumentLanguageMapper languageMapper, Properties imcmsProperties) {
        return DocumentLanguages.create(languageMapper, imcmsProperties);
    }

    @Bean //fixme: rewrite!
    public Config createConfigFromProperties(Properties imcmsProperties) {
        class WebappRelativeFileConverter implements Converter {
            @SuppressWarnings("unchecked")
            public File convert(Class type, Object value) {
                return FileUtility.getFileFromWebappRelativePath((String) value);
            }
        }

        final Config config = new Config();
        ConvertUtils.register(new WebappRelativeFileConverter(), File.class);
        final PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(config);

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (null == propertyDescriptor.getWriteMethod()) {
                continue;
            }

            final String uncapitalizedPropertyName = propertyDescriptor.getName();
            final String capitalizedPropertyName = StringUtils.capitalize(uncapitalizedPropertyName);
            final String propertyValue = imcmsProperties.getProperty(capitalizedPropertyName);

            if (null != propertyValue) {
                try {
                    BeanUtils.setProperty(config, uncapitalizedPropertyName, propertyValue);
                } catch (Exception e) {
                    LOG.error("Failed to set property " + capitalizedPropertyName, e.getCause());
                    continue;
                }
            }

            try {
                String setPropertyValue = BeanUtils.getProperty(config, uncapitalizedPropertyName);
                if (null != setPropertyValue) {
                    LOG.info(capitalizedPropertyName + " = " + setPropertyValue);
                } else {
                    LOG.warn(capitalizedPropertyName + " not set.");
                }
            } catch (Exception e) {
                LOG.error(e, e);
            }
        }
        return config;
    }

    @Bean
    public MailService mailService(@Value("${SmtpServer}") String host, @Value("${SmtpPort}") int port) {
        return new MailService(host, port);
    }

    @Bean
    public ResolvingQueryIndex documentIndex(Database database, Config config, DocumentMapper documentMapper,
                                             DocumentIndexServiceOps documentIndexServiceOps,
                                             DocumentSearchQueryConverter documentSearchQueryConverter) {

        final DocumentIndex index = DocumentIndexFactory.create(config, documentIndexServiceOps);
        final LoggingDocumentIndex documentIndex = new LoggingDocumentIndex(
                database,
                new PhaseQueryFixingDocumentIndex(index)
        );

        documentMapper.setDocumentIndex(documentIndex);

        return new DefaultResolvingQueryIndex(documentIndex, documentSearchQueryConverter);
    }

    @Bean
    public DocumentService<TextDocumentDTO> textDocumentService(DocumentService<DocumentDTO> documentService,
                                                                DocumentDtoFactory documentDtoFactory,
                                                                TextDocumentTemplateService textDocumentTemplateService) {

        return new TextDocumentService(documentService, documentDtoFactory, textDocumentTemplateService);
    }

    @Bean
    public DocumentService<FileDocumentDTO> fileDocumentService(DocumentService<DocumentDTO> documentService,
                                                                DocumentDtoFactory documentDtoFactory,
                                                                DocumentFileService documentFileService) {

        return new FileDocumentService(documentService, documentDtoFactory, documentFileService);
    }

    @Bean
    public DocumentService<UrlDocumentDTO> urlDocumentService(DocumentService<DocumentDTO> documentService,
                                                              DocumentDtoFactory documentDtoFactory,
                                                              DocumentUrlService documentUrlService) {

        return new UrlDocumentService(documentService, documentDtoFactory, documentUrlService);
    }

    @Bean
    public LanguageMapper languageMapper(Database createDatabase,
                                         LanguageService languageService,
                                         Config config) {
        return new LanguageMapper(createDatabase, config.getDefaultLanguage(), languageService);
    }
}
