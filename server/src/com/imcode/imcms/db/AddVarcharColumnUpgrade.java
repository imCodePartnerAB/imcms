package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.SqlBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class AddVarcharColumnUpgrade extends ImcmsDatabaseUpgrade {

    private Column column;

    private String previousColumnName;

    private String tableName;

    protected AddVarcharColumnUpgrade(org.apache.ddlutils.model.Database ddl,
            String tableName, String previousColumnName,
                            String name, int size, boolean required) {
        super(ddl);
        this.tableName = tableName;

        column = new Column();

        column.setType("varchar");
        column.setName(name);
        column.setSize("" + size);
        column.setRequired(required);

        this.previousColumnName = previousColumnName;
    }

    public void upgrade(Database database) throws DatabaseException {
        database.execute(new DdlUtilsSqlBuilderCommand() {
            protected Object executeSqlBuilder(DatabaseConnection databaseConnection,
                                               SqlBuilder sqlBuilder) throws IOException {
                final org.apache.ddlutils.model.Database actualDdl = sqlBuilder.getPlatform().readModelFromDatabase(databaseConnection.getConnection(), null);
                Table actualTable = actualDdl.findTable(tableName);
                Column previousColumn = actualTable.findColumn(previousColumnName);
                final List<ModelChange> changes = new ArrayList();
                AddColumnChange acc = new AddColumnChange(actualTable, column, previousColumn, null);
                acc.setAtEnd(true);
                changes.add(acc);
                sqlBuilder.processChanges(actualDdl, actualDdl, changes, null);
                return null ;
            }
        });
    }

}