package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;

public interface Database {

    String[] executeArrayProcedure(String procedure, String[] params) throws DatabaseException;

    int executeUpdateProcedure( String procedure, String[] params ) throws DatabaseException;

    String executeStringProcedure( String procedure, String[] params ) throws DatabaseException;

    String[][] execute2dArrayProcedure(String procedure, String[] params) throws DatabaseException;

    int executeUpdateQuery(String sqlStr, Object[] params) throws DatabaseException;

    String[] executeArrayQuery(String sqlStr, String[] params) throws DatabaseException;

    String executeStringQuery(String sqlStr, String[] params) throws DatabaseException;

    String[][] execute2dArrayQuery(String sqlstr, String[] params) throws DatabaseException;

    Object executeCommand( DatabaseCommand databaseCommand ) throws DatabaseException;
}