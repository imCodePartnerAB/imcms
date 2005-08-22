package imcode.server.db.commands;

import imcode.server.db.DatabaseCommand;
import org.apache.commons.lang.ArrayUtils;

public abstract class ProcedureDatabaseCommand implements DatabaseCommand {

    final String procedure;
    final Object[] parameters;

    protected ProcedureDatabaseCommand( String procedure, Object[] parameters ) {

        this.procedure = procedure;
        this.parameters = parameters;
    }

    public String toString() {
        return "procedure "+procedure+" "+ArrayUtils.toString( parameters ) ;
    }

}
