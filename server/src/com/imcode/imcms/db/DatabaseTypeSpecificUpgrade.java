package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;

import javax.sql.DataSource;
import java.sql.Connection;

import org.apache.ddlutils.PlatformUtils;
import org.apache.ddlutils.platform.mssql.MSSqlPlatform;
import org.apache.ddlutils.platform.mysql.MySqlPlatform;

abstract class DatabaseTypeSpecificUpgrade extends ImcmsDatabaseUpgrade {

    protected DatabaseTypeSpecificUpgrade(org.apache.ddlutils.model.Database ddl) {
        super(ddl);
    }

    public void upgrade(final Database database) throws UpgradeException {
        try {
            String databaseName = (String) database.execute(new DatabaseCommand() {
                public Object executeOn(DatabaseConnection databaseConnection) throws DatabaseException {
                    final Connection connection = databaseConnection.getConnection();
                    DataSource dataSource = new SingleConnectionDataSource(connection);
                    PlatformUtils platformUtils = new PlatformUtils();
                    return platformUtils.determineDatabaseType(dataSource);
                }
            });
            if ( MSSqlPlatform.DATABASENAME.equals(databaseName)) {
                upgradeMssql(database);
            } else if ( MySqlPlatform.DATABASENAME.equals(databaseName) ) {
                upgradeMysql(database);
            } else {
                upgradeOther(database);
            }
        } catch (RuntimeException re) {
            throw new UpgradeException(re) ;
        }
    }

    public abstract void upgradeOther(Database database) throws UpgradeException;

    public abstract void upgradeMssql(Database database) throws UpgradeException;

    public abstract void upgradeMysql(Database database) throws UpgradeException;

}
