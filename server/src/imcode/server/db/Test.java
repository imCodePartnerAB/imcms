package imcode.server.db;

import java.sql.*;

public class Test {
    public static void main( String[] args ) throws Exception {
        maxKeyGeneratorTest();

    }

    private static void maxKeyGeneratorTest() {
/*        throws SQLException {
        DatabaseService sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQLSERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        //sqlServer.initializeDatabase();

        SQLProcessor sqlProcessor = sqlServer.getSQLProcessor();
        final ConnectionPool connectionPool = sqlProcessor.getConnectionPool();

        new Thread( new Runnable() {
                public void run() {
                    try {
                        Connection conn = setUpConnection( connectionPool );
                        int maxMetaId1 = getMax( conn );
                        //Thread.sleep( 10 );
                        insertNextRow( conn, maxMetaId1, "Första tråden" );
                        conn.commit();
                        System.out.println( "första tråden klar" );
                    } catch( Exception ex ) {
                        System.out.println( ex );
                    }
                }
            }).start();

        //Thread.sleep( 10 );
        System.out.println( "Nu borde vi vänta!" );
        Connection conn = setUpConnection( connectionPool );
        int maxMetaId1 = getMax( conn );
        insertNextRow( conn, maxMetaId1, "Andra tråden" );
        conn.commit();
        conn.close();
        System.out.println( "klart!" );
    }

    private static Connection setUpConnection( ConnectionPool connectionPool ) throws SQLException {
        final Connection conn = connectionPool.getConnection();
        conn.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
        conn.setAutoCommit(false);
        return conn;
    }

    private static void insertNextRow( final Connection conn, int maxMetaId1, String name ) throws SQLException {
        PreparedStatement insertStatment = conn.prepareStatement("insert into sections (section_id, section_name) values (?,?)" );
        int nextMax = maxMetaId1 + 1;
        insertStatment.setObject( 1, new Integer(nextMax) );
        insertStatment.setObject( 2, name );
        insertStatment.executeUpdate();
    }

    private static int getMax( final Connection conn ) throws SQLException {
        Statement maxStatment = conn.createStatement();
        maxStatment.execute( "select max(section_id) from sections");
        ResultSet rs = maxStatment.getResultSet();
        rs.next();
        int maxMetaId1 = rs.getInt( 1 );
        System.out.println( "max section_id = " + maxMetaId1 );
        return maxMetaId1;
*/
    }
}
