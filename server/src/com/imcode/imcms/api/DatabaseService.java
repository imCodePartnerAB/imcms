package com.imcode.imcms.api;

import imcode.server.db.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseService {

    private ConnectionPool myOwnConnectionPool;

    public DatabaseService( ConnectionPool apiConnectionPool ) {
        this.myOwnConnectionPool = apiConnectionPool;
    }

    /**
     * Get a databaseconnection from the connectionpool.
     *
     * <strong>IMPORTANT</strong>: Do not forget to make sure the connection is closed,
     * else is won't be returned to the pool.
     *
     * @return a databaseconnection from the connectionpool.

     * @throws SQLException if there was a problem getting the connection.
     */

    public Connection getConnection() throws SQLException {
        return myOwnConnectionPool.getConnection() ;
    }
}
