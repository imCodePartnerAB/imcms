package imcode.server.db;

import org.apache.log4j.Logger;
import org.apache.commons.lang.NullArgumentException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DBConnect {

    private Connection connection = null;
    private ResultSet resultSet = null;
    private ResultSetMetaData resultSetMetaData = null;
    private PreparedStatement preparedStatement;
    private CallableStatement callableStatement = null;
    private String sqlQueryString = "";
    private String sqlProcedure = "";
    private String[] columnLabels;
    private int columnCount;

    private final static Logger log = Logger.getLogger( DBConnect.class );

    DBConnect( ConnectionPool conPool ) {
        try {
            connection = conPool.getConnection();
        } catch ( SQLException e ) {
            getException( null, e );
        }
    }

    List executeQuery() {

        List results = new ArrayList();

        // Execute SQL-string
        try {
            preparedStatement.executeQuery();
            resultSet = preparedStatement.getResultSet();
            resultSetMetaData = resultSet.getMetaData();
            columnCount = resultSetMetaData.getColumnCount();
            columnLabels = new String[columnCount];
            for ( int i = 0; i < columnCount; ) {
                columnLabels[i] = resultSetMetaData.getColumnLabel( ++i );
            }

            while ( resultSet.next() ) {
                for ( int i = 1; i <= columnCount; i++ ) {
                    String s = resultSet.getString( i );
                    results.add( s );
                }
            }
        } catch ( SQLException ex ) {
            log.error( "Error in executeQuery()", ex );
            throw getException( null, ex );
        } finally {
            closeConnection();
        }

        return results;
    }

    private RuntimeException getException( String message, SQLException ex ) {
        String sqlState = ex.getSQLState();
        if ("23000".equals( sqlState )) {
            return new IntegrityConstraintViolationSQLException(ex) ;
        } else if ("01004".equals( sqlState )) {
            return new StringTruncationSQLException(ex) ;
        } else {
            log.debug( "SQLException with SQLState "+sqlState) ;
        }
        return new RuntimeException( message+": "+ex.getMessage(), ex );
    }

    int executeUpdateQuery() {
        try {
            return preparedStatement.executeUpdate();
        } catch ( SQLException ex ) {
            log.error( "Error in executeUpdateQuery()", ex );
            throw getException( null, ex );
        } finally {
            closeConnection();
        }
    }

    private List executeProcedure() {

        List results = new ArrayList();
        try {
            resultSet = callableStatement.executeQuery();
            resultSetMetaData = resultSet.getMetaData();
            columnCount = resultSetMetaData.getColumnCount();

            columnLabels = new String[columnCount];
            for ( int i = 0; i < columnCount; ) {
                columnLabels[i] = resultSetMetaData.getColumnLabel( ++i );
            }
            while ( resultSet.next() ) {
                for ( int i = 1; i <= columnCount; i++ ) {
                    String s = resultSet.getString( i );
                    results.add( s );
                }
            }
        } catch ( SQLException ex ) {
            log.error( "Error in executeProcedure()", ex );
            throw getException( null, ex );
        } finally {
            closeConnection();
        }
        return results;
    }

    private int executeUpdateProcedure() {
        int res = 0;
        try {
            res = callableStatement.executeUpdate();
        } catch ( SQLException ex ) {
            log.error( "Error in executeUpdateProcedure()", ex );
            throw getException( null, ex );
        } finally {
            closeConnection();
        }
        return res;
    }

    String[] getColumnLabels() {
        return columnLabels;
    }

    int getColumnCount() {
        return columnCount;
    }

    private void closeConnection() {
        try {
            if ( null != resultSet ) {
                resultSet.close();
            }
            if ( null != preparedStatement ) {
                preparedStatement.close();
            }
            if ( null != callableStatement ) {
                callableStatement.close();
            }
            connection.close();
        } catch ( SQLException ex ) {
            log.error( "Failed to close connection.", ex );
        }
        connection = null;
    }

    private void prepareProcedureStatementAndSetParameters( String procedure, String[] params ) {
        if ( null == procedure ) {
            throw new NullArgumentException( procedure );
        }
        sqlProcedure = "{call " + procedure + "}";
        try {
            callableStatement = connection.prepareCall( sqlProcedure );
            setParameters( callableStatement, params );
        } catch ( SQLException ex ) {
            log.error( "Error in prepareProcedureStatementAndSetParameters()", ex );
            throw getException( procedure, ex );
        }
    }

    private void setParameters( PreparedStatement statement, String[] params ) throws SQLException {
        for ( int i = 0; i < params.length; ++i ) {
            statement.setString( i + 1, params[i] );
        }
    }

    int executeUpdateProcedure( String procedure, String[] params ) {
        procedure = addQuestionMarksToProcedureCall( procedure, params );
        prepareProcedureStatementAndSetParameters( procedure, params );
        return executeUpdateProcedure();
    }

    List executeProcedure( String procedure, String[] params ) {
        procedure = addQuestionMarksToProcedureCall( procedure, params );
        prepareProcedureStatementAndSetParameters( procedure, params );
        return executeProcedure();
    }

    private static String addQuestionMarksToProcedureCall( String procedure, String[] params ) {
        StringBuffer procedureBuffer = new StringBuffer( procedure );
        procedureBuffer.append( "(" );
        if ( params.length > 0 ) {
            procedureBuffer.append( "?" );
            for ( int i = 1; i < params.length; ++i ) {
                procedureBuffer.append( ", ?" );
            }
        }
        procedureBuffer.append( ")" );
        return procedureBuffer.toString();
    }

    void setSQLString( String sqlStr, String[] params ) {
        sqlQueryString = sqlStr;
        prepareQueryStatementAndSetParameters( params );
    }

    private void prepareQueryStatementAndSetParameters( String[] params ) {
        try {
            preparedStatement = connection.prepareStatement( sqlQueryString );
            setParameters( preparedStatement, params );
        } catch ( SQLException ex ) {
            log.error( "Error in prepareQueryStatementAndSetParameters()", ex );
            throw getException( null, ex );
        }
    }

}
