package com.imcode.imcms.config;

import com.imcode.db.Database;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.api.MailService;
import com.imcode.imcms.components.Validator;
import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.PropertyService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.domain.service.VersionedContentService;
import com.imcode.imcms.domain.service.api.FileDocumentService;
import com.imcode.imcms.domain.service.api.TextDocumentService;
import com.imcode.imcms.domain.service.api.UrlDocumentService;
import com.imcode.imcms.mapping.DocumentLanguageMapper;
import com.imcode.imcms.mapping.DocumentLoader;
import com.imcode.imcms.mapping.DocumentLoaderCachingProxy;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentVersionMapper;
import com.imcode.imcms.mapping.ImageCacheMapper;
import com.imcode.imcms.servlet.ImageCacheManager;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.server.Config;
import imcode.server.DefaultResolvingQueryIndex;
import imcode.server.LanguageMapper;
import imcode.server.LoggingDocumentIndex;
import imcode.server.PhaseQueryFixingDocumentIndex;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexFactory;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.util.io.FileUtility;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
@PropertySource(value = {
        "/WEB-INF/conf/server.properties",
        "classpath:test.server.properties"},
        name = "imcms.properties", ignoreResourceNotFound = true)
@Import({
        DBConfig.class,
        ApplicationConfig.class,
        MappingConfig.class,
        WebConfig.class
})
@ComponentScan({
        "com.imcode.imcms.domain",
        "com.imcode.imcms.mapping",
        "imcode.util",
        "imcode.server",
        "com.imcode.imcms.components",
        "com.imcode.imcms.db",
})
class MainConfig {

    private static final Logger LOG = Logger.getLogger(MainConfig.class);

    //    Required to be able to access properties file from environment at other configs
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Properties imcmsProperties(StandardEnvironment env,
                                      Validator<Properties> propertiesValidator,
                                      @Value("WEB-INF/solr") File defaultSolrFolder) {

        final Properties imcmsProperties = (Properties) env.getPropertySources().get("imcms.properties").getSource();
        final String solrHome = defaultSolrFolder.getAbsolutePath();
        imcmsProperties.setProperty("SolrHome", solrHome);

        propertiesValidator.validate(imcmsProperties);

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
    public ResolvingQueryIndex documentIndex(Database databaseWithAutoCommit, DocumentMapper documentMapper,
                                             DocumentSearchQueryConverter documentSearchQueryConverter,
                                             DocumentIndexFactory documentIndexFactory) {

        final DocumentIndex index = documentIndexFactory.create();
        final LoggingDocumentIndex documentIndex = new LoggingDocumentIndex(
                databaseWithAutoCommit,
                new PhaseQueryFixingDocumentIndex(index)
        );

        final DefaultResolvingQueryIndex resolvingQueryIndex = new DefaultResolvingQueryIndex(
                documentIndex, documentSearchQueryConverter
        );

        documentMapper.setDocumentIndex(resolvingQueryIndex);

        return resolvingQueryIndex;
    }

    @Bean
    public DocumentLoaderCachingProxy documentLoaderCachingProxy(DocumentVersionMapper docVersionMapper,
                                                                 DocumentLoader documentLoader,
                                                                 DocumentLanguages languages,
                                                                 PropertyService propertyService,
                                                                 Config config) {

        return new DocumentLoaderCachingProxy(docVersionMapper, documentLoader, languages, propertyService, config);
    }

    @Bean
    public DocumentService<TextDocumentDTO> textDocumentService(DocumentService<DocumentDTO> documentService,
                                                                TextDocumentTemplateService textDocumentTemplateService,
                                                                TextService textService,
                                                                ImageService imageService) {

        return new TextDocumentService(documentService, textDocumentTemplateService, imageService, textService);
    }

    @Bean
    @SneakyThrows
    public DocumentService<FileDocumentDTO> fileDocumentService(DocumentService<DocumentDTO> documentService,
                                                                DocumentFileService documentFileService,
                                                                Config config,
                                                                @Value("${FilePath}") Resource filesRoot) {

        return new FileDocumentService(documentService, documentFileService, filesRoot.getFile(), config);
    }

    @Bean
    public DocumentService<UrlDocumentDTO> urlDocumentService(DocumentService<DocumentDTO> documentService,
                                                              DocumentUrlService documentUrlService) {

        return new UrlDocumentService(documentService, documentUrlService);
    }

    @Bean
    public LanguageMapper languageMapper(Database database, LanguageService languageService, Config config) {
        return new LanguageMapper(database, config.getDefaultLanguage(), languageService);
    }

    @Bean
    public ImageCacheManager imageCacheManager(ImageCacheMapper imageCacheMapper, Config config) {
        return new ImageCacheManager(imageCacheMapper, config);
    }

    @Bean
    public PathMatcher pathMatcher() {
        return new AntPathMatcher(); // The AntPathMatcher is thread safe
    }

    @Bean
    public List<VersionedContentService> versionedContentServices(VersionedContentService menuService,
                                                                  VersionedContentService imageService,
                                                                  VersionedContentService loopService,
                                                                  VersionedContentService textService,
                                                                  VersionedContentService defaultCommonContentService,
                                                                  VersionedContentService defaultDocumentFileService,
                                                                  VersionedContentService defaultDocumentUrlService) {

        return Arrays.asList(
                menuService, imageService, loopService, textService,
                defaultCommonContentService, defaultDocumentFileService, defaultDocumentUrlService
        );
    }
}
