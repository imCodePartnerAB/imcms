package imcode.server.db.commands;

import imcode.server.db.DatabaseConnection;
import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;

public class DeleteWhereColumnsEqualDatabaseCommand extends UpdateTableWithColumnValuesDatabaseCommand {

    public DeleteWhereColumnsEqualDatabaseCommand( String table, String column, Object columnValue ) {
        this(table, new Object[][] {{column,  columnValue}});
    }

    public DeleteWhereColumnsEqualDatabaseCommand(String table, Object[][] columnNamesAndValues) {
        super(table, columnNamesAndValues);
    }

    public Object executeOn( DatabaseConnection connection ) throws DatabaseException {
        Collection whereClauses = CollectionUtils.collect(Arrays.asList(columnNames), new ColumnNameToWhereClauseTransformer());
        String joinedWhereClauses = StringUtils.join(whereClauses.iterator()," AND ") ;
        return new Integer(connection.executeUpdate("DELETE FROM "+tableName+" WHERE "+joinedWhereClauses, columnValues)) ;
    }

    private static class ColumnNameToWhereClauseTransformer implements Transformer {
        public Object transform(Object object) {
            return object + " = ?" ;
        }
    }
}
