package imcode.server.db.sql;

import org.apache.log4j.Logger;

import java.sql.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;

import imcode.server.db.sql.SQLTypeNull;
import imcode.server.db.sql.ConnectionPool;

public class SQLProcessorNoTransaction {

    private static Logger log = Logger.getLogger( SQLProcessorNoTransaction.class );

    private ConnectionPool connectionPool;

    public SQLProcessorNoTransaction( ConnectionPool connectionPool ) {
        this.connectionPool = connectionPool;
    }

    public SQLTransaction startTransaction() {
        SQLTransaction result = null;
        Connection con;
        try {
             con = connectionPool.getConnection();
             result = new SQLTransaction( con );
        }
        catch( SQLException ex ) {
            static_logSQLException( "SQLExcetion in startTransaction()", ex );
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
            setParamsIntoStatment( statement, paramValues );
            ResultSet rs = statement.executeQuery();
            result = mapResults( rs, resultProc );
        } catch( SQLException ex ) {
            static_logSQLException( sql, ex );
        } finally {
            static_closeStatement( statement );
            static_closeConnection( con );
        }
        return result;
    }

    public int executeUpdate( String sql, Object[] paramValues ) {
        Connection con = null;
        int rowsModified = 0;
        try {
            con = connectionPool.getConnection();
            rowsModified = executeUpdate( con, sql, paramValues );
        } catch (SQLException ex ) {
            static_logSQLException( sql, ex );
        } finally {
            static_closeConnection( con );
        }
        return rowsModified;
    }

    public void executeBatchUpdate( String[] sqlCommands ) {
        Connection con = null;
        try {
            con = connectionPool.getConnection();
            static_executeBatchUpdate( con, sqlCommands );
        } catch( SQLException  ex ) {
            static_logSQLException( "Exception in static_executeBatchUpdate()", ex );
        }
        finally {
            static_closeConnection( con );
        }
    }

    static void static_executeBatchUpdate( Connection con, String[] sqlCommands ) {
        try {
            Statement statment = con.createStatement();
            for( int i = 0; i < sqlCommands.length; i++ ) {
                String command = sqlCommands[i];
                statment.addBatch( command );
            }
            statment.executeBatch();
        }
        catch( SQLException ex ) {
            static_logSQLException( "batch update failed, ", ex );
        }
    }

    static int executeUpdate( Connection con, String sql, Object[] statmentValues ) {
        PreparedStatement statement = null;
        int rowCount = 0;
        try {
            statement = con.prepareStatement( sql );
            if( statmentValues != null ) {
                for( int i = 0; i < statmentValues.length; i++ ) {
                    Object value = statmentValues[i];
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
            static_logSQLException( sql, ex );
        }
        finally {
            static_closeStatement( statement );
        }
        return rowCount;
    }

    static void static_logSQLException( String sql, SQLException ex ) {
        log.error( "Couldn't execute the command '" + sql + "'" , ex );
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

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    static ArrayList mapResults( ResultSet rs, ResultProcessor resultProcessor ) throws SQLException {
        ArrayList result = new ArrayList();
        while( rs.next() ) {
            Object temp = resultProcessor.mapOneRow( rs );
            result.add( temp );
        }
        return result;
    }

    static void setParamsIntoStatment( PreparedStatement statement, Object[] paramValues ) throws SQLException {
        if( paramValues != null ) {
            for( int i = 0; i < paramValues.length; i++ ) {
                Object value = paramValues[i];
                if( value == null ) {
                    throw new NullPointerException( "Can't do anyting with a null value" );
                } else if ( value instanceof SQLTypeNull ) {
                    statement.setNull( i + 1, ((SQLTypeNull)value).getFieldType() );
                } else {
                    statement.setObject( i + 1, value );
                }
            }
        }
    }
}