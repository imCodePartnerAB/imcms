package com.imcode.imcms.dao;

import java.io.FileReader;

import javax.sql.DataSource;

import com.imcode.imcms.Suite;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.After;
import org.junit.Before;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class DaoTest {
	
	public static final String SQL_SCRIPTS_HOME = "src/test/resources";

    public DaoTest() {
        
    }


	@Before
    public void initTester() throws Exception {
        String[] scriptsNames = getSQLScriptsNames();
        String[] sqlScriptsPaths = new String[scriptsNames.length];

        for (int i = 0; i < scriptsNames.length; i++) {
            sqlScriptsPaths[i] = SQL_SCRIPTS_HOME + "/" + scriptsNames[i];
        }

        Suite.recreateTestDB(sqlScriptsPaths);

        AnnotationConfiguration config = new AnnotationConfiguration().
            setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect").
            setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver").
            setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/imcms_test?characterEncoding=utf8").
            setProperty("hibernate.connection.username", "root").
            setProperty("hibernate.connection.password", "").
            setProperty("hibernate.connection.pool_size", "1").
            setProperty("hibernate.connection.autocommit", "true").
            setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider").
            //setProperty("hibernate.hbm2ddl.auto", "create-drop").

            setProperty("hibernate.show_sql", "true");

        configure(config);

        SessionFactory sessionFactory = config.buildSessionFactory();

        init(sessionFactory);        
	}

    
    protected String[] getSQLScriptsNames() {
         return new String[0];
     }
    

    /**
     * Configures hibernate conf.
     * @param cfg
     */
    protected void configure(AnnotationConfiguration cfg) {}

    protected void init(SessionFactory f) {}
}