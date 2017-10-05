package com.imcode.imcms.config;

import com.imcode.imcms.db.DB;
import com.imcode.imcms.db.Schema;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.imcode.imcms.persistence.repository",
        "com.imcode.imcms.mapping.jpa",
        "com.imcode.imcms.imagearchive.entity"
})
public class DBConfig {

    private final Properties imcmsProperties;

    @Autowired
    public DBConfig(Properties imcmsProperties) {
        this.imcmsProperties = imcmsProperties;
    }

    @Bean
    public BasicDataSource dataSource() {
        final BasicDataSource basicDataSource = new BasicDataSource();
        setImcmsDataSourceProperties(basicDataSource);
        return basicDataSource;
    }

    //    Wasn't in previous config
    @Bean
    public BasicDataSource dataSourceWithAutoCommit() {
        final BasicDataSource basicDataSource = new BasicDataSource();

        setImcmsDataSourceProperties(basicDataSource);
        basicDataSource.setDefaultAutoCommit(true);

        return basicDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        runSqlDiffs(dataSource);
        final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setJpaDialect(new HibernateJpaDialect());
        entityManagerFactory.setPackagesToScan("com.imcode.imcms.imagearchive", "com.imcode.imcms.mapping.jpa", "com.imcode.imcms.persistence.entity");
        entityManagerFactory.setPersistenceUnitName("com.imcode.imcms");
        entityManagerFactory.setJpaPropertyMap(createHibernateJpaProperties());
        return entityManagerFactory;
    }

    private void runSqlDiffs(DataSource dataSource) {
        final URL sqlDiffsResource = Thread.currentThread()
                .getContextClassLoader()
                .getResource("sql");

        final String sqlResourcesPath = Optional.ofNullable(sqlDiffsResource)
                .orElseThrow(() -> new RuntimeException("SQL resources folder not found in classpath!"))
                .getPath();

        final InputStream inputStream = getDatabaseSchemaInputStream();
        final Schema schema = Schema.fromInputStream(inputStream).setScriptsDir(sqlResourcesPath);

        new DB(dataSource).prepare(schema);
    }

    private InputStream getDatabaseSchemaInputStream() {
        final InputStream resourceAsStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("schema.xml");

        if (resourceAsStream == null) {
            String errMsg = "Database schema config file 'schema.xml' can not be found in the classpath.";
            throw new RuntimeException(errMsg);
        }

        return resourceAsStream;
    }

    private Map<String, String> createHibernateJpaProperties() {
        final HashMap<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.use_sql_comments", "true");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.hbm2ddl.auto", imcmsProperties.getProperty("hbm2ddl.auto"));

        return properties;
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

    private void setImcmsDataSourceProperties(BasicDataSource basicDataSource) {
        basicDataSource.setDriverClassName(imcmsProperties.getProperty("JdbcDriver"));
        basicDataSource.setUrl(imcmsProperties.getProperty("JdbcUrl"));
        basicDataSource.setUsername(imcmsProperties.getProperty("User"));
        basicDataSource.setPassword(imcmsProperties.getProperty("Password"));
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setValidationQuery("select 1");
        basicDataSource.setDefaultAutoCommit(false);
        basicDataSource.setMaxTotal(20);
        basicDataSource.setMaxTotal(Integer.parseInt(imcmsProperties.getProperty("MaxConnectionCount")));
    }
}
