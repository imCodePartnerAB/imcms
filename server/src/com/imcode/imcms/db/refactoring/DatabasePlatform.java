package com.imcode.imcms.db.refactoring;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.db.DdlUtilsPlatformCommand;
import com.imcode.imcms.db.refactoring.model.Column;
import com.imcode.imcms.db.refactoring.model.DdlUtilsForeignKey;
import com.imcode.imcms.db.refactoring.model.DdlUtilsTable;
import com.imcode.imcms.db.refactoring.model.ForeignKey;
import com.imcode.imcms.db.refactoring.model.Table;
import org.apache.commons.lang.StringUtils;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.platform.mssql.MSSqlPlatform;
import org.apache.ddlutils.platform.mysql.MySqlPlatform;
import org.apache.log4j.Logger;

import java.util.Collection;

public abstract class DatabasePlatform {

    private final static Logger LOG = Logger.getLogger(DatabasePlatform.class);
    protected final Database database;

    protected DatabasePlatform(Database database) {
        this.database = database;
    }

    public void dropTable(String tableName) {
        dropForeignKeys(tableName);
        update("DROP TABLE "+tableName);
    }

    public void update(String sql) {
        LOG.trace(sql);
        database.execute(new SqlUpdateCommand(sql, null)) ;
    }

    public void dropForeignKeys(final String tableName) {
        database.execute(new DdlUtilsPlatformCommand() {
            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                org.apache.ddlutils.model.Database actualDdl = platform.readModelFromDatabase(null);
                org.apache.ddlutils.model.Table table = actualDdl.findTable(tableName);
                for ( org.apache.ddlutils.model.ForeignKey foreignKey : table.getForeignKeys() ) {
                    dropForeignKey(new DdlUtilsForeignKey(tableName, foreignKey)) ;
                }
                return null;
            }
        }) ;
    }

    public abstract void dropForeignKey(ForeignKey foreignKey) ;

    public abstract void alterColumn(Table table, String columnName, Column column) ;

    public static DatabasePlatform getInstance(final Database database) {
        return (DatabasePlatform) database.execute(new DdlUtilsPlatformCommand() {
            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                if (platform instanceof MySqlPlatform ) {
                    return new MysqlDatabasePlatform(database);
                } else if (platform instanceof MSSqlPlatform ) {
                    return new MssqlDatabasePlatform(database);
                } else {
                    throw new DatabaseNotSupportedException();
                }
            }
        });
    }

    public void addForeignKeys(Collection<ForeignKey> foreignKeys) {
        for ( ForeignKey foreignKey : foreignKeys ) {
            addForeignKey(foreignKey);
        }
    }

    public void addForeignKey(ForeignKey foreignKey) {
        update("ALTER TABLE "+foreignKey.getLocalTableName()+" ADD "+createForeignKeyDefinition(foreignKey));
    }

    public void alterColumn(String tableName, String columnName, Column column) {
        alterColumn(getTable(tableName), columnName, column);
    }

    private Table getTable(final String tableName) {
        return new DdlUtilsTable((org.apache.ddlutils.model.Table) database.execute(new DdlUtilsPlatformCommand() {
            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                return platform.readModelFromDatabase(null).findTable(tableName);
            }
        }));
    }

    public String createForeignKeyDefinition(ForeignKey foreignKey) {
        return "CONSTRAINT "+foreignKey.getName()
               +" FOREIGN KEY ( "
               + StringUtils.join(foreignKey.getLocalColumnNames().iterator(), ", ")
               +") REFERENCES "+foreignKey.getForeignTableName()+" ( "
               +StringUtils.join(foreignKey.getForeignColumnNames().iterator(), ", ")
               +" )";
    }

    protected void dropForeignKeys(Iterable<ForeignKey> foreignKeys) {
        for ( ForeignKey foreignKey : foreignKeys ) {
            dropForeignKey(foreignKey);
        }
    }

}
