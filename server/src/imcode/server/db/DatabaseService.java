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

/**
 * STOP! Before changing anyting in this class, make sure to run (all) the test in class TestDatabaseService.
 * They can take a while, but it is essentialt to keep this class working with multiple databases that those test are
 * run before and after a change, and that new test is added as soon as a new functionality in this class is added.
 * /Hasse
 */
public class DatabaseService {
    final static int MIMER = 0;
    final static int SQL_SERVER = 1;
    final static int MY_SQL = 2;

    private static final char END_OF_COMMAND = ';';
    private final static String FILE_PATH = "E:/backuppas/projekt/imcode2003/imCMS/1.3/sql/multipledatabases/";
    private static final String DROP_TABLES = "1.droptables.sql";
    private static final String CREATE_TABLES = "2.createtables.sql";
    private static final String ADD_TYPE_DATA = "3.inserttypedata.sql";
    private static final String INSERT_TYPE_DATA = "4.insertdefaultpagesusersandroles.sql";

    private String ADITIONAL_TEST_DATA = "5.insertaditionaltestdata.sql";

    private static String SQL92_TYPE_TIMESTAMP = "timestamp";
    private static String SQL_SERVER_TIMESTAMP_TYPE = "datetime";

    private static Logger log = Logger.getLogger( DatabaseService.class );

    private SQLProcessor sqlProcessor;

    SQLProcessor getSQLProcessor() { return sqlProcessor; }

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

            commands = readCommandsFromFile( INSERT_TYPE_DATA );
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

