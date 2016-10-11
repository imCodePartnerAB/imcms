package com.imcode.imcms.config;

import com.imcode.imcms.imagearchive.Config;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.PropertyEditor;
import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zemluk on 11.10.16.
 */
@Configuration
@EnableWebMvc

//JPA annotations
//TODO: Probably should be moved to another config
@EnableJpaRepositories(basePackages = "com.imcode.imcms.mapping.jpa")
@EnableTransactionManagement


@ComponentScan({"com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.api.linker", "imcode.util", "com.imcode.imcms.servlet.apis"})
//@PropertySource("classpath:config.properties")

//TODO: Check properties for  system-properties-mode="NEVER" option
@PropertySource(value = "/WEB-INF/conf/server.properties", ignoreResourceNotFound = true)
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;


    //    configContext.xml
    @Bean
    public CustomEditorConfigurer customEditorConfigurer() {
        CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
        Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new ManagedMap<>();
        customEditors.put(java.io.File.class, com.imcode.imcms.imagearchive.util.FileEditor.class);
        customEditors.put(java.io.File[].class, com.imcode.imcms.imagearchive.util.FileArrayEditor.class);
        customEditorConfigurer.setCustomEditors(customEditors);
        return customEditorConfigurer;


/*        Map<String, Class<?>> customEditors = new ManagedMap<String, Class<?>>();
        customEditors.put("com.mongodb.MongoCredential[]", MongoCredentialPropertyEditor.class);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CustomEditorConfigurer.class);
        builder.addPropertyValue("customEditors", customEditors);

        return builder;*/
    }

    @Bean
    public Config imageArchiveConfig() {
        Config config = new Config();

        config.setStoragePath(new File(env.getProperty("ImageArchiveStoragePath")));
        config.setTmpPath(new File(env.getProperty("ImageArchiveTempPath")));
        config.setImageMagickPath(new File(env.getProperty("ImageMagickPath")));
        config.setImagesPath(new File(env.getProperty("ImageArchiveImagesPath")));
        config.setLibrariesPath(new File(env.getProperty("ImageArchiveLibrariesPath")));
        config.setOldLibraryPaths(new File[]{new File(env.getProperty("ImageArchiveOldLibraryPaths"))});
        config.setUsersLibraryFolder(env.getProperty("ImageArchiveUsersLibraryFolder"));
        config.setMaxImageUploadSize(Long.parseLong(env.getProperty("ImageArchiveMaxImageUploadSize")));
        config.setMaxZipUploadSize(Long.parseLong(env.getProperty("ImageArchiveMaxZipUploadSize")));

        //TODO: Look like there another bean required
//        config.setLanguages();

        return config;
    }

    //beansContext.xml
    //it's empty


    //applicationContext.xml

    //TODO configure JPA

    //Unattended content
//  <context:annotation-config/>
//	<tx:annotation-driven proxy-target-class="false"/>
//	<aop:config proxy-target-class="false"/>
//
//	<jpa:repositories base-package="com.imcode.imcms.mapping.jpa" entity-manager-factory-ref="myEmf"/>


    @Bean
    public BasicDataSource basicDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("JdbcDriver"));
        dataSource.setUrl(env.getProperty("JdbcUrl"));
        dataSource.setUsername(env.getProperty("User"));
        dataSource.setPassword(env.getProperty("Password"));
        dataSource.setTestOnBorrow(true);
        dataSource.setValidationQuery("select 1");
        dataSource.setDefaultAutoCommit(false);
        dataSource.setMaxTotal(Integer.parseInt(env.getProperty("MaxConnectionCount")));

        return dataSource;
    }

//    //    TODO: Probably it's wrong implementation
//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
////TODO: is all hibernate config should be placed here?
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//
////TODO: Is that needed???
//        vendorAdapter.setGenerateDdl(true);
//
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.mapping.jpa");
//        factory.setDataSource(basicDataSource());
//
////TODO: Is that needed???
//        factory.afterPropertiesSet();
//
//        return factory.getObject();
//    }

    @Bean
    public LocalContainerEntityManagerFactoryBean factoryBean() {
        //TODO: Check possible that must be located at properties
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(basicDataSource());
        factoryBean.setPersistenceUnitName("com.imcode.imcms");
        factoryBean.setPersistenceUnitManager(defaultPersistenceUnitManager());

        //TODO: is all hibernate config should be placed here?
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        //TODO: Is that needed???
        vendorAdapter.setGenerateDdl(true);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        factoryBean.setJpaProperties(props);

        return factoryBean;
    }


    @Bean
    public DefaultPersistenceUnitManager defaultPersistenceUnitManager() {
        Map<String, DataSource> dataSources = new HashedMap();
        dataSources.put("localDataSource", basicDataSource());
        dataSources.put("remoteDataSource", basicDataSource());

        DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
        persistenceUnitManager.setPersistenceXmlLocations("classpath:META-INF/persistence.xml");
        persistenceUnitManager.setDataSources(dataSources);
        persistenceUnitManager.setDefaultDataSource(basicDataSource());
        return persistenceUnitManager;
    }

    //TODO: Possibley not required if @EnableTransactionManagement exists
    @Bean
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory((EntityManagerFactory) factoryBean());
        return jpaTransactionManager;
    }

    @Bean
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        //TODO: Move that values to properties
        source.setBasenames("/WEB-INF/locale/imcms", "/WEB-INF/locale/image_archive");
        source.setFallbackToSystemLocale(false);
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }


    //web-servlet.xml

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(Long.parseLong(env.getProperty("ImageArchiveMaxImageUploadSize")));
        return multipartResolver;
    }

    @Bean
    public UrlBasedViewResolver urlBasedViewResolver() {
        UrlBasedViewResolver urlBasedViewResolver = new UrlBasedViewResolver();
        urlBasedViewResolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
        urlBasedViewResolver.setPrefix("/WEB-INF/jsp/imcms/views/");
        urlBasedViewResolver.setSuffix(".jsp");

        return urlBasedViewResolver;
    }
}
