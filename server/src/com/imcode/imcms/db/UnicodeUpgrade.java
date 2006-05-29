package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.SingleConnectionDatabase;
import com.imcode.db.commands.SqlUpdateCommand;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.platform.SqlBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

class UnicodeUpgrade extends DatabaseTypeSpecificUpgrade {

    private final static Logger LOG = Logger.getLogger(UnicodeUpgrade.class);
    
    protected UnicodeUpgrade(org.apache.ddlutils.model.Database ddl) {
        super(ddl);
    }

    public void upgradeOther(Database database) throws UpgradeException {
        throw new DatabaseNotSupportedException();
    }

    public void upgradeMssql(Database database) throws UpgradeException {

        database.execute(new DatabaseCommand() {
            public Object executeOn(DatabaseConnection databaseConnection) throws DatabaseException {
                Platform platform = DatabaseUtils.getPlatform(databaseConnection);
                org.apache.ddlutils.model.Database actualDdl = platform.readModelFromDatabase(databaseConnection.getConnection(), null);
                Database db = new SingleConnectionDatabase(databaseConnection);
                SqlBuilder sqlBuilder = platform.getSqlBuilder();
                for ( Table actualTable : actualDdl.getTables() ) {
                    try {
                        Table wantedTable = ddl.findTable(actualTable.getName());
                        if (null != wantedTable) {
                            String temporaryTableName = "Tmp_" + actualTable.getName();
                            Table existingTemporaryTable = actualDdl.findTable(temporaryTableName);
                            if (null != existingTemporaryTable) {
                                dropTable(db, sqlBuilder, existingTemporaryTable);
                            }
                            renameTable(db, actualTable, temporaryTableName);
                            createTable(db, sqlBuilder, wantedTable, ddl);
                            copyTableData(db, actualTable, wantedTable);
                            dropTable(db, sqlBuilder, actualTable);
                        }
                    } catch ( IOException e ) {
                        throw new DatabaseException(null, e);
                    }
                }
                return null;
            }

        }) ;
    }

    private void renameTable(Database db, Table table, String newName) {
        LOG.info("Renaming table "+table.getName()+" to "+newName);
        db.execute(new SqlUpdateCommand("EXEC sp_rename ?, ?", new String[] { table.getName(), newName })) ;
        table.setName(newName);
    }

    private void copyTableData(Database db, Table sourceTable, Table targetTable) {
        LOG.info("Copying contents of table "+sourceTable.getName()+" to "+targetTable.getName());
        String columnNames = StringUtils.join(CollectionUtils.collect(Arrays.asList(sourceTable.getColumns()), new Transformer() {
            public Object transform(Object input) {
                Column column = (Column) input ;
                return column.getName() ;
            }
        }).iterator(), ", ") ;
        try {
            db.execute(new SqlUpdateCommand("INSERT INTO "+targetTable.getName()+ " ("+columnNames+")"
                                            + " SELECT "+columnNames+" FROM "+sourceTable.getName(),
                                            new Object[0])) ;
        } catch ( DatabaseException e ) {
            db.execute(new SqlUpdateCommand("SET IDENTITY_INSERT "+targetTable.getName()+" ON"
                                            + " INSERT INTO "+targetTable.getName()+ " ("+columnNames+")"
                                            + " SELECT "+columnNames+" FROM "+sourceTable.getName()
                                            + " SET IDENTITY_INSERT "+targetTable.getName()+" OFF",
                                            new Object[0])) ;
        }
    }

    private void createTable(Database db, SqlBuilder sqlBuilder, Table table,
                             org.apache.ddlutils.model.Database wantedDdl) throws IOException {
        LOG.info("Creating table "+table.getName());
        String createTableSql = getCreateTableSql(sqlBuilder, table, wantedDdl);
        db.execute(new SqlUpdateCommand(createTableSql, new Object[0])) ;
    }

    private void dropTable(Database db, SqlBuilder sqlBuilder, Table table) throws IOException {
        LOG.info("Dropping table "+table.getName());
        db.execute(new SqlUpdateCommand(getDropTableSql(sqlBuilder, table), new Object[0])) ;
    }

    private String getDropTableSql(SqlBuilder sqlBuilder, Table table) throws IOException {
        StringWriter writer = new StringWriter();
        sqlBuilder.setWriter(writer);
        sqlBuilder.dropTable(table);
        return writer.toString();
    }

    private String getCreateTableSql(SqlBuilder sqlBuilder, Table temporaryTable,
                                     org.apache.ddlutils.model.Database wantedDdl) throws IOException {
        StringWriter writer = new StringWriter();
        sqlBuilder.setWriter(writer);
        sqlBuilder.createTable(wantedDdl, temporaryTable);
        return writer.toString();
    }

    public void upgradeMysql(Database database) throws UpgradeException {
        Table[] tables = ddl.getTables();
        for ( Table table : tables ) {
            database.execute(new SqlUpdateCommand("ALTER TABLE " + table.getName() + " CHARACTER SET UTF8", new Object[0]));
        }
    }
}
