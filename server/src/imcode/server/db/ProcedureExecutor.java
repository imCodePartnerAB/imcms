package imcode.server.db;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.SQLException;

public interface ProcedureExecutor {

    int executeUpdateProcedure( Connection connection, String procedure, String[] params ) throws SQLException;

    Object executeProcedure( Connection connection, String procedure, String[] params,
                             ResultSetHandler resultSetHandler ) throws SQLException;
}