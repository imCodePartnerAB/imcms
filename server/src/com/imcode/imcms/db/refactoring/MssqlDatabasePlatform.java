package com.imcode.imcms.db.refactoring;

import com.imcode.db.Database;
import com.imcode.imcms.db.refactoring.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class MssqlDatabasePlatform extends DatabasePlatform {

    public MssqlDatabasePlatform(Database database) {
        super(database);
    }

    public void dropForeignKey(ForeignKey foreignKey) {
        update("ALTER TABLE "+foreignKey.getLocalTableName()+" DROP CONSTRAINT "+foreignKey.getName());
    }

    public void alterColumn(Table table, final String columnName, final Column column) {
        final String tableName = table.getName();
        final String temporaryTableName = tableName+"_tmp";
        TableWrapper newTable = new TableWrapper(table) {
            public String getName() {
                return temporaryTableName;
            }

            public Collection<Column> getColumns() {
                return CollectionUtils.collect(super.getColumns(), new Transformer() {
                    public Object transform(Object input) {
                        Column c = (Column) input;
                        if ( columnName.equals(c.getName()) ) {
                            return column;
                        } else {
                            return c;
                        }
                    }
                });
            }

            public Collection<ForeignKey> getForeignKeys() {
                return CollectionUtils.select(super.getForeignKeys(), new NotPredicate(new ForeignKeyHasLocalColumnName(columnName))) ;
            }

        };
        dropForeignKeys(table.getForeignKeys());
        createTable(newTable);
        copyTable(tableName, temporaryTableName);
        dropTable(tableName);
        renameTable(temporaryTableName, tableName);
    }

    private void renameTable(String oldName, String newName) {
        update("sp_rename '"+oldName+"', '"+newName+"'");
    }

    public void copyTable(String tableName, String temporaryTableName) {
        update("INSERT INTO "+temporaryTableName+" SELECT * FROM "+tableName);
    }

    protected String createColumnDefinition(Column column) {
        List<String> columnDefinition = new ArrayList<String>();
        columnDefinition.add(column.getName());
        columnDefinition.add(getTypeString(column));
        columnDefinition.add(column.isNullable() ? "NULL" : "NOT NULL") ;
        if (column.isAutoIncremented()) {
            columnDefinition.add("IDENTITY");
        }
        return StringUtils.join(columnDefinition.iterator(), " ");
    }

    protected String getTypeString(Column column) {
        String typeString = super.getTypeString(column);
        if (column.getType() == Type.VARCHAR) {
            typeString = "N"+typeString;
        }
        return typeString;
    }

}
