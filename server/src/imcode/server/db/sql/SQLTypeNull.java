package imcode.server.db.sql;

public class SQLTypeNull {
    private int type;

    /**
     * @param fieldType @see java.sql.Types
     */
    public SQLTypeNull( int fieldType ) {
        type = fieldType;
    }

    public int getFieldType() {
        return type;
    }

    public String toString() {
        return "Null sql type:" + type;
    }
}
