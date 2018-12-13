package com.imcode.imcms.db;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StringArrayResultSetHandler implements ResultSetHandler<String[]> {

    public String[] handle(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        List<String> results = new ArrayList<>();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String s = resultSet.getString(i);
                results.add(s);
            }
        }
        return results.toArray(new String[results.size()]);
    }
}
