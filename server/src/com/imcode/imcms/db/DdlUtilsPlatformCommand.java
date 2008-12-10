package com.imcode.imcms.db;

import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import org.apache.ddlutils.Platform;

public abstract class DdlUtilsPlatformCommand implements DatabaseCommand {

    public Object executeOn(DatabaseConnection databaseConnection) throws DatabaseException {
        Platform platform = DatabaseUtils.getPlatform(databaseConnection);
        return executePlatform(databaseConnection, platform);
    }

    protected abstract Object executePlatform(DatabaseConnection databaseConnection, Platform platform) ;
}
