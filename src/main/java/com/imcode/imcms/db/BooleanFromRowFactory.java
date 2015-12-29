package com.imcode.imcms.db;

/**
 * Created by zemluk on 23.12.15.
 */

import com.imcode.db.handlers.ObjectFromRowFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanFromRowFactory implements ObjectFromRowFactory {

    private final int columnIndex;

    public BooleanFromRowFactory() {
        this(1);
    }

    public BooleanFromRowFactory(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Object createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
        return resultSet.getBoolean(columnIndex);
    }

    public Class getClassOfCreatedObjects() {
        return boolean.class;
    }
}
