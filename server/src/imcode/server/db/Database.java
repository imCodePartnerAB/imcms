package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;

public interface Database {

    String[] executeArrayProcedure(String procedure, Object[] parameters) throws DatabaseException;

    int executeUpdateProcedure( String procedure, Object[] parameters ) throws DatabaseException;

    String executeStringProcedure( String procedure, Object[] parameters ) throws DatabaseException;

    String[][] execute2dArrayProcedure(String procedure, Object[] parameters) throws DatabaseException;

    int executeUpdateQuery(String sqlStr, Object[] parameters) throws DatabaseException;

    String[] executeArrayQuery(String sqlStr, Object[] parameters) throws DatabaseException;

    String executeStringQuery(String sqlStr, Object[] parameters) throws DatabaseException;

    String[][] execute2dArrayQuery(String sqlstr, Object[] parameters) throws DatabaseException;

    Object executeCommand( DatabaseCommand databaseCommand ) throws DatabaseException;
}