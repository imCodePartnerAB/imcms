package com.imcode.imcms.config;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.Database;
import com.imcode.imcms.api.DatabaseService;
import com.imcode.imcms.db.DB;
import com.imcode.imcms.db.Schema;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.imcode.imcms.persistence.repository",
        "com.imcode.imcms.mapping.jpa"
})
class DBConfig {

    private final Properties imcmsProperties;

    @Value("classpath:sql")
    private Resource sqlDiffsResource;

    @Value("classpath:schema.xml")
    private Resource schemaXmlResource;

    @Autowired
    public DBConfig(Properties imcmsProperties) {
        this.imcmsProperties = imcmsProperties;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        setImcmsDataSourceProperties(config);
        return new HikariDataSource(config);
    }

    @Bean
    public DataSource dataSourceWithAutoCommit() {
        HikariConfig config = new HikariConfig();
        setImcmsDataSourceProperties(config);
        config.setAutoCommit(true);
        return new HikariDataSource(config);
    }

    @Bean
    public DatabaseService databaseService(@Qualifier("dataSourceWithAutoCommit") DataSource dataSource) {
        return new DatabaseService(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) throws IOException {
        runSqlDiffs(dataSource);
        final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setJpaDialect(new HibernateJpaDialect());
        entityManagerFactory.setPackagesToScan("com.imcode.imcms.mapping.jpa", "com.imcode.imcms.persistence.entity");
        entityManagerFactory.setPersistenceUnitName("com.imcode.imcms");
        entityManagerFactory.setJpaPropertyMap(createHibernateJpaProperties());
        return entityManagerFactory;
    }

    private void runSqlDiffs(DataSource dataSource) throws IOException {
        final URI sqlResourcesURI = sqlDiffsResource.getURI();
        final InputStream schemaXmlInputStream = schemaXmlResource.getInputStream();
        final Schema schema = Schema.fromInputStream(schemaXmlInputStream)
                .setScriptsDir(sqlResourcesURI);

        new DB(dataSource).prepare(schema);
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        final JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf);
        return jpaTransactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public Database database(DataSource dataSource) {
        return new DataSourceDatabase(dataSource);
    }

    @Bean
    public Database databaseWithAutoCommit(@Qualifier("dataSourceWithAutoCommit") DataSource dataSource) {
        return new DataSourceDatabase(dataSource);
    }

    private void setImcmsDataSourceProperties(HikariConfig config) {
        config.setDriverClassName(imcmsProperties.getProperty("JdbcDriver"));
        config.setJdbcUrl(imcmsProperties.getProperty("JdbcUrl"));
        config.setUsername(imcmsProperties.getProperty("User"));
        config.setPassword(imcmsProperties.getProperty("Password"));
        config.setConnectionTestQuery("select 1");
        config.setConnectionTimeout(Duration.ofSeconds(40).toMillis());
        config.setAutoCommit(false);
        config.setMaximumPoolSize(Integer.parseInt(StringUtils.defaultString(
                imcmsProperties.getProperty("MaxConnectionCount"),
                "20"
        )));

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);
    }

    private Map<String, String> createHibernateJpaProperties() {
        final HashMap<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.use_sql_comments", "true");
        properties.put("hibernate.show_sql", imcmsProperties.getProperty("show_sql", "false"));
        properties.put("hibernate.hbm2ddl.auto", imcmsProperties.getProperty("hbm2ddl.auto"));
        properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        properties.put("hibernate.cache.use_second_level_cache", "true");
        properties.put("hibernate.cache.use_query_cache", "true");

        return properties;
    }
}
