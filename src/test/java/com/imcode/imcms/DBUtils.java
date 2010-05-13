package com.imcode.imcms;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * 
 */
public class DBUtils {

    public static final String TEST_SQL_SCRIPTS_HOME = "src/test/resources";

    
    public static interface CustomConfigurationCallback {
        void configure(AnnotationConfiguration conf);
    }

    
    public static SessionFactory createTestDBSessionFactory(CustomConfigurationCallback callback) {

        AnnotationConfiguration conf = new AnnotationConfiguration().
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

        callback.configure(conf);

        return conf.buildSessionFactory();
    }


    public static SessionFactory createTestDBSessionFactory(final Class... annotatedClasses) {

        return createTestDBSessionFactory(new CustomConfigurationCallback() {
            public void configure(AnnotationConfiguration conf) {
                for (Class clazz: annotatedClasses) {
                    conf.addAnnotatedClass(clazz);
                }
            }
        });
    }


    public static SessionFactory createTestDBSessionFactory(final Class[] annotatedClasses, final String... xmlFiles) {

        return createTestDBSessionFactory(new CustomConfigurationCallback() {
            public void configure(AnnotationConfiguration conf) {
                for (Class clazz: annotatedClasses) {
                    conf.addAnnotatedClass(clazz);
                }

                for (String xmlFile: xmlFiles) {
                    conf.addFile(xmlFile);
                }
            }
        });
    }


    public static void recreateTestDB() {
        recreateTestDB(new String[0]);
    }

    public static void recreateTestDB(String... scriptsNames) {
        String[] sqlScriptsPaths = new String[scriptsNames.length];

        for (int i = 0; i < scriptsNames.length; i++) {
            sqlScriptsPaths[i] = TEST_SQL_SCRIPTS_HOME + "/" + scriptsNames[i];
        }

        Script.recreateTestDB(sqlScriptsPaths);
    }


    public static void runScriptsOnTestDB(String... scriptsNames) {
        String[] sqlScriptsPaths = new String[scriptsNames.length];

        for (int i = 0; i < scriptsNames.length; i++) {
            sqlScriptsPaths[i] = TEST_SQL_SCRIPTS_HOME + "/" + scriptsNames[i];
        }

        Script.recreateTestDB(sqlScriptsPaths);
    }    
}
