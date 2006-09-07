package com.imcode.imcms.db;

import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import org.apache.ddlutils.alteration.AddTableChange;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.SqlBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTableUpgrade extends ImcmsDatabaseUpgrade {

    private final String tableName;

    public CreateTableUpgrade(Database wantedDdl, String tableName) {
        super(wantedDdl);
        this.tableName = tableName;
    }

    public void upgrade(com.imcode.db.Database database) throws DatabaseException {
        database.execute(new DdlUtilsSqlBuilderCommand() {
            protected Object executeSqlBuilder(DatabaseConnection databaseConnection,
                                               SqlBuilder sqlBuilder) throws IOException {
                final Database actualDdl = sqlBuilder.getPlatform().readModelFromDatabase(databaseConnection.getConnection(), null);
                Table table = wantedDdl.findTable(tableName);
                if ( null == table ) {
                    throw new DatabaseException("Table " + tableName + " missing from ddl", null);
                }
                if ( null != actualDdl.findTable(tableName) ) {
                    return null ;
                }
                final List<ModelChange> changes = new ArrayList();
                changes.add(new AddTableChange(table));
                sqlBuilder.processChanges(actualDdl, wantedDdl, changes, null);
                return null;
            }
        });

    }
}
