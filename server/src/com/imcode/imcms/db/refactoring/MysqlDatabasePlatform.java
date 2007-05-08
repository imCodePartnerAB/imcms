package com.imcode.imcms.db.refactoring;

import com.imcode.db.Database;
import com.imcode.imcms.db.refactoring.model.Column;
import com.imcode.imcms.db.refactoring.model.ForeignKey;
import com.imcode.imcms.db.refactoring.model.ForeignKeyHasLocalColumnName;
import com.imcode.imcms.db.refactoring.model.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MysqlDatabasePlatform extends DatabasePlatform {

    public MysqlDatabasePlatform(Database database) {
        super(database);
    }

    public void dropForeignKey(ForeignKey foreignKey) {
        update("ALTER TABLE " + foreignKey.getLocalTableName() + " DROP FOREIGN KEY " + foreignKey.getName());
    }

    public void alterColumn(Table table, final String columnName, final Column column) {
        List<ForeignKey> foreignKeysWithLocalColumnName = new ArrayList();
        CollectionUtils.select(table.getForeignKeys(), new ForeignKeyHasLocalColumnName(columnName), foreignKeysWithLocalColumnName);
        dropForeignKeys(foreignKeysWithLocalColumnName);
        update("ALTER TABLE "+table.getName() +" CHANGE COLUMN "+columnName+" "+createColumnDefinition(column));
    }

    protected String createColumnDefinition(Column column) {
        List<String> columnDefinition = new ArrayList();
        columnDefinition.add(column.getName());
        columnDefinition.add(getTypeString(column));
        columnDefinition.add(column.isNullable() ? "NULL" : "NOT NULL") ;
        if (column.hasDefault()) {
            columnDefinition.add("DEFAULT "+column.getDefaultValue()) ;
        } else if (column.isAutoIncremented()) {
            columnDefinition.add("AUTO_INCREMENT") ;
        }
        return StringUtils.join(columnDefinition.iterator(), " ");
    }

    public void createTable(Table table) {
        super.createTable(table);
    }

    protected String createTableDefinition(Table table) {
        return super.createTableDefinition(table)+" ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}
