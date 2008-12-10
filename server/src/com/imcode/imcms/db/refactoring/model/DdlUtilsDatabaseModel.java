package com.imcode.imcms.db.refactoring.model;

import org.apache.ddlutils.model.Database;

public class DdlUtilsDatabaseModel implements DatabaseModel {

    private Database model;

    public DdlUtilsDatabaseModel(Database model) {
        this.model = model ;
    }

    public Table getTable(String tableName) {
        return new DdlUtilsTable(model.findTable(tableName));
    }
}
