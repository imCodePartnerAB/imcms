package imcode.server.db.sql;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLTransaction {

    public static class TransactionException extends RuntimeException {
        public TransactionException( String message, Throwable ex ) {
            super( message, ex );
        }
    }

    private ConnectionPool connectionPool;
    private int transactionIsolationLevel;
    private int maxNoRetries;
    private Connection currentConnection;
    private int rowCount = 0;
    private static Logger log = Logger.getLogger( SQLTransaction.class );

    public SQLTransaction( ConnectionPool connectionPool, int transactionIsolationLevel, int maxNoRetries ) throws SQLException {
        this.connectionPool = connectionPool;
        this.transactionIsolationLevel = transactionIsolationLevel;
        this.maxNoRetries = maxNoRetries;
    }

    public void executeAndCommit( TransactionContent transactionContent ) {
        Throwable latestException = null;
        boolean succeded = false;
        int tryNo = 0;
        while( !succeded && tryNo < maxNoRetries ) {
            tryNo++;
            if( tryNo > 1 ) {
                log.info( "Failure in executeAndCommit, trying again, try no: " + tryNo );
            }
            try {
                this.currentConnection = connectionPool.getConnection();
                this.currentConnection.setTransactionIsolation( transactionIsolationLevel );
                this.currentConnection.setAutoCommit( false );

                transactionContent.execute();

                currentConnection.commit();
                succeded = true;
            } catch( SQLException ex ) {
                latestException = ex;
                SQLProcessorNoTransaction.static_logSQLException( log, "Exception when executing transactionContent! Rolls back the transaction ", ex );
                try {
                    currentConnection.rollback();
                } catch( SQLException ex2 ) {
                    // Swallow.
                    SQLProcessorNoTransaction.static_logSQLException( log, "Exception when rollback", ex );
                }
            } finally {
                SQLProcessorNoTransaction.static_closeConnection( currentConnection );
            }
        }
        if( !succeded ) {
            throw new TransactionException("Exception in executeAndCommit, exception thrown after " + tryNo + " tries.", latestException );
        }
    }

    public void executeUpdate( String sql, Object[] params ) {
        PreparedStatement statement = null;
        int rowCount = 0;
        try {
            statement = currentConnection.prepareStatement( sql );
            if( params != null ) {
                for( int i = 0; i < params.length; i++ ) {
                    Object value = params[i];
                    if( value == null ) {
                        throw new NullPointerException( "Can't do anyting with a null value" );
                    } else if ( value instanceof SQLTypeNull ) {
                        statement.setNull( i + 1, ((SQLTypeNull)value).getFieldType() );
                    } else {
                        statement.setObject( i + 1, value );
                    }
                }
            }
            rowCount = statement.executeUpdate();
        } catch( SQLException ex ) {
            SQLProcessorNoTransaction.static_logSQLException( log, sql, ex );
        }
        finally {
            SQLProcessorNoTransaction.static_closeStatement( statement );
        }
        this.rowCount += rowCount;
    }

    public ArrayList executeQuery( String sql, Object[] paramValues, ResultProcessor resultProcessor ) throws SQLException {
        ArrayList result = new ArrayList();
        PreparedStatement statement = null;
        ResultSet rs;
        try {
            statement = currentConnection.prepareStatement( sql );
            SQLProcessorNoTransaction.static_setParamsIntoStatment( statement, paramValues );
            rs = statement.executeQuery();
            result = SQLProcessorNoTransaction.static_mapResults( rs, resultProcessor );
        } finally {
            SQLProcessorNoTransaction.static_closeStatement( statement );
        }
        return result;
    }

    public int getRowCount() {
        return rowCount;
    }

}
