package imcode.server.db.impl;

import imcode.server.db.*;
import imcode.server.db.commands.*;
import imcode.server.db.exceptions.DatabaseException;
import imcode.server.db.handlers.FlatStringArrayResultSetHandler;
import imcode.server.db.handlers.MultiStringArrayResultSetHandler;
import imcode.server.db.handlers.SingleStringResultSetHandler;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DefaultDatabase implements Database {

    QueryRunner queryRunner;
    ProcedureExecutor procedureExecutor;
    Logger log = Logger.getLogger(DefaultDatabase.class);

    public DefaultDatabase( QueryRunner queryRunner, ProcedureExecutor procedureExecutor ) {
        this.queryRunner = queryRunner;
        this.procedureExecutor = procedureExecutor;
    }

    public Object executeCommand( final DatabaseCommand databaseCommand ) throws DatabaseException {
        try {
            Connection connection = queryRunner.getDataSource().getConnection();
            try {
                DatabaseConnection defaultDatabaseConnection = new DefaultDatabaseConnection( connection, queryRunner, procedureExecutor );
                return databaseCommand.executeOn( defaultDatabaseConnection );
            } catch( DatabaseException e ) {
                throw new DatabaseException( "Executing "+databaseCommand, e ) ;
            } finally {
                connection.close() ;
            }
        } catch ( SQLException e ) {
            throw DatabaseException.fromSQLException( null, e );
        }
    }

    public String executeStringQuery( final String sql, final String[] parameters ) throws DatabaseException {
        return (String)executeCommand( new QueryDatabaseCommand( sql, parameters, new SingleStringResultSetHandler() ) );
    }

    public String[] executeArrayQuery( final String sql, final String[] parameters ) throws DatabaseException {
        return (String[])executeCommand( new QueryDatabaseCommand( sql, parameters, new FlatStringArrayResultSetHandler() ) );
    }

    public String[][] execute2dArrayQuery( final String sql, final String[] parameters ) throws DatabaseException {
        return (String[][])executeCommand( new QueryDatabaseCommand( sql, parameters, new MultiStringArrayResultSetHandler() ) );
    }

    public int executeUpdateQuery( final String sql, final Object[] parameters ) throws DatabaseException {
        return ((Integer)executeCommand( new UpdateDatabaseCommand( sql, parameters ) )).intValue();
    }

    public int executeUpdateProcedure( final String procedure, final String[] params ) throws DatabaseException {
        return ((Integer)executeCommand( new UpdateProcedureDatabaseCommand( procedure, params ) )).intValue() ;
    }

    public String executeStringProcedure( final String procedure, final String[] params ) throws DatabaseException {
        return (String)executeCommand( new QueryProcedureDatabaseCommand( procedure, params, new SingleStringResultSetHandler() ) ) ;
    }

    public String[] executeArrayProcedure( final String procedure, final String[] params ) throws DatabaseException {
        return (String[])executeCommand( new QueryProcedureDatabaseCommand( procedure, params, new FlatStringArrayResultSetHandler() ) ) ;
    }

    public String[][] execute2dArrayProcedure( final String procedure, final String[] params ) throws DatabaseException {
        return (String[][])executeCommand( new QueryProcedureDatabaseCommand( procedure, params, new MultiStringArrayResultSetHandler() ) ) ;
    }

}
