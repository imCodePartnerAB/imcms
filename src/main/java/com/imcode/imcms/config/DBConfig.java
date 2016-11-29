package com.imcode.imcms.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zemluk on 13.10.16.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.imcode.imcms.mapping.jpa")
@EnableTransactionManagement
//@Import({ApplicationConfig.class})
//@ImportResource("classpath:hibernate.cfg.xml")

public class DBConfig {

    @Autowired
    private Environment env;

/*

    //    TODO just another way to obtain properties
    */
/*@Value("${JdbcUrl}")
    private String jdbcUrl;*//*


//    TODO: Maybe type should be changed to DataSource instead of BasicDataSource
    @Bean(destroyMethod = "close", name = "dataSource")
//    @Bean(destroyMethod = "close", name = "dataSourceWithAutoCommit")
//    @Bean(destroyMethod = "close")
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("JdbcDriver"));
        dataSource.setUrl(env.getProperty("JdbcUrl"));
        dataSource.setUsername(env.getProperty("User"));
        dataSource.setPassword(env.getProperty("Password"));
        dataSource.setTestOnBorrow(true);
        dataSource.setValidationQuery("select 1");
        dataSource.setDefaultAutoCommit(false);
        dataSource.setMaxTotal(20);
        dataSource.setMaxTotal(Integer.parseInt(env.getProperty("MaxConnectionCount")));
        return dataSource;
    }

//        @Bean(destroyMethod = "close", name = "dataSource")
    @Bean(destroyMethod = "close", name = "dataSourceWithAutoCommit")
//    @Bean(destroyMethod = "close")
    public BasicDataSource dataSourceWithAutoCommit() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("JdbcDriver"));
        dataSource.setUrl(env.getProperty("JdbcUrl"));
        dataSource.setUsername(env.getProperty("User"));
        dataSource.setPassword(env.getProperty("Password"));
        dataSource.setTestOnBorrow(true);
        dataSource.setValidationQuery("select 1");
        dataSource.setDefaultAutoCommit(false);
        dataSource.setMaxTotal(20);
        dataSource.setMaxTotal(Integer.parseInt(env.getProperty("MaxConnectionCount")));
        return dataSource;
    }

    @Bean
    public DefaultPersistenceUnitManager defaultPersistenceUnitManager() {
        Map<String, DataSource> dataSources = new HashedMap();
        dataSources.put("localDataSource", dataSource());
        dataSources.put("remoteDataSource", dataSource());

        DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
        persistenceUnitManager.setPersistenceXmlLocations("classpath:META-INF/persistence.xml");
        persistenceUnitManager.setDataSources(dataSources);
        persistenceUnitManager.setDefaultDataSource(dataSource());
        return persistenceUnitManager;
    }


*/
/*    @Bean
    public EntityManagerFactory entityManagerFactory() {

//TODO: is all hibernate config should be placed here?
//TODO: Find a way move that config to annotations
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.imcode.imcms.mapping.jpa");
        factory.setDataSource(basicDataSource());
        factory.setPersistenceUnitName("com.imcode.imcms");
        factory.setPersistenceUnitManager(defaultPersistenceUnitManager());
        factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        factory.setJpaProperties(props);
//TODO: Is that needed???
        factory.afterPropertiesSet();
        return factory.getObject();
    }*//*




//    @Bean(name = "ownEntityManagerFactory")
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory()
    {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(Boolean.TRUE);
//        vendorAdapter.setShowSql(Boolean.TRUE);
//        factory.setDataSource(basicDataSource());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPersistenceUnitManager(defaultPersistenceUnitManager());
        factory.setPackagesToScan("com.imcode.imcms.mapping.jpa");
        factory.setDataSource(dataSource());
        factory.setPersistenceUnitName("com.imcode.imcms");
        Properties jpaProperties = new Properties();
//        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        factory.setJpaProperties(jpaProperties);
//        factory.afterPropertiesSet();
        factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        return factory;
    }

//    @Bean
//    public PlatformTransactionManager transactionManager()
//    {
//        EntityManagerFactory factory = entityManagerFactory().getObject();
//        return new JpaTransactionManager(factory);
//    }

    //TODO: Possibly not required if @EnableTransactionManagement exists
    @Bean(name = "transactionManager")
    public PlatformTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return jpaTransactionManager;
    }





    //Moved from appConfig
    */
/*    @Bean
    public Config imageArchiveConfig() {
        BidiMap languages = new DualHashBidiMap();
        languages.put("eng", "en");
        languages.put("swe", "sv");

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


//        config.setStoragePath(new File(""));
//        config.setTmpPath(new File(""));
//        config.setImageMagickPath(new File(""));
//        config.setImagesPath(new File(""));
//        config.setLibrariesPath(new File(""));
//        config.setOldLibraryPaths(new File[]{new File("")});
//        config.setUsersLibraryFolder("");
//        config.setMaxImageUploadSize(20);
//        config.setMaxZipUploadSize(20);

        config.setLanguages(languages);
        return config;
    }*/


