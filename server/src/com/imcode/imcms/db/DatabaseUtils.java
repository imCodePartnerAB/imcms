package com.imcode.imcms.db;

import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.mock.MockDatabase;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.PlatformUtils;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.platform.mssql.MSSqlPlatform;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

public class DatabaseUtils {

    private DatabaseUtils() {
    }

    private static String currentDatabaseName = null;

    public static Database getWantedDdl() throws IOException {
        DatabaseIO io = new DatabaseIO();
        io.setValidateXml(false);
        return io.read(new FileReader(new File(Imcms.getPath(), "WEB-INF/sql/ddl.xml")));
    }

    public static Platform getPlatform(DatabaseConnection databaseConnection) {
        return PlatformFactory.createNewPlatformInstance(new SingleConnectionDataSource(databaseConnection.getConnection()));
    }

    public static String getCurrentDatabaseName(ImcmsServices services) {
        if (currentDatabaseName == null) {
            com.imcode.db.Database database = services.getDatabase();
            if (database instanceof MockDatabase) {
                currentDatabaseName = "mock";
            }
            else {
                currentDatabaseName = (String) services.getDatabase().execute(new DatabaseCommand() {
                    public Object executeOn(DatabaseConnection databaseConnection) throws DatabaseException {
                        final Connection connection = databaseConnection.getConnection();
                        DataSource dataSource = new SingleConnectionDataSource(connection);
                        PlatformUtils platformUtils = new PlatformUtils();
                        return platformUtils.determineDatabaseType(dataSource);
                    }
                });
            }
        }
        return currentDatabaseName;
    }

    public static boolean isDatabaseMSSql(ImcmsServices services) {
        return MSSqlPlatform.DATABASENAME.equals(getCurrentDatabaseName(services));
    }
}
