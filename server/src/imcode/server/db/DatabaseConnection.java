package imcode.server.db;

public interface DatabaseConnection {

    void executeUpdate( String sql, String[] parameters );

    Number executeUpdateAndGetGeneratedKey( String sql, String[] parameters );
}