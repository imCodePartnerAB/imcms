package imcode.server.db;

import imcode.util.FileUtility;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DBConnect {

    private Connection connection = null;
    private ResultSet resultSet = null;
    private ResultSetMetaData resultSetMetaData = null;
    private PreparedStatement preparedStatement;
    private String sqlQueryString = "";
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
            connection.close();
        } catch ( SQLException ex ) {
            log.error( "Failed to close connection.", ex );
        }
        connection = null;
    }

    private void prepareProcedureStatementAndSetParameters( String wantedProcedure, String[] params ) {
        File procedureFile = FileUtility.getFileFromWebappRelativePath("WEB-INF/sql/sprocs/"+wantedProcedure.toLowerCase()+".prc" ) ;
        try {
            String procedureSql = IOUtils.toString( new FileInputStream(procedureFile)) ;
            Pattern headerPattern = Pattern.compile( "CREATE\\s+PROCEDURE\\s+(\\w+)\\s+(.*)\\bAS\\b", Pattern.CASE_INSENSITIVE|Pattern.DOTALL) ;
            Matcher headerMatcher = headerPattern.matcher( procedureSql );
            if (!headerMatcher.find()) {
                throw new RuntimeException( "Failed to parse procedure "+wantedProcedure+": "+procedureSql);
            }
            String procedureName = headerMatcher.group( 1 );
            String parametersSQL = headerMatcher.group( 2 );
            String body = procedureSql.substring( headerMatcher.end() ) ;
            Pattern parameterPattern = Pattern.compile( "@(\\w+)") ;
            Matcher parametersMatcher = parameterPattern.matcher( parametersSQL );
            Map parameterMap = new HashMap();
            int headerParametersFound = 0 ;
            while (parametersMatcher.find()) {
                String parameterName = parametersMatcher.group( 1 );
                parameterMap.put(parameterName, params[headerParametersFound++]) ;
            }
            List parameters = new ArrayList();
            Matcher bodyParametersMatcher = parameterPattern.matcher( body ) ;
            StringBuffer bodyStringBuffer = new StringBuffer();
            while (bodyParametersMatcher.find()) {
                bodyParametersMatcher.appendReplacement( bodyStringBuffer, "?" ) ;
                String parameterName = bodyParametersMatcher.group( 1 );
                String parameterValue = (String)parameterMap.get( parameterName ) ;
                parameters.add(parameterValue) ;
            }
            bodyParametersMatcher.appendTail( bodyStringBuffer ) ;
            String bodyWithParametersReplaced = bodyStringBuffer.toString();
            String[] replacedParametersArray = (String[])parameters.toArray( new String[parameters.size()] );
            log.debug( "Procedure "+procedureName+" called with parameters\n"+ ArrayUtils.toString( replacedParametersArray ) +" and body\n"+bodyWithParametersReplaced);
            setSQLString( bodyWithParametersReplaced, replacedParametersArray);
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        }
    }

    private void setParameters( PreparedStatement statement, String[] params ) throws SQLException {
        for ( int i = 0; i < params.length; ++i ) {
            statement.setString( i + 1, params[i] );
        }
    }

    int executeUpdateProcedure( String procedure, String[] params ) {
        prepareProcedureStatementAndSetParameters( procedure, params );
        return executeUpdateQuery();
    }

    List executeProcedure( String procedure, String[] params ) {
        prepareProcedureStatementAndSetParameters( procedure, params );
        return executeQuery();
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
