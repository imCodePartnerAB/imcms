package imcode.server.db.handlers;

import imcode.server.db.ObjectFromRowFactory;
import org.apache.commons.dbutils.ResultSetHandler;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObjectArrayResultSetHandler implements ResultSetHandler {

    private ObjectFromRowFactory objectFromRowFactory ;

    public ObjectArrayResultSetHandler(ObjectFromRowFactory objectFromRowFactory) {
        this.objectFromRowFactory = objectFromRowFactory;
    }

    public Object handle(ResultSet resultSet) throws SQLException {
        List result = new ArrayList();
        while ( resultSet.next() ) {
            result.add(objectFromRowFactory.createObjectFromResultSetRow(resultSet));
        }
        Class resultArrayType = objectFromRowFactory.getClassOfCreatedObjects();
        return result.toArray((Object[]) Array.newInstance(resultArrayType, result.size()));
    }
}
