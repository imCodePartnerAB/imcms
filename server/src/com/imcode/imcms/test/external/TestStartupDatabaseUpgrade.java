package com.imcode.imcms.test.external;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.db.DdlUtilsPlatformCommand;
import com.imcode.imcms.db.ImcmsDatabaseCreator;
import com.imcode.imcms.db.StartupDatabaseUpgrade;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import imcode.server.Imcms;
import imcode.util.ShouldNotBeThrownException;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.alteration.*;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.ForeignKey;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.IndexColumn;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
        dropDatabase(database, databaseName);
        createDatabase(database, databaseName);
        database = createDataSourceDatabase(jdbcDriver, baseUrl + databaseName, jdbcUser, jdbcPassword);
        ImcmsDatabaseCreator databaseCreator = createDatabaseCreator();
        databaseCreator.createDatabase(database, getOldDdl());
        final Database wantedDdl = getWantedDdl();
        new StartupDatabaseUpgrade(wantedDdl, databaseCreator).upgrade(database);
        //assertDatabaseUpgraded(database, wantedDdl);
        assertFalse(templateFile.exists());
        assertTrue(new File(templatesDirectory, "demo_test.html").exists());
    }

    private void assertDatabaseUpgraded(DataSourceDatabase database, final Database wantedDdl) {
        database.execute(new DdlUtilsPlatformCommand() {
            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                Database actualDdl = platform.readModelFromDatabase(null);
                List<ModelChange> list = new ModelComparator(platform.getPlatformInfo(), true).compare(actualDdl, wantedDdl);
                if (!list.isEmpty()) {
                    StringWriter changesString = new StringWriter();
                    PrintWriter changesPrintWriter = new PrintWriter(changesString);
                    changesPrintWriter.println("Changes left for upgrade :");
                    for ( ModelChange change : list ) {
                        String changeString ;
                        if (change instanceof RemoveIndexChange ) {
                            RemoveIndexChange removeIndexChange = (RemoveIndexChange) change;
                            Index index = removeIndexChange.getIndex();
                            changeString = "Remove index "+index.getName() + " on column(s) "+ StringUtils.join(CollectionUtils.collect(Arrays.asList(index.getColumns()), new Transformer() {
                                public Object transform(Object input) {
                                    IndexColumn indexColumn = (IndexColumn) input ;
                                    return indexColumn.getName();
                                }
                            }).iterator(), ", ");
                        } else if (change instanceof ColumnDefaultValueChange ) {
                            ColumnDefaultValueChange columnDefaultValueChange = (ColumnDefaultValueChange) change;
                            changeString = "Change default value to "+columnDefaultValueChange.getNewDefaultValue();
                        } else if (change instanceof ColumnDataTypeChange ) {
                            ColumnDataTypeChange columnDataTypeChange = (ColumnDataTypeChange) change;
                            changeString = "Change data type to "+platform.getPlatformInfo().getNativeType(columnDataTypeChange.getNewTypeCode()) ;
                        } else if (change instanceof AddTableChange ) {
                            AddTableChange addTableChange = (AddTableChange) change;
                            changeString = "Add table "+addTableChange.getNewTable();
                        } else if (change instanceof RemoveForeignKeyChange) {
                            RemoveForeignKeyChange removeForeignKeyChange = (RemoveForeignKeyChange) change;
                            ForeignKey foreignKey = removeForeignKeyChange.getForeignKey();
                            changeString = "Remove foreign key to "+foreignKey.getForeignTableName();
                        } else if (change instanceof AddForeignKeyChange) {
                            AddForeignKeyChange removeForeignKeyChange = (AddForeignKeyChange) change;
                            ForeignKey newForeignKey = removeForeignKeyChange.getNewForeignKey();
                            changeString = "Add foreign key to "+newForeignKey.getForeignTableName();
                        } else if (change instanceof AddIndexChange) {
                            AddIndexChange addIndexChange = (AddIndexChange) change;
                            Index index = addIndexChange.getNewIndex();
                            changeString = "Add index "+index.getName();
                        } else {
                            changeString = change.toString();
                        }
                        if (change instanceof ColumnChange ) {
                            changeString = ((ColumnChange)change).getChangedColumn().getName()+": "+changeString ;
                        }
                        if (change instanceof TableChange ) {
                            changeString = ((TableChange)change).getChangedTable().getName()+": "+changeString ;
                        }
                        changesPrintWriter.println(changeString) ;
                    }
                    throw new AssertionFailedError(changesString.toString()) ;
                }
                return null;
            }
        }) ;
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
                protected Properties getLanguageProperties(String languageIso639_2) {
                    return new Properties() {
                        public String getProperty(String key) {
                            return "";
                        }
                    };
                }
            });
        } catch ( UnsupportedEncodingException e ) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    private Database getDdl(Reader reader) {
        return new DatabaseIO().read(reader);
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