package imcode.server.db;

import org.apache.log4j.Logger;

import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

public class DatabaseService {
    final static int MIMER = 0;
    final static int SQL_SERVER = 1;
    final static int MY_SQL = 2;

    private static final char END_OF_COMMAND = ';';
    private final static String FILE_PATH = "E:/backuppas/projekt/imcode2003/imCMS/1.3/sql/";
    private static final String DROP_TABLES = "tables/drop.new.sql";
    private static final String CREATE_TABLES = "tables/create.new.sql";
    private static final String ADD_TYPE_DATA = "data/types.new.sql";
    private static final String INSERT_NEW_DATA = "data/newdb.new.sql";

    private static String SQL92_TYPE_TIMESTAMP = "timestamp";
    private static String SQL_SERVER_TIMESTAMP_TYPE = "datetime";

    private static Logger log = Logger.getLogger( DatabaseService.class );

    private SQLProcessor sqlProcessor;
    private int databaseType;

    public DatabaseService( int databaseType, String hostName, int port, String databaseName, String user, String password ) {
        this.databaseType = databaseType;
        String serverUrl = null;
        String jdbcDriver = null;
        String serverName = null;

        String jdbcUrl;
        switch( databaseType ) {
            case MIMER:
                jdbcDriver = "com.mimer.jdbc.Driver";
                jdbcUrl = "jdbc:mimer://";
                serverUrl = jdbcUrl + hostName + ":" + port + "/" + databaseName;
                serverName = "Mimer test server";
                break;
            case SQL_SERVER:
                jdbcDriver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
                jdbcUrl = "jdbc:microsoft:sqlserver://";
                serverUrl = jdbcUrl + hostName + ":" + port + ";DatabaseName=" + databaseName;
                serverName = "SQL Server test server";
                break;
            case MY_SQL:
                jdbcDriver = "com.mysql.jdbc.Driver";
                jdbcUrl = "jdbc:mysql://";
                serverUrl = jdbcUrl + hostName + ":" + port + "/" + databaseName;
                serverName = "MySql test server";
                break;
        }

        int maxConnectionCount = 20;
        try {
            ConnectionPool connectionPool = new ConnectionPoolForNonPoolingDriver( serverName, jdbcDriver, serverUrl, user, password, maxConnectionCount );
            sqlProcessor = new SQLProcessor( connectionPool );
        } catch( Exception ex ) {
            log.fatal( "Couldn't initialize connection pool: serverName :' " + serverName + "', jdbcDriver : '" + jdbcDriver + "', serverUrl : " + serverUrl + "', user : '" + user + "', password :' " + password + "'"  );
            log.fatal( ex );
        }
    }

    void initializeDatabase() {
        try {
            Vector commands = readCommandsFromFile( DROP_TABLES );
            executeCommands( commands );
            log.info( "Dropped tables" );

            commands = readCommandsFromFile( CREATE_TABLES );
            switch( databaseType ) {
                case SQL_SERVER :
                case MY_SQL:
                    commands = changeTimestampToDateTimeType( commands );
                    break;
            }
            executeCommands( commands );
            log.info( "Created tables" );

            commands = readCommandsFromFile( ADD_TYPE_DATA );
            sqlProcessor.executeBatchUpdate( (String[])commands.toArray( new String[commands.size()] ) );
            log.info( "Added type data" );

            commands = readCommandsFromFile( INSERT_NEW_DATA );
            switch( databaseType ) {
                case MY_SQL:
                    commands = changeCharInCurrentTimestampCast( commands );
                    break;
            }
            sqlProcessor.executeBatchUpdate( (String[])commands.toArray( new String[commands.size()] ) );
            log.info( "Inserted data, finished!" );
        }
         catch( IOException ex ) {
            log.fatal("Couldn't open a file ", ex );
         }
    }

