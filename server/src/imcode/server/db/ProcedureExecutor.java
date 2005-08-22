package imcode.server.db;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.SQLException;

public interface ProcedureExecutor {

    int executeUpdateProcedure( Connection connection, String procedure, Object[] parameters ) throws SQLException;

    Object executeProcedure( Connection connection, String procedure, Object[] parameters,
                             ResultSetHandler resultSetHandler ) throws SQLException;
}