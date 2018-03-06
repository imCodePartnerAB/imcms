package com.imcode.imcms.db;

import com.imcode.db.handlers.RowTransformer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringFromRowFactory implements RowTransformer<String> {

    private final int columnIndex;

    public StringFromRowFactory() {
        this(1) ;
    }

    public StringFromRowFactory(int columnIndex) {
        this.columnIndex = columnIndex ;
    }

    public String createObjectFromResultSetRow(ResultSet resultSet) throws SQLException {
        return resultSet.getString(columnIndex) ;
    }

    public Class<String> getClassOfCreatedObjects() {
        return String.class;
    }
}
