package imcode.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class DateTestProgram {
    public static void main( String[] args ) throws Exception {
        String serverUrl;
        String jdbcDriver;
        String user;
        String password;

        boolean useSQLServer = false;
        if( useSQLServer ) {
            jdbcDriver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";

            String jdbcUrl = "jdbc:microsoft:sqlserver://";
            String host = "localhost";
            String port = "1433";
            String databaseName = "test";
            serverUrl = jdbcUrl + host + ":" + port + ";DatabaseName=" + databaseName;
            user = "sa";
            password = "sa";
        } else {
            jdbcDriver = "com.mimer.jdbc.Driver";
            //jdbcDriver = "com.mimer.jtrace.driver";

            String jdbcUrl = "jdbc:mimer://";
            String host = "localhost";
            String port = "1360";// default for mimer
            String databaseName = "test";
            serverUrl = jdbcUrl + host + ":" + port + "/" + databaseName;
            user = "sysadm";
            password = "admin";
        }

        Connection conn = getConnectionPool( jdbcDriver, serverUrl, user, password );

        String sql = "SELECT * from test" ;
        PreparedStatement prepStatement = conn.prepareStatement(sql);
        boolean ok = prepStatement.execute();
        System.out.println( "ok = " + ok );
        ResultSet rs = prepStatement.getResultSet();
        while( rs.next() ) {
            Timestamp timestamp = rs.getTimestamp("datetime");
            System.out.println( "datetime.getTimestamp() = " + timestamp.toString() );
        }

        String updateSql = "INSERT INTO test (datetime) VALUES (?)";
        PreparedStatement updateStatment = conn.prepareStatement(updateSql);
        java.util.Date utilDate = new java.util.Date();

        updateStatment.setTimestamp( 1, new Timestamp( utilDate.getTime() ) );
/*
        String dateStr = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( utilDate );
        System.out.println( "dateStr "  + utilDate );
*/

        ok = updateStatment.execute();
        System.out.println( "ok= " + ok );
    }

    private static Connection getConnectionPool( String jdbcDriver, String serverUrl, String user, String password ) throws Exception {
        int maxConnectionCount = 20;
        ConnectionPoolForNonPoolingDriver connectionPool = new ConnectionPoolForNonPoolingDriver( "", jdbcDriver, serverUrl, user, password, maxConnectionCount );
        Connection conn = connectionPool.getConnection();
        return conn;
    }
}
