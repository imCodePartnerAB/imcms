package com.imcode.imcms.db;

import com.imcode.db.DatabaseException;

public class DatabaseNotSupportedException extends DatabaseException {

    public DatabaseNotSupportedException(String string, Throwable throwable) {
        super(string, throwable);
    }
}
