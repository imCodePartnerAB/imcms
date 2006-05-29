package com.imcode.imcms.db;

import com.imcode.db.Database;

public interface DatabaseUpgrade {

    void upgrade(Database database) throws UpgradeException;

}
