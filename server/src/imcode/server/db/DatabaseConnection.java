package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;

public interface DatabaseConnection {

    int executeUpdate( String sql, Object[] parameters ) throws DatabaseException;

    Number executeUpdateAndGetGeneratedKey( String sql, Object[] parameters ) throws DatabaseException;

    int executeUpdateProcedure( String procedure, Object[] parameters ) throws DatabaseException;

    Object executeQuery( String sqlQuery, Object[] parameters,
                                 ResultSetHandler resultSetHandler ) throws DatabaseException;

    Object executeProcedure( String procedure, Object[] parameters, ResultSetHandler resultSetHandler ) throws DatabaseException;

    Connection getConnection();
}