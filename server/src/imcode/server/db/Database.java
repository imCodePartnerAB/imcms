package imcode.server.db;

public interface Database {

    String[] sqlProcedure(String procedure, String[] params) ;

    int sqlUpdateProcedure( String procedure, String[] params );

    String sqlProcedureStr( String procedure, String[] params );

    int sqlUpdateQuery(String sqlStr, String[] params);

    String[][] sqlProcedureMulti(String procedure, String[] params);

    String[] sqlQuery(String sqlStr, String[] params);

    String sqlQueryStr(String sqlStr, String[] params);

    String[][] sqlQueryMulti(String sqlstr, String[] params);

    Object executeCommand( DatabaseCommand databaseCommand );
}