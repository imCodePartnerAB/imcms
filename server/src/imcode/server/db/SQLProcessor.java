package imcode.server.db;

import org.apache.log4j.Logger;

import java.sql.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLProcessor {

    private static Logger log = Logger.getLogger( SQLProcessor.class );

    private ConnectionPool connectionPool;

    SQLProcessor( ConnectionPool connectionPool ) {
        this.connectionPool = connectionPool;
    }

    static abstract class ResultProcessor {
        abstract Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException;
    }

    ArrayList executeQuery( String sql, Object[] paramValues, ResultProcessor resultProc ) {
        Connection conn = null;
        ArrayList result = new ArrayList();
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            ResultSet rs1 = null;
            try {
                statement = conn.prepareStatement( sql );
                if( paramValues != null ) {
                    static_buildStatement( statement, paramValues );
                }
                rs1 = statement.executeQuery();
            } catch( SQLException e ) {
                static_logSQLException( sql, e );
            }
            ResultSet rs = rs1;
            while( rs.next() ) {
                Object temp = resultProc.mapOneRowFromResultsetToObject( rs );
                if( null != temp ) {
                    result.add( temp );
                }
            }
        } catch( SQLException ex ) {
            log.fatal( "Exception in executeQuery()", ex );
        } finally {
            static_closeStatement( statement );
            static_closeConnection( conn );
        }
        return result;
    }

    int executeUpdate( String sql, Object[] paramValues ) {
        Connection conn = null;
        int rowsModified = 0;
        try {
            conn = connectionPool.getConnection();
            rowsModified = executeUpdate( conn, sql, paramValues );
        } catch (SQLException ex ) {
            log.fatal( "Exception in static_executeUpdate()", ex );
        } finally {
            static_closeConnection( conn );
        }
        return rowsModified;
    }

    void executeBatchUpdate( String[] sqlCommands ) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            static_executeBatchUpdate( conn, sqlCommands );
        } catch( SQLException  ex ) {
            static_logSQLException( "Exception in static_executeBatchUpdate()", ex );
        }
        finally {
            static_closeConnection( conn );
        }
    }

    private static void static_executeBatchUpdate( Connection conn, String[] sqlCommands ) {
        try {
            Statement statment = conn.createStatement();
            for( int i = 0; i < sqlCommands.length; i++ ) {
                String command = sqlCommands[i];
                statment.addBatch( command );
            }
            statment.executeBatch();
        }
        catch( SQLException ex ) {
            static_logSQLException( "batch update failed", ex );
        }
    }

    int executeUpdate( Connection conn, String sql, Object[] statmentValues ) {
        PreparedStatement statement = null;
        int rowsModified = 0;
        try {
            statement = conn.prepareStatement( sql );
            if( statmentValues != null ) {
                static_buildStatement( statement, statmentValues );
            }
            rowsModified = statement.executeUpdate();
        } catch( SQLException ex ) {
            static_logSQLException( sql, ex );
        }
        finally {
            static_closeStatement( statement );
        }
        return rowsModified;
    }

    private static void static_logSQLException( String sql, SQLException ex ) {
        log.error( "Couldn't execute the command '" + sql + "'" , ex );
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

    private static void static_closeConnection( Connection conn ) {
        try {
            if( conn != null ) {
                conn.close();
            }
        } catch( SQLException ex ) {
            // Swallow
        }
    }
}