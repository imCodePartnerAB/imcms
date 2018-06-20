package com.imcode.imcms.db;

import com.imcode.db.handlers.RowTransformer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanFromRowFactory implements RowTransformer<Boolean> {

    private final int columnIndex;

    public BooleanFromRowFactory() {
        this(1);
    }

    public BooleanFromRowFactory(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public Boolean createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
        return resultSet.getBoolean(columnIndex);
    }

    public Class<Boolean> getClassOfCreatedObjects() {
        return boolean.class;
    }
}
