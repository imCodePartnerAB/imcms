package imcode.server.db;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;
import java.io.*;

/**
 * STOP! Before changing anyting in this class, make sure to run (all) the test in class TestDatabaseService.
 * They can take a while, but it is essentialt to keep this class working with multiple databases that those test are
 * run before and after a change, and that new test is added as soon as a new functionality in this class is added.
 * /Hasse
 */

// todo: generell fråga, borde loggningen ligga i denna klass?

public abstract class DatabaseService {
    private static final char END_OF_COMMAND = ';';
    private static final String DROP_TABLES = "1.droptables.sql";
    private static final String CREATE_TABLES = "2.createtables.sql";
    private static final String INSERT_TYPE_DATA = "3.inserttypedata.sql";
    private static final String INSERT_DEFAULT_DATA = "4.insertdefaultpagesusersandroles.sql";

    private String ADITIONAL_TEST_DATA = "5.insertaditionaltestdata.sql";

    private static final String SQL92_TYPE_TIMESTAMP = "TIMESTAMP";
    private static String COMMON_TIMESTAMP_TYPE_DATETIME = "DATETIME"; // for example MySQL and SQLServer

    SQLProcessor sqlProcessor;
    private Logger log;
    private File filePath;

    /**
     *  overide this in subclass if needed.
     * @param commands
     * @return
     */
    ArrayList filterInsertCommands( ArrayList commands ) {
        // default do nothing, overide in sub classes when needed
        return commands;
    }

    /**
     *  overide this in subclass if needed.
     * @param commands
     * @return
     */
    ArrayList filterCreateCommands( ArrayList commands ) {
        // default do nothing, overide in sub classes when needed
        return commands;
    }

