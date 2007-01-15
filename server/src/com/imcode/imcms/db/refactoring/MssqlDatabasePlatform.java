package com.imcode.imcms.db.refactoring;

import com.imcode.db.Database;
import com.imcode.imcms.db.refactoring.model.Column;
import com.imcode.imcms.db.refactoring.model.ForeignKey;
import com.imcode.imcms.db.refactoring.model.Table;
import com.imcode.imcms.db.refactoring.model.TableWrapper;
import com.imcode.imcms.db.refactoring.model.ForeignKeyHasLocalColumnName;
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

    private void copyTable(String tableName, String temporaryTableName) {
        update("INSERT INTO "+temporaryTableName+" SELECT * FROM "+tableName);
    }

    private void createTable(Table table) {
        update("CREATE TABLE "+table.getName()+" ( "+createTableDefinition(table) +" )");
    }

    private String createTableDefinition(Table table) {
        List<String> tableDefinition = new ArrayList<String>();
        tableDefinition.add(createColumnDefinitions(table.getColumns()));
        List<String> primaryKeyColumnNames = new ArrayList<String>();
        for ( Column column : table.getColumns() ) {
            if (column.isPrimaryKey()) {
                primaryKeyColumnNames.add(column.getName()) ;
            }
        }
        if (!primaryKeyColumnNames.isEmpty()) {
            tableDefinition.add("PRIMARY KEY ( "+StringUtils.join(primaryKeyColumnNames.iterator(), ", ")+" )");
        }
        for ( ForeignKey foreignKey : table.getForeignKeys() ) {
            tableDefinition.add(createForeignKeyDefinition(foreignKey));
        }
        return StringUtils.join(tableDefinition.iterator(), ", ");
    }

    private String createColumnDefinitions(Collection<Column> columns) {
        List<String> columnDefinitions =  new ArrayList<String>();
        for ( Column column : columns ) {
            columnDefinitions.add(createColumnDefinition(column));
        }
        return StringUtils.join(columnDefinitions.iterator(), ", ");
    }

    private String createColumnDefinition(Column column) {
        List<String> columnDefinition = new ArrayList<String>();
        columnDefinition.add(column.getName());
        columnDefinition.add(getTypeString(column));
        columnDefinition.add(column.isNullable() ? "NULL" : "NOT NULL") ;
        if (column.isAutoIncremented()) {
            columnDefinition.add("IDENTITY");
        }
        return StringUtils.join(columnDefinition.iterator(), " ");
    }

    private String getTypeString(Column column) {
        String typeString = null;
        switch(column.getType()) {
            case INTEGER:
                typeString = "INTEGER";
                break;
            case VARCHAR:
                typeString = "NVARCHAR("+column.getSize()+")";
                break;
        }
        return typeString;
    }

}
