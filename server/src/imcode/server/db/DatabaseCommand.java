package imcode.server.db;

public abstract class DatabaseCommand {

    public abstract void executeOn( DatabaseConnection connection ) ;

}
