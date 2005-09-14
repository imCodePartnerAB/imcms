package imcode.server.db.impl;

import org.apache.commons.lang.NotImplementedException;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public class MockResultSet implements ResultSet  {

    Object[][] rows ;
    int rowIndex = -1 ;

    public MockResultSet(Object[][] objects) {
        rows = objects ;
    }

    public boolean next() throws SQLException {
        if ( rows.length - 1 == rowIndex ) {
            return false ;
        }
        rowIndex++ ;
        return true;
    }

    public void close() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.close");
    }

    public boolean wasNull() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.wasNull");
    }

    public String getString(int columnIndex) throws SQLException {
        Object object = getObject(columnIndex);
        if (null == object) {
            return null ;
        }
        if (!(object instanceof String)) {
            return ""+object ;
        }
        return (String)object;
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBoolean");
    }

    public byte getByte(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getByte");
    }

    public short getShort(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getShort");
    }

    public int getInt(int columnIndex) throws SQLException {
        return ((Number)getObject(columnIndex)).intValue() ;
    }

    public long getLong(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getLong");
    }

    public float getFloat(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getFloat");
    }

    public double getDouble(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getDouble");
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBigDecimal");
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBytes");
    }

    public Date getDate(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getDate");
    }

    public Time getTime(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getTime");
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getTimestamp");
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getAsciiStream");
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getUnicodeStream");
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBinaryStream");
    }

    public String getString(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getString");
    }

    public boolean getBoolean(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBoolean");
    }

    public byte getByte(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getByte");
    }

    public short getShort(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getShort");
    }

    public int getInt(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getInt");
    }

    public long getLong(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getLong");
    }

    public float getFloat(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getFloat");
    }

    public double getDouble(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getDouble");
    }


    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBigDecimal");
    }

    public byte[] getBytes(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBytes");
    }

    public Date getDate(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getDate");
    }

    public Time getTime(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getTime");
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getTimestamp");
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getAsciiStream");
    }


    public InputStream getUnicodeStream(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getUnicodeStream");
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBinaryStream");
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getWarnings");
    }

    public void clearWarnings() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.clearWarnings");
    }

    public String getCursorName() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getCursorName");
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return new ResultSetMetaData() {
            public int getColumnCount() {
                if ( 0 == rows.length ) {
                    return 0 ;
                }
                return rows[0].length ;
            }

            public boolean isAutoIncrement(int column) throws SQLException {
                throw new NotImplementedException(".isAutoIncrement");
            }

            public boolean isCaseSensitive(int column) throws SQLException {
                throw new NotImplementedException(".isCaseSensitive");
            }

            public boolean isSearchable(int column) throws SQLException {
                throw new NotImplementedException(".isSearchable");
            }

            public boolean isCurrency(int column) throws SQLException {
                throw new NotImplementedException(".isCurrency");
            }

            public int isNullable(int column) throws SQLException {
                throw new NotImplementedException(".isNullable");
            }

            public boolean isSigned(int column) throws SQLException {
                throw new NotImplementedException(".isSigned");
            }

            public int getColumnDisplaySize(int column) throws SQLException {
                throw new NotImplementedException(".getColumnDisplaySize");
            }

            public String getColumnLabel(int column) throws SQLException {
                throw new NotImplementedException(".getColumnLabel");
            }

            public String getColumnName(int column) throws SQLException {
                throw new NotImplementedException(".getColumnName");
            }

            public String getSchemaName(int column) throws SQLException {
                throw new NotImplementedException(".getSchemaName");
            }

            public int getPrecision(int column) throws SQLException {
                throw new NotImplementedException(".getPrecision");
            }

            public int getScale(int column) throws SQLException {
                throw new NotImplementedException(".getScale");
            }

            public String getTableName(int column) throws SQLException {
                throw new NotImplementedException(".getTableName");
            }

            public String getCatalogName(int column) throws SQLException {
                throw new NotImplementedException(".getCatalogName");
            }

            public int getColumnType(int column) throws SQLException {
                throw new NotImplementedException(".getColumnType");
            }

            public String getColumnTypeName(int column) throws SQLException {
                throw new NotImplementedException(".getColumnTypeName");
            }

            public boolean isReadOnly(int column) throws SQLException {
                throw new NotImplementedException(".isReadOnly");
            }

            public boolean isWritable(int column) throws SQLException {
                throw new NotImplementedException(".isWritable");
            }

            public boolean isDefinitelyWritable(int column) throws SQLException {
                throw new NotImplementedException(".isDefinitelyWritable");
            }

            public String getColumnClassName(int column) throws SQLException {
                throw new NotImplementedException(".getColumnClassName");
            }
        };
    }

    public Object getObject(int columnIndex) throws SQLException {
        return rows[rowIndex][columnIndex-1] ;
    }

    public Object getObject(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getObject");
    }

    public int findColumn(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.findColumn");
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getCharacterStream");
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getCharacterStream");
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBigDecimal");
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBigDecimal");
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.isBeforeFirst");
    }

    public boolean isAfterLast() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.isAfterLast");
    }

    public boolean isFirst() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.isFirst");
    }

    public boolean isLast() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.isLast");
    }

    public void beforeFirst() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.beforeFirst");
    }

    public void afterLast() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.afterLast");
    }

    public boolean first() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.first");
    }

    public boolean last() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.last");
    }

    public int getRow() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getRow");
    }

    public boolean absolute(int row) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.absolute");
    }

    public boolean relative(int rows) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.relative");
    }

    public boolean previous() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.previous");
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.setFetchDirection");
    }

    public int getFetchDirection() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getFetchDirection");
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.setFetchSize");
    }

    public int getFetchSize() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getFetchSize");
    }

    public int getType() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getType");
    }

    public int getConcurrency() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getConcurrency");
    }

    public boolean rowUpdated() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.rowUpdated");
    }

    public boolean rowInserted() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.rowInserted");
    }

    public boolean rowDeleted() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.rowDeleted");
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateNull");
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBoolean");
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateByte");
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateShort");
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateInt");
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateLong");
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateFloat");
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateDouble");
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBigDecimal");
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateString");
    }

    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBytes");
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateDate");
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateTime");
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateTimestamp");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateAsciiStream");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBinaryStream");
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateCharacterStream");
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateObject");
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateObject");
    }

    public void updateNull(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateNull");
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBoolean");
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateByte");
    }

    public void updateShort(String columnName, short x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateShort");
    }

    public void updateInt(String columnName, int x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateInt");
    }

    public void updateLong(String columnName, long x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateLong");
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateFloat");
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateDouble");
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBigDecimal");
    }

    public void updateString(String columnName, String x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateString");
    }

    public void updateBytes(String columnName, byte x[]) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBytes");
    }

    public void updateDate(String columnName, Date x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateDate");
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateTime");
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateTimestamp");
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateAsciiStream");
    }

    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBinaryStream");
    }

    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateCharacterStream");
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateObject");
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateObject");
    }

    public void insertRow() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.insertRow");
    }

    public void updateRow() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateRow");
    }

    public void deleteRow() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.deleteRow");
    }

    public void refreshRow() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.refreshRow");
    }

    public void cancelRowUpdates() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.cancelRowUpdates");
    }

    public void moveToInsertRow() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.moveToInsertRow");
    }

    public void moveToCurrentRow() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.moveToCurrentRow");
    }

    public Statement getStatement() throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getStatement");
    }

    public Object getObject(int i, Map map) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getObject");
    }

    public Ref getRef(int i) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getRef");
    }

    public Blob getBlob(int i) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBlob");
    }

    public Clob getClob(int i) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getClob");
    }

    public Array getArray(int i) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getArray");
    }

    public Object getObject(String colName, Map map) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getObject");
    }

    public Ref getRef(String colName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getRef");
    }

    public Blob getBlob(String colName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getBlob");
    }

    public Clob getClob(String colName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getClob");
    }

    public Array getArray(String colName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getArray");
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getDate");
    }

    public Date getDate(String columnName, Calendar cal) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getDate");
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getTime");
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getTime");
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getTimestamp");
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getTimestamp");
    }

    public URL getURL(int columnIndex) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getURL");
    }

    public URL getURL(String columnName) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.getURL");
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateRef");
    }

    public void updateRef(String columnName, Ref x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateRef");
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBlob");
    }

    public void updateBlob(String columnName, Blob x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateBlob");
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateClob");
    }

    public void updateClob(String columnName, Clob x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateClob");
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateArray");
    }

    public void updateArray(String columnName, Array x) throws SQLException {
        throw new NotImplementedException("imcode.server.db.impl.MockResultSet.updateArray");
    }
}
