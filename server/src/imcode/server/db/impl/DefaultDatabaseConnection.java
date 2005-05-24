package imcode.server.db.impl;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.ProcedureExecutor;
import imcode.server.db.handlers.FlatStringArrayResultSetHandler;
import imcode.server.db.handlers.MultiStringArrayResultSetHandler;
import imcode.server.db.handlers.SingleStringResultSetHandler;
import imcode.server.db.commands.QueryDatabaseCommand;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.*;

public class DefaultDatabaseConnection implements DatabaseConnection {

    private Connection connection;
    private QueryRunner queryRunner;
    private ProcedureExecutor procedureExecutor;

    public DefaultDatabaseConnection(Connection connection, QueryRunner queryRunner,
                                     ProcedureExecutor procedureExecutor) {
        this.connection = connection;
        this.queryRunner = queryRunner;
        this.procedureExecutor = procedureExecutor;
    }

    public int executeUpdate(String sql, Object[] parameters) throws DatabaseException {
        try {
            return queryRunner.update(connection, sql, parameters);
        } catch (SQLException se) {
            throw DatabaseException.fromSQLException(null, se);
        }
    }

    public Number executeUpdateAndGetGeneratedKey(String sql, String[] parameters) throws DatabaseException {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            try {
                setPreparedStatementParameters(preparedStatement, parameters);
                preparedStatement.executeUpdate();
                ResultSet generatedKeysResultSet = preparedStatement.getGeneratedKeys();
                try {
                    Number result = null;
                    if (generatedKeysResultSet.next()) {
                        result = (Number) generatedKeysResultSet.getObject(1);
                    }
                    return result;
                } finally {
                    generatedKeysResultSet.close();
                }
            } finally {
                preparedStatement.close();
            }
        } catch (SQLException se) {
            throw DatabaseException.fromSQLException(null, se);
        }
    }

    public int executeUpdateProcedure(String procedure, String[] parameters) throws DatabaseException {
        try {
            return procedureExecutor.executeUpdateProcedure(connection, procedure, parameters);
        } catch (SQLException e) {
            throw DatabaseException.fromSQLException(null, e);
        }
    }

    static void setPreparedStatementParameters(PreparedStatement preparedStatement, String[] parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setString(i + 1, parameters[i]);
        }
    }

    public Object executeQuery(String sqlQuery, String[] parameters,
                               ResultSetHandler resultSetHandler) throws DatabaseException {
        try {
            return queryRunner.query(connection, sqlQuery, parameters, resultSetHandler);
        } catch (SQLException e) {
            throw DatabaseException.fromSQLException(null, e);
        }
    }

    public Object executeProcedure(String procedure, String[] parameters, ResultSetHandler resultSetHandler) throws DatabaseException {
        try {
            return procedureExecutor.executeProcedure(connection, procedure, parameters, resultSetHandler);
        } catch (SQLException e) {
            throw DatabaseException.fromSQLException(null, e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
