package imcode.server.db.impl;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.DatabaseConnectionWrapper;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.UnhandledException;

import java.io.IOException;
import java.io.Writer;

public class WriterDatabaseConnectionWrapper extends DatabaseConnectionWrapper {

    private Writer writer;

    public WriterDatabaseConnectionWrapper(DatabaseConnection databaseConnection, Writer writer) {
        super(databaseConnection);
        this.writer = writer;
    }

    private void write(String sql) {
        try {
            writer.write(sql) ;
            writer.flush();
        } catch ( IOException e ) {
            throw new UnhandledException(e);
        }
    }

    public int executeUpdate(String sql, Object[] parameters) throws DatabaseException {
        write(sql);
        return super.executeUpdate(sql, parameters);
    }

    public Number executeUpdateAndGetGeneratedKey(String sql, Object[] parameters) throws DatabaseException {
        write(sql) ;
        return super.executeUpdateAndGetGeneratedKey(sql, parameters) ;
    }

    public int executeUpdateProcedure(String procedure, Object[] parameters) throws DatabaseException {
        write(procedure);
        return super.executeUpdateProcedure(procedure, parameters);
    }

    public Object executeQuery(String sqlQuery, Object[] parameters,
                               ResultSetHandler resultSetHandler) throws DatabaseException {
        write(sqlQuery);
        return super.executeQuery(sqlQuery, parameters, resultSetHandler);
    }

    public Object executeProcedure(String procedure, Object[] parameters,
                                   ResultSetHandler resultSetHandler) throws DatabaseException {
        write(procedure);
        return super.executeProcedure(procedure, parameters, resultSetHandler);
    }
}
