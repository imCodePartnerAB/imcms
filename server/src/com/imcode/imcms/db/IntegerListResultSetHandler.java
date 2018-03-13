package com.imcode.imcms.db;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.03.18.
 */
public class IntegerListResultSetHandler implements ResultSetHandler<List<Integer>> {
    @Override
    public List<Integer> handle(ResultSet resultSet) throws SQLException {
        final int columnCount = resultSet.getMetaData().getColumnCount();
        final List<Integer> results = new ArrayList<>();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                int result = resultSet.getInt(i);
                results.add(result);
            }
        }
        return results;
    }
}
