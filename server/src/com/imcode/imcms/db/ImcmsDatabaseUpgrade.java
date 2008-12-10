package com.imcode.imcms.db;

import org.apache.ddlutils.model.Database;

public abstract class ImcmsDatabaseUpgrade implements DatabaseUpgrade {

    protected Database wantedDdl;

    protected ImcmsDatabaseUpgrade(Database ddl) {
        this.wantedDdl = ddl;
    }
}
