package imcode.server.db.handlers;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SingleStringResultSetHandler implements ResultSetHandler {

    public Object handle( ResultSet resultSet ) throws SQLException {
        if (resultSet.next()) {
            return resultSet.getString( 1 ) ;
        } else {
            return null ;
        }
    }
}
