package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import com.imcode.db.commands.SqlUpdateCommand;
import com.imcode.imcms.db.refactoring.DatabaseNotSupportedException;
import org.apache.ddlutils.alteration.*;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.platform.SqlBuilder;
import org.apache.ddlutils.platform.CreationParameters;
import org.apache.ddlutils.Platform;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class UnicodeUpgrade extends DatabaseTypeSpecificUpgrade {

    private final static Logger LOG = Logger.getLogger(UnicodeUpgrade.class);
    
    protected UnicodeUpgrade(org.apache.ddlutils.model.Database ddl) {
        super(ddl);
    }

    public void upgradeOther(Database database) throws DatabaseException {
        throw new DatabaseNotSupportedException();
    }

    public void upgradeMssql(Database database) throws DatabaseException {
        rebuildTextTables(database);
    }

    private void rebuildTextTables(Database database) {
        database.execute(new DdlUtilsSqlBuilderCommand() {
            protected Object executeSqlBuilder(DatabaseConnection databaseConnection,
                                               SqlBuilder sqlBuilder) throws IOException {
                org.apache.ddlutils.model.Database sourceDdl = sqlBuilder.getPlatform().readModelFromDatabase(databaseConnection.getConnection(), null);
                List<ModelChange> changes = new ArrayList() ;
                for ( Table sourceTable : sourceDdl.getTables() ) {
                    if (null == wantedDdl.findTable(sourceTable.getName())) {
                        continue;
                    }
                    for ( Column column : sourceTable.getColumns() ) {
                        if (column.isOfTextType()) {
                            LOG.debug("Rebuilding table "+sourceTable) ;
                            changes.add(new ColumnOrderChange(sourceTable, Collections.EMPTY_MAP)) ;
                            break ;
                        }
                    }
                }
                sqlBuilder.processChanges(sourceDdl, sourceDdl, changes, new CreationParameters());
                return null ;
            }
        }) ;
    }

    public void upgradeMysql(Database database) throws DatabaseException {
        Table[] tables = (Table[]) database.execute(new DdlUtilsPlatformCommand() {
            protected Object executePlatform(DatabaseConnection databaseConnection, Platform platform) {
                return platform.readModelFromDatabase(null).getTables();
            }
        });
        for ( Table table : tables ) {
            database.execute(new SqlUpdateCommand("ALTER TABLE " + table.getName() + " CONVERT TO CHARACTER SET UTF8", new Object[0]));
        }
    }
}
