package imcode.server.db;

import imcode.server.db.exceptions.DatabaseException;
import org.apache.commons.lang.UnhandledException;

public class ExceptionUnhandlingDatabase implements Database {

    Database wrappedDatabase ;

    public ExceptionUnhandlingDatabase( Database delegate ) {
        this.wrappedDatabase = delegate;
    }

    public String[][] execute2dArrayProcedure( String procedure, String[] params ) {
        try {
            return wrappedDatabase.execute2dArrayProcedure( procedure, params );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public String[][] execute2dArrayQuery( String sqlstr, String[] params ) {
        try {
            return wrappedDatabase.execute2dArrayQuery( sqlstr, params );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public String[] executeArrayProcedure( String procedure, String[] params ) {
        try {
            return wrappedDatabase.executeArrayProcedure( procedure, params );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public String[] executeArrayQuery( String sqlStr, String[] params ) {
        try {
            return wrappedDatabase.executeArrayQuery( sqlStr, params );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public Object executeCommand( DatabaseCommand databaseCommand ) {
        try {
            return wrappedDatabase.executeCommand( databaseCommand );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public String executeStringProcedure( String procedure, String[] params ) {
        try {
            return wrappedDatabase.executeStringProcedure( procedure, params );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public String executeStringQuery( String sqlStr, String[] params ) {
        try {
            return wrappedDatabase.executeStringQuery( sqlStr, params );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public int executeUpdateProcedure( String procedure, String[] params ) {
        try {
            return wrappedDatabase.executeUpdateProcedure( procedure, params );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public int executeUpdateQuery( String sqlStr, Object[] params ) {
        try {
            return wrappedDatabase.executeUpdateQuery( sqlStr, params );
        } catch ( DatabaseException e ) {
            throw new UnhandledException( e );
        }
    }

    public Database getWrappedDatabase() {
        return wrappedDatabase;
    }
}