    @Bean
    public BasicDataSource dataSource() {
        // org.apache.commons.dbcp.BasicDataSource
        BasicDataSource basicDataSource = new BasicDataSource();


        basicDataSource.setDriverClassName(env.getProperty("JdbcDriver"));
        basicDataSource.setUrl(env.getProperty("JdbcUrl"));
        basicDataSource.setUsername(env.getProperty("User"));
        basicDataSource.setPassword(env.getProperty("Password"));
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setValidationQuery("select 1");
        basicDataSource.setDefaultAutoCommit(false);
        basicDataSource.setMaxTotal(20);
        basicDataSource.setMaxTotal(Integer.parseInt(env.getProperty("MaxConnectionCount")));


//        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        basicDataSource.setUrl("jdbc:mysql://localhost:3306/spring");
//        basicDataSource.setUsername("root");
//        basicDataSource.setPassword("IHave1Dream!");
        return basicDataSource;
    }

    //    Wasn't in previous config
    @Bean
    public BasicDataSource dataSourceWithAutoCommit() {
        // org.apache.commons.dbcp.BasicDataSource
        BasicDataSource basicDataSource = new BasicDataSource();


        basicDataSource.setDriverClassName(env.getProperty("JdbcDriver"));
        basicDataSource.setUrl(env.getProperty("JdbcUrl"));
        basicDataSource.setUsername(env.getProperty("User"));
        basicDataSource.setPassword(env.getProperty("Password"));
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setValidationQuery("select 1");
        basicDataSource.setDefaultAutoCommit(false);
        basicDataSource.setMaxTotal(20);
        basicDataSource.setMaxTotal(Integer.parseInt(env.getProperty("MaxConnectionCount")));
        basicDataSource.setDefaultAutoCommit(true);

//        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        basicDataSource.setUrl("jdbc:mysql://localhost:3306/spring");
//        basicDataSource.setUsername("root");
//        basicDataSource.setPassword("IHave1Dream!");
        return basicDataSource;
    }

/*

    @Bean
    public DefaultPersistenceUnitManager defaultPersistenceUnitManager() {
        Map<String, DataSource> dataSources = new HashedMap();
        dataSources.put("localDataSource", dataSource());
        dataSources.put("remoteDataSource", dataSource());

        DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
//        persistenceUnitManager.setPersistenceXmlLocations("classpath:META-INF/persistence.xml");
        persistenceUnitManager.setPersistenceXmlLocations("classpath:hibernate.cfg.xml");
        persistenceUnitManager.setDataSources(dataSources);
        persistenceUnitManager.setDefaultDataSource(dataSource());
        return persistenceUnitManager;
    }
*/

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
//    entityManagerFactory.setPersistenceUnitName("hibernate-persistence");
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setJpaDialect(new HibernateJpaDialect());
        entityManagerFactory.setPackagesToScan("com.imcode.imcms.mapping.jpa");
        entityManagerFactory.setPersistenceUnitName("com.imcode.imcms");
//        entityManagerFactory.setPersistenceUnitManager(defaultPersistenceUnitManager());

//entityManagerFactory.setPersistenceXmlLocation("/META-INF/persistence.xml");


//        persistenceUnitManager.setPersistenceXmlLocations("classpath:hibernate.cfg.xml");


        entityManagerFactory.setJpaPropertyMap(hibernateJpaProperties());
//        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory;

    }

    private Map<String, ?> hibernateJpaProperties() {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
//        properties.put("hibernate.hbm2ddl.auto", "create");
//        properties.put("hibernate.show_sql", "false");
//        properties.put("hibernate.format_sql", "false");
//        properties.put("hibernate.hbm2ddl.import_files", "insert-data.sql");
//        properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");

//        properties.put("hibernate.c3p0.min_size", "2");
//        properties.put("hibernate.c3p0.max_size", "5");
//        properties.put("hibernate.c3p0.timeout", "300"); // 5mins

        return properties;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        //org.springframework.orm.jpa.JpaTransactionManager
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf);
        return jpaTransactionManager;
    }

}
