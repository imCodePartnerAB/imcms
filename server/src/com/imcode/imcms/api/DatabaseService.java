package com.imcode.imcms.api;

import imcode.server.db.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseService {

    private ConnectionPool connectionPool;

    public DatabaseService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
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
        return connectionPool.getConnection() ;
    }
}
