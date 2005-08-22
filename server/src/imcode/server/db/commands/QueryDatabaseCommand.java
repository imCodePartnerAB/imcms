package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.ArrayUtils;

public class QueryDatabaseCommand implements DatabaseCommand {

    private final String sql;
    private final Object[] parameters;
    private final ResultSetHandler resultSetHandler;

    public QueryDatabaseCommand( String sql, Object[] parameters, ResultSetHandler resultSetHandler ) {
        this.sql = sql;
        this.parameters = parameters;
        this.resultSetHandler = resultSetHandler;
    }

    public Object executeOn( DatabaseConnection connection ) throws DatabaseException {
        return connection.executeQuery( sql, parameters, resultSetHandler );
    }

    public String toString() {
        return "query "+sql+" "+ArrayUtils.toString( parameters ) ;
    }
}
