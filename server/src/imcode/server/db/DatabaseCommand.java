package imcode.server.db;

public interface DatabaseCommand {

    Object executeOn( DatabaseConnection connection ) ;

}
