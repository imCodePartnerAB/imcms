package com.imcode.imcms.db;

import com.imcode.db.handlers.RowTransformer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringFromRowFactory implements RowTransformer {

    private final int columnIndex;

    public StringFromRowFactory() {
        this(1);
    }

    public StringFromRowFactory(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Object createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    public Class getClassOfCreatedObjects() {
        return String.class;
    }
}
