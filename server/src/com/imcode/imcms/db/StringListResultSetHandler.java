package com.imcode.imcms.db;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StringListResultSetHandler implements ResultSetHandler<List<String>> {

    @Override
    public List<String> handle(ResultSet resultSet) throws SQLException {
        final int columnCount = resultSet.getMetaData().getColumnCount();
        final List<String> results = new ArrayList<>();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String resultString = resultSet.getString(i);
                results.add(resultString);
            }
        }
        return results;
    }

}
