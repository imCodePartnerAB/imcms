package imcode.server.db.impl;

import imcode.server.db.DatabaseConnection;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.NotImplementedException;

import java.sql.Connection;

public class MockDatabaseConnection implements DatabaseConnection {

    private MockDatabase database;

    public MockDatabaseConnection( MockDatabase mockDatabase ) {
        this.database = mockDatabase ;
    }

    public Number executeUpdateAndGetGeneratedKey( String sql, Object[] parameters ) {
        return (Number)database.getResultForSqlCall( sql, parameters ) ;
    }

    public int executeUpdateProcedure( String procedure, Object[] parameters ) {
        return database.executeUpdateProcedure( procedure, parameters );
    }

    public Object executeQuery( String sqlQuery, Object[] parameters,
                                ResultSetHandler resultSetHandler ) {
        return database.executeQuery(sqlQuery, parameters, resultSetHandler) ;
    }

    public Object executeProcedure( String procedure, Object[] parameters, ResultSetHandler resultSetHandler ) {
        throw new NotImplementedException( MockDatabaseConnection.class ) ;
    }

    public Connection getConnection() {
        return new MockConnection() ;
    }

    public int executeUpdate( String sql, Object[] parameters ) {
        return database.executeUpdateQuery( sql, parameters );
    }
}
