package com.imcode.imcms;

import clojure.lang.ArraySeq;
import clojure.lang.RT;
import clojure.lang.Var;
import org.testng.annotations.*;

import javax.sql.DataSource;

/**
 * Integration with scripted code.
 */
public class Script {

    static {
        try {
            RT.load("com/imcode/imcms/runtime");
            RT.load("com/imcode/imcms/project");
            RT.load("com/imcode/imcms/project/db");
            RT.load("com/imcode/imcms/project/db/schema");
            RT.load("com/imcode/cljlib/db");
            RT.load("com/imcode/cljlib/db/schema");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Object callClojureFn(String ns, String fn, Object... args) {
        try {
            Var var = RT.var(ns, fn);

            return var.applyTo(RT.seq(args));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String testSchemaName() throws Exception {
        return (String)RT.var("com.imcode.imcms.project", "db-test-schema-name")
               .invoke();
    }

    public static DataSource testDBDatasource() throws Exception {
        return (DataSource)RT.var("com.imcode.imcms.project.db", "create-test-ds")
               .invoke(true);
    }


    public static void recreateTestDB(String[] sqlScriptsPaths) {
        try {
            RT.var("com.imcode.imcms.project.db.schema", "recreate-test")
                .invoke(sqlScriptsPaths);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void runScriptsOnTestDB(String[] sqlScriptsPaths) {
        try {
            RT.var("com.imcode.imcms.project.db", "run-scripts-on-test-db")
                .invoke(sqlScriptsPaths);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
    

    public void initImcms() throws Exception {
        RT.var("com.imcode.imcms.project", "init-imcms")
            .invoke();
    }
}