package imcode.server.db;

public interface DatabaseConnection {

    void executeUpdateQuery( String sql, String[] parameters );

    String executeUpdateAndSelectString( String sql, String[] parameters );

    Number executeUpdateAndGetGeneratedKey( String sql, String[] parameters );
}