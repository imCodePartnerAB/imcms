package imcode.server.db;

public interface DatabaseCommand {

    void executeOn( DatabaseConnection connection ) ;

}
