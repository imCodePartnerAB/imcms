package imcode.server.db.handlers;

import imcode.server.db.ObjectFromRowFactory;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectFromFirstRowResultSetHandler implements ResultSetHandler {

    ObjectFromRowFactory objectFromRowFactory ;

    public ObjectFromFirstRowResultSetHandler(ObjectFromRowFactory objectFromRowFactory) {
        this.objectFromRowFactory = objectFromRowFactory;
    }

    public Object handle(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null ;
        }
        return objectFromRowFactory.createObjectFromResultSetRow(resultSet) ;
    }
}
