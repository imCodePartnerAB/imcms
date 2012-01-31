package com.imcode.imcms.test.external;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.db.ImcmsDatabaseCreator;
import com.imcode.imcms.db.StartupDatabaseUpgrade;
import com.imcode.imcms.db.refactoring.DatabasePlatform;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import com.imcode.imcms.util.EmptyEnumeration;
import imcode.server.Imcms;
import imcode.util.ShouldNotBeThrownException;
import junit.framework.TestCase;
import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;

import java.io.*;
import java.util.*;

public class TestStartupDatabaseUpgrade extends TestCase {

    public void testCreateMysql() throws Exception {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String host = "localhost";
        String baseUrl = "jdbc:mysql://" + host + ":3306/" ;
        String jdbcUser = "root";
        String jdbcPassword = "";
        String databaseName = "imcmstest";
        doTest(jdbcDriver, baseUrl, jdbcUser, jdbcPassword, databaseName);
    }

    public void testCreateMssql() throws Exception {
        String jdbcDriver = "net.sourceforge.jtds.jdbc.Driver";
        String host = "localhost";
        String baseUrl = "jdbc:jtds:sqlserver://" + host + ":1433/";
        String jdbcUser = "sa";
        String jdbcPassword = "";
        String databaseName = "imcmstest";
        doTest(jdbcDriver, baseUrl, jdbcUser, jdbcPassword, databaseName);
    }

    private void doTest(String jdbcDriver, String baseUrl, String jdbcUser, String jdbcPassword,
                        String databaseName) throws IOException {
        File root = new File("tmp/test/");
        FileUtils.deleteDirectory(root);
        Imcms.setPath(root);
        File templatesDirectory = new File(root, "WEB-INF/templates/text");
        templatesDirectory.mkdirs();
        File templateFile = new File(templatesDirectory, "1.html");
        templateFile.createNewFile();
        DataSourceDatabase database = createDataSourceDatabase(jdbcDriver, baseUrl, jdbcUser, jdbcPassword);
        DatabasePlatform databasePlatform = DatabasePlatform.getInstance(database);
        databasePlatform.dropDatabase(databaseName);
        databasePlatform.createDatabase(databaseName);
        database = createDataSourceDatabase(jdbcDriver, baseUrl + databaseName, jdbcUser, jdbcPassword);
        ImcmsDatabaseCreator databaseCreator = createDatabaseCreator();
        databaseCreator.createDatabase(database, getOldDdl());
        final Database wantedDdl = getWantedDdl();
        new StartupDatabaseUpgrade(wantedDdl, databaseCreator).upgrade(database);
        //assertDatabaseUpgraded(database, wantedDdl);
        assertFalse(templateFile.exists());
        assertTrue(new File(templatesDirectory, "demo_test.html").exists());
    }

    private Database getOldDdl() {
        return getDdl(getOldDdlXmlReader());
    }

    private Reader getOldDdlXmlReader() {
        try {
            return new InputStreamReader(getClass().getResourceAsStream("/com/imcode/imcms/test/external/imcms-ddl-3.0.xml"), "UTF-8") ;
        } catch ( UnsupportedEncodingException e ) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    private Database getWantedDdl() throws IOException {
        return getDdl(new StringReader(getDdlXml()));
    }

    private ImcmsDatabaseCreator createDatabaseCreator() {
        try {
            InputStreamReader initScriptReader = new InputStreamReader(getClass().getResourceAsStream("/com/imcode/imcms/test/external/imcms-init-3.0.sql"), "UTF-8");
            return new ImcmsDatabaseCreator(initScriptReader, new LocalizedMessageProvider() {
                public ResourceBundle getResourceBundle(String languageIso639_2) {
                    return new NullResourceBundle();
                }
            });
        } catch ( UnsupportedEncodingException e ) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    private Database getDdl(Reader reader) {
        DatabaseIO io = new DatabaseIO();
        io.setValidateXml(false);
        return io.read(reader);
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

    private static class NullResourceBundle extends ResourceBundle {

        protected Object handleGetObject(String key) {
            return "";
        }

        public Enumeration<String> getKeys() {
            return new EmptyEnumeration();
        }
    }
}