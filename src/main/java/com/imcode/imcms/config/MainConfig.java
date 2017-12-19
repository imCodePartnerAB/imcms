package com.imcode.imcms.config;

import com.imcode.db.Database;
import com.imcode.imcms.api.DatabaseService;
import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.api.MailService;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.db.ProcedureExecutor;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentLanguageMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.server.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexFactory;
import imcode.server.document.index.service.impl.DocumentContentIndexer;
import imcode.server.document.index.service.impl.DocumentIndexServiceOps;
import imcode.server.document.index.service.impl.DocumentIndexer;
import imcode.util.CachingFileLoader;
import imcode.util.io.FileUtility;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;

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
        "imcode.server"
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

    @Bean
    public CachingFileLoader createCachingFileLoader() {
        return new CachingFileLoader();
    }

    @Bean
    public Config createConfigFromProperties(Properties imcmsProperties) {
        class WebappRelativeFileConverter implements Converter {
            @SuppressWarnings("unchecked")
            public File convert(Class type, Object value) {
                return FileUtility.getFileFromWebappRelativePath((String) value);
            }
        }

        Config config = new Config();
        ConvertUtils.register(new WebappRelativeFileConverter(), File.class);
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(config);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (null == propertyDescriptor.getWriteMethod()) {
                continue;
            }
            String uncapitalizedPropertyName = propertyDescriptor.getName();
            String capitalizedPropertyName = StringUtils.capitalize(uncapitalizedPropertyName);
            String propertyValue = imcmsProperties.getProperty(capitalizedPropertyName);
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
    public ProcedureExecutor procedureExecutor(Database database, CachingFileLoader fileLoader,
                                               @Value("classpath:sql") Resource sqlResource) {
        return new DefaultProcedureExecutor(database, fileLoader, sqlResource);
    }

    @Bean
    public ImcmsServices imcmsServices(Properties imcmsProperties, Database database, DocumentLanguages languages,
                                       LocalizedMessageProvider localizedMessageProvider, Config config,
                                       ApplicationContext applicationContext, CachingFileLoader fileLoader,
                                       DatabaseService databaseService, MailService mailService,
                                       TemplateService templateService, ProcedureExecutor procedureExecutor,
                                       DocumentMapper documentMapper) {

        return new DefaultImcmsServices(
                database,
                imcmsProperties,
                localizedMessageProvider,
                fileLoader,
                applicationContext,
                config,
                languages,
                databaseService,
                mailService,
                templateService,
                documentMapper,
                procedureExecutor
        );
    }

    @Bean
    public DocumentIndexer documentIndexer(CategoryMapper categoryMapper, Config config) {
        return new DocumentIndexer(categoryMapper, new DocumentContentIndexer(config));
    }

    @Bean
    public DocumentIndexServiceOps documentIndexServiceOps(DocumentMapper documentMapper,
                                                           DocumentIndexer documentIndexer,
                                                           DocumentLanguages documentLanguages) {

        return new DocumentIndexServiceOps(documentMapper, documentIndexer, documentLanguages);
    }

    @Bean
    public DocumentIndex documentIndex(Database database, Config config, DocumentMapper documentMapper,
                                       DocumentIndexServiceOps documentIndexServiceOps) {

        final DocumentIndex index = DocumentIndexFactory.create(config, documentIndexServiceOps);
        final LoggingDocumentIndex documentIndex = new LoggingDocumentIndex(
                database,
                new PhaseQueryFixingDocumentIndex(index)
        );

        documentMapper.setDocumentIndex(documentIndex);

        return documentIndex;
    }
}
