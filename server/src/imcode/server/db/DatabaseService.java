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
            log.fatal( "Couldn't initialize connection pool: serverName :' " + serverName + "', jdbcDriver : '" + jdbcDriver + "', serverUrl : " + serverUrl + "', user : '" + user + "', login_password :' " + password + "'" );
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
                case SQL_SERVER:
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
        } catch( IOException ex ) {
            log.fatal( "Couldn't open a file ", ex );
        }
    }

    private Vector changeCharInCurrentTimestampCast( Vector commands ) {
        Vector modifiedCommands = new Vector();
        // CAST(CURRENT_TIMESTAMP AS CHAR(80)) is changed to CAST(CURRENT_TIMESTAMP AS CHAR)"
        String patternStr = "CAST *\\( *CURRENT_TIMESTAMP *AS *CHAR *\\( *[0-9]+ *\\) *\\)";
        String replacementStr = "CAST(CURRENT_TIMESTAMP AS CHAR)";
        Pattern pattern = Pattern.compile( patternStr, Pattern.CASE_INSENSITIVE );

        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            Matcher matcher = pattern.matcher( command );
            String modifiedCommand = matcher.replaceAll( replacementStr );
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
        // sqlProcessor.executeBatchUpdate( con, (String[])commands.toArray( new String[commands.size()] ) );
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

    public Table_roles[] sproc_GetAllRoles_but_user() {
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

        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, resultProcessor );
        return (Table_roles[])queryResult.toArray( new Table_roles[queryResult.size()] );
    }

    static class Table_users {
        int user_id;
        private String login_name;
        private String login_password;
        private String first_name;
        private String last_name;
        private String title;
        private String company;
        private String address;
        private String city;
        private String zip;
        private String country;
        private String county_council;
        private String email;
        private int external;
        private int last_page;
        private int archive_mode;
        private int lang_id;
        private int user_type;
        int active;
        private Timestamp create_date;

        public Table_users( int user_id, String login_name, String login_password, String first_name, String last_name, String title, String company, String address, String city, String zip, String country, String county_council, String email, int external, int last_page, int archive_mode, int lang_id, int user_type, int active, Timestamp create_date ) {
            this.user_id = user_id;
            this.login_name = login_name;
            this.login_password = login_password;
            this.first_name = first_name;
            this.last_name = last_name;
            this.title = title;
            this.company = company;
            this.address = address;
            this.city = city;
            this.zip = zip;
            this.country = country;
            this.county_council = county_council;
            this.email = email;
            this.external = external;
            this.last_page = last_page;
            this.archive_mode = archive_mode;
            this.lang_id = lang_id;
            this.user_type = user_type;
            this.active = active;
            this.create_date = create_date;
        }

        public boolean equals( Object o ) {
            if( this == o )
                return true;
            if( !(o instanceof Table_users) )
                return false;

            final Table_users usersTabelData = (Table_users)o;

            if( active != usersTabelData.active )
                return false;
            if( archive_mode != usersTabelData.archive_mode )
                return false;
            if( external != usersTabelData.external )
                return false;
            if( lang_id != usersTabelData.lang_id )
                return false;
            if( last_page != usersTabelData.last_page )
                return false;
            if( user_id != usersTabelData.user_id )
                return false;
            if( user_type != usersTabelData.user_type )
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
            //            if( create_date != null ? !create_date.equals( usersTabelData.create_date ) : usersTabelData.create_date != null )
            //                return false;
            if( email != null ? !email.equals( usersTabelData.email ) : usersTabelData.email != null )
                return false;
            if( first_name != null ? !first_name.equals( usersTabelData.first_name ) : usersTabelData.first_name != null )
                return false;
            if( last_name != null ? !last_name.equals( usersTabelData.last_name ) : usersTabelData.last_name != null )
                return false;
            if( login_name != null ? !login_name.equals( usersTabelData.login_name ) : usersTabelData.login_name != null )
                return false;
            if( login_password != null ? !login_password.equals( usersTabelData.login_password ) : usersTabelData.login_password != null )
                return false;
            if( title != null ? !title.equals( usersTabelData.title ) : usersTabelData.title != null )
                return false;
            if( zip != null ? !zip.equals( usersTabelData.zip ) : usersTabelData.zip != null )
                return false;

            return true;
        }

        public String toString() {
            return create_date.toString();
        }
    }

    /**
     *  Document me!
     * @return
     */

    Table_users[] sproc_GetAllUsers_OrderByLastName() {
        String sql = "select user_id,login_name,login_password,first_name,last_name,title,company,address,city,zip,country,county_council,email,external,last_page,archive_mode,lang_id,user_type,active,create_date from users ORDER BY last_name";

        SQLProcessor.ResultProcessor resultProcessor = new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                Table_users result = null;
                int user_id = rs.getInt( "user_id" );
                String login_name = rs.getString( "login_name" );
                String login_password = rs.getString( "login_password" );
                String first_name = rs.getString( "first_name" );
                String last_name = rs.getString( "last_name" );
                String title = rs.getString( "title" );
                String company = rs.getString( "company" );
                String address = rs.getString( "address" );
                String city = rs.getString( "city" );
                String zip = rs.getString( "zip" );
                String country = rs.getString( "country" );
                String county_council = rs.getString( "county_council" );
                String email = rs.getString( "email" );
                int external = rs.getInt( "external" );
                int last_page = rs.getInt( "last_page" );
                int archive_mode = rs.getInt( "archive_mode" );
                int lang_id = rs.getInt( "lang_id" );
                int user_type = rs.getInt( "user_type" );
                int active = rs.getInt( "active" );
                Timestamp create_date = rs.getTimestamp( "create_date" );
                result = new Table_users( user_id, login_name, login_password, first_name, last_name, title, company, address, city, zip, country, county_council, email, external, last_page, archive_mode, lang_id, user_type, active, create_date );
                return result;
            }
        };

        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, resultProcessor );
        return (Table_users[])queryResult.toArray( new Table_users[queryResult.size()] );
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

    ViewTemplateGroup[] sproc_GetTemplatesInGroup( int groupId ) {
        String sql = "SELECT t.template_id,simple_name FROM  templates t JOIN templates_cref c ON  t.template_id = c.template_id " + "WHERE c.group_id = ? " + "ORDER BY simple_name";
        Object[] paramValues = new Object[]{new Integer( groupId )};

        SQLProcessor.ResultProcessor resultProcessor = new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                ViewTemplateGroup result = null;
                int templateId = rs.getInt( "template_id" );
                String simpleName = rs.getString( "simple_name" );
                result = new ViewTemplateGroup( templateId, simpleName );
                return result;
            }
        };

        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, resultProcessor );
        return (ViewTemplateGroup[])queryResult.toArray( new ViewTemplateGroup[queryResult.size()] );
    }

    // todo, ska man behöva stoppa in user_id här? Kan man inte bara få ett unikt?
    int sproc_AddNewuser( Table_users userData ) {
        String sql = "INSERT INTO users (user_id, login_name, login_password, first_name, last_name, title, company, address, city, zip, country, county_council, email, external, last_page, archive_mode, lang_id, user_type, active, create_date ) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] paramValues = new Object[]{new Integer( userData.user_id ), userData.login_name, userData.login_password, userData.first_name, userData.last_name, userData.title, userData.company, userData.address, userData.city, userData.zip, userData.country, userData.county_council, userData.email, new Integer( userData.external ), new Integer( 1001 ), new Integer( 0 ), new Integer( userData.lang_id ), new Integer( userData.user_type ), new Integer( userData.active ), userData.create_date};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    // todo: flytta in detta i addNewUser istället, och se till att det fungerar concurrently.
    int sproc_getHighestUserId() {
        String columnName = "user_id";
        String TableName = "users";
        return 1 + getMaxIntValue( TableName, columnName );
    }

    int sproc_updateUser( Table_users userData ) {
        String sql = "Update users set " + "login_name = ?, " + "login_password = ?, " + "first_name = ?, " + "last_name = ?, " + "title = ?, " + "company = ?, " + "address =  ?, " + "city = ?, " + "zip = ?, " + "country = ?, " + "county_council =?, " + "email = ?, " + "user_type = ?, " + "active = ?, " + "lang_id = ? " + "WHERE user_id = ?";
        Object[] paramValues = new Object[]{userData.login_name, userData.login_password, userData.first_name, userData.last_name, userData.title, userData.company, userData.address, userData.city, userData.zip, userData.country, userData.county_council, userData.email, new Integer( userData.user_type ), new Integer( userData.active ), new Integer( userData.lang_id ), new Integer( userData.user_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    /*
    This function adds a new phone numbers to the db. Used by AdminUserProps
    */
    // todo: se till att detta fungerar även om fler försöker göra insert samtigit.
    // Todo: nöja mig med synchroniced på metoden? Eller se till att testa några gånger tills det går igenom?
    int sproc_phoneNbrAdd( int userId, String number, int phoneType ) {
        String tableName = "phones";
        String primaryKeyColumnName = "phone_id";
        int newPhoneId = 1 + getMaxIntValue( tableName, primaryKeyColumnName );

        String sql = "INSERT INTO PHONES ( phone_id , number , user_id, phonetype_id ) VALUES ( ? , ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( newPhoneId ), number, new Integer( userId ), new Integer( phoneType )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    private int getMaxIntValue( String tableName, String columnName ) {
        String sql = "SELECT MAX(" + columnName + ") FROM " + tableName;
        SQLProcessor.ResultProcessor resultProcessor = new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int id = rs.getInt( 1 );
                return new Integer( id );
            }
        };
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, resultProcessor );
        Integer id = (Integer)(queryResult.get( 0 ));
        if( id == null ) {
            return 0;
        } else {
            return id.intValue();
        }
    }

    // todo: ta bort från samtliga forreign key ställen (och inte bara från user_roles_crossref)? phones, user_flags_crossref, user_rights, useradmin_role_crossref
    // todo: Or Split into two, depending on how it is used.
    int sproc_delUser( int user_id ) {
        SQLProcessor.SQLTransaction trans = sqlProcessor.startTransaction();
        int rowCount = 0;
        try {
            String sqlUserRoles = "DELETE FROM user_roles_crossref WHERE user_id = " + user_id;
            rowCount = trans.executeUpdate( sqlUserRoles, null );

            String sqlUsers = "DELETE FROM users WHERE user_id = " + user_id;
            rowCount += trans.executeUpdate( sqlUsers, null );

            trans.commit();
        } catch( SQLException ex ) {
            log.warn( "sproc_delUser(" + user_id + ") failed", ex );
            trans.rollback();
        }
        return rowCount;
    }

    /**
     * Add role a Useradmin have administration rights on user with that roles.
     * A useradmin is only allowed to administrate users with those roles.
     * @param user_id The user id for the user, that user should have the role Useradmin (1)
     * @param role_id The role of other users that this user should hav (new) permissions to administrate.
     * @return 1 if succed, otherwise 0.
     */
    int sproc_AddUseradminPermissibleRoles( int user_id, int role_id ) {
        String sql = "INSERT INTO useradmin_role_crossref (user_id, role_id ) " +
            "VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{ new Integer(user_id), new Integer(role_id) };
       return sqlProcessor.executeUpdate( sql, paramValues );
    }


    static class Table_user_roles_crossref {
        int user_id;
        int role_id;

        public Table_user_roles_crossref( int user_id, int role_id ) {
            this.user_id = user_id;
            this.role_id = role_id;
        }

        public boolean equals( Object o ) {
            if( this == o )
                return true;
            if( !(o instanceof Table_user_roles_crossref) )
                return false;

            final Table_user_roles_crossref table_user_roles_crossref = (Table_user_roles_crossref)o;

            if( role_id != table_user_roles_crossref.role_id )
                return false;
            if( user_id != table_user_roles_crossref.user_id )
                return false;

            return true;
        }
    }

    /**
     * Adds a role to a particular user
     */
    int sproc_AddUserRole( Table_user_roles_crossref userRoleTupple ) {
        // Lets check if the role already exists
       ArrayList querryResult = sql_selectUserAndRoleFrom_user_roles_crossref( userRoleTupple );

       if( querryResult.size() == 0 ) {
           Object[] paramValues = new Object[]{ new Integer( userRoleTupple.user_id ), new Integer( userRoleTupple.role_id ) };
           String sqlInsert = "INSERT INTO user_roles_crossref(user_id, role_id) VALUES( ? , ? )";
           return sqlProcessor.executeUpdate( sqlInsert, paramValues );
       } else {
           return 0;
       }
    }

    int sproc_ChangeUserActiveStatus( int user_id, boolean active ) {
        String sql = "UPDATE users SET active = ? WHERE user_id = ? ";
        Integer activeInteger = new Integer( active?1:0 );
        Integer userIdInteger = new Integer( user_id );
        Object[] paramValues = new Object[]{ activeInteger, userIdInteger };
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    private ArrayList sql_selectUserAndRoleFrom_user_roles_crossref( Table_user_roles_crossref userRoleTupple ) {
        String sqlSelect = "SELECT user_id, role_id FROM user_roles_crossref WHERE user_id = ? AND role_id = ? ";
        Object[] paramValues = new Object[]{ new Integer( userRoleTupple.user_id ), new Integer( userRoleTupple.role_id ) };
        return sqlProcessor.executeQuery( sqlSelect, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_user_roles_crossref( rs.getInt("user_id"), rs.getInt("role_id"));
            }
        } );
    }

    // todo döp om denna till, userExists eller nåt och ändra signaturen
    /**
     * Because different databses treats upper/lower case differently this method makes a
     * ignoreCases  match.
     * @param userName
     * @return
     */
    String sproc_FindUserName( String userName ) {
        String sql = "SELECT login_name FROM users WHERE LOWER(login_name) = ? ";
        Object[] paramValues = new Object[]{ userName.toLowerCase() };
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return rs.getString( "login_name" );
            }
        } );
        if( 0 == queryResult.size() ) {
            return null;
        } else {
            return (String)queryResult.get(0);
        }
    }

    // todo döp om till deleteAllPhonenumbersForUser eller nåt
    // todo klumpa ihop med delete userses?
    int sproc_DelPhoneNr( int user_id ) {
        String sql = "DELETE FROM phones WHERE user_id = ? ";
        Object[] paramValues = new Object[]{ new Integer( user_id ) };
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    int sproc_PhoneNbrDelete( int phone_id ) {
        String sql = "DELETE FROM PHONES WHERE phone_id = ? ";
        Object[] paramValues = new Object[]{ new Integer( phone_id ) };
        return sqlProcessor.executeUpdate( sql, paramValues );
    }
}
