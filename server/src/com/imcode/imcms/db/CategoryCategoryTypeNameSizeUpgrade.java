package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;
import org.apache.log4j.Logger;


class CategoryCategoryTypeNameSizeUpgrade extends ImcmsDatabaseUpgrade {

    private final static Logger LOG = Logger.getLogger(CategoryCategoryTypeNameSizeUpgrade.class);
    private final static String[] TABLES_TO_UPDATE = { "categories", "category_types" };
    private final static String COLUMN_NAME= "name";
     private final static String NEW_COLUMN_SIZE= "128";


    protected CategoryCategoryTypeNameSizeUpgrade(org.apache.ddlutils.model.Database ddl) {
        super(ddl);
    }


    public void upgrade(Database database) throws UpgradeException {

        database.execute(new DatabaseCommand() {
            public Object executeOn(DatabaseConnection databaseConnection) throws DatabaseException {
                Platform platform = DatabaseUtils.getPlatform(databaseConnection);
                org.apache.ddlutils.model.Database actualDdl = platform.readModelFromDatabase(databaseConnection.getConnection(), null);
                for ( int i = 0 ; i < TABLES_TO_UPDATE.length; i++ ) {
                    try {
                        Table actualTable = actualDdl.findTable(TABLES_TO_UPDATE[i]);
                        Column column = actualTable.findColumn(COLUMN_NAME);
                        String tableName = actualTable.getName();
                        column.setSize(NEW_COLUMN_SIZE);
                        LOG.info("Alter table " + tableName + " , set new size on column name: " + COLUMN_NAME + " to " + column.getSize());
                        platform.alterTables(actualDdl, false, true, false);
                    } catch ( Exception e) {
                        LOG.error("Exception when try to change size on column:name in table categories and category_types " + e);
                        throw new DatabaseException(null, e);
                    }
                }
                return null;
            }

        }) ;
    }


}
