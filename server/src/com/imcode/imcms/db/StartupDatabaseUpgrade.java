package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.CompositeDatabaseCommand;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.db.handlers.SingleObjectHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.platform.SqlBuilder;
import org.apache.ddlutils.platform.CreationParameters;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.Locale;

import imcode.util.LocalizedMessage;

public class StartupDatabaseUpgrade extends ImcmsDatabaseUpgrade {

    private static final String SQL_STATE__MISSING_TABLE = "42S02";

    private final static Logger LOG = Logger.getLogger(StartupDatabaseUpgrade.class);

    DatabaseVersionUpgradePair[] upgrades = new DatabaseVersionUpgradePair[] {
            new DatabaseVersionUpgradePair(4, 0, new CreateTableUpgrade(wantedDdl, "database_version")),
            new DatabaseVersionUpgradePair(4, 1, new UnicodeUpgrade(wantedDdl)),
            new DatabaseVersionUpgradePair(4, 2, new CompositeUpgrade(
                    new ColumnSizeUpgrade(wantedDdl, "categories", "name", 128),
                    new ColumnSizeUpgrade(wantedDdl, "category_types", "name", 128))
            ),
            new DatabaseVersionUpgradePair(4, 3, new CreateTableUpgrade(wantedDdl, "document_properties")),
    };
    private File scriptPath;

    public StartupDatabaseUpgrade(org.apache.ddlutils.model.Database ddl, File scriptPath) {
        super(ddl);
        this.scriptPath = scriptPath;
    }

    public void upgrade(Database database) throws DatabaseException {
        DatabaseVersion databaseVersion = getDatabaseVersion(database);
        if ( null == databaseVersion ) {
            int tableCount = (Integer) database.execute(new DdlUtilsSqlBuilderCommand() {
                protected Object executeSqlBuilder(DatabaseConnection databaseConnection,
                                                   SqlBuilder sqlBuilder) {
                    org.apache.ddlutils.model.Database database = sqlBuilder.getPlatform().readModelFromDatabase(databaseConnection.getConnection(), null);
                    return new Integer(database.getTableCount());
                }
            });
            if ( 0 == tableCount ) {
                createDatabase(database);
                return;
            }
            databaseVersion = new DatabaseVersion(0,0);
        }
        upgradeDatabase(databaseVersion, database);
    }

    private void upgradeDatabase(DatabaseVersion databaseVersion, Database database) {
        LOG.info("Database is version "+databaseVersion) ;
        if (getLastDatabaseVersion().compareTo(databaseVersion) > 0 ) {
            for ( DatabaseVersionUpgradePair versionUpgradePair : upgrades ) {
                DatabaseVersion upgradeVersion = versionUpgradePair.getVersion();
                if ( upgradeVersion.compareTo(databaseVersion) > 0 ) {
                    LOG.info("Upgrading database to version "+upgradeVersion);
                    versionUpgradePair.getUpgrade().upgrade(database);
                    setDatabaseVersion(database, upgradeVersion);
                    databaseVersion = upgradeVersion ;
                }
            }
            LOG.info("Database upgraded to version "+databaseVersion);
        }
    }

    private void setDatabaseVersion(Database database, DatabaseVersion upgradeVersion) {
        Integer rowsUpdated = (Integer) database.execute(new SqlUpdateCommand("UPDATE database_version SET major = ?, minor = ?",
                                                                              new Object[] {
                                                                                      upgradeVersion.getMajorVersion(),
                                                                                      upgradeVersion.getMinorVersion() }));
        if (0 == rowsUpdated) {
            database.execute(new InsertIntoTableDatabaseCommand("database_version", new Object[][] {
                    { "major", upgradeVersion.getMajorVersion() },
                    { "minor", upgradeVersion.getMinorVersion() },
            })) ;
        }
    }

    private void createDatabase(Database database) {
        DatabaseVersion lastDatabaseVersion = getLastDatabaseVersion();
        database.execute(new CompositeDatabaseCommand(
                new DatabaseCommand[] {
                        new DdlUtilsPlatformCommand() {
                            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                                CreationParameters params = new CreationParameters();
                                params.addParameter(null, "ENGINE", "InnoDB");
                                params.addParameter(null, "CHARACTER SET", "UTF8");
                                platform.createTables(wantedDdl, params, false, false);
                                return null ;
                            }
                        },
                        new DdlUtilsPlatformCommand() {
                            protected Object executePlatform(DatabaseConnection databaseConnection,
                                                             Platform platform) {
                                String sql;
                                try {
                                    sql = getSqlScript("types.sql")
                                          + getSqlScript("newdb.sql");
                                } catch ( IOException e ) {
                                    throw new RuntimeException(e);
                                }
                                sql = massageSql(platform, sql);
                                platform.evaluateBatch(databaseConnection.getConnection(), sql, false) ;
                                return null ;
                            }
                        },
                        new InsertIntoTableDatabaseCommand("database_version", new Object[][] {
                                { "major", lastDatabaseVersion.getMajorVersion() },
                                { "minor", lastDatabaseVersion.getMinorVersion() },
                        })
                }
        ));
    }

    private String massageSql(Platform platform, String sql) {
        String platformName = platform.getName().toLowerCase();
        sql = Pattern.compile(
                "^-- " + platformName + " ", Pattern.MULTILINE).matcher(sql).replaceAll("");
        sql = Pattern.compile("^-- \\w+ (.*?)\n", Pattern.MULTILINE).matcher(sql).replaceAll("");
        String language = Locale.getDefault().getISO3Language() ;
        if ( StringUtils.isBlank(language)) {
            language = "eng" ;
        }
        sql = sql.replaceAll("@language@", language);
        sql = sql.replaceAll("@headline@", new LocalizedMessage("start_document/headline").toLocalizedString(language)) ;
        sql = sql.replaceAll("@text1@", new LocalizedMessage("start_document/text1").toLocalizedString(language)) ;
        sql = sql.replaceAll("@text2@", new LocalizedMessage("start_document/text2").toLocalizedString(language)) ;
        return sql;
    }

    private String getSqlScript(String scriptName) throws IOException {
        File file = new File(scriptPath,scriptName);
        return IOUtils.toString(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
    }

    private DatabaseVersion getLastDatabaseVersion() {
        return upgrades[upgrades.length - 1].getVersion();
    }

    private DatabaseVersion getDatabaseVersion(Database database) {
        SqlQueryCommand sqlQueryCommand = new SqlQueryCommand("SELECT major, minor FROM database_version", new Object[0], new SingleObjectHandler(new RowTransformer() {
            public Object createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
                int major = resultSet.getInt("major");
                int minor = resultSet.getInt("minor");
                return new DatabaseVersion(major, minor);
            }

            public Class getClassOfCreatedObjects() {
                return DatabaseVersion.class;
            }
        }));
        try {
            return (DatabaseVersion) database.execute(sqlQueryCommand);
        } catch ( DatabaseException dbe ) {
            for ( Throwable t = dbe; null != t; t = t.getCause() ) {
                if ( t instanceof SQLException ) {
                    for ( SQLException se = (SQLException) t; null != se; se = se.getNextException() ) {
                        if ( SQL_STATE__MISSING_TABLE.equals(se.getSQLState()) ) {
                            return null;
                        }
                    }
                }
            }
            throw dbe;

        }
    }
}
