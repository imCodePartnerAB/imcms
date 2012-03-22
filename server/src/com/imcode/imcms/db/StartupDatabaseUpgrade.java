package com.imcode.imcms.db;

import com.imcode.db.*;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.commands.TransactionDatabaseCommand;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.db.handlers.SingleObjectHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ddlutils.platform.SqlBuilder;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;

import imcode.server.Imcms;

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
            new DatabaseVersionUpgradePair(4, 4, new TemplateNamesUpgrade(new File(Imcms.getPath(), "WEB-INF/templates/text"))),
            new DatabaseVersionUpgradePair(4, 5, new CompositeUpgrade(
                   new CreateTableUpgrade(wantedDdl, "texts_history"),
                   new CreateTableUpgrade(wantedDdl, "images_history"))),
            new DatabaseVersionUpgradePair(4, 6, new CompositeUpgrade(
                   new CreateTableUpgrade(wantedDdl, "menus_history"),
                   new CreateTableUpgrade(wantedDdl, "childs_history"))),
            new DatabaseVersionUpgradePair(4,7, new CreateTableUpgrade(wantedDdl, "document_search_log")),
            new DatabaseVersionUpgradePair(4,8, new CreateTableUpgrade(wantedDdl, "profiles")),
    };
    private ImcmsDatabaseCreator imcmsDatabaseCreator ;

    public StartupDatabaseUpgrade(org.apache.ddlutils.model.Database ddl,
                                  ImcmsDatabaseCreator imcmsDatabaseCreator) {
        super(ddl);
        this.imcmsDatabaseCreator = imcmsDatabaseCreator;
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
                createDatabaseAndSetVersion(database, wantedDdl);
                return;
            }
            databaseVersion = new DatabaseVersion(0,0);
        }
        upgradeDatabase(databaseVersion, database);
    }

    private void upgradeDatabase(DatabaseVersion databaseVersion, Database database) {
        LOG.info("The current database version is " + databaseVersion);
        
        for (final DatabaseVersionUpgradePair versionUpgradePair : upgrades) {
            final DatabaseVersion upgradeVersion = versionUpgradePair.getVersion();
            if (upgradeVersion.compareTo(databaseVersion) > 0) {
                LOG.info("Upgrading database to version " + upgradeVersion);
                database.execute(new TransactionDatabaseCommand() {
                    public Object executeInTransaction(DatabaseConnection connection) throws DatabaseException {
                        SingleConnectionDatabase database = new SingleConnectionDatabase(connection);
                        versionUpgradePair.getUpgrade().upgrade(database);
                        setDatabaseVersion(database, upgradeVersion);
                        return null;
                    }
                });
                databaseVersion = upgradeVersion;
            }
        }

        runScriptBasedUpgrade(databaseVersion, database);
    }

    private void runScriptBasedUpgrade(final DatabaseVersion currentVersion, Database database) {
        database.execute(new DatabaseCommand() {
            public Object executeOn(DatabaseConnection connection) throws DatabaseException {
                SingleConnectionDatabase database = new SingleConnectionDatabase(connection);
                ScriptBasedUpgrade upgrade = new ScriptBasedUpgrade(wantedDdl, currentVersion);

                upgrade.upgrade(database);
                
                return null;
            }
        });
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

    private void createDatabaseAndSetVersion(Database database, org.apache.ddlutils.model.Database wantedDdl) {
        imcmsDatabaseCreator.createDatabase(database, wantedDdl);
        DatabaseVersion lastDatabaseVersion = getLastDatabaseVersion();
        setDatabaseVersion(database, lastDatabaseVersion);

        runScriptBasedUpgrade(lastDatabaseVersion, database);
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
