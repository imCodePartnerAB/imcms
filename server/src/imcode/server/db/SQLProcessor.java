package imcode.server.db;

import java.sql.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public class SQLProcessor {

    /**
     *
     * @param conn An open connection to use
     * @param sql The statment to use in a PreparedStatement call, can have '?' as placeholders for values to put in
     * @param statmentValues The values to insert in the statement in the order of '?'.
     * @return The result of the querry
     */

    public ResultSet executeQuery( Connection conn, String sql, Object[] statmentValues ) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.prepareStatement( sql );
            if( statmentValues != null ) {
                static_buildStatement( statement, statmentValues );
            }
            rs = statement.executeQuery();
        } catch( SQLException e ) {
            logSQLException( e, sql );
            static_closeStatement( statement );
        }
        return rs;
    }

    public void executeUpdate( Connection conn, String sql, Object[] statmentValues ) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement( sql );
            if( statmentValues != null ) {
                static_buildStatement( statement, statmentValues );
            }
            statement.executeUpdate();
        } catch( SQLException e ) {
            logSQLException( e, sql );
        }
        finally {
            static_closeStatement( statement );
        }
    }

    private void logSQLException( SQLException ex, String sql ) {
        System.out.println( "Couldn't execute the command '" + sql + "'" );
        ex.printStackTrace( System.out );
    }

    private static void static_buildStatement( PreparedStatement stmnt, Object[] values ) throws SQLException {
        for( int i = 0; i < values.length; i++ ) {
            Object value = values[i];
            if( value == null ) {
                throw new NullPointerException( "Can't do anyting with a null value" );
            } else if ( value instanceof SQLTypeNull ) {
                stmnt.setNull( i + 1, ((SQLTypeNull)value).getFieldType() );
            } else {
                stmnt.setObject( i + 1, value );
            }
        }
    }

    private static void static_closeStatement( Statement stmnt ) {
        if( stmnt != null ) {
            try {
                stmnt.close();
            } catch( SQLException e ) {
                //swallow exception.  Since we're closing it we'll let it be dead.
            }
        }
    }
}