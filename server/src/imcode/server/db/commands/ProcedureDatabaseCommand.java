package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import org.apache.commons.lang.ArrayUtils;

public abstract class ProcedureDatabaseCommand implements DatabaseCommand {

    final String procedure;
    final String[] params;

    protected ProcedureDatabaseCommand( String procedure, String[] params ) {

        this.procedure = procedure;
        this.params = params;
    }

    public String toString() {
        return "procedure "+procedure+" "+ArrayUtils.toString( params ) ;
    }

}
