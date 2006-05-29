package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.SingleConnectionDatabase;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.db.handlers.SingleObjectHandler;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.SqlBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StartupDatabaseUpgrade extends ImcmsDatabaseUpgrade {

    private static final String SQL_STATE__MISSING_TABLE = "42S02";

    public StartupDatabaseUpgrade(org.apache.ddlutils.model.Database ddl) {
        super(ddl) ;
    }

    DatabaseVersionUpgradePair[] upgrades = new DatabaseVersionUpgradePair[] {
            new DatabaseVersionUpgradePair(4,1, new UnicodeUpgrade(ddl)) 
    };

    public void upgrade(Database database) throws UpgradeException {
        DatabaseVersion databaseVersion = getDatabaseVersion(database);
        if (null == databaseVersion) {
            database.execute(new InsertIntoTableDatabaseCommand("database_version", new Object[][] {
                    { "major", new Integer(0) },
                    { "minor", new Integer(0) }
            }));
            databaseVersion = getDatabaseVersion(database);
        }
        for ( DatabaseVersionUpgradePair versionUpgradePair : upgrades ) {
            DatabaseVersion upgradeVersion = versionUpgradePair.getVersion();
            if ( upgradeVersion.compareTo(databaseVersion) > 0 ) {
                versionUpgradePair.getUpgrade().upgrade(database);
                database.execute(new SqlUpdateCommand("UPDATE database_version SET major = ?, minor = ?", 
                                                      new Object[] { 
                                                              upgradeVersion.getMajorVersion(), 
                                                              upgradeVersion.getMinorVersion()})) ;
            }
        }
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
                            database.execute(new DatabaseCommand() {
                                public Object executeOn(
                                        DatabaseConnection databaseConnection) throws DatabaseException {
                                    Platform platform = DatabaseUtils.getPlatform(databaseConnection);
                                    Table table = ddl.findTable("database_version");
                                    if (null == table) {
                                        throw new DatabaseException("database_version table missing from ddl", null) ;
                                    }
                                    SqlBuilder sqlBuilder = platform.getSqlBuilder();
                                    StringWriter writer = new StringWriter();
                                    sqlBuilder.setWriter(writer);
                                    try {
                                        sqlBuilder.createTable(ddl, table);
                                    } catch ( IOException e ) {
                                        throw new DatabaseException(null, e);
                                    }
                                    SingleConnectionDatabase db = new SingleConnectionDatabase(databaseConnection);
                                    db.execute(new SqlUpdateCommand(writer.toString(), new Object[0]));
                                    return null;

                                }
                            });
                            return (DatabaseVersion) database.execute(sqlQueryCommand);
                        }
                    }
                }
            }
            throw dbe;

        }
    }
}
