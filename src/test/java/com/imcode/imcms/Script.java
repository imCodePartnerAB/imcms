package com.imcode.imcms;

import clojure.lang.RT;
import clojure.lang.Var;
import org.hibernate.SessionFactory;

import javax.sql.DataSource;

/**
 * Integration with scripted code.
 *
 * Part of test related code is implemented in Clojure.
 */
public class Script {

    public static final String TEST_SQL_SCRIPTS_HOME = "src/test/resources/sql";

    static {
        try {
            RT.load("com/imcode/imcms/boot");
            RT.load("com/imcode/imcms/project");
            RT.load("com/imcode/imcms/db_test");

            RT.load("com/imcode/imcms/runtime");
            RT.load("com/imcode/cljlib/db");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    

//    public static Object callClojureFn(String ns, String fn, Object... args) {
//        try {
//            Var var = RT.var(ns, fn);
//
//            return var.applyTo(RT.seq(args));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


    public static String getDBName() throws Exception {
        return (String)RT.var("com.imcode.imcms.db-test", "db-name")
               .invoke();
    }

    public static DataSource createDBDataSource(boolean autocommit) throws Exception {
        return (DataSource)RT.var("com.imcode.imcms.db-test", "create-ds")
               .invoke(autocommit);
    }

    public static void recreateDB() {
        try {
            RT.var("com.imcode.imcms.db-test", "recreate")
                .invoke();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void prepareDB(boolean recreateBofrePrepare) {
        try {
            RT.var("com.imcode.imcms.db-test", "prepare")
                .invoke(recreateBofrePrepare);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    public static void runDBScripts(String... sqlScriptsPaths) {
        try {
            RT.var("com.imcode.imcms.db-test", "run-scripts")
                .invoke(createPaths(sqlScriptsPaths));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
    

    public static void initImcms(Boolean prepareDBOnStart) {
        try {
            RT.var("com.imcode.imcms.project", "init-imcms").invoke(prepareDBOnStart);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String[] createPaths(String[] scriptsNames) {
        String[] scriptsPaths = new String[scriptsNames.length];

        for (int i = 0; i < scriptsNames.length; i++) {
            scriptsPaths[i] = TEST_SQL_SCRIPTS_HOME + "/" + scriptsNames[i];
        }

        return scriptsPaths;
    }


    public static SessionFactory createHibernateSessionFactory(Class... annotatedClasses) {
        return createHibernateSessionFactory(annotatedClasses, new String[0]);
    }


    public static SessionFactory createHibernateSessionFactory(Class[] annotatedClasses, String... xmlFiles) {

        try {
            return (SessionFactory)RT.var("com.imcode.imcms.db-test", "create-hibernate-sf")
                .invoke(annotatedClasses, xmlFiles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}