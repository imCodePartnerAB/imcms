package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;

public interface DatabaseConnection {

    int executeUpdate( String sql, Object[] parameters ) throws DatabaseException;

    Number executeUpdateAndGetGeneratedKey( String sql, String[] parameters ) throws DatabaseException;

    int executeUpdateProcedure( String procedure, String[] parameters ) throws DatabaseException;

    Object executeQuery( String sqlQuery, String[] parameters,
                                 ResultSetHandler resultSetHandler ) throws DatabaseException;

    Object executeProcedure( String procedure, String[] params, ResultSetHandler resultSetHandler ) throws DatabaseException;

    Connection getConnection();
}