package imcode.server.db.impl;

import imcode.server.db.*;
import imcode.server.db.commands.UpdateDatabaseCommand;
import imcode.server.db.commands.UpdateProcedureDatabaseCommand;
import imcode.server.db.handlers.FlatStringArrayResultSetHandler;
import imcode.server.db.handlers.MultiStringArrayResultSetHandler;
import imcode.server.db.handlers.SingleStringResultSetHandler;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.SQLException;

public class DefaultDatabase implements Database {

    QueryRunner queryRunner;
    ProcedureExecutor procedureExecutor;

    public DefaultDatabase( QueryRunner queryRunner, ProcedureExecutor procedureExecutor ) {
        this.queryRunner = queryRunner;
        this.procedureExecutor = procedureExecutor;
    }

    public Object executeCommand( final DatabaseCommand databaseCommand ) {
        try {
            Connection connection = queryRunner.getDataSource().getConnection();
            try {
                DatabaseConnection defaultDatabaseConnection = new DefaultDatabaseConnection( connection, queryRunner, procedureExecutor );
                return databaseCommand.executeOn( defaultDatabaseConnection );
            } finally {
                connection.close() ;
            }
        } catch ( SQLException e ) {
            throw DatabaseException.from( e );
        }
    }

    public String sqlQueryStr( final String sql, final String[] parameters ) {
        return (String)executeCommand( new QueryDatabaseCommand( sql, parameters, new SingleStringResultSetHandler() ) );
    }

    public String[] sqlQuery( final String sql, final String[] parameters ) {
        return (String[])executeCommand( new QueryDatabaseCommand( sql, parameters, new FlatStringArrayResultSetHandler() ) );
    }

    public String[][] sqlQueryMulti( final String sql, final String[] parameters ) {
        return (String[][])executeCommand( new QueryDatabaseCommand( sql, parameters, new MultiStringArrayResultSetHandler() ) );
    }

    public int sqlUpdateQuery( final String sql, final String[] parameters ) {
        return ((Integer)executeCommand( new UpdateDatabaseCommand( sql, parameters ) )).intValue();
    }

    public int sqlUpdateProcedure( final String procedure, final String[] params ) {
        return ((Integer)executeCommand( new UpdateProcedureDatabaseCommand( procedure, params ) )).intValue() ;
    }

    public String sqlProcedureStr( final String procedure, final String[] params ) {
        return (String)executeCommand( new ProcedureDatabaseCommand( procedure, params, new SingleStringResultSetHandler() ) ) ;
    }

    public String[] sqlProcedure( final String procedure, final String[] params ) {
        return (String[])executeCommand( new ProcedureDatabaseCommand( procedure, params, new FlatStringArrayResultSetHandler() ) ) ;
    }

    public String[][] sqlProcedureMulti( final String procedure, final String[] params ) {
        return (String[][])executeCommand( new ProcedureDatabaseCommand( procedure, params, new MultiStringArrayResultSetHandler() ) ) ;
    }

    private static class QueryDatabaseCommand implements DatabaseCommand {

        private final String sql;
        private final String[] parameters;
        private final ResultSetHandler resultSetHandler;

        QueryDatabaseCommand( String sql, String[] parameters, ResultSetHandler resultSetHandler ) {
            this.sql = sql;
            this.parameters = parameters;
            this.resultSetHandler = resultSetHandler;
        }

        public Object executeOn( DatabaseConnection connection ) {
            return connection.executeQuery( sql, parameters, resultSetHandler );
        }
    }

    private static class ProcedureDatabaseCommand implements DatabaseCommand {

        private final String procedure;
        private final String[] params;
        private final ResultSetHandler resultSetHandler;

        ProcedureDatabaseCommand( String procedure, String[] params, ResultSetHandler resultSetHandler ) {
            this.procedure = procedure;
            this.params = params;
            this.resultSetHandler = resultSetHandler;
        }

        public Object executeOn( DatabaseConnection connection ) {
            return connection.executeProcedure( procedure, params, resultSetHandler );
        }
    }

}
