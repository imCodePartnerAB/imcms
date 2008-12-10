package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;

public interface DatabaseUpgrade {

    void upgrade(Database database) throws DatabaseException;

}
