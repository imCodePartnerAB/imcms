package imcode.server.db.sql;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class SQLProcessorNoTransaction {

    private static Logger log = Logger.getLogger( SQLProcessorNoTransaction.class );
    private ConnectionPool connectionPool;

    /**
     * This method should not be used in an idela world. Instead all calls to the database should be through
     * this class or through a SQLTransaction created and received from this class.
     * @deprecated
     */
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public SQLProcessorNoTransaction( ConnectionPool connectionPool ) {
        this.connectionPool = connectionPool;
    }

    public SQLTransaction createNewTransaction( int transactionIsolationLevel, int noOfRetries ) {
        SQLTransaction result = null;
        try {
            result = new SQLTransaction( connectionPool, transactionIsolationLevel, noOfRetries );
        } catch( SQLException ex ) {
            static_logSQLException( log, "SQLExcetion in createNewTransaction()", ex );
        }
        return result;
    }

    public ArrayList executeQuery( String sql, Object[] paramValues, ResultProcessor resultProc ) {
        Connection con = null;
        ArrayList result = new ArrayList();
        PreparedStatement statement = null;
        try {
            con = connectionPool.getConnection();
            statement = con.prepareStatement( sql );
            static_setParamsIntoStatment( statement, paramValues );
            ResultSet rs = statement.executeQuery();
            result = static_mapResults( rs, resultProc );
        } catch( SQLException ex ) {
            static_logSQLException( log, sql, ex );
        } finally {
            static_closeStatement( statement );
            static_closeConnection( con );
        }
        return result;
    }

    public void executeBatchUpdate( String[] sqlCommands ) {
        Connection con = null;
        try {
            con = connectionPool.getConnection();
            Statement statment = con.createStatement();
            for( int i = 0; i < sqlCommands.length; i++ ) {
                String command = sqlCommands[i];
                statment.addBatch( command );
            }
            statment.executeBatch();
        } catch( SQLException ex ) {
            static_logSQLException( log, "Exception in static_executeBatchUpdate()", ex );
        } finally {
            static_closeConnection( con );
        }
    }

    static void static_logSQLException( Logger log, String sql, SQLException ex ) {
        log.error( "Couldn't execute the command '" + sql + "'", ex );
    }

    static void static_closeStatement( Statement stmnt ) {
        if( stmnt != null ) {
            try {
                stmnt.close();
            } catch( SQLException e ) {
                //swallow exception.  Since we're closing it we'll let it be dead.
            }
        }
    }

    static void static_closeConnection( Connection con ) {
        try {
            if( con != null ) {
                con.close();
            }
        } catch( SQLException ex ) {
            // Swallow
        }
    }

    static ArrayList static_mapResults( ResultSet rs, ResultProcessor resultProcessor ) throws SQLException {
        ArrayList result = new ArrayList();
        while( rs.next() ) {
            Object temp = resultProcessor.mapOneRow( rs );
            result.add( temp );
        }
        return result;
    }

    static void static_setParamsIntoStatment( PreparedStatement statement, Object[] paramValues ) throws SQLException {
        if( paramValues != null ) {
            for( int i = 0; i < paramValues.length; i++ ) {
                Object value = paramValues[i];
                if( value == null ) {
                    throw new NullPointerException( "Can't do anyting with a null value" );
                } else if( value instanceof SQLTypeNull ) {
                    statement.setNull( i + 1, ((SQLTypeNull)value).getFieldType() );
                } else {
                    statement.setObject( i + 1, value );
                }
            }
        }
    }

}