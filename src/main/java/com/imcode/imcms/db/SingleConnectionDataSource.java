package com.imcode.imcms.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.imcode.db.jdbc.ConnectionWrapper;

public class SingleConnectionDataSource implements DataSource {

    private final Connection connection;

    public SingleConnectionDataSource(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() throws SQLException {
        return new ConnectionWrapper(connection) {
            public void close() throws SQLException {

            }
        };
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return null ;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null ;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    public void setLoginTimeout(int seconds) throws SQLException {
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }
}
