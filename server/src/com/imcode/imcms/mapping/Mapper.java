package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import com.imcode.db.commands.DeleteWhereColumnsEqualDatabaseCommand;
import com.imcode.db.commands.UpdateTableWhereColumnEqualsDatabaseCommand;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.db.handlers.RowTransformer;
import com.imcode.db.handlers.SingleObjectHandler;
import com.imcode.db.handlers.CollectionHandler;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Mapper<E extends Mapper.MapperObject> {

    private final Database database;

    protected Mapper(Database database) {
        this.database = database;
    }

    public E get(Object id) {
        return (E) database.execute(new SqlQueryCommand(getSelectSql()+" WHERE "+getIdColumnName()+" = ?", new Object[] { id } , new SingleObjectHandler(getRowTransformer())));
    }

    public List<E> getAll() {
        return getAll(null, getDefaultOrderBy());
    }

    protected List<E> getAll(String where, String orderBy) {
        String selectSql = getSelectSql();
        if (null != where) {
            selectSql += " WHERE "+where;
        }
        if (null != orderBy) {
            selectSql += " ORDER BY "+orderBy;
        }
        return (List<E>) database.execute(new SqlQueryCommand(selectSql, null, new CollectionHandler(new ArrayList(), getRowTransformer())));
    }

    public E create(E e) {
        return get(database.execute(new InsertIntoTableDatabaseCommand(getTableName(), getDataValues(e))));
    }

    public void delete(Object id) {
        database.execute(new DeleteWhereColumnsEqualDatabaseCommand(getTableName(), getIdColumnName(), id));
    }

    public void update(E e) {
        database.execute(new UpdateTableWhereColumnEqualsDatabaseCommand(getTableName(), getIdColumnName(), e.getId(), getDataValues(e)));
    }

    private String getSelectSql() {
        return "SELECT " + StringUtils.join(getColumnNames().iterator(), ", ") + " FROM " + getTableName();
    }

    private List<String> getColumnNames() {
        List<String> columnNames = new ArrayList(getDataColumnNames());
        columnNames.add(0, getIdColumnName());
        return columnNames;
    }

    private RowTransformer getRowTransformer() {
        return new RowTransformer() {
            public Object createObjectFromResultSetRow(ResultSet rs) throws SQLException {
                return convertRow(rs);
            }

            public Class getClassOfCreatedObjects() {
                return null;
            }
        };
    }

    protected abstract String getTableName() ;
    protected abstract String getIdColumnName() ;
    protected abstract List<String> getDataColumnNames() ;
    protected abstract E convertRow(ResultSet rs) throws SQLException;
    protected abstract Object[][] getDataValues(E e) ;

    protected String getDefaultOrderBy() {
        return null;
    }

    public interface MapperObject {

        Object getId();

    }
}
