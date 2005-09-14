package imcode.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ObjectFromRowFactory {
    Object createObjectFromResultSetRow(ResultSet resultSet) throws SQLException;
    
    Class getClassOfCreatedObjects() ;
}
