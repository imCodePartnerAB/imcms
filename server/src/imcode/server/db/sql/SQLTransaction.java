package imcode.server.db.sql;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SQLTransaction {

    public class TransactionException extends RuntimeException {
        public TransactionException( String message, Throwable ex ) {
            super( message, ex );
        }
    }

    private ConnectionPool connectionPool;
    private int transactionIsolationLevel;
    private Connection currentConntection;

    private int rowCount = 0;
    private Object transactionResult;

    private static Logger logger = Logger.getLogger( SQLTransaction.class );

    public SQLTransaction( ConnectionPool connectionPool, int transactionIsolationLevel ) throws SQLException {
        this.connectionPool = connectionPool;
        this.transactionIsolationLevel = transactionIsolationLevel;
    }

    public void executeAndCommit( TransactionContent transactionContent ) {
        executeAndCommit( 1, transactionContent );
    }

    public void executeAndCommit( int maxNoOfTries, TransactionContent transactionContent ) {
        Throwable latestException = null;
        boolean succeded = false;
        int tryNo = 0;
        while( !succeded && tryNo < maxNoOfTries ) {
            tryNo++;
            if( tryNo > 1 ) {
                logger.info( "Failure in executeAndCommit, trying again, try no: " + tryNo );
            }
            try {
                this.currentConntection = connectionPool.getConnection();
                this.currentConntection.setTransactionIsolation( transactionIsolationLevel );
                this.currentConntection.setAutoCommit( false );

                transactionContent.execute();

                currentConntection.commit();
                succeded = true;
            } catch( SQLException ex ) {
                latestException = ex;
                SQLProcessorNoTransaction.static_logSQLException( "Exception when executing transactionContent! Rolls back the transaction ", ex );
                try {
                    currentConntection.rollback();
                } catch( SQLException ex2 ) {
                    // Swallow.
                    SQLProcessorNoTransaction.static_logSQLException( "Exception when rollback", ex );
                }
            } finally {
                SQLProcessorNoTransaction.static_closeConnection( currentConntection );
            }
        }
        if( !succeded ) {
            throw new TransactionException("Exception in executeAndCommit, exception thrown after " + tryNo + " tries.", latestException );
        }
    }

    public int executeUpdate( String sql, Object[] params ) throws SQLException {
        int rowCount = SQLProcessorNoTransaction.executeUpdate( currentConntection, sql, params );
        this.rowCount += rowCount;
        return rowCount;
    }

    public ArrayList executeQuery( String sql, Object[] paramValues, ResultProcessor resultProcessor ) throws SQLException {
        ArrayList result = new ArrayList();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = currentConntection.prepareStatement( sql );
            SQLProcessorNoTransaction.setParamsIntoStatment( statement, paramValues );
            rs = statement.executeQuery();
            result = SQLProcessorNoTransaction.mapResults( rs, resultProcessor );
        } finally {
            SQLProcessorNoTransaction.static_closeStatement( statement );
        }
        return result;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setTransactionResult( Object value ) {
        transactionResult = value;
    }

    public Object getTransactionResult() {
        return transactionResult;
    }
}
