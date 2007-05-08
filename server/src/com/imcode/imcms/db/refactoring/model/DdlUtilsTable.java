package com.imcode.imcms.db.refactoring.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class DdlUtilsTable implements Table {

    private final org.apache.ddlutils.model.Table table;

    public DdlUtilsTable(org.apache.ddlutils.model.Table table) {
        this.table = table;
    }

    public String getName() {
        return table.getName();
    }

    public Collection<Column> getColumns() {
        return wrapColumns(table.getColumns());
    }

    public Collection<ForeignKey> getForeignKeys() {
        List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
        for ( org.apache.ddlutils.model.ForeignKey foreignKey : table.getForeignKeys() ) {
            foreignKeys.add(new DdlUtilsForeignKey(getName(), foreignKey));
        }
        return foreignKeys;
    }

    private List<Column> wrapColumns(org.apache.ddlutils.model.Column[] ddlutilsColumns) {
        List<Column> columns = new ArrayList<Column>();
        for ( org.apache.ddlutils.model.Column column : ddlutilsColumns ) {
            columns.add(new DdlUtilsColumn(column));
        }
        return columns;
    }

}
