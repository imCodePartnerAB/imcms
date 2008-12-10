package com.imcode.imcms.db;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StringArrayArrayResultSetHandler implements ResultSetHandler {

    public Object handle( ResultSet resultSet ) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        List results = new ArrayList();
        while ( resultSet.next() ) {
            String[] row = new String[columnCount];
            for ( int i = 0; i < columnCount; i++ ) {
                row[i] = resultSet.getString( i + 1 );
            }
            results.add( row );
        }
        return (String[][])results.toArray( new String[results.size()][] );
    }
}
