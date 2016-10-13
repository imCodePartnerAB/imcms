package com.imcode.imcms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Created by zemluk on 11.10.16.
 */
@Configuration
@EnableWebMvc

//@ComponentScan({"com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.api.linker", "imcode.util", "com.imcode.imcms.servlet.apis", "com.imcode.imcms.config"})
//@PropertySource("classpath:config.properties")
//@Import({ ApplicationProperties.class})
//TODO: Check properties for  system-properties-mode="NEVER" option
//@PropertySource( name = "props", value = "classpath:/WEB-INF/conf/server.properties", ignoreResourceNotFound = true)
//@PropertySource("/WEB-INF/conf/server.properties")
//@PropertySource(value = "build.properties")
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    public Environment env;


/*
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("/WEB-INF/conf/server.properties"));
        propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(true);

        return propertySourcesPlaceholderConfigurer;
    }*/

    /*@Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        configurer.setIgnoreResourceNotFound(true);
        return configurer;
    }*/

//    @Value("#{props['JdbcUrl']}")


/*

    //    configContext.xml

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


    //    TODO: Probably it's wrong implementation
    @Bean
    public EntityManagerFactory entityManagerFactory() {

//        String url =  PropertyManager.getPropertyFrom(propertiesFile, "ImageArchiveStoragePath");


//TODO: is all hibernate config should be placed here?
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

//TODO: Is that needed???
//        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("com.imcode.imcms.mapping", "com.imcode.imcms.imagearchive", "com.imcode.imcms.mapping.jpa");
//        factory.setDataSource(basicDataSource());
//


        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

//        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//        factory.setDataSource(basicDataSource());
        factory.setPersistenceUnitName("com.imcode.imcms");
        factory.setPersistenceUnitManager(defaultPersistenceUnitManager());

        //TODO: is all hibernate config should be placed here?
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        //TODO: Is that needed???
        vendorAdapter.setGenerateDdl(true);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        factory.setJpaProperties(props);


//TODO: Is that needed???
        factory.afterPropertiesSet();

        return factory.getObject();
    }

//    @Bean
//    public LocalContainerEntityManagerFactoryBean factoryBean() {
//        //TODO: Check possible that must be located at properties
//        Properties props = new Properties();
//        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
//
//        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//        factoryBean.setDataSource(basicDataSource());
//        factoryBean.setPersistenceUnitName("com.imcode.imcms");
//        factoryBean.setPersistenceUnitManager(defaultPersistenceUnitManager());
//
//        //TODO: is all hibernate config should be placed here?
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        //TODO: Is that needed???
//        vendorAdapter.setGenerateDdl(true);
//        factoryBean.setJpaVendorAdapter(vendorAdapter);
//        factoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
//        factoryBean.setJpaProperties(props);
//
//        return factoryBean;
//    }


    @Bean
    public DefaultPersistenceUnitManager defaultPersistenceUnitManager() {
        Map<String, DataSource> dataSources = new HashedMap();
//        dataSources.put("localDataSource", basicDataSource());
//        dataSources.put("remoteDataSource", basicDataSource());

        DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
        persistenceUnitManager.setPersistenceXmlLocations("classpath:META-INF/persistence.xml");
        persistenceUnitManager.setDataSources(dataSources);
//        persistenceUnitManager.setDefaultDataSource(basicDataSource());
        return persistenceUnitManager;
    }

    //TODO: Possibley not required if @EnableTransactionManagement exists
    @Bean
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory());
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
*/

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
