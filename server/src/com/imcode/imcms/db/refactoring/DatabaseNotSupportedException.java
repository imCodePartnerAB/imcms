package com.imcode.imcms.db.refactoring;

import com.imcode.db.DatabaseException;

public class DatabaseNotSupportedException extends DatabaseException {

    public DatabaseNotSupportedException() {
        super(null, null);
    }
}
