package imcode.server.db;

import java.io.File ;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MySQLDatabaseService extends DatabaseService {

    private static final String TEXT_TYPE_MY_SQL = "TEXT";

    public MySQLDatabaseService( String hostName, Integer port, String databaseName, String user, String password, Integer maxConnectionCount  ) {
        super( Logger.getLogger( MySQLDatabaseService.class ) );
        // log.debug( "Creating a 'My SQL' database service");
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String jdbcUrl = "jdbc:mysql://";
        String serverUrl = jdbcUrl + hostName + ":" + port + "/" + databaseName;
        String serverName = "MySql test server";

        super.initConnectionPoolAndSQLProcessor( serverName, jdbcDriver, serverUrl, user, password, maxConnectionCount );
    }

    ArrayList filterInsertCommands( ArrayList commands ) {
        commands = changeCharInCurrentTimestampCast( commands );
        return commands;
    }

    private ArrayList changeCharInCurrentTimestampCast( ArrayList commands ) {
        ArrayList modifiedCommands = new ArrayList();
        // CAST(CURRENT_TIMESTAMP AS CHAR(80)) is changed to CAST(CURRENT_TIMESTAMP AS CHAR)"
        String patternStr = "CAST *\\( *CURRENT_TIMESTAMP *AS *CHAR *\\( *[0-9]+ *\\) *\\)";
        Pattern pattern = Pattern.compile( patternStr, Pattern.CASE_INSENSITIVE );
        String replacementStr = "CAST(CURRENT_TIMESTAMP AS CHAR)";

        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            Matcher matcher = pattern.matcher( command );
            String modifiedCommand = matcher.replaceAll( replacementStr );
            modifiedCommands.add( modifiedCommand );
        }

        return modifiedCommands;
    }

    ArrayList filterCreateCommands( ArrayList commands ) {
        commands = changeTimestampToDateTime( commands );
        commands = changeCHAR256PlusToTextForMySQL( commands );
        return commands;
    }

    /**
     * MySQL dosen't have VARCHAR larger than VARCHAR(255). This method replaces the occurences found (this far) in the code
     * with TEXT type.
     * @param commands
     * @return
     */
    // todo Denna går att göra generellare, för tillfället implementerar den enbart > 1 000 generellt
    private ArrayList changeCHAR256PlusToTextForMySQL( ArrayList commands ) {
        ArrayList modifiedCommands = new ArrayList();
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            command = command.replaceAll( "VARCHAR\\s*\\(\\s*\\d{4,}\\s*\\)", TEXT_TYPE_MY_SQL); // "VARCHAR ( 1000 )" -> "TEXT"
            command = command.replaceAll( "NCHAR\\s*VARYING\\s*\\(\\s*\\d{4,}\\s*\\)", TEXT_TYPE_MY_SQL); // "NCHAR VARYING ( 15000 )" -> "TEXT"
            modifiedCommands.add( command );
        }
        return modifiedCommands;
    }
}
