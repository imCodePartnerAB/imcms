package com.imcode.imcms.api;

import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.db.ConnectionPool;

import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseService {

    private IMCServiceInterface service;
    private ConnectionPool myOwnConnectionPool;

    public DatabaseService( IMCServiceInterface service ) {
        this.service = service;
        this.myOwnConnectionPool = ApplicationServer.createNewConnecionPoolToImcmsDatabase();
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
