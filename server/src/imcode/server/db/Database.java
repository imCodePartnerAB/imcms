package imcode.server.db;

import org.apache.commons.lang.UnhandledException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class Database {

    ConnectionPool connectionPool;

    public Database( ConnectionPool connectionPool ) {
        this.connectionPool = connectionPool;
    }

    public void executeTransaction( DatabaseCommand databaseCommand ) {
        try {
            Connection connection = connectionPool.getConnection();
            try {
                connection.setAutoCommit( false );
                databaseCommand.executeOn( new DatabaseConnection( connection ) );
                connection.commit();
            } catch ( Throwable t ) {
                connection.rollback();
                throw new UnhandledException( t );
            } finally {
                connection.setAutoCommit( true );
                connection.close() ;
            }
        } catch ( SQLException e ) {
            throw new UnhandledException( e );
        }
    }

    public String[] sqlQuery( String sqlQuery, String[] parameters ) {
        return SqlHelpers.sqlQuery( connectionPool, sqlQuery, parameters );
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        return SqlHelpers.sqlQueryStr( connectionPool, sqlStr, params );
    }

    /**
     * Send a sql update query to the database
     */
    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        return SqlHelpers.sqlUpdateQuery( connectionPool, sqlStr, params );
    }

    /**
     * The preferred way of getting data from the db.
     * String.trim()'s the results.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     */
    public String[] sqlProcedure( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedure( connectionPool, procedure, params );
    }

    /**
     * The preferred way of getting data to the db.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     * @return updateCount or -1 if error
     */
    public int sqlUpdateProcedure( String procedure, String[] params ) {
        return SqlHelpers.sqlUpdateProcedure( connectionPool, procedure, params );
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedureStr( connectionPool, procedure, params );
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedureHash( connectionPool, procedure, params );
    }

    /**
     * Send a procedure to the database and return a multi string array
     */
    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        return SqlHelpers.sqlProcedureMulti( connectionPool, procedure, params );
    }

    public String[][] sqlQueryMulti( String sqlQuery, String[] params ) {
        return SqlHelpers.sqlQueryMulti( connectionPool, sqlQuery, params );
    }

}
