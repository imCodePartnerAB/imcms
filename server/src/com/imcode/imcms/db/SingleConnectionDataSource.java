package com.imcode.imcms.db;

import com.imcode.db.jdbc.ConnectionWrapper;
import org.apache.commons.lang.NotImplementedException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class SingleConnectionDataSource implements DataSource {

    private final Connection connection;

    public SingleConnectionDataSource(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() throws SQLException {
        return new ConnectionWrapper(connection) {
            public void close() throws SQLException {

            }

            public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new NotImplementedException();
            }

            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                throw new NotImplementedException();
            }

            public Clob createClob() throws SQLException {
                throw new NotImplementedException();
            }

            public Blob createBlob() throws SQLException {
                throw new NotImplementedException();
            }

            public NClob createNClob() throws SQLException {
                throw new NotImplementedException();
            }

            public SQLXML createSQLXML() throws SQLException {
                throw new NotImplementedException();
            }

            public boolean isValid(int timeout) throws SQLException {
                throw new NotImplementedException();
            }

            public void setClientInfo(String name, String value) throws SQLClientInfoException {
                throw new NotImplementedException();
            }

            public String getClientInfo(String name) throws SQLException {
                throw new NotImplementedException();
            }

            public Properties getClientInfo() throws SQLException {
                throw new NotImplementedException();
            }

            public void setClientInfo(Properties properties) throws SQLClientInfoException {
                throw new NotImplementedException();
            }

            public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
                throw new NotImplementedException();
            }

            public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
                throw new NotImplementedException();
            }

            public String getSchema() throws SQLException {
                return null;
            }

            public void setSchema(String schema) throws SQLException {

            }

            public void abort(Executor executor) throws SQLException {

            }

            public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

            }

            public int getNetworkTimeout() throws SQLException {
                return 0;
            }
        };


    }

    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new NotImplementedException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new NotImplementedException();
    }
}
