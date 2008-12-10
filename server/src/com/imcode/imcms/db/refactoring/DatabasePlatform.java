package com.imcode.imcms.db.refactoring;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.db.DdlUtilsPlatformCommand;
import com.imcode.imcms.db.refactoring.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.platform.mssql.MSSqlPlatform;
import org.apache.ddlutils.platform.mysql.MySqlPlatform;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

public abstract class DatabasePlatform {

    private final static Logger LOG = Logger.getLogger(DatabasePlatform.class);
    protected final Database database;
    private static final String OBJECT_DOES_NOT_EXIST = "42S02";

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
        return getModel().getTable(tableName);
    }

    private DdlUtilsDatabaseModel getModel() {
        return new DdlUtilsDatabaseModel((org.apache.ddlutils.model.Database) database.execute(new DdlUtilsPlatformCommand() {
            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                return platform.readModelFromDatabase(null);
            }
        }));
    }

    protected void dropForeignKeys(Iterable<ForeignKey> foreignKeys) {
        for ( ForeignKey foreignKey : foreignKeys ) {
            dropForeignKey(foreignKey);
        }
    }

    public void dropDatabase(String databaseName) {
        try {
            update("DROP DATABASE "+databaseName) ;
        } catch ( DatabaseException e ) {
            String sqlState = getSqlState(e);
            if ( !OBJECT_DOES_NOT_EXIST.equals(sqlState) ) {
                throw e;
            }
        }
    }

    private String getSqlState(DatabaseException e) {
        for (Throwable t = e; null != t; t = t.getCause()) {
            if (t instanceof SQLException ) {
                for (SQLException se = (SQLException) t; null != se; se=se.getNextException()) {
                    if (null != se.getSQLState()) {
                        return se.getSQLState();
                    }
                }
            }
        }
        return null;
    }

    public void createDatabase(String databaseName) {
        update("CREATE DATABASE "+databaseName);
    }

    public void createTable(Table table) {
        update(createTableDefinition(table));
    }

    protected String createTableDefinition(Table table) {
        return "CREATE TABLE "+table.getName()+" ( "+createTableDefinitionContent(table) +" )";
    }

    private String createTableDefinitionContent(Table table) {
        List<String> tableDefinition = new ArrayList<String>();
        tableDefinition.add(createColumnDefinitions(table.getColumns()));
        List<String> primaryKeyColumnNames = new ArrayList<String>();
        for ( Column column : table.getColumns() ) {
            if (column.isPrimaryKey()) {
                primaryKeyColumnNames.add(column.getName()) ;
            }
        }
        if (!primaryKeyColumnNames.isEmpty()) {
            tableDefinition.add(createPrimaryKeyDefinition(primaryKeyColumnNames));
        }
        for ( ForeignKey foreignKey : table.getForeignKeys() ) {
            tableDefinition.add(createForeignKeyDefinition(foreignKey));
        }
        return StringUtils.join(tableDefinition.iterator(), ", ");
    }

    private String createPrimaryKeyDefinition(List<String> primaryKeyColumnNames) {
        return "PRIMARY KEY ( "+ StringUtils.join(primaryKeyColumnNames.iterator(), ", ")+" )";
    }

    protected String createForeignKeyDefinition(ForeignKey foreignKey) {
        String name = foreignKey.getName();
        if (null == name) {
            name = "FK__"+foreignKey.getLocalTableName()+"__"+foreignKey.getForeignTableName();
        }
        return "CONSTRAINT "+name
               +" FOREIGN KEY ( "
               + StringUtils.join(foreignKey.getLocalColumnNames().iterator(), ", ")
               +" ) REFERENCES "+foreignKey.getForeignTableName()+" ( "
               +StringUtils.join(foreignKey.getForeignColumnNames().iterator(), ", ")
               +" )";
    }

    private String createColumnDefinitions(Collection<Column> columns) {
        List<String> columnDefinitions =  new ArrayList<String>();
        for ( Column column : columns ) {
            columnDefinitions.add(createColumnDefinition(column));
        }
        return StringUtils.join(columnDefinitions.iterator(), ", ");
    }

    protected abstract String createColumnDefinition(Column column) ;

    protected String getTypeString(Column column) {
        String typeString = null;
        switch ( column.getType() ) {
            case INTEGER:
                typeString = "INTEGER";
                break;
            case VARCHAR:
                typeString = 0 != column.getSize() ? "VARCHAR("+column.getSize()+")" : "TEXT";
                break;
            case DATETIME:
                typeString = "DATETIME";
                break;
        }
        return typeString;
    }
}
