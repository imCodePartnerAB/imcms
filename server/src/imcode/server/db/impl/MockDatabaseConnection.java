package imcode.server.db.impl;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.NotImplementedException;

import java.sql.Connection;

public class MockDatabaseConnection implements DatabaseConnection {

    private MockDatabase database;

    public MockDatabaseConnection( MockDatabase mockDatabase ) {
        this.database = mockDatabase ;
    }

    public Number executeUpdateAndGetGeneratedKey( String sql, String[] parameters ) {
        return (Number)database.getResultForSqlCall( sql, parameters ) ;
    }

    public int executeUpdateProcedure( String procedure, String[] parameters ) {
        return database.executeUpdateProcedure( procedure, parameters );
    }

    public Object executeQuery( String sqlQuery, String[] parameters,
                                ResultSetHandler resultSetHandler ) {
        return database.executeQuery(sqlQuery, parameters, resultSetHandler) ;
    }

    public Object executeProcedure( String procedure, String[] params, ResultSetHandler resultSetHandler ) {
        throw new NotImplementedException( MockDatabaseConnection.class ) ;
    }

    public Connection getConnection() {
        return new MockConnection() ;
    }

    public String[] executeArrayQuery(String sql, String[] parameters) throws DatabaseException {
        throw new NotImplementedException( MockDatabaseConnection.class ) ;
    }

    public String[][] execute2dArrayQuery(String sql, String[] parameters) throws DatabaseException {
        throw new NotImplementedException( MockDatabaseConnection.class ) ;
    }

    public String executeStringQuery(String sqlStr, String[] parameters) {
        throw new NotImplementedException( MockDatabaseConnection.class ) ;
    }

    public int executeUpdate( String sql, Object[] parameters ) {
        return database.executeUpdateQuery( sql, parameters );
    }
}
