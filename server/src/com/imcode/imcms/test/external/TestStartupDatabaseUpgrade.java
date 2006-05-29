package com.imcode.imcms.test.external;

import junit.framework.*;
import com.imcode.imcms.db.StartupDatabaseUpgrade;
import com.imcode.imcms.db.ImcmsDatabaseUpgrade;
import com.imcode.db.DataSourceDatabase;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.commons.io.CopyUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.SimpleLayout;
import imcode.server.Imcms;

import java.io.*;
import java.util.Properties;

public class TestStartupDatabaseUpgrade extends TestCase {

    public void testUpgrade() throws Exception {
        Database ddl = new DatabaseIO().read(new StringReader(getDdlXml()));
        ImcmsDatabaseUpgrade startupDatabaseUpgrade = new StartupDatabaseUpgrade(ddl);
        startupDatabaseUpgrade.upgrade(getMysqlDatabase());
        startupDatabaseUpgrade.upgrade(getMssqlDatabase());
    }

    private DataSourceDatabase getMysqlDatabase() {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String jdbcUrl = "jdbc:mysql://localhost:3306/imcms";
        String jdbcUser = "";
        String jdbcPassword = "";
        return createDataSourceDatabase(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
    }

    private DataSourceDatabase getMssqlDatabase() {
        String jdbcDriver = "net.sourceforge.jtds.jdbc.Driver";
        String jdbcUrl = "jdbc:jtds:sqlserver://localhost:1433/imcms";
        String jdbcUser = "";
        String jdbcPassword = "";
        return createDataSourceDatabase(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
    }

    private DataSourceDatabase createDataSourceDatabase(String jdbcDriver, String jdbcUrl, String jdbcUser,
                                                        String jdbcPassword) {
        return new DataSourceDatabase(Imcms.createDataSource(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword, 20));
    }

    private String getDdlXml() throws IOException {
        StringWriter ddlXmlWriter = new StringWriter();
        ddlXmlWriter.write("<database name=\"imcms\">\n") ;
        File[] tableFiles = new File("sql/tables/").listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".tab") ;
            }
        });
        for ( File tableFile : tableFiles ) {
            Reader reader = new InputStreamReader(new FileInputStream(tableFile), "iso-8859-1");
            CopyUtils.copy(reader, ddlXmlWriter) ;
        }
        ddlXmlWriter.write("</database>") ;
        return ddlXmlWriter.toString();
    }

}