    /**
     * Use this in subclass when filtering create commands if needed.
     * @param commands
     * @return
     */
    ArrayList changeTimestampToDateTime( ArrayList commands ) {
        ArrayList modifiedCommands = new ArrayList();
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            String modifiedCommand = command.replaceAll( SQL92_TYPE_TIMESTAMP, COMMON_TIMESTAMP_TYPE_DATETIME );
            modifiedCommands.add( modifiedCommand );
        }
        return modifiedCommands;
    }

    DatabaseService( File filePath, Logger log ) {
        this.filePath = filePath;
        this.log = log;
    }

    void initConnectionPoolAndSQLProcessor( String serverName, String jdbcDriver, String serverUrl, String user, String password ) {
        int maxConnectionCount = 20;
        try {
            ConnectionPool connectionPool = new ConnectionPoolForNonPoolingDriver( serverName, jdbcDriver, serverUrl, user, password, maxConnectionCount );
            sqlProcessor = new SQLProcessor( connectionPool );
        } catch( Exception ex ) {
            log.fatal( "Couldn't initialize connection pool: serverName :' " + serverName + "', jdbcDriver : '" + jdbcDriver + "', serverUrl : " + serverUrl + "', user : '" + user + "', login_password :' " + password + "'" );
            log.fatal( ex );
        }
    }

    void setupDatabaseWithTablesAndData() {
        try {
            ArrayList commands = readCommandsFromFile( DROP_TABLES );
            executeCommands( commands );

            commands = readCommandsFromFile( CREATE_TABLES );
            executeCreateCommands( commands );

            // I tried to use batchUpdate but for the current Mimer driver that only works for SELECT, INSERT, UPDATE,
            // and DELETE operations and this method is also used for create table and drop table commands. /Hasse
            // sqlProcessor.executeBatchUpdate( con, (String[])commands.toArray( new String[commands.size()] ) );

            commands = readCommandsFromFile( INSERT_TYPE_DATA );
            sqlProcessor.executeBatchUpdate( (String[])commands.toArray( new String[commands.size()] ) );

            commands = readCommandsFromFile( INSERT_DEFAULT_DATA );
            commands = filterInsertCommands( commands );
            sqlProcessor.executeBatchUpdate( (String[])commands.toArray( new String[commands.size()] ) );
        } catch( IOException ex ) {
            log.fatal( "Couldn't open a file ", ex );
        }
    }

    private void executeCreateCommands( ArrayList commands ) {
        commands = filterCreateCommands( commands );
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            //            System.out.println( command.length() < 25 ? command : command.substring( 0, 25 ) );
            sqlProcessor.executeUpdate( command, null );
        }
    }

    void createTestData() throws IOException {
        ArrayList commands = readCommandsFromFile( ADITIONAL_TEST_DATA );
        sqlProcessor.executeBatchUpdate( (String[])commands.toArray( new String[commands.size()] ) );
    }

    private void executeCommands( ArrayList commands ) {
        for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
            String command = (String)iterator.next();
            //            System.out.println( command.length() < 25 ? command : command.substring( 0, 25 ) );
            sqlProcessor.executeUpdate( command, null );
        }

        // I tried to use batchUpdate but for the current Mimer driver that only works for SELECT, INSERT, UPDATE,
        // and DELETE operations and this method is also used for create table and drop table commands. /Hasse
        // sqlProcessor.executeBatchUpdate( con, (String[])commands.toArray( new String[commands.size()] ) );
    }

    private ArrayList readCommandsFromFile( String fileName ) throws IOException {
        File sqlScriptingFile = new File( filePath, fileName );

        BufferedReader reader = new BufferedReader( new FileReader( sqlScriptingFile ) );
        StringBuffer commandBuff = new StringBuffer();
        ArrayList commands = new ArrayList();
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
        Table_roles( int role_id, String role_name, int permissions, int admin_role ) {
            this.role_id = role_id;
            this.role_name = role_name;
            this.permissions = permissions;
            this.admin_role = admin_role;
        }

        int role_id;
        String role_name;
        int permissions;
        int admin_role;
    }

    Table_roles[] sproc_GetAllRoles_but_user() {
        String sql = "SELECT role_id, role_name, permissions, admin_role FROM roles WHERE role_name <> 'Users' ORDER BY role_name";
        Object[] paramValues = null;

        SQLProcessor.ResultProcessor resultProcessor = new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int role_id = rs.getInt( "role_id" );
                String role_name = rs.getString( "role_name" );
                int permissions = rs.getInt( "permissions" );
                int admin_role = rs.getInt( "admin_role" );
                return new Table_roles( role_id, role_name, permissions, admin_role );
            }
        };

        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, resultProcessor );
        return (Table_roles[])queryResult.toArray( new Table_roles[queryResult.size()] );
    }

    static class Table_users {
        int user_id;
        String login_name;
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

        Table_users( int user_id, String login_name, String login_password, String first_name, String last_name, String title, String company, String address, String city, String zip, String country, String county_council, String email, int external, int last_page, int archive_mode, int lang_id, int user_type, int active, Timestamp create_date ) {
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
    }

    /**
     *  Document me!
     * @return
     */

    Table_users[] sproc_GetAllUsers_OrderByLastName() {
        String sql = "select user_id,login_name,login_password,first_name,last_name,title,company,address,city,zip,country,county_council,email,external,last_page,archive_mode,lang_id,user_type,active,create_date from users ORDER BY last_name";
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                Table_users result = mapTableUsers( rs );
                return result;
            }
        } );
        return (Table_users[])queryResult.toArray( new Table_users[queryResult.size()] );
    }

    private Table_users mapTableUsers( ResultSet rs ) throws SQLException {
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

    static class View_TemplateGroup {
        private int id;
        private String simpleName;

        View_TemplateGroup( int id, String simpleName ) {
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
                int templateId = rs.getInt( "template_id" );
                String simpleName = rs.getString( "simple_name" );
                View_TemplateGroup result = new View_TemplateGroup( templateId, simpleName );
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
        int result = -1;
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        String columnName = "user_id";
        String TableName = "users";
        result = getMaxIntValue( transaction, TableName, columnName );
        transaction.commit();
        return result;
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
        int rowCount = 0;
        SQLProcessor.SQLTransaction transcation = sqlProcessor.startTransaction();
        try {
            rowCount = addToTable_phones( transcation, number, userId, phoneType );
            transcation.commit();
        } catch( SQLException ex ) {
            transcation.rollback();
        }
        return rowCount;
    }

    private int addToTable_phones( SQLProcessor.SQLTransaction transaction, String number, int userId, int phoneType ) throws SQLException {
        String tableName = "phones";
        String primaryKeyColumnName = "phone_id";
        int newPhoneId = 1 + getMaxIntValue( transaction, tableName, primaryKeyColumnName );
        String sql = "INSERT INTO phones ( phone_id , number , user_id, phonetype_id ) VALUES ( ? , ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( newPhoneId ), number, new Integer( userId ), new Integer( phoneType )};
        return transaction.executeUpdate( sql, paramValues );
    }

    private int getMaxIntValue( SQLProcessor.SQLTransaction transaction, String tableName, String columnName ) {
        String sql = "SELECT MAX(" + columnName + ") FROM " + tableName;
        ArrayList queryResult = transaction.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
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
                return new Integer( rs.getInt( "role_id" ) );
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
        String sql = "DELETE FROM phones WHERE phone_id = ? ";
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
        View_phone( int phone_id, String phoneNumber ) {
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
        View_userAndPhone( int phone_id, String number, int user_id, int phonetype_id, String typename ) {
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
                int phone_id = rs.getInt( "phone_id" );
                String number = rs.getString( "number" );
                int user_id = rs.getInt( "user_id" );
                int phonetype_id = rs.getInt( "phonetype_id" );
                String typename = rs.getString( "typename" );
                return new View_userAndPhone( phone_id, number, user_id, phonetype_id, typename );
            }
        } );
        return (View_userAndPhone[])queryResult.toArray( new View_userAndPhone[queryResult.size()] );
    }

    int sproc_DocumentDelete( int meta_id ) {
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        int rowCount = 0;
        try {
            Object[] paramValues = new Object[]{new Integer( meta_id )};
            rowCount += transaction.executeUpdate( "delete from meta_classification where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from childs where to_meta_id = 	?", paramValues );
            rowCount += transaction.executeUpdate( "delete from childs where meta_id =	?", paramValues );
            rowCount += transaction.executeUpdate( "delete from text_docs where meta_id = 	?", paramValues );
            rowCount += transaction.executeUpdate( "delete from texts where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from images where meta_id = ?", paramValues );
            rowCount += transaction.executeUpdate( "delete from roles_rights where meta_id = ?", paramValues );
            //            rowCount += transaction.executeUpdate( "delete from user_rights where meta_id = ?", paramValues );
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
        }
        ;
        return rowCount;
    }

    // todo: Döp om till documentExixts eller nåt...
    boolean sproc_FindMetaId( int meta_id ) {
        String sql = "SELECT meta_id FROM meta WHERE meta_id = ?";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
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
    // todo: Ska inte ändringsdatumen uppdateras i denna också?
    int sproc_AddExistingDocToMenu( int meta_id, int existing_meta_id, int doc_menu_no ) {
        // test if this is the first child
        String sqlLinksCount = "select count(*) from childs where meta_id = ?  and menu_sort = ? ";
        Object[] paramValuesLinksCount = new Object[]{new Integer( meta_id ), new Integer( doc_menu_no )};
        ArrayList countResult = sqlProcessor.executeQuery( sqlLinksCount, paramValuesLinksCount, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( 1 ) );
            }
        } );
        Integer countItem = (Integer)countResult.get( 0 );

        int manualSortOrder = 500;
        if( countItem.intValue() > 0 ) {// update manual_sort_order
            String sqlSortOrder = "select max(manual_sort_order) from childs where meta_id = ? and menu_sort = ?";
            Object[] paramValuesSortOrder = new Object[]{new Integer( meta_id ), new Integer( doc_menu_no )};
            ArrayList sortOrderResult = sqlProcessor.executeQuery( sqlSortOrder, paramValuesSortOrder, new SQLProcessor.ResultProcessor() {
                Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                    return new Integer( rs.getInt( 1 ) );
                }
            } );
            manualSortOrder = ((Integer)sortOrderResult.get( 0 )).intValue() + 10;
        }

        //- test if child already exist in this menu. If not, then we will add the child to the menu.
        String sqlThisLinksCount = "select count(*) from childs where meta_id = ? and to_meta_id = ? and menu_sort = ?";
        Object[] paramValuesThisLinksCount = new Object[]{new Integer( meta_id ), new Integer( existing_meta_id ), new Integer( doc_menu_no )};
        ArrayList queryResult = sqlProcessor.executeQuery( sqlThisLinksCount, paramValuesThisLinksCount, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( 1 ) );
            }
        } );

        Integer thisCountItem = (Integer)queryResult.get( 0 );
        int rowCount = 0;
        if( thisCountItem.intValue() == 0 ) {
            String sql = "insert into childs( meta_id, to_meta_id, menu_sort, manual_sort_order) values( ?, ?, ?, ? )";
            Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( existing_meta_id ), new Integer( doc_menu_no ), new Integer( manualSortOrder )};
            rowCount = sqlProcessor.executeUpdate( sql, paramValues );

            sql = "UPDATE meta SET date_modified = ? WHERE meta_id = ?";
            paramValues = new Object[]{new Timestamp( new java.util.Date().getTime() ), new Integer( meta_id )};
            rowCount += sqlProcessor.executeUpdate( sql, paramValues );
        }
        return rowCount;
    }

    static class View_DocumentForUser {
        View_DocumentForUser( int meta_id, int parentcount, String meta_headline, int doc_type ) {
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
        Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( start ), new Integer( end )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int parentcount = rs.getInt( "parentcount" );
                String meta_headline = rs.getString( "meta_headline" );
                int doc_type = rs.getInt( "doc_type" );
                return new View_DocumentForUser( meta_id, parentcount, meta_headline, doc_type );
            }
        } );
        return (View_DocumentForUser[])queryResult.toArray( new View_DocumentForUser[queryResult.size()] );
    }

    static class View_ChildData {
        View_ChildData( int to_meta_id, int menu_sort, int manual_sort_order, int doc_type, boolean archive, String target, Timestamp date_created, Timestamp date_modified, String meta_headline, String meta_text, String meta_image, String frame_name, Timestamp activated_datetime, Timestamp archived_datetime, String filename ) {
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
        Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( user_id )};

        if( sortOrder.intValue() == 3 ) {
            sql += "order by  menu_sort,c.manual_sort_order desc";
        } else if( sortOrder.intValue() == 2 ) {
            sql += "order by  menu_sort,convert (varchar,date_modified,120) desc";
        } else if( sortOrder.intValue() == 1 ) {
            sql += "order by  menu_sort,meta_headline";
        }

        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int to_meta_id = rs.getInt( "to_meta_id" );
                int menu_sort = rs.getInt( "menu_sort" );
                int manual_sort_order = rs.getInt( "manual_sort_order" );
                int doc_type = rs.getInt( "doc_type" );
                boolean archive = (rs.getInt( "archive" ) == 1);
                String target = rs.getString( "target" );
                Timestamp date_created = rs.getTimestamp( "date_created" );
                Timestamp date_modified = rs.getTimestamp( "date_modified" );
                String meta_headline = rs.getString( "meta_headline" );
                String meta_text = rs.getString( "meta_text" );
                String meta_image = rs.getString( "meta_image" );
                String frame_name = rs.getString( "frame_name" );
                Timestamp activated_datetime = rs.getTimestamp( "activated_datetime" );
                Timestamp archived_datetime = rs.getTimestamp( "archived_datetime" );
                String filename = rs.getString( "filename" );
                return new View_ChildData( to_meta_id, menu_sort, manual_sort_order, doc_type, archive, target, date_created, date_modified, meta_headline,
                                           meta_text, meta_image, frame_name, activated_datetime, archived_datetime, filename );
            }
        } );
        return (View_ChildData[])queryResult.toArray( new View_ChildData[queryResult.size()] );
    }

    private Integer getMenuSortOrder( int meta_id ) {
        String sql = "select sort_order from text_docs where meta_id = ?";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "sort_order" ) );
            }
        } );
        return (Integer)queryResult.get( 0 );
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
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "role_id" ) );
            }
        } );
        return queryResult.size() != 0;
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
        Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( admin_role )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "admin_role" ) );
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
        Object[] parameterValues = new Object[]{new Integer( user_id ), new Integer( meta_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, parameterValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "meta_id" ) );
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
            rowCount += deletaAllUserRoles( user_id );
        } else {
            rowCount += deleteUserRole( user_id, role_id );
        }
        return rowCount;
    }

    private int deletaAllUserRoles( int user_id ) {
        String sql = "DELETE FROM user_roles_crossref WHERE user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    private int deleteUserRole( int user_id, int role_id ) {
        String sql = "DELETE FROM user_roles_crossref WHERE user_id = ? AND role_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( role_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    String sproc_GetFileName( int metaId ) {
        String sql = "SELECT filename FROM fileupload_docs WHERE meta_id = ?";
        Object[] paramValues = new Object[]{new Integer( metaId )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return rs.getString( "filename" );
            }
        } );
        return (String)(queryResult.size() > 0 ? queryResult.get( 0 ) : null);
    }

    int sproc_GetDocType( int meta_id ) {
        String sql = "SELECT doc_type FROM meta WHERE meta_id = ?";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "doc_type" ) );
            }
        } );
        return ((Integer)queryResult.get( 0 )).intValue();
    }

    class Table_doc_types {
        Table_doc_types( int doc_type, String type ) {
            this.doc_type = doc_type;
            this.type = type;
        }

        int doc_type;
        String type;
    }

    Table_doc_types[] sproc_GetDocTypes( String lang_prefix ) {
        String sql = "SELECT doc_type,type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type";
        Object[] paramValues = new Object[]{lang_prefix};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int doc_type = rs.getInt( "doc_type" );
                String type = rs.getString( "type" );
                return new Table_doc_types( doc_type, type );
            }
        } );
        return (Table_doc_types[])queryResult.toArray( new Table_doc_types[queryResult.size()] );
    }

    /**
     *
     * @param parent_meta_id The document to insert into
     * @param parent_menu_id
     * @param user_id
     * @param childrensMetaIds The id's to copy.
     * @param copyPrefix prefix added to meta_headline
     * @return The meta-ids of the pages that are filedocs.
     */
    int[] sproc_copyDocs( int parent_meta_id, int parent_menu_id, int user_id, int[] childrensMetaIds, String copyPrefix ) {

        ArrayList fileDocs = new ArrayList();
        ArrayList notFileDocs = new ArrayList();

        for( int i = 0; i < childrensMetaIds.length; i++ ) {
            int meta_id = childrensMetaIds[i];
            if( isFileDoc( meta_id ) ) {
                fileDocs.add( new Integer( meta_id ) );
            } else {
                notFileDocs.add( new Integer( meta_id ) );
            }
        }

        int[] result = new int[notFileDocs.size()];
        for( int i = 0; i < result.length; i++ ) {
            result[i] = ((Integer)fileDocs.get( i )).intValue();
        }

        Integer userId = new Integer( user_id );
        Iterator iterator = notFileDocs.iterator();
        while( iterator.hasNext() ) {
            Integer meta_id = (Integer)iterator.next();
            if( hasCopyRights( userId, new Integer( parent_meta_id ), meta_id ) ) {
                copyDocument( meta_id );
                sproc_AddExistingDocToMenu( parent_meta_id, meta_id.intValue(), parent_menu_id );
            }
        }

        return result;
    }

    boolean isFileDoc( int meta_id ) {
        String sql = "SELECT meta_id FROM meta WHERE meta_id = ? AND doc_type = 8";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "meta_id" ) );
            }
        } );
        return queryResult.size() > 0;
    }

    private boolean hasCopyRights( Integer user_id, Integer parent_meta_id, Integer meta_id_to_copy ) {
        if( !sproc_CheckUserDocSharePermission( user_id.intValue(), meta_id_to_copy.intValue() ) ) {
            return false;
        }
        return mayUserAddDocumentToParent( user_id, parent_meta_id, meta_id_to_copy );
    }

    private boolean mayUserAddDocumentToParent( Integer user_id, Integer parent_meta_id, Integer meta_id_to_add ) {
        int doc_type = sproc_GetDocType( meta_id_to_add.intValue() );
        // TODO: Use default-language instead of "se"
        View_doc_types[] user_doc_types = sproc_GetDocTypesForUser( user_id.intValue(), parent_meta_id.intValue(), "se" );
        boolean userCanCreateDocType = false;
        for( int i = 0; i < user_doc_types.length; ++i ) {
            int userDocType = user_doc_types[i].doc_type;
            if( doc_type == userDocType ) {
                userCanCreateDocType = true;
                break;
            }
        }
        return userCanCreateDocType;
    }

    static class View_doc_types {
        public View_doc_types( int doc_type, String type ) {
            this.doc_type = doc_type;
            this.type = type;
        }

        int doc_type;
        String type;
    }

    /**
     Nice query that fetches all document types a user may create in a document,
     for easy insertion into an html-option-list, no less!
     */
    View_doc_types[] sproc_GetDocTypesForUser( int user_id, int meta_id, String lang_prefix ) {
        String sql = "SELECT DISTINCT dt.doc_type, dt.type " +
            "FROM doc_types dt " +
            "JOIN user_roles_crossref urc " +
            "ON urc.user_id = ? " +
            "AND dt.lang_prefix = ? " +
            "LEFT JOIN roles_rights rr " +
            "ON rr.meta_id = ? " +
            "AND rr.role_id = urc.role_id " +
            "LEFT JOIN doc_permission_sets dps " +
            "ON dps.meta_id = rr.meta_id " +
            "AND dps.set_id = rr.set_id " +
            "LEFT JOIN doc_permission_sets_ex dpse " +
            "ON dpse.permission_data = dt.doc_type " +
            "AND dpse.meta_id = rr.meta_id " +
            "AND dpse.set_id = rr.set_id " +
            "AND dpse.permission_id = 8 " + // -- Create document
            "WHERE dpse.permission_data IS NOT NULL " +
            "OR rr.set_id = 0 " +
            "OR urc.role_id = 0 " +
            "ORDER BY dt.doc_type";
        Object[] paramValues = new Object[]{new Integer( user_id ), lang_prefix, new Integer( meta_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int doc_type = rs.getInt( "doc_type" );
                String type = rs.getString( "type" );
                return new View_doc_types( doc_type, type );
            }
        } );
        return (View_doc_types[])queryResult.toArray( new View_doc_types[queryResult.size()] );
    }

    static class Table_meta {
        Table_meta( int meta_id, String description, int doc_type, String meta_headline, String meta_text, String meta_image, int owner_id, int permissions, int shared, int expand, int show_meta, int help_text_id, int archive, int status_id, String lang_prefix, String classification, Timestamp date_created, Timestamp date_modified, int sort_position, int menu_position, int disable_search, String target, String frame_name, int activate, Timestamp activated_datetim, Timestamp archived_datetime ) {
            this.meta_id = meta_id;
            this.description = description;
            this.doc_type = doc_type;
            this.meta_headline = meta_headline;
            this.meta_text = meta_text;
            this.meta_image = meta_image;
            this.owner_id = owner_id;
            this.permissions = permissions;
            this.shared = shared;
            this.expand = expand;
            this.show_meta = show_meta;
            this.help_text_id = help_text_id;
            this.archive = archive;
            this.status_id = status_id;
            this.lang_prefix = lang_prefix;
            this.classification = classification;
            this.date_created = date_created;
            this.date_modified = date_modified;
            this.sort_position = sort_position;
            this.menu_position = menu_position;
            this.disable_search = disable_search;
            this.target = target;
            this.frame_name = frame_name;
            this.activate = activate;
            this.activated_datetime = activated_datetim;
            this.archived_datetime = archived_datetime;
        }

        int meta_id;
        String description;
        int doc_type;
        String meta_headline;
        String meta_text;
        String meta_image;
        int owner_id;
        int permissions;
        int shared;
        int expand;
        int show_meta;
        int help_text_id;
        int archive;
        int status_id;
        String lang_prefix;
        String classification;
        Timestamp date_created;
        Timestamp date_modified;
        int sort_position;
        int menu_position;
        int disable_search;
        String target;
        String frame_name;
        int activate;
        Timestamp activated_datetime;
        Timestamp archived_datetime;
    }

    private Table_meta getFomTable_meta( Integer meta_id ) {
        String sql = "Select meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, " +
            "shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, " +
            "date_modified, sort_position, menu_position, disable_search, target, frame_name, activate, " +
            "activated_datetime, archived_datetime " +
            "FROM meta WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                String description = rs.getString( "description" );
                int doc_type = rs.getInt( "doc_type" );
                String meta_headline = rs.getString( "meta_headline" );
                String meta_text = rs.getString( "meta_text" );
                String meta_image = rs.getString( "meta_image" );
                int owner_id = rs.getInt( "owner_id" );
                int permissions = rs.getInt( "permissions" );
                int shared = rs.getInt( "shared" );
                int expand = rs.getInt( "expand" );
                int show_meta = rs.getInt( "show_meta" );
                int help_text_id = rs.getInt( "help_text_id" );
                int archive = rs.getInt( "archive" );
                int status_id = rs.getInt( "status_id" );
                String lang_prefix = rs.getString( "lang_prefix" );
                String classification = rs.getString( "classification" );
                Timestamp date_created = rs.getTimestamp( "date_created" );
                Timestamp date_modified = rs.getTimestamp( "date_modified" );
                int sort_position = rs.getInt( "sort_position" );
                int menu_position = rs.getInt( "menu_position" );
                int disable_search = rs.getInt( "disable_search" );
                String target = rs.getString( "target" );
                String frame_name = rs.getString( "frame_name" );
                int activate = rs.getInt( "activate" );
                Timestamp activated_datetime = rs.getTimestamp( "activated_datetime" );
                Timestamp archived_datetime = rs.getTimestamp( "archived_datetime" );
                return new Table_meta( meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id,
                                       permissions, shared, expand, show_meta, help_text_id, archive, status_id,
                                       lang_prefix, classification, date_created, date_modified, sort_position,
                                       menu_position, disable_search, target, frame_name, activate, activated_datetime, archived_datetime );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_meta)queryResult.get( 0 );
        }
    }

    private int addToTable_meta( SQLProcessor.SQLTransaction transaction, Table_meta tableData ) throws SQLException {
        String sql = "INSERT INTO META (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, " +
            "permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, " +
            "date_created, date_modified, sort_position, menu_position, disable_search, target, frame_name, activate, " +
            "activated_datetime, archived_datetime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.description, new Integer( tableData.doc_type ),
                                            tableData.meta_headline, tableData.meta_text, tableData.meta_image,
                                            new Integer( tableData.owner_id ), new Integer( tableData.permissions ),
                                            new Integer( tableData.shared ), new Integer( tableData.expand ), new Integer( tableData.show_meta ),
                                            new Integer( tableData.help_text_id ), new Integer( tableData.archive ),
                                            new Integer( tableData.status_id ), tableData.lang_prefix, tableData.classification,
                                            tableData.date_created, tableData.date_modified, new Integer( tableData.sort_position ),
                                            new Integer( tableData.menu_position ), new Integer( tableData.disable_search ),
                                            tableData.target, tableData.frame_name, new Integer( tableData.activate ),
                                            tableData.activated_datetime,
                                            (null == tableData.archived_datetime) ? (Object)new SQLTypeNull( Types.TIMESTAMP ) : tableData.archived_datetime
        };
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_text_docs {
        Table_text_docs( int meta_id, int template_id, int group_id, int sort_order, int default_template_1, int default_template_2 ) {
            this.meta_id = meta_id;
            this.template_id = template_id;
            this.group_id = group_id;
            this.sort_order = sort_order;
            this.default_template_1 = default_template_1;
            this.default_template_2 = default_template_2;
        }

        int meta_id;
        int template_id;
        int group_id;
        int sort_order;
        int default_template_1;
        int default_template_2;
    }

    private Table_text_docs getFromTable_text_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, template_id, group_id, sort_order, default_template_1, default_template_2 " +
            "FROM text_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int template_id = rs.getInt( "template_id" );
                int group_id = rs.getInt( "group_id" );
                int sort_order = rs.getInt( "sort_order" );
                int default_template_1 = rs.getInt( "default_template_1" );
                int default_template_2 = rs.getInt( "default_template_2" );
                return new Table_text_docs( meta_id, template_id, group_id, sort_order, default_template_1, default_template_2 );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_text_docs)queryResult.get( 0 );
        }
    }

    private int addToTable_text_docs( SQLProcessor.SQLTransaction transaction, Table_text_docs tableData ) throws SQLException {
        String sql = "INSERT INTO text_docs ( meta_id, template_id, group_id, sort_order, default_template_1, default_template_2 ) " +
            "VALUES ( ?,?,?,?,?,? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.template_id ),
                                            new Integer( tableData.group_id ), new Integer( tableData.sort_order ),
                                            new Integer( tableData.default_template_1 ), new Integer( tableData.default_template_2 )};
        return transaction.executeUpdate( sql, paramValues );
    };

    static class Table_url_docs {
        Table_url_docs( int meta_id, String frame_name, String target, String url_ref, String url_txt, String lang_prefix ) {
            this.meta_id = meta_id;
            this.frame_name = frame_name;
            this.target = target;
            this.url_ref = url_ref;
            this.url_txt = url_txt;
            this.lang_prefix = lang_prefix;
        }

        int meta_id;
        String frame_name;
        String target;
        String url_ref;
        String url_txt;
        String lang_prefix;
    }

    private Table_url_docs getFromTable_url_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, frame_name, target, url_ref, url_txt, lang_prefix FROM url_docs WHERE meta_id = ?";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                String frame_name = rs.getString( "frame_name" );
                String target = rs.getString( "target" );
                String url_ref = rs.getString( "url_ref" );
                String url_txt = rs.getString( "url_txt" );
                String lang_prefix = rs.getString( "lang_prefix" );
                return new Table_url_docs( meta_id, frame_name, target, url_ref, url_txt, lang_prefix );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_url_docs)queryResult.get( 0 );
        }
    }

    private int addToTable_url_docs( SQLProcessor.SQLTransaction transaction, Table_url_docs tableData ) throws SQLException {
        String sql = "INSERT INTO url_docs (meta_id, frame_name, target, url_ref, url_txt, lang_prefix ) " +
            "VALUES ( ?, ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.frame_name, tableData.target,
                                            tableData.url_ref, tableData.url_txt, tableData.lang_prefix};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_browser_docs {
        Table_browser_docs( int meta_id, int to_meta_id, int browser_id ) {
            this.meta_id = meta_id;
            this.to_meta_id = to_meta_id;
            this.browser_id = browser_id;
        }

        int meta_id;
        int to_meta_id;
        int browser_id;
    }

    private Table_browser_docs getFromTable_browser_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, to_meta_id, browser_id FROM browser_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int to_meta_id = rs.getInt( "to_meta_id" );
                int browser_id = rs.getInt( "browser_id" );
                return new Table_browser_docs( meta_id, to_meta_id, browser_id );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_browser_docs)queryResult.get( 0 );
        }
    }

    private int addToTable_browser_docs( SQLProcessor.SQLTransaction transaction, Table_browser_docs tableData ) throws SQLException {
        String sql = "INSERT INTO browser_docs ( meta_id, to_meta_id, browser_id ) VALUES (?,?,?)";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.to_meta_id ),
                                            new Integer( tableData.browser_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_frameset_docs {
        Table_frameset_docs( int meta_id, String frame_set ) {
            this.meta_id = meta_id;
            this.frame_set = frame_set;
        }

        int meta_id;
        String frame_set;
    }

    private Table_frameset_docs getFromTable_frameset_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, frame_set FROM frameset_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                String frame_set = rs.getString( "frame_set" );
                return new Table_frameset_docs( meta_id, frame_set );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_frameset_docs)queryResult.get( 0 );
        }
    }

    private int addToTable_frameset_docs( SQLProcessor.SQLTransaction transaction, Table_frameset_docs tableData ) throws SQLException {
        String sql = "INSERT INTO frameset_docs ( meta_id, frame_set ) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.frame_set};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_fileupload_docs {
        Table_fileupload_docs( int meta_id, String filename, String mime ) {
            this.meta_id = meta_id;
            this.filename = filename;
            this.mime = mime;
        }

        int meta_id;
        String filename;
        String mime;
    }

    private Table_fileupload_docs getFromTable_fileupload_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, filename, mime FROM fileupload_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                String filename = rs.getString( "filename" );
                String mime = rs.getString( "mime" );
                return new Table_fileupload_docs( meta_id, filename, mime );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_fileupload_docs)queryResult.get( 0 );
        }
    }

    private int addToTable_fileupload_docs( SQLProcessor.SQLTransaction transaction, Table_fileupload_docs tableData ) throws SQLException {
        String sql = "INSERT INTO fileupload_docs ( meta_id, filename, mime ) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.filename, tableData.mime};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_texts {
        Table_texts( int meta_id, int name, String text, int type, int counter ) {
            this.meta_id = meta_id;
            this.name = name;
            this.text = text;
            this.type = type;
            this.counter = counter;
        }

        int meta_id;
        int name;
        String text;
        int type;
        int counter;
    }

    private Table_texts[] getFromTable_texts( Integer meta_id ) {
        String sql = "SELECT meta_id, name, text, type, counter FROM texts WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int name = rs.getInt( "name" );
                String text = rs.getString( "text" );
                int type = rs.getInt( "type" );
                int counter = rs.getInt( "counter" );
                return new Table_texts( meta_id, name, text, type, counter );
            }
        } );
        return (Table_texts[])queryResult.toArray( new Table_texts[queryResult.size()] );
    }

    private int addToTable_texts( SQLProcessor.SQLTransaction transaction, Table_texts tableData ) throws SQLException {
        String sql = "INSERT INTO texts ( meta_id, name, text, type, counter ) VALUES ( ?, ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.name ), tableData.text,
                                            new Integer( tableData.type ), new Integer( tableData.counter )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_images {
        Table_images( int meta_id, int width, int height, int border, int v_space, int h_space, int name, String image_name, String target, String target_name, String align, String alt_text, String low_scr, String imgurl, String linkurl ) {
            this.meta_id = meta_id;
            this.width = width;
            this.height = height;
            this.border = border;
            this.v_space = v_space;
            this.h_space = h_space;
            this.name = name;
            this.image_name = image_name;
            this.target = target;
            this.target_name = target_name;
            this.align = align;
            this.alt_text = alt_text;
            this.low_scr = low_scr;
            this.imgurl = imgurl;
            this.linkurl = linkurl;
        }

        int meta_id;
        int width;
        int height;
        int border;
        int v_space;
        int h_space;
        int name;
        String image_name;
        String target;
        String target_name;
        String align;
        String alt_text;
        String low_scr;
        String imgurl;
        String linkurl;
    }

    // todo: Varning, Denna sproc returnerar inte exakt i den ordning som den ursprungliga sproc ville ha det.
    // todo: ej heller inehåller resultatet den formaterade varianten av name: '#img'+convert(varchar(5), name)+'#'
    Table_images[] sproc_getImages( int meta_id ) {
        return getFromTable_images( new Integer( meta_id ) );
    }

    private Table_images[] getFromTable_images( Integer meta_id ) {
        String sql = "SELECT meta_id, width, height, border, v_space, h_space, name, image_name, target, target_name, " +
            "align, alt_text, low_scr, imgurl, linkurl FROM images WHERE meta_id = ?";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int width = rs.getInt( "width" );
                int height = rs.getInt( "height" );
                int border = rs.getInt( "border" );
                int v_space = rs.getInt( "v_space" );
                int h_space = rs.getInt( "h_space" );
                int name = rs.getInt( "name" );
                String image_name = rs.getString( "image_name" );
                String target = rs.getString( "target" );
                String target_name = rs.getString( "target_name" );
                String align = rs.getString( "align" );
                String alt_text = rs.getString( "alt_text" );
                String low_scr = rs.getString( "low_scr" );
                String imgurl = rs.getString( "imgurl" );
                String linkurl = rs.getString( "linkurl" );
                return new Table_images( meta_id, width, height, border, v_space, h_space, name, image_name, target, target_name, align, alt_text, low_scr, imgurl, linkurl );
            }
        } );
        return (Table_images[])queryResult.toArray( new Table_images[queryResult.size()] );
    }

    private int addToTable_images( SQLProcessor.SQLTransaction transaction, Table_images tableData ) throws SQLException {
        String sql = "INSERT INTO images (meta_id, width, height, border, v_space, h_space, name, image_name, target, target_name," +
            " align, alt_text, low_scr, imgurl, linkurl ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.width ),
                                            new Integer( tableData.height ), new Integer( tableData.border ),
                                            new Integer( tableData.v_space ), new Integer( tableData.h_space ),
                                            new Integer( tableData.name ), tableData.image_name, tableData.target,
                                            tableData.target_name, tableData.align, tableData.alt_text, tableData.low_scr,
                                            tableData.imgurl, tableData.linkurl};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_includes {
        Table_includes( int meta_id, int include_id, int included_meta_id ) {
            this.meta_id = meta_id;
            this.include_id = include_id;
            this.included_meta_id = included_meta_id;
        }

        int meta_id;
        int include_id;
        int included_meta_id;
    }

    Table_includes[] sproc_GetInclues( int meta_id ) {
        return getFromTable_includes( new Integer( meta_id ) );
    }

    private Table_includes[] getFromTable_includes( Integer meta_id ) {
        String sql = "SELECT meta_id, include_id, included_meta_id FROM includes WHERE meta_id = ? ORDER BY include_id";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int include_id = rs.getInt( "include_id" );
                int included_meta_id = rs.getInt( "included_meta_id" );
                return new Table_includes( meta_id, include_id, included_meta_id );
            }
        } );
        return (Table_includes[])queryResult.toArray( new Table_includes[queryResult.size()] );
    }

    private int addToTable_includes( SQLProcessor.SQLTransaction transaction, Table_includes tableData ) throws SQLException {
        String sql = "INSERT INTO includes ( meta_id, include_id, included_meta_id ) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.include_id ),
                                            new Integer( tableData.included_meta_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_doc_permission_sets {
        Table_doc_permission_sets( int meta_id, int set_id, int permission_id ) {
            this.meta_id = meta_id;
            this.set_id = set_id;
            this.permission_id = permission_id;
        }

        int meta_id;
        int set_id;
        int permission_id;
    }

    private Table_doc_permission_sets getFromTable_doc_permission_sets( Integer meta_id ) {
        String sql = "SELECT meta_id, set_id, permission_id FROM doc_permission_sets WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int set_id = rs.getInt( "set_id" );
                int permission_id = rs.getInt( "permission_id" );
                return new Table_doc_permission_sets( meta_id, set_id, permission_id );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_doc_permission_sets)queryResult.get( 0 );
        }
    }

    private int addToTable_doc_permission_sets( SQLProcessor.SQLTransaction transaction, Table_doc_permission_sets tableData ) throws SQLException {
        String sql = "INSERT INTO doc_permission_sets ( meta_id, set_id, permission_id ) VALUES( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_new_doc_permission_sets {
        Table_new_doc_permission_sets( int meta_id, int set_id, int permission_id ) {
            this.meta_id = meta_id;
            this.set_id = set_id;
            this.permission_id = permission_id;
        }

        int meta_id;
        int set_id;
        int permission_id;
    }

    private Table_new_doc_permission_sets getFromTable_new_doc_permission_sets( Integer meta_id ) {
        String sql = "SELECT meta_id, set_id, permission_id FROM new_doc_permission_sets WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int set_id = rs.getInt( "set_id" );
                int permission_id = rs.getInt( "permission_id" );
                return new Table_new_doc_permission_sets( meta_id, set_id, permission_id );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_new_doc_permission_sets)queryResult.get( 0 );
        }
    }

    private int addToTable_new_doc_permission_sets( SQLProcessor.SQLTransaction transaction, Table_new_doc_permission_sets tableData ) throws SQLException {
        String sql = "INSERT INTO new_doc_permission_sets (meta_id, set_id, permission_id) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_doc_permission_sets_ex {
        Table_doc_permission_sets_ex( int meta_id, int set_id, int permission_id, int permission_data ) {
            this.meta_id = meta_id;
            this.set_id = set_id;
            this.permission_id = permission_id;
            this.permission_data = permission_data;
        }

        int meta_id;
        int set_id;
        int permission_id;
        int permission_data;
    }

    private Table_doc_permission_sets_ex getFromTable_doc_permission_sets_ex( Integer meta_id ) {
        String sql = "SELECT meta_id, set_id, permission_id, permission_data FROM doc_permission_sets_ex WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int set_id = rs.getInt( "set_id" );
                int permission_id = rs.getInt( "permission_id" );
                int permission_data = rs.getInt( "permission_data" );
                return new Table_doc_permission_sets_ex( meta_id, set_id, permission_id, permission_data );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_doc_permission_sets_ex)queryResult.get( 0 );
        }
    }

    private int addToTable_doc_permission_sets_ex( SQLProcessor.SQLTransaction transaction, Table_doc_permission_sets_ex tableData ) throws SQLException {
        String sql = "INSERT INTO doc_permission_sets_ex (meta_id, set_id, permission_id, permission_data) VALUES ( ?, ?, ?, ? ) ";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id ), new Integer( tableData.permission_data )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_new_doc_permission_sets_ex {
        Table_new_doc_permission_sets_ex( int meta_id, int set_id, int permission_id, int permission_data ) {
            this.meta_id = meta_id;
            this.set_id = set_id;
            this.permission_id = permission_id;
            this.permission_data = permission_data;
        }

        int meta_id;
        int set_id;
        int permission_id;
        int permission_data;
    }

    private Table_new_doc_permission_sets_ex getFromTable_new_doc_permission_sets_ex( Integer meta_id ) {
        String sql = "SELECT meta_id, set_id, permission_id, permission_data FROM new_doc_permission_sets_ex WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int set_id = rs.getInt( "set_id" );
                int permission_id = rs.getInt( "permission_id" );
                int permission_data = rs.getInt( "permission_data" );
                return new Table_new_doc_permission_sets_ex( meta_id, set_id, permission_id, permission_data );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_new_doc_permission_sets_ex)queryResult.get( 0 );
        }
    }

    private int addToTable_new_doc_permission_sets_ex( SQLProcessor.SQLTransaction transaction, Table_new_doc_permission_sets_ex tableData ) throws SQLException {
        String sql = "INSERT INTO new_doc_permission_sets_ex (meta_id, set_id, permission_id, permission_data) VALUES ( ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id ), new Integer( tableData.permission_data )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_roles_rights {
        Table_roles_rights( int role_id, int meta_id, int set_id ) {
            this.role_id = role_id;
            this.meta_id = meta_id;
            this.set_id = set_id;
        }

        int role_id;
        int meta_id;
        int set_id;
    }

    private Table_roles_rights getFromTable_roles_rights( Integer meta_id ) {
        String sql = "SELECT role_id, meta_id, set_id FROM roles_rights WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int role_id = rs.getInt( "role_id" );
                int meta_id = rs.getInt( "meta_id" );
                int set_id = rs.getInt( "set_id" );
                return new Table_roles_rights( role_id, meta_id, set_id );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_roles_rights)queryResult.get( 0 );
        }
    }

    private int addToTable_roles_rights( SQLProcessor.SQLTransaction transaction, Table_roles_rights tableData ) throws SQLException {
        String sql = "INSERT INTO roles_rights (role_id, meta_id, set_id) VALUES ( ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.role_id ), new Integer( tableData.meta_id ), new Integer( tableData.set_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_meta_classification {
        public Table_meta_classification( int meta_id, int class_id ) {
            this.meta_id = meta_id;
            this.class_id = class_id;
        }

        int meta_id;
        int class_id;
    }

    private Table_meta_classification getFromTable_meta_classification( Integer meta_id ) {
        String sql = "SELECT meta_id, class_id FROM meta_classification WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int class_id = rs.getInt( "class_id" );
                return new Table_meta_classification( meta_id, class_id );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_meta_classification)queryResult.get( 0 );
        }
    }

    private int addToTable_meta_classification( SQLProcessor.SQLTransaction transaction, Table_meta_classification tableData ) throws SQLException {
        String sql = "INSERT INTO meta_classification (meta_id, class_id) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.class_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_meta_section {
        public Table_meta_section( int meta_id, int section_id ) {
            this.meta_id = meta_id;
            this.section_id = section_id;
        }

        int meta_id;
        int section_id;
    }

    private Table_meta_section getFromTable_meta_section( Integer meta_id ) {
        String sql = "SELECT meta_id, section_id FROM meta_section WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int meta_id = rs.getInt( "meta_id" );
                int section_id = rs.getInt( "section_id" );
                return new Table_meta_section( meta_id, section_id );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_meta_section)queryResult.get( 0 );
        }
    }

    private int addToTable_meta_section( SQLProcessor.SQLTransaction transaction, Table_meta_section tableData ) throws SQLException {
        String sql = "INSERT INTO meta_section ( meta_id, section_id ) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.section_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    // todo: Lös detta med att två uppdateringar kan skriva över varandra.
    private int copyDocument( Integer meta_id ) {
        int rowCount = 0;
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        int nexFreeMetaId = 1 + getMaxIntValue( transaction, "meta", "meta_id" );
        try {
            Table_meta metaToBeCopied = getFomTable_meta( meta_id );
            metaToBeCopied.meta_id = nexFreeMetaId;
            rowCount += addToTable_meta( transaction, metaToBeCopied );

            Table_text_docs textDocsToBeCopied = getFromTable_text_docs( meta_id );
            if( null != textDocsToBeCopied ) {
                textDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_text_docs( transaction, textDocsToBeCopied );
            }

            Table_url_docs urlDocsToBeCopied = getFromTable_url_docs( meta_id );
            if( null != urlDocsToBeCopied ) {
                urlDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_url_docs( transaction, urlDocsToBeCopied );
            }

            Table_browser_docs browserDocsToBeCopied = getFromTable_browser_docs( meta_id );
            if( null != browserDocsToBeCopied ) {
                browserDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_browser_docs( transaction, browserDocsToBeCopied );
            }

            Table_frameset_docs framesetDocsToBeCopied = getFromTable_frameset_docs( meta_id );
            if( null != framesetDocsToBeCopied ) {
                framesetDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_frameset_docs( transaction, framesetDocsToBeCopied );
            }

            Table_fileupload_docs fileUploadDocsToBeCopied = getFromTable_fileupload_docs( meta_id );
            if( null != fileUploadDocsToBeCopied ) {
                fileUploadDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_fileupload_docs( transaction, fileUploadDocsToBeCopied );
            }

            Table_texts[] textsToBeCopied = getFromTable_texts( meta_id );
            for( int i = 0; i < textsToBeCopied.length; i++ ) {
                Table_texts textToBeCopied = textsToBeCopied[i];
                textToBeCopied.meta_id = meta_id.intValue();
                int newCounterValue = getMaxIntValue( transaction, "texts", "counter" );
                textToBeCopied.counter = newCounterValue;
                rowCount += addToTable_texts( transaction, textToBeCopied );
            }

            Table_images[] imagesToBeCopied = getFromTable_images( meta_id );
            for( int i = 0; i < imagesToBeCopied.length; i++ ) {
                Table_images imageToBeCopied = imagesToBeCopied[i];
                imageToBeCopied.meta_id = meta_id.intValue();
                rowCount += addToTable_images( transaction, imageToBeCopied );
            }

            Table_includes[] includesToBeCopied = getFromTable_includes( meta_id );
            for( int i = 0; i < includesToBeCopied.length; i++ ) {
                Table_includes table_includes = includesToBeCopied[i];
                table_includes.meta_id = nexFreeMetaId;
                rowCount += addToTable_includes( transaction, table_includes );
            }

            Table_doc_permission_sets docPermissionSetsToBeCopied = getFromTable_doc_permission_sets( meta_id );
            if( null != docPermissionSetsToBeCopied ) {
                docPermissionSetsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_doc_permission_sets( transaction, docPermissionSetsToBeCopied );
            }

            Table_new_doc_permission_sets newDocPermissionSetsToBeCopied = getFromTable_new_doc_permission_sets( meta_id );
            if( null != newDocPermissionSetsToBeCopied ) {
                newDocPermissionSetsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_new_doc_permission_sets( transaction, newDocPermissionSetsToBeCopied );
            }

            Table_doc_permission_sets_ex docPermissionsSetsExToBeCopied = getFromTable_doc_permission_sets_ex( meta_id );
            if( null != docPermissionsSetsExToBeCopied ) {
                docPermissionsSetsExToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_doc_permission_sets_ex( transaction, docPermissionsSetsExToBeCopied );
            }

            Table_new_doc_permission_sets_ex newDocPermissionSetsExToBeCopied = getFromTable_new_doc_permission_sets_ex( meta_id );
            if( null != newDocPermissionSetsExToBeCopied ) {
                newDocPermissionSetsExToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_new_doc_permission_sets_ex( transaction, newDocPermissionSetsExToBeCopied );
            }

            Table_roles_rights rolesRightsToBeCopied = getFromTable_roles_rights( meta_id );
            if( null != rolesRightsToBeCopied ) {
                rolesRightsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_roles_rights( transaction, rolesRightsToBeCopied );
            }

            Table_meta_classification metaClassificationToBeCopied = getFromTable_meta_classification( meta_id );
            if( null != metaClassificationToBeCopied ) {
                metaClassificationToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_meta_classification( transaction, metaClassificationToBeCopied );
            }

            Table_meta_section metaSectionToBeCopied = getFromTable_meta_section( meta_id );
            if( null != metaSectionToBeCopied ) {
                metaSectionToBeCopied.meta_id = nexFreeMetaId;
                rowCount += addToTable_meta_section( transaction, metaSectionToBeCopied );
            }

            transaction.commit();
        } catch( SQLException ex ) {
            transaction.rollback();
        }
        return rowCount;
    }

    /**
     Retrieve a text with type
     */
    // Todo: Denna returnerade i orginalutförandet endast en del av datan som finns för rad i tabellen texts,
    // todo: ok att returnera hela på detta sätt?
    Table_texts sproc_GetText( int meta_id, int name ) {
        Table_texts[] allTextInDocument = getFromTable_texts( new Integer( meta_id ) );
        for( int i = 0; i < allTextInDocument.length; i++ ) {
            Table_texts table_texts = allTextInDocument[i];
            if( table_texts.name == name ) {
                return table_texts;
            }
        }
        return null;
    }

    int sproc_deleteInclude( int meta_id, int include_id ) {
        String sql = "DELETE FROM includes WHERE meta_id = ? AND include_id = ? ";
        Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( include_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    Table_users getFromTable_users( Integer user_id ) {
        String sql = "select user_id,login_name,login_password,first_name,last_name,title,company,address,city,zip,country,county_council,email,external,last_page,archive_mode,lang_id,user_type,active,create_date from users " +
            "WHERE user_id = ? ";
        Object[] paramValues = new Object[]{user_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                Table_users result = mapTableUsers( rs );
                return result;
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_users)queryResult.get( 0 );
        }
    }

    String[] sproc_GetUserRoles( int user_id ) {
        String sql = "SELECT role_name from roles, user_roles_crossref " +
            "WHERE roles.role_id = user_roles_crossref.role_id AND user_roles_crossref.user_id = ?";
        Object paramValues[] = new Object[]{new Integer( user_id )};
        List roleNames = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                String role_name = rs.getString( "role_name" );
                return role_name;
            }
        } );

        return (String[])roleNames.toArray( new String[roleNames.size()] );
    }

    // todo: Denna returnerar lang_id istället för lang prefix
    // todo: anropa även sproc_GetLangPrefixFromId för denna information
    Table_users sproc_GetUserInfo( int user_id ) {
        return getFromTable_users( new Integer( user_id ) );
    }

    /** Get the users preferred language. Used by the administrator functions.
     * Begin with getting the users langId from the userobject.
     */
    Table_lang_prefixes sproc_GetLangPrefixFromId( int lang_id ) {
        return getFromTable_lang_prefixes( new Integer( lang_id ));
    }

    static class Table_lang_prefixes {
        public Table_lang_prefixes( int lang_id, String lang_prefix ) {
            this.lang_id = lang_id;
            this.lang_prefix = lang_prefix;
        }

        int lang_id;
        String lang_prefix;
    }

    Table_lang_prefixes getFromTable_lang_prefixes( Integer lang_id ) {
        String sql = "SELECT lang_id, lang_prefix FROM lang_prefixes WHERE lang_id = ? ";
        Object[] paramValues = new Object[]{ lang_id };
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int lang_id = rs.getInt("lang_id");
                String lang_prefix = rs.getString("lang_prefix");
                return new Table_lang_prefixes( lang_id, lang_prefix );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_lang_prefixes)queryResult.get( 0 );
        }
    }
}
