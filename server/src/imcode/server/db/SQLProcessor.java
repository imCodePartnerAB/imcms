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

    class SQLTransaction {
        private Connection con;
        private SQLProcessor sqlProcessor;

        public SQLTransaction( SQLProcessor sqlProcessor, Connection con ) throws SQLException {
            this.con = con;
            this.sqlProcessor = sqlProcessor;
            this.con.setAutoCommit( false );
        }

        public int executeUpdate( String sql, Object[] params ) throws SQLException {
            return sqlProcessor.executeUpdate( con, sql, params );
        }

        public void commit() {
            try {
                con.commit();
            } catch (SQLException ex ) {
                static_logSQLException( "SQLException in commit()", ex );
            }
            finally {
                static_closeConnection( con );
            }
        }

        public void rollback() {
            try {
                con.rollback();
            } catch (SQLException ex ) {
                static_logSQLException( "SQLException in commit()", ex );
            }
            finally {
                static_closeConnection( con );
            }
        }

        public ArrayList executeQuery( String sql, Object[] paramValues, ResultProcessor resultProcessor ) {
            return sqlProcessor.executeQuery( sql, paramValues, resultProcessor );
        }
    }

    public SQLTransaction startTransaction() {
        SQLTransaction result = null;
        Connection con = null;
        try {
             con = connectionPool.getConnection();
             result = new SQLTransaction( this, con );
        }
        catch( SQLException ex ) {
            static_logSQLException( "SQLExcetion in startTransaction()", ex );
        }
        return result;
    }


    ArrayList executeQuery( String sql, Object[] paramValues, ResultProcessor resultProc ) {
        Connection con = null;
        ArrayList result = new ArrayList();
        PreparedStatement statement = null;
        try {
            con = connectionPool.getConnection();
            ResultSet rs = null;
            try {
                statement = con.prepareStatement( sql );
                if( paramValues != null ) {
                    static_buildStatement( statement, paramValues );
                }
                rs = statement.executeQuery();
            } catch( SQLException e ) {
                static_logSQLException( sql, e );
            }
            while( rs.next() ) {
                Object temp = resultProc.mapOneRowFromResultsetToObject( rs );
                result.add( temp );
            }
        } catch( SQLException ex ) {
            log.fatal( "Exception in executeQuery()", ex );
        } finally {
            static_closeStatement( statement );
            static_closeConnection( con );
        }
        return result;
    }

    int executeUpdate( String sql, Object[] paramValues ) {
        Connection con = null;
        int rowsModified = 0;
        try {
            con = connectionPool.getConnection();
            rowsModified = executeUpdate( con, sql, paramValues );
        } catch (SQLException ex ) {
            log.fatal( "Exception in static_executeUpdate()", ex );
        } finally {
            static_closeConnection( con );
        }
        return rowsModified;
    }

    void executeBatchUpdate( String[] sqlCommands ) {
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

    private static void static_executeBatchUpdate( Connection con, String[] sqlCommands ) {
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

    private int executeUpdate( Connection con, String sql, Object[] statmentValues ) {
        PreparedStatement statement = null;
        int rowCount = 0;
        try {
            statement = con.prepareStatement( sql );
            if( statmentValues != null ) {
                static_buildStatement( statement, statmentValues );
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

    private static void static_closeConnection( Connection con ) {
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
}