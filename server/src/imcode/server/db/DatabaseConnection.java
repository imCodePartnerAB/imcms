package imcode.server.db;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;

public interface DatabaseConnection {

    int executeUpdate( String sql, String[] parameters );

    Number executeUpdateAndGetGeneratedKey( String sql, String[] parameters );

    int executeUpdateProcedure( String procedure, String[] parameters );

    Object executeQuery( String sqlQuery, String[] parameters,
                                 ResultSetHandler resultSetHandler );

    Object executeProcedure( String procedure, String[] params, ResultSetHandler resultSetHandler );

    Connection getConnection();
}