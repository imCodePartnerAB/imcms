package imcode.server.db;

import java.util.Map;

public class MockDatabase implements Database {

    public String[] sqlProcedure( String procedure, String[] params ) {
        return new String[0];  // TODO
    }

    public Map sqlProcedureHash( String procedure, String[] params ) {
        return null;  // TODO
    }

    public int sqlUpdateProcedure( String procedure, String[] params ) {
        return 0;  // TODO
    }

    public String sqlProcedureStr( String procedure, String[] params ) {
        return null;  // TODO
    }

    public int sqlUpdateQuery( String sqlStr, String[] params ) {
        return 0;  // TODO
    }

    public String[][] sqlProcedureMulti( String procedure, String[] params ) {
        return new String[0][];  // TODO
    }

    public String[] sqlQuery( String sqlStr, String[] params ) {
        return new String[0];  // TODO
    }

    public String sqlQueryStr( String sqlStr, String[] params ) {
        return null;  // TODO
    }

    public String[][] sqlQueryMulti( String sqlstr, String[] params ) {
        return new String[0][];  // TODO
    }

    public void executeTransaction( DatabaseCommand databaseCommand ) {
        // TODO
    }
}
