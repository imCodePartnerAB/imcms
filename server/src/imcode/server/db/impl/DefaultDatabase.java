package imcode.server.db.impl;

import imcode.server.db.Database;
import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.ProcedureExecutor;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;

public class DefaultDatabase implements Database {

    QueryRunner queryRunner;
    ProcedureExecutor procedureExecutor;

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
}
