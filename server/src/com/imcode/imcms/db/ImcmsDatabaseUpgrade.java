package com.imcode.imcms.db;

import org.apache.ddlutils.model.Database;

public abstract class ImcmsDatabaseUpgrade implements DatabaseUpgrade {

    protected org.apache.ddlutils.model.Database ddl;

    protected ImcmsDatabaseUpgrade(Database ddl) {
        this.ddl = ddl;
    }
}
