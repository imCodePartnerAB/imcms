package com.imcode.imcms.api;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseService {

    private DataSource dataSource;

    public DatabaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get a databaseconnection from the connectionpool.
     *
     * <strong>IMPORTANT</strong>: Do not forget to make sure the connection is closed,
     * otherwise it won't be returned to the pool.
     *
     * @return a databaseconnection from the connectionpool.
     * @throws SQLException if there was a problem getting the connection.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection() ;
    }
    
    /**
     * @return A {@link DataSource}.
     */
    public DataSource getDataSource() {
        return dataSource ;
    }
}