    void initTestData() throws IOException {
        Vector commands = readCommandsFromFile( ADITIONAL_TEST_DATA );
        sqlProcessor.executeBatchUpdate( (String[])commands.toArray( new String[commands.size()] ) );
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

    Table_roles[] sproc_GetAllRoles_but_user() {
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
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
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
        } );
        return (Table_users[])queryResult.toArray( new Table_users[queryResult.size()] );
    }

    static class View_TemplateGroup {
        private int id;
        private String simpleName;

        public View_TemplateGroup( int id, String simpleName ) {
            this.id = id;
            this.simpleName = simpleName;
        }

        public boolean equals( Object o ) {
            if( this == o )
                return true;
            if( !(o instanceof View_TemplateGroup) )
                return false;

            final View_TemplateGroup viewTemplateGroup = (View_TemplateGroup)o;

            if( id != viewTemplateGroup.id )
                return false;
            if( simpleName != null ? !simpleName.equals( viewTemplateGroup.simpleName ) : viewTemplateGroup.simpleName != null )
                return false;

            return true;
        }
    }

    View_TemplateGroup[] sproc_GetTemplatesInGroup( int groupId ) {
        String sql = "SELECT t.template_id,simple_name FROM  templates t JOIN templates_cref c ON  t.template_id = c.template_id " + "WHERE c.group_id = ? " + "ORDER BY simple_name";
        Object[] paramValues = new Object[]{new Integer( groupId )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                View_TemplateGroup result = null;
                int templateId = rs.getInt( "template_id" );
                String simpleName = rs.getString( "simple_name" );
                result = new View_TemplateGroup( templateId, simpleName );
                return result;
            }
        } );
        return (View_TemplateGroup[])queryResult.toArray( new View_TemplateGroup[queryResult.size()] );
    }

    // todo, ska man behöva stoppa in user_id här? Kan man inte bara få ett unikt?
    int sproc_AddNewuser( Table_users userData ) {
        String sql = "INSERT INTO users (user_id, login_name, login_password, first_name, last_name, title, company, address, city, zip, country, county_council, email, external, last_page, archive_mode, lang_id, user_type, active, create_date ) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] paramValues = new Object[]{new Integer( userData.user_id ), userData.login_name, userData.login_password, userData.first_name, userData.last_name, userData.title, userData.company, userData.address, userData.city, userData.zip, userData.country, userData.county_council, userData.email, new Integer( userData.external ), new Integer( 1001 ), new Integer( 0 ), new Integer( userData.lang_id ), new Integer( userData.user_type ), new Integer( userData.active ), userData.create_date};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    // todo: flytta in detta i addNewUser istället, och se till att det fungerar concurrently.
    // todo: kolla att det inte är highest+1 som förväntas.
    int sproc_getHighestUserId() {
        String columnName = "user_id";
        String TableName = "users";
        return getMaxIntValue( TableName, columnName );
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
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int id = rs.getInt( 1 );
                return new Integer( id );
            }
        } );
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
        Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( role_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    /**
     * Adds a role to a particular user
     */
    int sproc_AddUserRole( int user_id, int role_id ) {
        // Lets check if the role already exists
        String sqlSelect = "SELECT role_id FROM user_roles_crossref WHERE user_id = ? AND role_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( role_id )};
        ArrayList querryResult = sqlProcessor.executeQuery( sqlSelect, paramValues, new SQLProcessor.ResultProcessor() {
                            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                                return new Integer(rs.getInt( "role_id" ));
                            }
                        } );

        if( querryResult.size() == 0 ) {
            String sqlInsert = "INSERT INTO user_roles_crossref(user_id, role_id) VALUES( ? , ? )";
            return sqlProcessor.executeUpdate( sqlInsert, paramValues );
        } else {
            return 0;
        }
    }

    int sproc_ChangeUserActiveStatus( int user_id, boolean active ) {
        String sql = "UPDATE users SET active = ? WHERE user_id = ? ";
        Integer activeInteger = new Integer( active ? 1 : 0 );
        Integer userIdInteger = new Integer( user_id );
        Object[] paramValues = new Object[]{activeInteger, userIdInteger};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    /**
     * Because different databses treats upper/lower case differently this method makes a
     * ignoreCases  match.
     * @param userName
     * @return
     */
    // todo döp om denna till, userExists eller nåt
    boolean sproc_FindUserName( String userName ) {
        String sql = "SELECT login_name FROM users WHERE LOWER(login_name) = ? ";
        Object[] paramValues = new Object[]{userName.toLowerCase()};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return rs.getString( "login_name" );
            }
        } );
        return queryResult.size() == 1;
    }

    // todo döp om till deleteAllPhonenumbersForUser eller nåt
    // todo klumpa ihop med delete userses?
    int sproc_DelPhoneNr( int user_id ) {
        String sql = "DELETE FROM phones WHERE user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    int sproc_PhoneNbrDelete( int phone_id ) {
        String sql = "DELETE FROM PHONES WHERE phone_id = ? ";
        Object[] paramValues = new Object[]{new Integer( phone_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    int sproc_PhoneNbrUpdate( int user_id, int phone_id, String number, int phonetype_id ) {
        String sql = "UPDATE phones SET number = ?, phonetype_id = ? " +
            "WHERE user_id = ? AND phone_id = ? ";
        Object[] paramValues = new Object[]{number, new Integer( phonetype_id ),
                                            new Integer( user_id ), new Integer( phone_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    String sproc_GetPhonetypeName( int phonetype_id, int lang_id ) {
        String sql = "select typename from phonetypes " +
            "where phonetype_id = ? and lang_id = ? ";
        Object[] paramValues = new Object[]{new Integer( phonetype_id ), new Integer( lang_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return rs.getString( 1 );
            }
        } );
        return (String)queryResult.get( 0 );
    }

    static class View_phonetypes {
        View_phonetypes( int phonetype_id, String typename ) {
            this.phonetype_id = phonetype_id;
            this.typename = typename;
        }

        int phonetype_id;
        String typename;
    }

    View_phonetypes[] sproc_GetPhonetypes_ORDER_BY_phonetype_id( int lang_id ) {
        String sql = " SELECT  phonetype_id, typename FROM phonetypes WHERE lang_id = ? ORDER BY phonetype_id";
        Object[] paramValues = new Object[]{new Integer( lang_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int phonetype_id = rs.getInt( "phonetype_id" );
                String typename = rs.getString( "typename" );
                return new View_phonetypes( phonetype_id, typename );
            }
        } );
        return (View_phonetypes[])queryResult.toArray( new View_phonetypes[queryResult.size()] );
    }

    static class View_phone {
        public View_phone( int phone_id, String phoneNumber ) {
            this.phone_id = phone_id;
            this.phoneNumber = phoneNumber;
        }

        int phone_id;
        String phoneNumber;
    }

    /**
     * Used to generate a list with all type of users. Used from UserChangePrefs
     * @param user_id
     * @return
     */
    // todo: Warning, this method used to RTIM the phone numer result, not any longer...
    View_phone[] sproc_GetUserPhones( int user_id ) {
        String sql = "SELECT p.phone_id, p.number FROM users u , phones p " +
            "WHERE u.user_id = p.user_id AND u.user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int phone_id = rs.getInt( "phone_id" );
                String phoneNumber = rs.getString( "number" );
                return new View_phone( phone_id, phoneNumber );
            }
        } );
        return (View_phone[])queryResult.toArray( new View_phone[queryResult.size()] );
    }

    static class View_userAndPhone {
        public View_userAndPhone( int phone_id, String number, int user_id, int phonetype_id, String typename ) {
            this.phone_id = phone_id;
            this.number = number;
            this.user_id = user_id;
            this.phonetype_id = phonetype_id;
            this.typename = typename;
        }

        int phone_id;
        String number;
        int user_id;
        int phonetype_id;
        String typename;
    }

    // todo: Do we realy need to return user_id?
    // todo: This should be able to be used instead of sproc_GetUserPhones, why not?
    View_userAndPhone[] sproc_GetUserPhoneNumbers( int user_id ) {
        String sql = "SELECT phones.phone_id, phones.number, phones.user_id, phones.phonetype_id, phonetypes.typename " +
            "FROM phones " +
            "INNER JOIN users ON phones.user_id = users.user_id " +
            "INNER JOIN phonetypes ON phones.phonetype_id = phonetypes.phonetype_id AND users.lang_id = phonetypes.lang_id " +
            "WHERE phones.user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int phone_id = rs.getInt("phone_id");
                String number = rs.getString("number");
                int user_id = rs.getInt("user_id");
                int phonetype_id = rs.getInt("phonetype_id");
                String typename = rs.getString("typename");
                return new View_userAndPhone( phone_id, number, user_id, phonetype_id, typename );
            }
        } );
        return (View_userAndPhone[])queryResult.toArray( new View_userAndPhone[queryResult.size()] );
    }

    int sproc_DocumentDelete( int meta_id ) {
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        int rowCount = 0;
        try {
            Object[] paramValues = new Object[]{ new Integer(meta_id) };
            rowCount += transaction.executeUpdate( "delete from meta_classification where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from childs where to_meta_id = 	?", paramValues );
            rowCount += transaction.executeUpdate( "delete from childs where meta_id =	?", paramValues );
            rowCount += transaction.executeUpdate( "delete from text_docs where meta_id = 	?", paramValues );
            rowCount += transaction.executeUpdate( "delete from texts where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from images where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from roles_rights where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from user_rights where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from url_docs where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from browser_docs where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from fileupload_docs where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from frameset_docs where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from new_doc_permission_sets_ex where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from new_doc_permission_sets where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from doc_permission_sets_ex where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from doc_permission_sets where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from includes where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from meta_section where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from meta where meta_id = ?", paramValues );
            transaction.commit();
        } catch( SQLException e ) {
            transaction.rollback();
        };
        return rowCount;
    }

    // todo: Döp om till documentExixts eller nåt...
    boolean sproc_FindMetaId( int meta_id ) {
        String sql = "SELECT meta_id FROM meta WHERE meta_id = ?";
        Object[] paramValues = new Object[]{ new Integer( meta_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                return new Integer( meta_id );
            }
        } );
        return queryResult.size() == 1;
    }

    /**
     *
     * @param meta_id Document that will have the link
     * @param existing_meta_id Document that will be linked to
     * @param doc_menu_no Menu number in meta_id
     * @return 0 if already exists, 1 if new link was added.
     */
    // todo: Testa denna!!! Och gå igenom nogrannt!!!
    int sproc_AddExistingDocToMenu( int meta_id, int existing_meta_id, int doc_menu_no ) {
        // test if this is the first child
        String sqlLinksCount = "select count(*) from childs where meta_id = ?  and menu_sort = ? ";
        Object[] paramValuesLinksCount = new Object[]{ new Integer( meta_id ), new Integer( doc_menu_no )};
        ArrayList countResult = sqlProcessor.executeQuery( sqlLinksCount, paramValuesLinksCount, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer(rs.getInt(1));
            }
        } );
        Integer countItem = (Integer)countResult.get(0);

        int manualSortOrder = 500;
        if( countItem.intValue() > 0 ) {// update manual_sort_order
            String sqlSortOrder = "select max(manual_sort_order) from childs where meta_id = ? and menu_sort = ?";
            Object[] paramValuesSortOrder = new Object[]{ new Integer( meta_id ), new Integer( doc_menu_no )};
            ArrayList sortOrderResult = sqlProcessor.executeQuery( sqlSortOrder, paramValuesSortOrder, new SQLProcessor.ResultProcessor() {
                Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                    return new Integer(rs.getInt(1));
                }
            } );
            manualSortOrder = ((Integer)sortOrderResult.get(0)).intValue() + 10;
        }

        //- test if child already exist in this menu. If not, then we will add the child to the menu.
        String sqlThisLinksCount = "select count(*) from childs where meta_id = ? and to_meta_id = ? and menu_sort = ?";
        Object[] paramValuesThisLinksCount = new Object[]{ new Integer( meta_id), new Integer( existing_meta_id ), new Integer(doc_menu_no ) } ;
        ArrayList queryResult = sqlProcessor.executeQuery( sqlThisLinksCount, paramValuesThisLinksCount, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer(rs.getInt(1));
            }
        } );

        Integer thisCountItem = (Integer)queryResult.get(0);
        if( thisCountItem.intValue() == 0 ) {
            String sql = "insert into childs( meta_id, to_meta_id, menu_sort, manual_sort_order) values( ?, ?, ?, ? )";
            Object[] paramValues = new Object[]{ new Integer( meta_id ), new Integer( existing_meta_id ), new Integer(doc_menu_no), new Integer(manualSortOrder) };
            return sqlProcessor.executeUpdate( sql, paramValues );
        } else {
            return 0;
        }
    }

    static class View_DocumentForUser {
        public View_DocumentForUser( int meta_id, int parentcount, String meta_headline, int doc_type ) {
            this.meta_id = meta_id;
            this.parentcount = parentcount;
            this.meta_headline = meta_headline;
            this.doc_type = doc_type;
        }

        int meta_id;
        int parentcount;
        String meta_headline;
        int doc_type;
    }
    /**
     * Lists documents user is allowed to see.
     * @param user_id
     * @param start
     * @param end
     * @return
     */
    // todo: döp om till getDocsForUser eller nåt.
    // todo: Tog bort i första raden ett DISTINCT från COUNT(DISTINCT c.meta_id) till COUNT(c.meta_id), fundera igenom om det gör något?
    View_DocumentForUser[] sproc_getDocs( int user_id, int start, int end ) {
        String sql = "SELECT DISTINCT m.meta_id, COUNT(c.meta_id) parentcount, meta_headline, doc_type FROM meta m " +
            "LEFT JOIN childs c ON c.to_meta_id = m.meta_id " +
            "LEFT JOIN roles_rights rr  ON rr.meta_id = m.meta_id AND rr.set_id < 4 " +
            "JOIN user_roles_crossref urc ON urc.user_id = ? AND ( urc.role_id = 0 OR ( urc.role_id = rr.role_id ) OR m.shared = 1 ) " +
            "WHERE m.activate = 1 AND m.meta_id > (?-1) AND m.meta_id < (?+1) " +
            "GROUP BY m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id " +
            "ORDER BY m.meta_id";
        Object[] paramValues = new Object[]{ new Integer( user_id ), new Integer( start ), new Integer( end )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt("meta_id");
                int parentcount = rs.getInt("parentcount");
                String meta_headline = rs.getString("meta_headline");
                int doc_type = rs.getInt("doc_type");
                return new View_DocumentForUser(meta_id, parentcount, meta_headline, doc_type);
            }
        } );
        return (View_DocumentForUser[])queryResult.toArray( new View_DocumentForUser[ queryResult.size() ]);
    }

    static class View_ChildData {
        public View_ChildData( int to_meta_id, int menu_sort, int manual_sort_order, int doc_type, boolean archive, String target, Timestamp date_created, Timestamp date_modified, String meta_headline, String meta_text, String meta_image, String frame_name, Timestamp activated_datetime, Timestamp archived_datetime, String filename ) {
            this.to_meta_id = to_meta_id;
            this.menu_sort = menu_sort;
            this.manual_sort_order = manual_sort_order;
            this.doc_type = doc_type;
            this.archive = archive;
            this.target = target;
            this.date_created = date_created;
            this.date_modified = date_modified;
            this.meta_headline = meta_headline;
            this.meta_text = meta_text;
            this.meta_image = meta_image;
            this.frame_name = frame_name;
            this.activated_datetime = activated_datetime;
            this.archived_datetime = archived_datetime;
            this.filename = filename;
        }

        int to_meta_id;
        int menu_sort;
        int manual_sort_order;
        int doc_type;
        boolean archive;
        String target;
        Timestamp date_created;
        Timestamp date_modified;
        String meta_headline;
        String meta_text;
        String meta_image;
        String frame_name;
        Timestamp activated_datetime;
        Timestamp archived_datetime;
        String filename;
    }
    /**
     * Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
     * @param meta_id
     * @param user_id
     */
    // todo WARNING, i anropande kod måste en förändring ske!
    // todo Den bortkommenterade reden nedan beräknar om man har rätt att editera eller ej.
    // todo Se till att göra den kollen på annat sätt efteråt för varje dokument.
    View_ChildData[] sproc_getChilds( int meta_id, int user_id ) {
        Integer sortOrder = getMenuSortOrder( meta_id );
        String sql =
            "select to_meta_id, c.menu_sort,manual_sort_order, doc_type," +
            "  archive,target, date_created, date_modified," +
            "  meta_headline,meta_text,meta_image,frame_name," +
            "  activated_datetime,archived_datetime," +
//            "  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1))," +
            "fd.filename " +
            "from  childs c " +
            "join meta m " +
            "   on m.meta_id = c.to_meta_id " + // meta.meta_id corresponds to childs.to_meta_id
            "   and  m.activate > 0 " + // Only include the documents that are active in the meta table
            "   and  c.meta_id = ? " + // Only include documents that are children to this particular meta_id
            "left join roles_rights rr " + // We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin
            "   on c.to_meta_id = rr.meta_id " + // Only include rows with the documents we are interested in
            "left join doc_permission_sets dps " + // Include the permission_sets
            "   on c.to_meta_id = dps.meta_id " + // for each document
            "   and dps.set_id = rr.set_id " + // and only the sets for the roles we are interested in
            "   and dps.permission_id > 0 " + // and only the sets that have any permission
            "join user_roles_crossref urc " + // This table tells us which users have which roles
            "   on urc.user_id = ? " + // Only include the rows with the user we are interested in...
            "   and ( " +
            "      rr.role_id = urc.role_id " + //  Include rows where the users roles match the roles that have permissions on the documents
            "   or urc.role_id = 0" + // and also include the rows that tells us this user is a superadmin
            "      or ( " +
            "         m.show_meta != 0 " + //  and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
            "      ) " +
            "   ) " +
            "left join fileupload_docs fd " +
            "   on fd.meta_id = c.to_meta_id " +
            "group by to_meta_id, c.menu_sort,manual_sort_order, doc_type, archive,target, date_created, date_modified, meta_headline,meta_text,meta_image,frame_name, activated_datetime,archived_datetime, fd.filename ";
        Object[] paramValues = new Object[]{ new Integer( meta_id ), new Integer(user_id) };

        if( sortOrder.intValue() == 3 ) {
            sql += "order by  menu_sort,c.manual_sort_order desc";
        } else if( sortOrder.intValue() == 2 ) {
            sql += "order by  menu_sort,convert (varchar,date_modified,120) desc";
        } else if( sortOrder.intValue() == 1 ) {
            sql += "order by  menu_sort,meta_headline";
        }

        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int to_meta_id = rs.getInt("to_meta_id");
                int menu_sort = rs.getInt("menu_sort");
                int manual_sort_order = rs.getInt("manual_sort_order");
                int doc_type = rs.getInt("doc_type");
                boolean archive = (rs.getInt("archive")==1);
                String target = rs.getString("target");
                Timestamp date_created = rs.getTimestamp("date_created");
                Timestamp date_modified = rs.getTimestamp("date_modified");
                String meta_headline = rs.getString("meta_headline");
                String meta_text = rs.getString("meta_text");
                String meta_image = rs.getString("meta_image");
                String frame_name = rs.getString("frame_name");
                Timestamp activated_datetime = rs.getTimestamp("activated_datetime");
                Timestamp archived_datetime = rs.getTimestamp("archived_datetime");
                String filename = rs.getString("filename");
                return new View_ChildData( to_meta_id, menu_sort, manual_sort_order, doc_type, archive, target, date_created, date_modified, meta_headline,
                                           meta_text, meta_image, frame_name, activated_datetime, archived_datetime, filename );
            }
        } );
        return (View_ChildData[])queryResult.toArray( new View_ChildData[queryResult.size()] );
    }

    private Integer getMenuSortOrder( int meta_id ) {
        String sql = "select sort_order from text_docs where meta_id = ?";
        Object[] paramValues = new Object[]{ new Integer(meta_id)};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer(rs.getInt("sort_order"));
            }
        } );
        return (Integer)queryResult.get(0);
    }

    /*
    Detects if a user is administrator or not
    */

    /**
     *
     * @return role_ids
     */
    // In the databse sproc it returned the user_id, role_id, but the code that used it
    // assumed it was just roles. And only one role.
    // So a changed this to return only role_id's.
    // But I think that it should return a boolean true or fals if it is a
    boolean sproc_CheckAdminRights( int user_id ) {
        String sql = "SELECT roles.role_id FROM users " +
            "INNER JOIN user_roles_crossref ON users.user_id = user_roles_crossref.user_id " +
            "INNER JOIN roles ON user_roles_crossref.role_id = roles.role_id " +
            "WHERE roles.role_id = 0 AND users.user_id = ?";
        Object[] paramValues = new Object[]{ new Integer( user_id) } ;
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt("role_id"));
            }
        } );
        return queryResult.size() != 0;
    }

    /**
     * This procedure takes a list of document-ids (meta_ids)
     * and returns a list of which of those are file-docs.
     *
     * @param meta_ids
     * @return meta_id's that is files
     */
    int[] sproc_CheckForFileDocs( int[] meta_ids ) {
        String sql = "SELECT meta_id FROM meta WHERE doc_type = 8";
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt("meta_id"));
            }
        } );

        ArrayList fileDocIds = new ArrayList();
        for( int i = 0; i < meta_ids.length; i++ ) {
            Integer meta_id = new Integer(meta_ids[i]);
            if( queryResult.contains( meta_id ) ){
                fileDocIds.add( meta_id );
            }
        }

        int[] result = new int[ fileDocIds.size() ];
        for( int i = 0; i < result.length; i++ ) {
           result[i] = ((Integer)fileDocIds.get(i)).intValue();
        }
        return result;
    }

    /**
     *
     * @param userId
     * @param admin_role
     * @return
     */
    // todo, se till att denna inte anropas direkt utan att man anropar metoder isUserAdmin och isSuperAdmin istället.
    // med admin_role satt till 1 resp 2.
    boolean sproc_checkUserAdminrole( int user_id, int admin_role ) {
        String sql = "SELECT admin_role FROM user_roles_crossref " +
            "INNER JOIN roles ON user_roles_crossref.role_id = roles.role_id " +
            "WHERE (user_roles_crossref.user_id = ? ) AND (roles.admin_role = ? )";
        Object[] paramValues = new Object[]{ new Integer( user_id ), new Integer( admin_role ) };
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt("admin_role"));
            }
        } );
        return queryResult.size() == 1;
    }

    // todo: Döp om till hasUserSharePemissionForDocument
    boolean sproc_CheckUserDocSharePermission( int user_id, int meta_id ) {
        String sql = "SELECT m.meta_id FROM meta m " +
            "JOIN user_roles_crossref urc ON urc.user_id = ? AND m.meta_id = ? " +
            "LEFT join roles_rights rr ON rr.meta_id = m.meta_id AND rr.role_id = urc.role_id " +
            "WHERE ( shared = 1 OR	rr.set_id < 3 OR urc.role_id = 0 ) ";
        Object[] parameterValues = new Object[]{ new Integer( user_id ), new Integer( meta_id ) };
        ArrayList queryResult = sqlProcessor.executeQuery( sql, parameterValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer(rs.getInt("meta_id"));
            }
        } );
        return queryResult.size() == 1;
    }

    /**
     * Delete roles for a user
     * If roleId = -1 then the administrator is a Superadmin and we have to delete
     * all roles.
     * Else the administrator is a Useradmin and we delete only a one role
     *
     * @param user_id
     * @param role_id
     * @return
     */
    // todo: se till att man använder de två implementations metoderna direkt i stället.
    // todo: döp om till deleteUsersRole
    int sproc_DelUserRoles( int user_id, int role_id ) {
        int rowCount = 0;
        if( role_id == -1 ) {
            rowCount = deletaAllUserRoles( user_id );
        } else {
            rowCount = deleteUserRole( user_id, role_id );
        }
        return rowCount;
    }

    private int deletaAllUserRoles( int user_id ) {
        String sql = "DELETE FROM user_roles_crossref WHERE user_id = ? ";
        Object[] paramValues = new Object[]{ new Integer( user_id ) };
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    private int deleteUserRole( int user_id, int role_id ) {
        String sql = "DELETE FROM user_roles_crossref WHERE user_id = ? AND role_id = ? ";
        Object[] paramValues = new Object[]{ new Integer( user_id ), new Integer( role_id ) };
        return sqlProcessor.executeUpdate( sql, paramValues );
    }
}
