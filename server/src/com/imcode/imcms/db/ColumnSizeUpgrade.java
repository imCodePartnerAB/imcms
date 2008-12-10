package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import org.apache.ddlutils.platform.SqlBuilder;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.alteration.ColumnSizeChange;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

class ColumnSizeUpgrade extends ImcmsDatabaseUpgrade {

    private String tableName;
    private String columnName;
    private int columnSize;

    protected ColumnSizeUpgrade(org.apache.ddlutils.model.Database ddl, String tableName,
                                String columnName, int columnSize) {
        super(ddl);
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnSize = columnSize;
    }

    public void upgrade(Database database) throws DatabaseException {
        database.execute(new DdlUtilsSqlBuilderCommand() {
            protected Object executeSqlBuilder(DatabaseConnection databaseConnection,
                                               SqlBuilder sqlBuilder) throws IOException {
                final org.apache.ddlutils.model.Database actualDdl = sqlBuilder.getPlatform().readModelFromDatabase(databaseConnection.getConnection(), null);
                Table actualTable = actualDdl.findTable(tableName);
                Column column = actualTable.findColumn(columnName);
                column.setSize("" + columnSize);
                final List<ModelChange> changes = new ArrayList();
                changes.add(new ColumnSizeChange(actualTable, column, columnSize, 0));
                sqlBuilder.processChanges(actualDdl, actualDdl, changes, null);
                return null ;
            }
        });
    }

}
