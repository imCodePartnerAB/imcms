package imcode.server.db;

import java.sql.*;
import java.io.*;
import java.util.StringTokenizer;

public class DatabaseAcessor {

    private final static String FILE_PATH = "E:/backuppas/projekt/imcode2003/imCMS/1.3/sql/tables/";

    public static void main( String[] args ) throws Exception {
        DatabaseAcessor accessor = new DatabaseAcessor();

        accessor.executeCommandsFromFile( SQL_SERVER, "drop.sql" );
        accessor.executeCommandsFromFile( SQL_SERVER, "create.sql" );

        accessor.executeCommandsFromFile( MIMER, "drop.sql" );
        accessor.executeCommandsFromFile( MIMER, "create.sql" );
    }

    private final static int MIMER = 0;
    private final static int SQL_SERVER = 1;
    private final static int MY_SQL = 2;
    private static String SQL92_TYPE_TIMESTAMP = "timestamp";
    private static String SQL_SERVER_TIMESTAMP_TYPE = "datetime";

    private ConnectionPool connectionPool;
    private SQLProcessor sqlProcessor = new SQLProcessor();

    private void executeCommandsFromFile( int databaseServer, String fileName ) throws Exception {
        Connection conn = getConnectionPool( databaseServer );
        File sqlScriptingFile = new File( FILE_PATH + fileName );

        String allFileContent = static_readFile( sqlScriptingFile );

        if( databaseServer == SQL_SERVER ) {
            allFileContent = static_changeSQLServerTimestampType( allFileContent );
        }
        String[] result = static_getAllCommands( allFileContent );
        String[] dropCommands = result;
        executeCommands( conn, dropCommands );
    }

    private Connection getConnectionPool( int databaseServer ) throws Exception {
        String serverUrl = null;
        String jdbcDriver = null;
        String user = null;
        String password = null;

        switch( databaseServer ) {
            case MIMER:
                jdbcDriver = "com.mimer.jdbc.Driver";
                serverUrl = static_getMimerUrl();
                user = "sysadm";
                password = "admin";
                break;
            case SQL_SERVER:
                jdbcDriver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
                serverUrl = static_getSQLServerUrl();
                user = "sa";
                password = "sa";
                break;
        }

        int maxConnectionCount = 20;
        connectionPool = new ConnectionPoolForNonPoolingDriver( "", jdbcDriver, serverUrl, user, password, maxConnectionCount );
        Connection conn = connectionPool.getConnection();
        return conn;
    }

    private void executeCommands( Connection conn, String[] commands ) {
        for( int i = 0; i < commands.length; i++ ) {
            String command = commands[i];
            System.out.println( command.length()<25?command:command.substring(0,25) );
            sqlProcessor.executeUpdate( conn, command, null );
        }
    }

    private static String static_changeSQLServerTimestampType( String createCommand ){
        String result = createCommand.replaceAll( SQL92_TYPE_TIMESTAMP, SQL_SERVER_TIMESTAMP_TYPE );
        return result;
    }

    private static String[] static_getAllCommands( String allFileContent ) {
        StringTokenizer tokenizer = new StringTokenizer( allFileContent, ";" );
        String[] result = new String[tokenizer.countTokens()];
        int i = 0;
        while( tokenizer.hasMoreTokens() ) {
            String temp = tokenizer.nextToken();
            result[i] = temp;
            i++;
        }
        return result;
    }

    private static String static_readFile( File sqlScriptingFile ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader( sqlScriptingFile ) );
        String aLine;
        StringBuffer allFileContent = new StringBuffer();
        do {
            aLine = reader.readLine();
            if( null != aLine ) {
                allFileContent.append( aLine );
            }
        } while( null != aLine );
        reader.close();
        return allFileContent.toString();
    }

    private static String static_getMimerUrl() {
        String serverUrl;
        String jdbcUrl = "jdbc:mimer://";
        String host = "localhost";
        String port = "1360";// default for mimer
        String databaseName = "test";
        serverUrl = jdbcUrl + host + ":" + port + "/" + databaseName;
        return serverUrl;
    }

    private static String static_getSQLServerUrl() {
        String serverUrl;
        String jdbcUrl = "jdbc:microsoft:sqlserver://";
        String host = "localhost";
        String port = "1433";
        String databaseName = "test";
        serverUrl = jdbcUrl + host + ":" + port + ";DatabaseName=" + databaseName;
        return serverUrl;
    }

}
