package com.imcode.imcms.db.refactoring.model;

import java.util.Collection;

public class TableWrapper implements Table {

    private Table wrappedTable ;

    public TableWrapper(Table wrappedTable) {
        this.wrappedTable = wrappedTable;
    }

    public String getName() {
        return wrappedTable.getName();
    }

    public Collection<Column> getColumns() {
        return wrappedTable.getColumns();
    }

    public Collection<ForeignKey> getForeignKeys() {
        return wrappedTable.getForeignKeys();
    }

}
