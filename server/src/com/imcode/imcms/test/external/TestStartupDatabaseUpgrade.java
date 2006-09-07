package com.imcode.imcms.test.external;

import junit.framework.*;
import com.imcode.imcms.db.StartupDatabaseUpgrade;
import com.imcode.imcms.db.ImcmsDatabaseUpgrade;
import com.imcode.db.DataSourceDatabase;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlUpdateCommand;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.commons.io.CopyUtils;
import imcode.server.Imcms;

import java.io.*;

public class TestStartupDatabaseUpgrade extends TestCase {

    public void testCreateMysql() throws Exception {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String host = "localhost";
        String baseUrl = "jdbc:mysql://" + host + ":3306/" ;
        String jdbcUser = "root";
        String jdbcPassword = "";
        String databaseName = "imcms";
        DataSourceDatabase database = createDataSourceDatabase(jdbcDriver, baseUrl, jdbcUser, jdbcPassword);
        dropDatabase(database, databaseName);
        createDatabase(database, databaseName);
        database = createDataSourceDatabase(jdbcDriver, baseUrl + databaseName, jdbcUser, jdbcPassword);
        getDatabaseUpgrade().upgrade(database);
    }

    public void testCreateMssql() throws Exception {
        String jdbcDriver = "net.sourceforge.jtds.jdbc.Driver";
        String host = "localhost";
        String baseUrl = "jdbc:jtds:sqlserver://" + host + ":1433/";
        String jdbcUser = "sa";
        String jdbcPassword = "";
        String databaseName = "imcms";
        DataSourceDatabase database = createDataSourceDatabase(jdbcDriver, baseUrl, jdbcUser, jdbcPassword);
        dropDatabase(database, databaseName);
        createDatabase(database, databaseName);
        database = createDataSourceDatabase(jdbcDriver, baseUrl + databaseName, jdbcUser, jdbcPassword);
        getDatabaseUpgrade().upgrade(database);
    }

    private ImcmsDatabaseUpgrade getDatabaseUpgrade() throws IOException {
        Database ddl = new DatabaseIO().read(new StringReader(getDdlXml()));
        ImcmsDatabaseUpgrade startupDatabaseUpgrade = new StartupDatabaseUpgrade(ddl, new File(Imcms.getPath(),"sql/data"));
        return startupDatabaseUpgrade;
    }

    private void createDatabase(DataSourceDatabase mssqlDatabase, String database) {
        mssqlDatabase.execute(new SqlUpdateCommand("CREATE DATABASE "+database,null)) ;
    }

    private void dropDatabase(DataSourceDatabase mssqlDatabase, String databaseName) {
        try {
            mssqlDatabase.execute(new SqlUpdateCommand("DROP DATABASE "+databaseName,null)) ;
        } catch( DatabaseException ignored ) {}
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