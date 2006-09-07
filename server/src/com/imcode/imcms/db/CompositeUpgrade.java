package com.imcode.imcms.db;

import com.imcode.db.Database;
import com.imcode.db.DatabaseException;

import java.util.ArrayList;
import java.util.List;

public class CompositeUpgrade implements DatabaseUpgrade {

    List<DatabaseUpgrade> upgrades = new ArrayList() ;

    public CompositeUpgrade(DatabaseUpgrade upgrade1, DatabaseUpgrade upgrade2) {
        upgrades.add(upgrade1) ;
        upgrades.add(upgrade2) ;
    }

    public void upgrade(Database database) throws DatabaseException {
        for ( DatabaseUpgrade upgrade : upgrades ) {
            upgrade.upgrade(database);
        }
    }
}
