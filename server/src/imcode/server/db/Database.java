package imcode.server.db;

import java.sql.*;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;

public class Database {
    private static final char END_OF_COMMAND = ';';
    private final static String FILE_PATH = "E:/backuppas/projekt/imcode2003/imCMS/1.3/sql/";
    private static final String DROP_TABLES = "tables/drop.new.sql";
    private static final String CREATE_TABLES = "tables/create.new.sql";
    private static final String ADD_TYPE_DATA = "data/types.new.sql";
    private static final String INSERT_NEW_DATA = "data/newdb.new.sql";

    public static void main( String[] args ) throws Exception {
        Database accessor = new Database();
        accessor.initializeDatabase( SQL_SERVER );
        accessor.initializeDatabase( MIMER );
    }

    void initializeDatabase( int serverType ) throws Exception {
        executeCommandsFromFile( serverType, DROP_TABLES );
        executeCommandsFromFile( serverType, CREATE_TABLES );
        executeCommandsFromFile( serverType, ADD_TYPE_DATA );
        executeCommandsFromFile( serverType, INSERT_NEW_DATA );
    }

    private final static int MIMER = 0;
    private final static int SQL_SERVER = 1;
    private final static int MY_SQL = 2;
    private static String SQL92_TYPE_TIMESTAMP = "timestamp";
    private static String SQL_SERVER_TIMESTAMP_TYPE = "datetime";

    private ConnectionPool connectionPool;
    private SQLProcessor sqlProcessor = new SQLProcessor();

    private void executeCommandsFromFile( int databaseServer, String fileName ) throws Exception {
        Vector commands = readCommandsFromFile( fileName );

        if( databaseServer == SQL_SERVER ) {
            commands = changeSQLSpecificDateTimeDataType( commands );
        }

        executeCommands( databaseServer, commands );
    }

    private Vector changeSQLSpecificDateTimeDataType( Vector commands ) {
        Vector modifiedCommands = new Vector();
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            String modifiedCommand = static_changeSQLServerTimestampType( command );
            modifiedCommands.add( modifiedCommand );
        }
        return modifiedCommands;
    }

    private void executeCommands( int databaseServer, Vector commands ) throws Exception {
        Connection conn = getConnectionPool( databaseServer );
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            System.out.println( command.length() < 25 ? command : command.substring( 0, 25 ) );
            sqlProcessor.executeUpdate( conn, command, null );
        }
    }

    private Vector readCommandsFromFile( String fileName ) throws IOException {
        File sqlScriptingFile = new File( FILE_PATH + fileName );

        BufferedReader reader = new BufferedReader( new FileReader( sqlScriptingFile ) );
        StringBuffer commandBuff = new StringBuffer();
        Vector commands = new Vector();
        String aLine;
        do {
            aLine = reader.readLine();
            if( null != aLine && !aLine.equals( "" ) ) {
                commandBuff.append( aLine );
                int lastCharPos = aLine.length() - 1;
                char endChar = aLine.charAt( lastCharPos );
                if( END_OF_COMMAND == endChar ) {
                    commandBuff.deleteCharAt( commandBuff.length() - 1 );
                    String command = commandBuff.toString();
                    commands.add( command );
                    commandBuff.setLength( 0 );
                }
            }
        } while( null != aLine );
        return commands;
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

    private static String static_changeSQLServerTimestampType( String createCommand ) {
        String result = createCommand.replaceAll( SQL92_TYPE_TIMESTAMP, SQL_SERVER_TIMESTAMP_TYPE );
        return result;
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
