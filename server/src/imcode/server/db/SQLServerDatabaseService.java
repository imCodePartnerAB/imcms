package imcode.server.db;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

class SQLServerDatabaseService extends DatabaseService {

    private String TEXT_TYPE_INTERNATIONAL_SQL_SERVER = "NTEXT";
    private String TEXT_TYPE_SQL_SERVER = "TEXT";

    public SQLServerDatabaseService( String hostName, int port, String databaseName, String user, String password ) {
        super( Logger.getLogger( SQLServerDatabaseService.class ) );
        // log.debug( "Creating a 'SQL Server' database service");
        String jdbcDriver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        String jdbcUrl = "jdbc:microsoft:sqlserver://";
        String serverUrl = jdbcUrl + hostName + ":" + port + ";DatabaseName=" + databaseName;
        String serverName = "SQL Server test server";
        super.initConnectionPoolAndSQLProcessor( serverName, jdbcDriver, serverUrl, user, password );
    }

    ArrayList filterCreateCommands( ArrayList commands ) {
        commands = changeTimestampToDateTime( commands );
        commands = changeCHAR8000PlusToTextForSQLServer( commands );
        return commands;
    }

    /**
     * SQL Server dosen't support VARCHAR larger than VARCHAR(8000). This method replaces the occurences found (this far) in the code with
     * with TEXT and NTEXT type..
     * @param commands
     * @return
     */
    // todo Denna går att göra generellare, för tillfället implementerar den enbart > 10 000 generellt
    // todo samt det enskillda tillfället på 5000.
    private ArrayList changeCHAR8000PlusToTextForSQLServer( ArrayList commands ) {
        ArrayList modifiedCommands = new ArrayList();
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            // todo: detta borde gå att göra till ett generellt reg exp? för varje tal störren än 255 byt ut mot text?
            command = command.replaceAll( "VARCHAR\\s*\\(\\s*\\d{5,}\\s*\\)", TEXT_TYPE_SQL_SERVER); // "VARCHAR ( 15000 )" -> "TEXT"
            command = command.replaceAll( "NCHAR\\s*VARYING\\s*\\(\\s*5000\\s*\\)", TEXT_TYPE_INTERNATIONAL_SQL_SERVER); // "NCHAR VARYING ( 5000 )" -> "TEXT"
            modifiedCommands.add( command );
        }
        return modifiedCommands;
    }
}