    private Vector changeCharInCurrentTimestampCast( Vector commands ) {
        Vector modifiedCommands = new Vector();
        // CAST(CURRENT_TIMESTAMP AS CHAR(80)) is changed to CAST(CURRENT_TIMESTAMP AS CHAR)"
        String patternStr = "CAST *\\( *CURRENT_TIMESTAMP *AS *CHAR *\\( *[0-9]+ *\\) *\\)";
        String replacementStr = "CAST(CURRENT_TIMESTAMP AS CHAR)";
        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);

        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            Matcher matcher = pattern.matcher(command);
            String modifiedCommand = matcher.replaceAll(replacementStr);
            modifiedCommands.add( modifiedCommand );
        }

        return modifiedCommands;
    }

    private Vector changeTimestampToDateTimeType( Vector commands ) {
        Vector modifiedCommands = new Vector();
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            String modifiedCommand = command.replaceAll( SQL92_TYPE_TIMESTAMP, SQL_SERVER_TIMESTAMP_TYPE );
            modifiedCommands.add( modifiedCommand );
        }
        return modifiedCommands;
    }

     private void executeCommands( Vector commands ) {
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
//            System.out.println( command.length() < 25 ? command : command.substring( 0, 25 ) );
            sqlProcessor.executeUpdate( command, null );
        }

        // I tried to use batchUpdate but for the current Mimer driver that only works for SELECT, INSERT, UPDATE,
        // and DELETE operations and this method is also used for create table and drop table commands. /Hasse
        // sqlProcessor.executeBatchUpdate( conn, (String[])commands.toArray( new String[commands.size()] ) );
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

    class Table_roles {
        private int id;
        private String name;

        public Table_roles( int id, String name ) {
            this.id = id;
            this.name = name;
        }

        public boolean equals( Object o ) {
            if( this == o )
                return true;
            if( !(o instanceof Table_roles) )
                return false;

            final Table_roles roleTableData = (Table_roles)o;

            if( id != roleTableData.id )
                return false;
            if( name != null ? !name.equals( roleTableData.name ) : roleTableData.name != null )
                return false;

            return true;
        }
    }

    /**
     *  Document me!
     */
    // todo: rename to getallroles _but_ user
    // todo: user RoleDomainObject or create one if it dosn't exist
    public Table_roles[] sprocGetAllRoles() {
        String sql = "SELECT role_id, role_name FROM roles ORDER BY role_name";
        Object[] paramValues = null;

        SQLProcessor.ResultProcessor resultProcessor = new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int id = rs.getInt( "role_id" );
                String name = rs.getString( "role_name" );

                Table_roles result = null;
                if( !name.equalsIgnoreCase( "users" ) ) { // all roles but user should be mapped.
                    result = new Table_roles( id, name );
                }
                return result;
            }
        };

        ArrayList result = sqlProcessor.executeQuery( sql, paramValues, resultProcessor );
        return (Table_roles[])result.toArray( new Table_roles[result.size()] );
    }

    static class Table_users {
        public Table_users( int userId, String loginName, String password, String firstName, String lastName, String title, String company, String address, String city, String zip, String country, String county_council, String emailAddress, int external, int lastPage, int archiveMode, int langId, int userType, int active, Timestamp createDate ) {
            this.userId = userId;
            this.loginName = loginName;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.title = title;
            this.company = company;
            this.address = address;
            this.city = city;
            this.zip = zip;
            this.country = country;
            this.county_council = county_council;
            this.emailAddress = emailAddress;
            this.external = external;
            this.lastPage = lastPage;
            this.archiveMode = archiveMode;
            this.langId = langId;
            this.userType = userType;
            this.active = active;
            this.createDate = createDate;
        }

        private int userId;
        private String loginName;
        private String password;
        private String firstName;
        private String lastName;
        private String title;
        private String company;
        private String address;
        private String city;
        private String zip;
        private String country;
        private String county_council;
        private String emailAddress;
        private int external;
        private int lastPage;
        private int archiveMode;
        private int langId;
        private int userType;
        private int active;
        private Timestamp createDate;

        public boolean equals( Object o ) {
            if( this == o )
                return true;
            if( !(o instanceof Table_users) )
                return false;

            final Table_users usersTabelData = (Table_users)o;

            if( active != usersTabelData.active )
                return false;
            if( archiveMode != usersTabelData.archiveMode )
                return false;
            if( external != usersTabelData.external )
                return false;
            if( langId != usersTabelData.langId )
                return false;
            if( lastPage != usersTabelData.lastPage )
                return false;
            if( userId != usersTabelData.userId )
                return false;
            if( userType != usersTabelData.userType )
                return false;
            if( address != null ? !address.equals( usersTabelData.address ) : usersTabelData.address != null )
                return false;
            if( city != null ? !city.equals( usersTabelData.city ) : usersTabelData.city != null )
                return false;
            if( company != null ? !company.equals( usersTabelData.company ) : usersTabelData.company != null )
                return false;
            if( country != null ? !country.equals( usersTabelData.country ) : usersTabelData.country != null )
                return false;
            if( county_council != null ? !county_council.equals( usersTabelData.county_council ) : usersTabelData.county_council != null )
                return false;
//            if( createDate != null ? !createDate.equals( usersTabelData.createDate ) : usersTabelData.createDate != null )
//                return false;
            if( emailAddress != null ? !emailAddress.equals( usersTabelData.emailAddress ) : usersTabelData.emailAddress != null )
                return false;
            if( firstName != null ? !firstName.equals( usersTabelData.firstName ) : usersTabelData.firstName != null )
                return false;
            if( lastName != null ? !lastName.equals( usersTabelData.lastName ) : usersTabelData.lastName != null )
                return false;
            if( loginName != null ? !loginName.equals( usersTabelData.loginName ) : usersTabelData.loginName != null )
                return false;
            if( password != null ? !password.equals( usersTabelData.password ) : usersTabelData.password != null )
                return false;
            if( title != null ? !title.equals( usersTabelData.title ) : usersTabelData.title != null )
                return false;
            if( zip != null ? !zip.equals( usersTabelData.zip ) : usersTabelData.zip != null )
                return false;

            return true;
        }

        public String toString() {
            return createDate.toString();
        }
    }

    /**
     *  Document me!
     * @return
     */

    Table_users[] sprocGetAllUsers() {
        String sql = "select user_id,login_name,login_password,first_name,last_name,title,company,address,city,zip,country,county_council,email,external,last_page,archive_mode,lang_id,user_type,active,create_date from users ORDER BY last_name";
        Object[] paramValues = null;

        SQLProcessor.ResultProcessor resultProcessor = new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                Table_users result = null;
                int userId = rs.getInt( "user_id" );
                String loginName = rs.getString("login_name");
                String password = rs.getString("login_password");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String title = rs.getString("title");
                String company = rs.getString("company");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String zip = rs.getString("zip");
                String country = rs.getString("country");
                String county_council = rs.getString("county_council");
                String emailAddress = rs.getString("email");
                int external = rs.getInt("external");
                int lastPage = rs.getInt("last_page");
                int archiveMode = rs.getInt("archive_mode");
                int langId = rs.getInt("lang_id");
                int userType = rs.getInt("user_type");
                int active = rs.getInt("active");
                Timestamp createDate = rs.getTimestamp("create_date");
                result = new Table_users( userId, loginName, password, firstName, lastName, title, company, address, city, zip, country, county_council, emailAddress, external, lastPage, archiveMode, langId, userType, active, createDate );
                return result;
            }
        };

        ArrayList result = sqlProcessor.executeQuery( sql, paramValues, resultProcessor );
        return (Table_users[])result.toArray(new Table_users[result.size()]);
    }

    static class ViewTemplateGroup {
        private int id;
        private String simpleName;

        public ViewTemplateGroup( int id, String simpleName ) {
            this.id = id;
            this.simpleName = simpleName;
        }

        public boolean equals( Object o ) {
            if( this == o )
                return true;
            if( !(o instanceof ViewTemplateGroup) )
                return false;

            final ViewTemplateGroup viewTemplateGroup = (ViewTemplateGroup)o;

            if( id != viewTemplateGroup.id )
                return false;
            if( simpleName != null ? !simpleName.equals( viewTemplateGroup.simpleName ) : viewTemplateGroup.simpleName != null )
                return false;

            return true;
        }
    }

    ViewTemplateGroup[] sprocGetTemplatesInGroup( int groupId ) {
        String sql = "SELECT t.template_id,simple_name FROM  templates t JOIN templates_cref c ON  t.template_id = c.template_id " +
            "WHERE c.group_id = ? " +
            "ORDER BY simple_name";
        Object[] paramValues = new Object[]{ new Integer(groupId) };

        SQLProcessor.ResultProcessor resultProcessor = new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                ViewTemplateGroup result = null;
                int templateId = rs.getInt("template_id");
                String simpleName = rs.getString("simple_name");
                result = new ViewTemplateGroup( templateId, simpleName );
                return result;
            }
        };

        ArrayList result = sqlProcessor.executeQuery( sql, paramValues, resultProcessor );
        return (ViewTemplateGroup[])result.toArray( new ViewTemplateGroup[result.size()] );
    }

    // todo, ska man behöva stoppa in userId här? Kan man inte bara få ett unikt?
    int sproc_AddNewuser( Table_users userData ) {
        String sql = "INSERT INTO users (user_id, login_name, login_password, first_name, last_name, title, company, address, city, zip, country, county_council, email, external, last_page, archive_mode, lang_id, user_type, active, create_date ) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] paramValues = new Object[]{ new Integer( userData.userId ), userData.loginName, userData.password, userData.firstName, userData.lastName, userData.title, userData.company, userData.address, userData.city, userData.zip, userData.country, userData.county_council, userData.emailAddress, new Integer( userData.external ), new Integer(1001), new Integer(0), new Integer(userData.langId), new Integer(userData.userType), new Integer( userData.active ), userData.createDate };
        return sqlProcessor.executeUpdate( sql, paramValues );
    }
}
