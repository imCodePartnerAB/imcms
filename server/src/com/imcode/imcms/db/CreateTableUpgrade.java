package com.imcode.imcms.db;

import com.imcode.db.DatabaseException;
import com.imcode.imcms.db.refactoring.DatabasePlatform;
import com.imcode.imcms.db.refactoring.model.DdlUtilsTable;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

public class CreateTableUpgrade extends ImcmsDatabaseUpgrade {

    private final String tableName;

    public CreateTableUpgrade(Database wantedDdl, String tableName) {
        super(wantedDdl);
        this.tableName = tableName;
    }

    public void upgrade(com.imcode.db.Database database) throws DatabaseException {
        DatabasePlatform databasePlatform = DatabasePlatform.getInstance(database);
        final Table table = wantedDdl.findTable(tableName);
        databasePlatform.createTable(new DdlUtilsTable(table));
    }
}
