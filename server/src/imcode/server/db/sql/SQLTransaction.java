package imcode.server.db.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SQLTransaction {
    private Connection con;
    private int rowCount = 0;

    public SQLTransaction( Connection con ) throws SQLException {
        this.con = con;
        this.con.setAutoCommit( false );
    }

    public void executeAndCommit( TransactionContent transactionContent ) {
        try {
            transactionContent.execute();
            con.commit();
        } catch( SQLException ex ) {
            SQLProcessorNoTransaction.static_logSQLException( "Rollback in executeAndCommit when executing content " + transactionContent, ex );
            try {
                con.rollback();
            } catch( SQLException ex2 ) {
                // Swallow.
                SQLProcessorNoTransaction.static_logSQLException( "Exception when rollbacl", ex );
            }
        } finally {
            SQLProcessorNoTransaction.static_closeConnection( con );
        }
    }

    public int executeUpdate( String sql, Object[] params ) throws SQLException {
        int rowCount = SQLProcessorNoTransaction.executeUpdate( con, sql, params );
        this.rowCount += rowCount;
        return rowCount;
    }

    public ArrayList executeQuery( String sql, Object[] paramValues, ResultProcessor resultProcessor ) throws SQLException {
        ArrayList result = new ArrayList();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = con.prepareStatement( sql );
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
}
