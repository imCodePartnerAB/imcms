package imcode.server.db;

import org.apache.log4j.Logger;

import java.sql.*;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;

public class Database {
    final static int MIMER = 0;
    final static int SQL_SERVER = 1;
    //final static int MY_SQL = 2;

    private static final char END_OF_COMMAND = ';';
    private final static String FILE_PATH = "E:/backuppas/projekt/imcode2003/imCMS/1.3/sql/";
    private static final String DROP_TABLES = "tables/drop.new.sql";
    private static final String CREATE_TABLES = "tables/create.new.sql";
    private static final String ADD_TYPE_DATA = "data/types.new.sql";
    private static final String INSERT_NEW_DATA = "data/newdb.new.sql";

    private static String SQL92_TYPE_TIMESTAMP = "timestamp";
    private static String SQL_SERVER_TIMESTAMP_TYPE = "datetime";

    public static void main( String[] args ) throws Exception {

        Database sqlServer = new Database( SQL_SERVER );
//        sqlServer.initializeDatabase();
        String resultSqlServer = sqlServer.test_sproc_getallroles();

        Database mimer = new Database(MIMER);
//        mimer.initializeDatabase();
        String resultMimer = mimer.test_sproc_getallroles();

        compare( resultSqlServer, resultMimer );
    }

    private static void compare( String sqlServer, String mimer ) {
        boolean equals = sqlServer.equals(mimer);
        if( equals ) {
            System.out.println( "ok" );
        } else {
            System.out.println( "Warning, not same result!" );
            System.out.println( "SqlServer: " + sqlServer );
            System.out.println( "Mimer    : " + mimer );
        }
    }

    private String test_sproc_getallroles() {
        String result = "";
        Role[] roles = sproc_getallroles();
        for( int i = 0; i < roles.length; i++ ) {
            Role role = roles[i];
            result += "" + role.getId() + ", " + role.getName();
        }
        return result;
    }

    private static Logger log = Logger.getLogger( Database.class );

    private ConnectionPool connectionPool;
    private SQLProcessor sqlProcessor = new SQLProcessor();
    private int databaseType;

    public Database( int databaseType ) {
        this.databaseType = databaseType;
        initConnectionPool( databaseType );
    }

    class Role {
        private int id;
        private String name;

        public Role( int id, String name ) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    // todo: rename to getallrolesexcept users
    public Role[] sproc_getallroles() {
        String sprocMethodName = "getallroles()";
        String select = "SELECT role_id, role_name ";
        String from = "FROM roles ";
        String orderBy = "ORDER BY role_name";
        String sql = select + from + orderBy;

        Vector roles = new Vector();
        ResultSet rs = null;
        Object[] values = null;

        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            rs = sqlProcessor.executeQuery( conn, sql, values );
            while( rs.next() ) {
                int id = rs.getInt( "role_id" );
                String name = rs.getString( "role_name" );
                if( !name.equalsIgnoreCase( "users" )) {
                    roles.add( new Role( id, name ));
                }
            }
        } catch( SQLException ex ) {
            log.fatal( sprocMethodName + " could not get a connection", ex );
        } finally {
            closeConnection( conn );
        }
        return (Role[])roles.toArray( new Role[roles.size()]);
    }

    private void closeConnection( Connection conn ) {
        try {
            if( conn != null ) {
                conn.close();
            }
        } catch( SQLException ex ) {
            // Swallow
        }
    }

    void initializeDatabase() throws Exception {
        executeCommandsFromFile( DROP_TABLES );
        executeCommandsFromFile( CREATE_TABLES );
        executeCommandsFromFile( ADD_TYPE_DATA );
        executeCommandsFromFile( INSERT_NEW_DATA );
    }

    private void executeCommandsFromFile( String fileName ) throws Exception {
        Vector commands = readCommandsFromFile( fileName );

        if( databaseType == SQL_SERVER ) {
            commands = changeSQLSpecificDateTimeDataType( commands );
        }

        executeCommands( commands );
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

    private void executeCommands( Vector commands ) throws Exception {
        Connection conn = connectionPool.getConnection();
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

    private void initConnectionPool( int databaseServer ) {
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
        try {
            connectionPool = new ConnectionPoolForNonPoolingDriver( "", jdbcDriver, serverUrl, user, password, maxConnectionCount );
        }
        catch( Exception ex ) {
            log.fatal("Couldn't initialize connection pool", ex );
        }
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
