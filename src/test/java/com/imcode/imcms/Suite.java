package com.imcode.imcms;

import clojure.lang.RT;
import clojure.lang.Var;
import org.testng.annotations.*;

import javax.sql.DataSource;

@Test
public class Suite {


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


    public static String testSchemaName() throws Exception {
        return (String)RT.var("com.imcode.imcms.project", "db-test-schema-name")
               .invoke();
    }

    public static DataSource testDBDatasource() throws Exception {
        return (DataSource)RT.var("com.imcode.imcms.project.db", "create-test-ds")
               .invoke(true);
    }


    public static void recreateTestDB(String[] sqlScriptsPaths) throws Exception {
        RT.var("com.imcode.imcms.project.db.schema", "recreate-test")
            .invoke(sqlScriptsPaths);
    }    
    

    @BeforeSuite
    public void initImcms() throws Exception {
        RT.var("com.imcode.imcms.project", "init-imcms")
            .invoke();
    }
}