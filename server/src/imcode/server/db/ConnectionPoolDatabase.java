package imcode.server.db;

import org.apache.commons.lang.UnhandledException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class ConnectionPoolDatabase implements Database {

    ConnectionPool connectionPool;

    public ConnectionPoolDatabase( ConnectionPool connectionPool ) {
        this.connectionPool = connectionPool;
    }

    public void executeTransaction( DatabaseCommand databaseCommand ) {
        try {
            Connection connection = connectionPool.getConnection();
            try {
                connection.setAutoCommit( false );
                databaseCommand.executeOn( new DefaultDatabaseConnection( connection ) );
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
        DBConnect dbc = new DBConnect( connectionPool );
        dbc.setSQLString( sqlQuery, parameters );
        List data = dbc.executeQuery();
        return createStringArrayFromSqlResults( data );
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        DBConnect dbc = new DBConnect( connectionPool );
        dbc.setSQLString( sqlStr, params );
        List data = dbc.executeQuery();
        return createStringFromSqlResults( data );
    }

    /**
     * Send a sql update query to the database
     */
    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        DBConnect dbc = new DBConnect( connectionPool );
        dbc.setSQLString( sqlStr, params );
        int res = dbc.executeUpdateQuery();
        return res;
    }

    /**
     * The preferred way of getting data from the db.
     * String.trim()'s the results.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     */
    public String[] sqlProcedure( String procedure, String[] params ) {
        String procedure1 = procedure;
        procedure1 = trimAndCheckNoWhitespace( procedure1 );

        DBConnect dbc = new DBConnect( connectionPool );
        List data = dbc.executeProcedure( procedure1, params );

        return createStringArrayFromSqlResults( data );
    }

    /**
     * The preferred way of getting data to the db.
     *
     * @param procedure The name of the procedure
     * @param params    The parameters of the procedure
     * @return updateCount or -1 if error
     */
    public int sqlUpdateProcedure( String procedure, String[] params ) {
        DBConnect dbc = new DBConnect( connectionPool );
        int res = dbc.executeUpdateProcedure( procedure, params );
        return res;
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        DBConnect dbc = new DBConnect( connectionPool );
        List data = dbc.executeProcedure( procedure, params );

        return createStringFromSqlResults( data );
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        procedure = trimAndCheckNoWhitespace( procedure );

        DBConnect dbc = new DBConnect( connectionPool );
        List data = dbc.executeProcedure( procedure, params );
        String[] meta = dbc.getColumnLabels();

        return createHashtableOfStringArrayFromSqlResults( data, meta );
    }

    /**
     * Send a procedure to the database and return a multi string array
     */
    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        procedure = trimAndCheckNoWhitespace( procedure );

        DBConnect dbc = new DBConnect( connectionPool );
        List data = dbc.executeProcedure( procedure, params );

        int columns = dbc.getColumnCount();

        return create2DStringArrayFromSqlResults( data, columns );
    }

    public String[][] sqlQueryMulti( String sqlQuery, String[] params ) {
        DBConnect dbc = new DBConnect( connectionPool );
        dbc.setSQLString( sqlQuery, params );

        List data = dbc.executeQuery();
        int columns = dbc.getColumnCount();

        return create2DStringArrayFromSqlResults( data, columns );
    }

    private static String trimAndCheckNoWhitespace( String procedure ) {
        procedure = procedure.trim();
        if ( procedure.matches( "\\s" ) ) {
            throw new IllegalArgumentException( "Procedurename contains whitespace. Procedure-parameters are not allowed in this method." );
        }
        return procedure;
    }

    private static String createStringFromSqlResults( List data ) {
        if ( data != null && !data.isEmpty() ) {
            return
                    null != data.get( 0 )
                    ? data.get( 0 ).toString()
                    : null;
        } else {
            return null;
        }
    }

    private static String[][] create2DStringArrayFromSqlResults( List data, int columns ) {
        if ( columns == 0 ) {
            return new String[0][0];
        }

        int rows = data.size() / columns;

        String[][] result = new String[rows][columns];
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < columns; j++ ) {
                result[i][j] =
                null != data.get( i * columns + j )
                ? data.get( i * columns + j ).toString()
                : null;
            }

        }

        return result;
    }

    private static String[] createStringArrayFromSqlResults( List data ) {
        String[] result = new String[data.size()];
        for ( int i = 0; i < data.size(); i++ ) {
            result[i] =
            null != data.get( i )
            ? data.get( i ).toString()
            : null;
        }
        return result;
    }

    private static Map createHashtableOfStringArrayFromSqlResults( List data, String[] meta ) {
        Map result = new HashMap( meta.length );

        if ( data.size() > 0 ) {

            for ( int i = 0; i < meta.length; i++ ) {
                String[] temp_str = new String[data.size() / meta.length];
                int counter = 0;

                for ( int j = i; j < data.size(); j += meta.length ) {
                    temp_str[counter++] =
                    null != data.get( j )
                    ? data.get( j ).toString()
                    : null;
                }
                result.put( meta[i], temp_str );
            }
            return result;
        } else {
            return new HashMap( 1 );
        }
    }
}
