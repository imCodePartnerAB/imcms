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
        private int role_id;
        private String role_name;
        private int permissions;
        private int admin_role;

        Table_roles( ResultSet rs ) throws SQLException {
            role_id = rs.getInt( "role_id" );
            role_name = rs.getString( "role_name" );
            permissions = rs.getInt( "permissions" );
            admin_role = rs.getInt( "admin_role" );
        }
    }

    Table_roles[] sproc_GetAllRoles_but_user() {
        String sql = "SELECT role_id, role_name, permissions, admin_role FROM roles WHERE role_name <> 'Users' ORDER BY role_name";
        Object[] paramValues = null;

        SQLProcessor.ResultProcessor resultProcessor = new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_roles( rs );
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

        Table_users( ResultSet rs ) throws SQLException {
            user_id = rs.getInt( "user_id" );
            login_name = rs.getString( "login_name" );
            login_password = rs.getString( "login_password" );
            first_name = rs.getString( "first_name" );
            last_name = rs.getString( "last_name" );
            title = rs.getString( "title" );
            company = rs.getString( "company" );
            address = rs.getString( "address" );
            city = rs.getString( "city" );
            zip = rs.getString( "zip" );
            country = rs.getString( "country" );
            county_council = rs.getString( "county_council" );
            email = rs.getString( "email" );
            external = rs.getInt( "external" );
            last_page = rs.getInt( "last_page" );
            archive_mode = rs.getInt( "archive_mode" );
            lang_id = rs.getInt( "lang_id" );
            user_type = rs.getInt( "user_type" );
            active = rs.getInt( "active" );
            create_date = rs.getTimestamp( "create_date" );
        }
    }

    Table_users[] sproc_GetAllUsers_OrderByLastName() {
        String sql = "select user_id,login_name,login_password,first_name,last_name,title,company,address,city,zip,country,county_council,email,external,last_page,archive_mode,lang_id,user_type,active,create_date from users ORDER BY last_name";
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_users( rs );
            }
        } );
        return (Table_users[])queryResult.toArray( new Table_users[queryResult.size()] );
    }

    // todo, ska man beh�va stoppa in user_id h�r? Kan man inte bara f� ett unikt?
    int sproc_AddNewuser( Table_users userData ) {
        String sql = "INSERT INTO users (user_id, login_name, login_password, first_name, last_name, title, company, address, city, zip, country, county_council, email, external, last_page, archive_mode, lang_id, user_type, active, create_date ) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] paramValues = new Object[]{new Integer( userData.user_id ), userData.login_name, userData.login_password, userData.first_name, userData.last_name, userData.title, userData.company, userData.address, userData.city, userData.zip, userData.country, userData.county_council, userData.email, new Integer( userData.external ), new Integer( 1001 ), new Integer( 0 ), new Integer( userData.lang_id ), new Integer( userData.user_type ), new Integer( userData.active ), userData.create_date};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    // todo: flytta in detta i addNewUser ist�llet, och se till att det fungerar concurrently.
    // todo: kolla att det inte �r highest+1 som f�rv�ntas.
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
        String sql = "Update users set " + "login_name = ?, " + "login_password = ?, " + "first_name = ?, " +
            "last_name = ?, " + "title = ?, " + "company = ?, " + "address =  ?, " + "city = ?, " + "zip = ?, " +
            "country = ?, " + "county_council =?, " + "email = ?, " + "user_type = ?, " + "active = ?, " +
            "lang_id = ? " + "WHERE user_id = ?";
        Object[] paramValues = new Object[]{userData.login_name, userData.login_password, userData.first_name, userData.last_name, userData.title, userData.company, userData.address, userData.city, userData.zip, userData.country, userData.county_council, userData.email, new Integer( userData.user_type ), new Integer( userData.active ), new Integer( userData.lang_id ), new Integer( userData.user_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    /*
    This function adds a new phone numbers to the db. Used by AdminUserProps
    */
    // todo: se till att detta fungerar �ven om fler f�rs�ker g�ra insert samtigit.
    // Todo: n�ja mig med synchroniced p� metoden? Eller se till att testa n�gra g�nger tills det g�r igenom?
    int sproc_phoneNbrAdd( int userId, String number, int phoneType ) {
        int rowCount = 0;
        SQLProcessor.SQLTransaction transcation = sqlProcessor.startTransaction();
        try {
            rowCount = insertInto_phones( transcation, number, userId, phoneType );
            transcation.commit();
        } catch( SQLException ex ) {
            transcation.rollback();
        }
        return rowCount;
    }

    private int insertInto_phones( SQLProcessor.SQLTransaction transaction, String number, int userId, int phoneType ) throws SQLException {
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

    // todo: ta bort fr�n samtliga forreign key st�llen (och inte bara fr�n user_roles_crossref)? phones, user_flags_crossref, user_rights, useradmin_role_crossref
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
    // todo d�p om denna till, userExists eller n�t
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

    // todo d�p om till deleteAllPhonenumbersForUser eller n�t
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

    static class Table_phonetypes {
        private int phonetype_id;
        private String typename;

        Table_phonetypes( ResultSet rs ) throws SQLException {
            phonetype_id = rs.getInt( "phonetype_id" );
            typename = rs.getString( "typename" );
        }
    }

    Table_phonetypes[] sproc_GetPhonetypes_ORDER_BY_phonetype_id( int lang_id ) {
        String sql = " SELECT  phonetype_id, typename FROM phonetypes WHERE lang_id = ? ORDER BY phonetype_id";
        Object[] paramValues = new Object[]{new Integer( lang_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_phonetypes( rs );
            }
        } );
        return (Table_phonetypes[])queryResult.toArray( new Table_phonetypes[queryResult.size()] );
    }

    static class Table_phone {
        private int phone_id;
        private String phoneNumber;

        Table_phone( ResultSet rs ) throws SQLException {
            phone_id = rs.getInt( "phone_id" );
            phoneNumber = rs.getString( "number" );
        }
    }

    /**
     * Used to generate a list with all type of users. Used from UserChangePrefs
     * @param user_id
     * @return
     */
    // todo: Warning, this method used to RTIM the phone numer result, not any longer...
    Table_phone[] sproc_GetUserPhones( int user_id ) {
        String sql = "SELECT p.phone_id, p.number FROM users u , phones p " +
            "WHERE u.user_id = p.user_id AND u.user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_phone( rs );
            }
        } );
        return (Table_phone[])queryResult.toArray( new Table_phone[queryResult.size()] );
    }

    static class MoreThanOneTable_phones_phonetypes {
        private int phone_id;
        private String number;
        private int user_id;
        private int phonetype_id;
        private String typename;

        MoreThanOneTable_phones_phonetypes( ResultSet rs ) throws SQLException {
            phone_id = rs.getInt( "phone_id" );
            number = rs.getString( "number" );
            user_id = rs.getInt( "user_id" );
            phonetype_id = rs.getInt( "phonetype_id" );
            typename = rs.getString( "typename" );
        }
    }

    // todo: Do we realy need to return user_id?
    // todo: This should be able to be used instead of sproc_GetUserPhones, why not?
    MoreThanOneTable_phones_phonetypes[] sproc_GetUserPhoneNumbers( int user_id ) {
        String sql = "SELECT phones.phone_id, phones.number, phones.user_id, phones.phonetype_id, phonetypes.typename " +
            "FROM phones " +
            "INNER JOIN users ON phones.user_id = users.user_id " +
            "INNER JOIN phonetypes ON phones.phonetype_id = phonetypes.phonetype_id AND users.lang_id = phonetypes.lang_id " +
            "WHERE phones.user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new MoreThanOneTable_phones_phonetypes( rs );
            }
        } );
        return (MoreThanOneTable_phones_phonetypes[])queryResult.toArray( new MoreThanOneTable_phones_phonetypes[queryResult.size()] );
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

    // todo: D�p om till documentExixts eller n�t...
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
    // todo: Testa denna!!! Och g� igenom nogrannt!!!
    // todo: Ska inte �ndringsdatumen uppdateras i denna ocks�?
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

    static class PartOfTable_document {
        private int meta_id;
        private int parentcount;
        private String meta_headline;
        private int doc_type;

        PartOfTable_document( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            parentcount = rs.getInt( "parentcount" );
            meta_headline = rs.getString( "meta_headline" );
            doc_type = rs.getInt( "doc_type" );
        }
    }

    /**
     * Lists documents user is allowed to see.
     * @param user_id
     * @param start
     * @param end
     * @return
     */
    // todo: d�p om till getDocsForUser eller n�t.
    // todo: Tog bort i f�rsta raden ett DISTINCT fr�n COUNT(DISTINCT c.meta_id) till COUNT(c.meta_id), fundera igenom om det g�r n�got?
    PartOfTable_document[] sproc_getDocs( int user_id, int start, int end ) {
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
                return new PartOfTable_document( rs );
            }
        } );
        return (PartOfTable_document[])queryResult.toArray( new PartOfTable_document[queryResult.size()] );
    }

    static class MoreThanOneTable_meta_childs {
        private int to_meta_id;
        private int menu_sort;
        private int manual_sort_order;
        private int doc_type;
        private boolean archive;
        private String target;
        private Timestamp date_created;
        private Timestamp date_modified;
        private String meta_headline;
        private String meta_text;
        private String meta_image;
        private String frame_name;
        private Timestamp activated_datetime;
        private Timestamp archived_datetime;
        private String filename;

        MoreThanOneTable_meta_childs( ResultSet rs ) throws SQLException {
            to_meta_id = rs.getInt( "to_meta_id" );
            menu_sort = rs.getInt( "menu_sort" );
            manual_sort_order = rs.getInt( "manual_sort_order" );
            doc_type = rs.getInt( "doc_type" );
            archive = (rs.getInt( "archive" ) == 1);
            target = rs.getString( "target" );
            date_created = rs.getTimestamp( "date_created" );
            date_modified = rs.getTimestamp( "date_modified" );
            meta_headline = rs.getString( "meta_headline" );
            meta_text = rs.getString( "meta_text" );
            meta_image = rs.getString( "meta_image" );
            frame_name = rs.getString( "frame_name" );
            activated_datetime = rs.getTimestamp( "activated_datetime" );
            archived_datetime = rs.getTimestamp( "archived_datetime" );
            filename = rs.getString( "filename" );
        }
    }
    /**
     * Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
     * @param meta_id
     * @param user_id
     */
    // todo WARNING, i anropande kod m�ste en f�r�ndring ske!
    // todo Den bortkommenterade reden nedan ber�knar om man har r�tt att editera eller ej.
    // todo Se till att g�ra den kollen p� annat s�tt efter�t f�r varje dokument.
    MoreThanOneTable_meta_childs[] sproc_getChilds( int meta_id, int user_id ) {
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
            "         m.show_meta != 0 " + //  and also include documents that are to be shown regardless of rights. (Visa �ven f�r obeh�riga)
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
                return new MoreThanOneTable_meta_childs( rs );
            }
        } );
        return (MoreThanOneTable_meta_childs[])queryResult.toArray( new MoreThanOneTable_meta_childs[queryResult.size()] );
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
    // todo, se till att denna inte anropas direkt utan att man anropar metoder isUserAdmin och isSuperAdmin ist�llet.
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

    // todo: D�p om till hasUserSharePemissionForDocument
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
    // todo: se till att man anv�nder de tv� implementations metoderna direkt i st�llet.
    // todo: d�p om till deleteUsersRole
    int sproc_DelUserRoles( int user_id, int role_id ) {
        int rowCount = 0;
        if( role_id == -1 ) {
            rowCount += deletaAllUserRoles( user_id );
        } else {
            rowCount += deleteFrom_user_roles( user_id, role_id );
        }
        return rowCount;
    }

    private int deletaAllUserRoles( int user_id ) {
        String sql = "DELETE FROM user_roles_crossref WHERE user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        return sqlProcessor.executeUpdate( sql, paramValues );
    }

    private int deleteFrom_user_roles( int user_id, int role_id ) {
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
        private int doc_type;
        private String type;

        Table_doc_types( ResultSet rs ) throws SQLException {
            doc_type = rs.getInt( "doc_type" );
            type = rs.getString( "type" );
        }
    }

    Table_doc_types[] sproc_GetDocTypes( String lang_prefix ) {
        String sql = "SELECT doc_type,type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type";
        Object[] paramValues = new Object[]{lang_prefix};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_doc_types( rs );
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

        int[] result = new int[fileDocs.size()];
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
        Table_doc_types[] user_doc_types = sproc_GetDocTypesForUser( user_id.intValue(), parent_meta_id.intValue(), "se" );
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

    /**
     Nice query that fetches all document types a user may create in a document,
     for easy insertion into an html-option-list, no less!
     */
    Table_doc_types[] sproc_GetDocTypesForUser( int user_id, int meta_id, String lang_prefix ) {
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
                return new Table_doc_types( rs );
            }
        } );
        return (Table_doc_types[])queryResult.toArray( new Table_doc_types[queryResult.size()] );
    }

    static class Table_meta {
        int meta_id;
        private String description;
        private int doc_type;
        private String meta_headline;
        private String meta_text;
        private String meta_image;
        private int owner_id;
        private int permissions;
        private int shared;
        private int expand;
        private int show_meta;
        private int help_text_id;
        private int archive;
        private int status_id;
        private String lang_prefix;
        private String classification;
        private Timestamp date_created;
        private Timestamp date_modified;
        private int sort_position;
        private int menu_position;
        private int disable_search;
        private String target;
        private String frame_name;
        private int activate;
        private Timestamp activated_datetime;
        private Timestamp archived_datetime;

        Table_meta( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            description = rs.getString( "description" );
            doc_type = rs.getInt( "doc_type" );
            meta_headline = rs.getString( "meta_headline" );
            meta_text = rs.getString( "meta_text" );
            meta_image = rs.getString( "meta_image" );
            owner_id = rs.getInt( "owner_id" );
            permissions = rs.getInt( "permissions" );
            shared = rs.getInt( "shared" );
            expand = rs.getInt( "expand" );
            show_meta = rs.getInt( "show_meta" );
            help_text_id = rs.getInt( "help_text_id" );
            archive = rs.getInt( "archive" );
            status_id = rs.getInt( "status_id" );
            lang_prefix = rs.getString( "lang_prefix" );
            classification = rs.getString( "classification" );
            date_created = rs.getTimestamp( "date_created" );
            date_modified = rs.getTimestamp( "date_modified" );
            sort_position = rs.getInt( "sort_position" );
            menu_position = rs.getInt( "menu_position" );
            disable_search = rs.getInt( "disable_search" );
            target = rs.getString( "target" );
            frame_name = rs.getString( "frame_name" );
            activate = rs.getInt( "activate" );
            activated_datetime = rs.getTimestamp( "activated_datetime" );
            archived_datetime = rs.getTimestamp( "archived_datetime" );
        }
    }

    Table_meta sproc_GetDocumentInfo( int meta_id ) {
        Table_meta result = selectFrom_meta( new Integer( meta_id ) );
        return result;
    }

    private Table_meta selectFrom_meta( Integer meta_id ) {
        String sql = "Select meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, " +
            "shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, " +
            "date_modified, sort_position, menu_position, disable_search, target, frame_name, activate, " +
            "activated_datetime, archived_datetime " +
            "FROM meta WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_meta( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_meta)queryResult.get( 0 );
        }
    }

    private int insertInto_meta( SQLProcessor.SQLTransaction transaction, Table_meta tableData ) throws SQLException {
        String sql = "INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, " +
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
        private int meta_id;
        private int template_id;
        private int group_id;
        private int sort_order;
        private int default_template_1;
        private int default_template_2;

        Table_text_docs( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            template_id = rs.getInt( "template_id" );
            group_id = rs.getInt( "group_id" );
            sort_order = rs.getInt( "sort_order" );
            default_template_1 = rs.getInt( "default_template_1" );
            default_template_2 = rs.getInt( "default_template_2" );
        }
    }

    private Table_text_docs selectFrom_text_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, template_id, group_id, sort_order, default_template_1, default_template_2 " +
            "FROM text_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_text_docs( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_text_docs)queryResult.get( 0 );
        }
    }

    private int insertInto_text_docs( SQLProcessor.SQLTransaction transaction, Table_text_docs tableData ) throws SQLException {
        String sql = "INSERT INTO text_docs ( meta_id, template_id, group_id, sort_order, default_template_1, default_template_2 ) " +
            "VALUES ( ?,?,?,?,?,? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.template_id ),
                                            new Integer( tableData.group_id ), new Integer( tableData.sort_order ),
                                            new Integer( tableData.default_template_1 ), new Integer( tableData.default_template_2 )};
        return transaction.executeUpdate( sql, paramValues );
    };

    static class Table_url_docs {
        private int meta_id;
        private String frame_name;
        private String target;
        private String url_ref;
        private String url_txt;
        private String lang_prefix;

        Table_url_docs( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            frame_name = rs.getString( "frame_name" );
            target = rs.getString( "target" );
            url_ref = rs.getString( "url_ref" );
            url_txt = rs.getString( "url_txt" );
            lang_prefix = rs.getString( "lang_prefix" );
        }
    }

    private Table_url_docs selectFrom_url_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, frame_name, target, url_ref, url_txt, lang_prefix FROM url_docs WHERE meta_id = ?";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_url_docs( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_url_docs)queryResult.get( 0 );
        }
    }

    private int insertInto_url_docs( SQLProcessor.SQLTransaction transaction, Table_url_docs tableData ) throws SQLException {
        String sql = "INSERT INTO url_docs (meta_id, frame_name, target, url_ref, url_txt, lang_prefix ) " +
            "VALUES ( ?, ?, ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.frame_name, tableData.target,
                                            tableData.url_ref, tableData.url_txt, tableData.lang_prefix};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_browser_docs {
        private int meta_id;
        private int to_meta_id;
        private int browser_id;

        Table_browser_docs( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            to_meta_id = rs.getInt( "to_meta_id" );
            browser_id = rs.getInt( "browser_id" );
        }
    }

    private Table_browser_docs selectFrom_browser_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, to_meta_id, browser_id FROM browser_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_browser_docs( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_browser_docs)queryResult.get( 0 );
        }
    }

    static class MoreThanOneTable_meta_browser_docs {
        private int to_meta_id;
        String meta_headline;

        MoreThanOneTable_meta_browser_docs( ResultSet rs ) throws SQLException {
            to_meta_id = rs.getInt( "to_meta_id" );
            meta_headline = rs.getString( "meta_headline" );
        }
    }

    MoreThanOneTable_meta_browser_docs[] sproc_getBrowserDocChilds( int meta_id, int user_id ) {
        String sql = "SELECT DISTINCT to_meta_id, meta_headline FROM browser_docs bd JOIN meta m " +
            "ON  bd.to_meta_id = m.meta_id AND bd.meta_id = ? " +
            "LEFT JOIN roles_rights rr ON rr.meta_id = m.meta_id AND rr.set_id < 4 " +
            "JOIN user_roles_crossref urc ON urc.user_id = ? AND (urc.role_id = 0 OR urc.role_id = rr.role_id OR m.shared = 1)" +
            "WHERE m.activate = 1 ORDER BY to_meta_id";
        Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( user_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new MoreThanOneTable_meta_browser_docs( rs );
            }
        } );
        return (MoreThanOneTable_meta_browser_docs[])queryResult.toArray( new MoreThanOneTable_meta_browser_docs[queryResult.size()] );
    }

    private int insertInto_browser_docs( SQLProcessor.SQLTransaction transaction, Table_browser_docs tableData ) throws SQLException {
        String sql = "INSERT INTO browser_docs ( meta_id, to_meta_id, browser_id ) VALUES (?,?,?)";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.to_meta_id ),
                                            new Integer( tableData.browser_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_frameset_docs {
        private int meta_id;
        private String frame_set;

        Table_frameset_docs( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            frame_set = rs.getString( "frame_set" );
        }
    }

    private Table_frameset_docs selectFrom_frameset_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, frame_set FROM frameset_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_frameset_docs( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_frameset_docs)queryResult.get( 0 );
        }
    }

    private int insertInto_frameset_docs( SQLProcessor.SQLTransaction transaction, Table_frameset_docs tableData ) throws SQLException {
        String sql = "INSERT INTO frameset_docs ( meta_id, frame_set ) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.frame_set};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_fileupload_docs {
        private int meta_id;
        private String filename;
        private String mime;

        Table_fileupload_docs( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            filename = rs.getString( "filename" );
            mime = rs.getString( "mime" );
        }
    }

    private Table_fileupload_docs selectFrom_fileupload_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, filename, mime FROM fileupload_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_fileupload_docs( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_fileupload_docs)queryResult.get( 0 );
        }
    }

    private int insertInto_fileupload_docs( SQLProcessor.SQLTransaction transaction, Table_fileupload_docs tableData ) throws SQLException {
        String sql = "INSERT INTO fileupload_docs ( meta_id, filename, mime ) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.filename, tableData.mime};
        return transaction.executeUpdate( sql, paramValues );
    }

    class Table_texts {
        private int meta_id;
        private int name;
        private String text;
        private int type;
        private int counter;

        Table_texts( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            name = rs.getInt( "name" );
            text = rs.getString( "text" );
            type = rs.getInt( "type" );
            counter = rs.getInt( "counter" );
        }
    }

    private Table_texts[] selectFrom_texts( Integer meta_id ) {
        String sql = "SELECT meta_id, name, text, type, counter FROM texts WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_texts( rs );
            }
        } );
        return (Table_texts[])queryResult.toArray( new Table_texts[queryResult.size()] );
    }

    private int insertInto_texts( SQLProcessor.SQLTransaction transaction, Table_texts tableData ) throws SQLException {
        String sql = "INSERT INTO texts ( meta_id, name, text, type, counter ) VALUES ( ?, ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.name ), tableData.text,
                                            new Integer( tableData.type ), new Integer( tableData.counter )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_images {
        private int meta_id;
        private int width;
        private int height;
        private int border;
        private int v_space;
        private int h_space;
        private int name;
        private String image_name;
        private String target;
        private String target_name;
        private String align;
        private String alt_text;
        private String low_scr;
        private String imgurl;
        private String linkurl;

        Table_images( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            width = rs.getInt( "width" );
            height = rs.getInt( "height" );
            border = rs.getInt( "border" );
            v_space = rs.getInt( "v_space" );
            h_space = rs.getInt( "h_space" );
            name = rs.getInt( "name" );
            image_name = rs.getString( "image_name" );
            target = rs.getString( "target" );
            target_name = rs.getString( "target_name" );
            align = rs.getString( "align" );
            alt_text = rs.getString( "alt_text" );
            low_scr = rs.getString( "low_scr" );
            imgurl = rs.getString( "imgurl" );
            linkurl = rs.getString( "linkurl" );
        }
    }

    // todo: Varning, Denna sproc returnerar inte exakt i den ordning som den ursprungliga sproc ville ha det.
    // todo: ej heller ineh�ller resultatet den formaterade varianten av name: '#img'+convert(varchar(5), name)+'#'
    Table_images[] sproc_getImages( int meta_id ) {
        return selectFrom_images( new Integer( meta_id ) );
    }

    private Table_images[] selectFrom_images( Integer meta_id ) {
        String sql = "SELECT meta_id, width, height, border, v_space, h_space, name, image_name, target, target_name, " +
            "align, alt_text, low_scr, imgurl, linkurl FROM images WHERE meta_id = ?";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_images( rs );
            }
        } );
        return (Table_images[])queryResult.toArray( new Table_images[queryResult.size()] );
    }

    private int insertInto_images( SQLProcessor.SQLTransaction transaction, Table_images tableData ) throws SQLException {
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
        private int meta_id;
        private int include_id;
        private int included_meta_id;

        Table_includes( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            include_id = rs.getInt( "include_id" );
            included_meta_id = rs.getInt( "included_meta_id" );
        }
    }

    Table_includes[] sproc_GetInclues( int meta_id ) {
        return selectFrom_includes( new Integer( meta_id ) );
    }

    private Table_includes[] selectFrom_includes( Integer meta_id ) {
        String sql = "SELECT meta_id, include_id, included_meta_id FROM includes WHERE meta_id = ? ORDER BY include_id";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_includes( rs );
            }
        } );
        return (Table_includes[])queryResult.toArray( new Table_includes[queryResult.size()] );
    }

    private int insertInto_includes( SQLProcessor.SQLTransaction transaction, Table_includes tableData ) throws SQLException {
        String sql = "INSERT INTO includes ( meta_id, include_id, included_meta_id ) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.include_id ),
                                            new Integer( tableData.included_meta_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_doc_permission_sets {
        private int meta_id;
        private int set_id;
        private int permission_id;

        Table_doc_permission_sets( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            set_id = rs.getInt( "set_id" );
            permission_id = rs.getInt( "permission_id" );
        }
    }

    private Table_doc_permission_sets selectFrom_doc_permission_sets( Integer meta_id ) {
        String sql = "SELECT meta_id, set_id, permission_id FROM doc_permission_sets WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_doc_permission_sets( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_doc_permission_sets)queryResult.get( 0 );
        }
    }

    private int insertInto_doc_permission_sets( SQLProcessor.SQLTransaction transaction, Table_doc_permission_sets tableData ) throws SQLException {
        String sql = "INSERT INTO doc_permission_sets ( meta_id, set_id, permission_id ) VALUES( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_new_doc_permission_sets {
        private int meta_id;
        private int set_id;
        private int permission_id;

        Table_new_doc_permission_sets( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            set_id = rs.getInt( "set_id" );
            permission_id = rs.getInt( "permission_id" );
        }
    }

    private Table_new_doc_permission_sets selectFrom_new_doc_permission_sets( Integer meta_id ) {
        String sql = "SELECT meta_id, set_id, permission_id FROM new_doc_permission_sets WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_new_doc_permission_sets( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_new_doc_permission_sets)queryResult.get( 0 );
        }
    }

    private int insertInto_new_doc_permission_sets( SQLProcessor.SQLTransaction transaction, Table_new_doc_permission_sets tableData ) throws SQLException {
        String sql = "INSERT INTO new_doc_permission_sets (meta_id, set_id, permission_id) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_doc_permission_sets_ex {
        private int meta_id;
        private int set_id;
        private int permission_id;
        private int permission_data;

        Table_doc_permission_sets_ex( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            set_id = rs.getInt( "set_id" );
            permission_id = rs.getInt( "permission_id" );
            permission_data = rs.getInt( "permission_data" );
        }
    }

    private Table_doc_permission_sets_ex selectFrom_doc_permission_sets_ex( Integer meta_id ) {
        String sql = "SELECT meta_id, set_id, permission_id, permission_data FROM doc_permission_sets_ex WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_doc_permission_sets_ex( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_doc_permission_sets_ex)queryResult.get( 0 );
        }
    }

    private int insertInto_doc_permission_sets_ex( SQLProcessor.SQLTransaction transaction, Table_doc_permission_sets_ex tableData ) throws SQLException {
        String sql = "INSERT INTO doc_permission_sets_ex (meta_id, set_id, permission_id, permission_data) VALUES ( ?, ?, ?, ? ) ";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id ), new Integer( tableData.permission_data )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_new_doc_permission_sets_ex {
        private int meta_id;
        private int set_id;
        private int permission_id;
        private int permission_data;

        Table_new_doc_permission_sets_ex( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            set_id = rs.getInt( "set_id" );
            permission_id = rs.getInt( "permission_id" );
            permission_data = rs.getInt( "permission_data" );
        }
    }

    private Table_new_doc_permission_sets_ex selectFrom_new_doc_permission_sets_ex( Integer meta_id ) {
        String sql = "SELECT meta_id, set_id, permission_id, permission_data FROM new_doc_permission_sets_ex WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_new_doc_permission_sets_ex( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_new_doc_permission_sets_ex)queryResult.get( 0 );
        }
    }

    private int insertInto_new_doc_permission_sets_ex( SQLProcessor.SQLTransaction transaction, Table_new_doc_permission_sets_ex tableData ) throws SQLException {
        String sql = "INSERT INTO new_doc_permission_sets_ex (meta_id, set_id, permission_id, permission_data) VALUES ( ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id ), new Integer( tableData.permission_data )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_roles_rights {
        private int role_id;
        private int meta_id;
        private int set_id;

        Table_roles_rights( ResultSet rs ) throws SQLException {
            role_id = rs.getInt( "role_id" );
            meta_id = rs.getInt( "meta_id" );
            set_id = rs.getInt( "set_id" );
        }
    }

    private Table_roles_rights selectFrom_roles_rights( Integer meta_id ) {
        String sql = "SELECT role_id, meta_id, set_id FROM roles_rights WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_roles_rights( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_roles_rights)queryResult.get( 0 );
        }
    }

    private int insertInto_roles_rights( SQLProcessor.SQLTransaction transaction, Table_roles_rights tableData ) throws SQLException {
        String sql = "INSERT INTO roles_rights (role_id, meta_id, set_id) VALUES ( ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.role_id ), new Integer( tableData.meta_id ), new Integer( tableData.set_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_meta_classification {
        private int meta_id;
        private int class_id;

        Table_meta_classification( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            class_id = rs.getInt( "class_id" );
        }
    }

    private Table_meta_classification selectFrom_meta_classification( Integer meta_id ) {
        String sql = "SELECT meta_id, class_id FROM meta_classification WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_meta_classification( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_meta_classification)queryResult.get( 0 );
        }
    }

    private int insertInto_meta_classification( SQLProcessor.SQLTransaction transaction, Table_meta_classification tableData ) throws SQLException {
        String sql = "INSERT INTO meta_classification (meta_id, class_id) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.class_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    static class Table_meta_section {
        int meta_id;
        int section_id;

        Table_meta_section() {}

        Table_meta_section( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            section_id = rs.getInt( "section_id" );
        }
    }

    private Table_meta_section selectFrom_meta_section( Integer meta_id ) {
        String sql = "SELECT meta_id, section_id FROM meta_section WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_meta_section( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_meta_section)queryResult.get( 0 );
        }
    }

    private int insertInto_meta_section( SQLProcessor.SQLTransaction transaction, Table_meta_section tableData ) throws SQLException {
        String sql = "INSERT INTO meta_section ( meta_id, section_id ) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.section_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    // todo: L�s detta med att tv� uppdateringar kan skriva �ver varandra.
    private int copyDocument( Integer meta_id ) {
        int rowCount = 0;
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        int nexFreeMetaId = 1 + getMaxIntValue( transaction, "meta", "meta_id" );
        try {
            Table_meta metaToBeCopied = selectFrom_meta( meta_id );
            metaToBeCopied.meta_id = nexFreeMetaId;
            rowCount += insertInto_meta( transaction, metaToBeCopied );

            Table_text_docs textDocsToBeCopied = selectFrom_text_docs( meta_id );
            if( null != textDocsToBeCopied ) {
                textDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_text_docs( transaction, textDocsToBeCopied );
            }

            Table_url_docs urlDocsToBeCopied = selectFrom_url_docs( meta_id );
            if( null != urlDocsToBeCopied ) {
                urlDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_url_docs( transaction, urlDocsToBeCopied );
            }

            Table_browser_docs browserDocsToBeCopied = selectFrom_browser_docs( meta_id );
            if( null != browserDocsToBeCopied ) {
                browserDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_browser_docs( transaction, browserDocsToBeCopied );
            }

            Table_frameset_docs framesetDocsToBeCopied = selectFrom_frameset_docs( meta_id );
            if( null != framesetDocsToBeCopied ) {
                framesetDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_frameset_docs( transaction, framesetDocsToBeCopied );
            }

            Table_fileupload_docs fileUploadDocsToBeCopied = selectFrom_fileupload_docs( meta_id );
            if( null != fileUploadDocsToBeCopied ) {
                fileUploadDocsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_fileupload_docs( transaction, fileUploadDocsToBeCopied );
            }

            Table_texts[] textsToBeCopied = selectFrom_texts( meta_id );
            for( int i = 0; i < textsToBeCopied.length; i++ ) {
                Table_texts textToBeCopied = textsToBeCopied[i];
                textToBeCopied.meta_id = meta_id.intValue();
                int newCounterValue = getMaxIntValue( transaction, "texts", "counter" );
                textToBeCopied.counter = newCounterValue;
                rowCount += insertInto_texts( transaction, textToBeCopied );
            }

            Table_images[] imagesToBeCopied = selectFrom_images( meta_id );
            for( int i = 0; i < imagesToBeCopied.length; i++ ) {
                Table_images imageToBeCopied = imagesToBeCopied[i];
                imageToBeCopied.meta_id = meta_id.intValue();
                rowCount += insertInto_images( transaction, imageToBeCopied );
            }

            Table_includes[] includesToBeCopied = selectFrom_includes( meta_id );
            for( int i = 0; i < includesToBeCopied.length; i++ ) {
                Table_includes table_includes = includesToBeCopied[i];
                table_includes.meta_id = nexFreeMetaId;
                rowCount += insertInto_includes( transaction, table_includes );
            }

            Table_doc_permission_sets docPermissionSetsToBeCopied = selectFrom_doc_permission_sets( meta_id );
            if( null != docPermissionSetsToBeCopied ) {
                docPermissionSetsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_doc_permission_sets( transaction, docPermissionSetsToBeCopied );
            }

            Table_new_doc_permission_sets newDocPermissionSetsToBeCopied = selectFrom_new_doc_permission_sets( meta_id );
            if( null != newDocPermissionSetsToBeCopied ) {
                newDocPermissionSetsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_new_doc_permission_sets( transaction, newDocPermissionSetsToBeCopied );
            }

            Table_doc_permission_sets_ex docPermissionsSetsExToBeCopied = selectFrom_doc_permission_sets_ex( meta_id );
            if( null != docPermissionsSetsExToBeCopied ) {
                docPermissionsSetsExToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_doc_permission_sets_ex( transaction, docPermissionsSetsExToBeCopied );
            }

            Table_new_doc_permission_sets_ex newDocPermissionSetsExToBeCopied = selectFrom_new_doc_permission_sets_ex( meta_id );
            if( null != newDocPermissionSetsExToBeCopied ) {
                newDocPermissionSetsExToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_new_doc_permission_sets_ex( transaction, newDocPermissionSetsExToBeCopied );
            }

            Table_roles_rights rolesRightsToBeCopied = selectFrom_roles_rights( meta_id );
            if( null != rolesRightsToBeCopied ) {
                rolesRightsToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_roles_rights( transaction, rolesRightsToBeCopied );
            }

            Table_meta_classification metaClassificationToBeCopied = selectFrom_meta_classification( meta_id );
            if( null != metaClassificationToBeCopied ) {
                metaClassificationToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_meta_classification( transaction, metaClassificationToBeCopied );
            }

            Table_meta_section metaSectionToBeCopied = selectFrom_meta_section( meta_id );
            if( null != metaSectionToBeCopied ) {
                metaSectionToBeCopied.meta_id = nexFreeMetaId;
                rowCount += insertInto_meta_section( transaction, metaSectionToBeCopied );
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
    // Todo: Denna returnerade i orginalutf�randet endast en del av datan som finns f�r rad i tabellen texts,
    // todo: ok att returnera hela p� detta s�tt?
    Table_texts sproc_GetText( int meta_id, int name ) {
        Table_texts[] allTextInDocument = selectFrom_texts( new Integer( meta_id ) );
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

    Table_users selectFrom_users( Integer user_id ) {
        String sql = "select user_id,login_name,login_password,first_name,last_name,title,company,address,city,zip,country,county_council,email,external,last_page,archive_mode,lang_id,user_type,active,create_date from users " +
            "WHERE user_id = ? ";
        Object[] paramValues = new Object[]{user_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_users( rs );
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

    // todo: Denna returnerar lang_id ist�llet f�r lang prefix
    // todo: anropa �ven sproc_GetLangPrefixFromId f�r denna information
    Table_users sproc_GetUserInfo( int user_id ) {
        return selectFrom_users( new Integer( user_id ) );
    }

    static class Table_languages {
        private String lang_prefix;
        private String user_prefix;
        private String language;

        Table_languages( ResultSet rs ) throws SQLException {
            lang_prefix = rs.getString( "lang_prefix" );
            user_prefix = rs.getString( "user_prefix" );
            language = rs.getString( "language" );
        }
    }

    Table_languages[] sproc_getLanguages() {
        String sql = "select lang_prefix, user_prefix, language from languages order by language";
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_languages( rs );
            }
        } );
        return (Table_languages[])queryResult.toArray( new Table_languages[queryResult.size()] );
    }

    /** Get the users preferred language. Used by the administrator functions.
     * Begin with getting the users langId from the userobject.
     */
    Table_lang_prefixes sproc_GetLangPrefixFromId( int lang_id ) {
        return selectFrom_lang_prefixes( new Integer( lang_id ) );
    }

    static class Table_lang_prefixes {
        private int lang_id;
        String lang_prefix;

        Table_lang_prefixes( ResultSet rs ) throws SQLException {
            lang_id = rs.getInt( "lang_id" );
            lang_prefix = rs.getString( "lang_prefix" );
        }
    }

    Table_lang_prefixes selectFrom_lang_prefixes( Integer lang_id ) {
        String sql = "SELECT lang_id, lang_prefix FROM lang_prefixes WHERE lang_id = ? ";
        Object[] paramValues = new Object[]{lang_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_lang_prefixes( rs );
            }
        } );
        if( queryResult.size() == 0 ) {
            return null;
        } else {
            return (Table_lang_prefixes)queryResult.get( 0 );
        }
    }

    int sproc_GetRoleIdByRoleName( String role_name ) {
        String sql = "SELECT role_id FROM roles WHERE role_name like ? ";
        Object[] paramValues = new Object[]{role_name};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "role_id" ) );
            }
        } );
        return ((Integer)queryResult.get( 0 )).intValue();
    }

    static class MoreThanOneTable_langprefixes_language {
        private int lang_id;
        private String language;

        MoreThanOneTable_langprefixes_language( ResultSet rs ) throws SQLException {
            lang_id = rs.getInt( "lang_id" );
            language = rs.getString( "language" );
        }
    }

    MoreThanOneTable_langprefixes_language[] sproc_GetLanguageList( String lang_prefix ) {
        String sql = "SELECT lp.lang_id , lang.language FROM lang_prefixes lp, languages lang " +
            "WHERE lp.lang_prefix = lang.lang_prefix AND lang.user_prefix = ? ";
        Object[] paramValues = new Object[]{lang_prefix};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new MoreThanOneTable_langprefixes_language( rs );
            }
        } );
        return (MoreThanOneTable_langprefixes_language[])queryResult.toArray( new MoreThanOneTable_langprefixes_language[queryResult.size()] );
    }

    static class Table_templates {
        private int template_id;
        private String simple_name;

        Table_templates( ResultSet rs ) throws SQLException {
            template_id = rs.getInt( "template_id" );
            simple_name = rs.getString( "simple_name" );
        }
    }

    Table_templates[] sproc_getTemplates() {
        String sql = "select template_id, simple_name from templates";
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_templates( rs );
            };
        } );
        return (Table_templates[])queryResult.toArray( new Table_templates[queryResult.size()] );
    }

    Table_templates[] sproc_GetTemplatesInGroup( int groupId ) {
        String sql = "SELECT t.template_id,simple_name FROM templates t " +
            "JOIN templates_cref c ON  t.template_id = c.template_id " +
            "WHERE c.group_id = ? " +
            "ORDER BY simple_name";
        Object[] paramValues = new Object[]{new Integer( groupId )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_templates( rs );
            }
        } );
        return (Table_templates[])queryResult.toArray( new Table_templates[queryResult.size()] );
    }

    int sproc_GetTemplateId( String simple_name ) {
        String sql = "SELECT template_id FROM templates WHERE simple_name = ? ";
        Object[] paramValues = new Object[]{simple_name};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                int template_id = rs.getInt( "template_id" );
                return new Integer( template_id );
            }
        } );
        return ((Integer)queryResult.get( 0 )).intValue();
    }

    String sproc_GetUserPassword( int user_id ) {
        String sql = "SELECT login_password FROM USERS WHERE user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return rs.getString( "login_password" );
            }
        } );
        if( 0 == queryResult.size() ) {
            return "";// todo: borde det inte vara null ist�llet?
        } else {
            return (String)queryResult.get( 0 );
        }
    }

    static class PartOfTable_roles {
        private int role_id;
        private String role_name;

        PartOfTable_roles( ResultSet rs ) throws SQLException {
            role_id = rs.getInt( "role_id" );
            role_name = rs.getString( "role_name" );
        }
    }

    PartOfTable_roles[] sproc_getUserRoleIds( int user_id ) {
        String sql = "SELECT roles.role_id, role_name FROM roles, user_roles_crossref WHERE roles.role_id = user_roles_crossref.role_id AND user_roles_crossref.user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new PartOfTable_roles( rs );
            }
        } );
        return (PartOfTable_roles[])queryResult.toArray( new PartOfTable_roles[queryResult.size()] );
    }

    static class PartOfTable_users {
        private int user_id;
        private String first_name;
        private String last_name;

        PartOfTable_users( ResultSet rs ) throws SQLException {
            user_id = rs.getInt( "user_id" );
            first_name = rs.getString( "first_name" );
            last_name = rs.getString( "last_name" );
        }
    }

    // todo: Denna returnerar inte riktigt samma som tidigare. F�r och Efternamn �r uppdelat i olika f�lt.
    PartOfTable_users[] sproc_GetUsersWhoBelongsToRole( int role_id ) {
        String sql = "SELECT u.user_id, u.last_name, u.first_name FROM user_roles_crossref us " +
            "JOIN users u ON us.user_id = u.user_id WHERE role_id = ? ORDER BY last_name";
        Object[] paramValues = new Object[]{new Integer( role_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new PartOfTable_users( rs );
            }
        } );
        return (PartOfTable_users[])queryResult.toArray( new PartOfTable_users[queryResult.size()] );
    }

    /**
     This function is used from AdminIpAcces servlet to generate a list
     */
    // todo: Denna returnerar inte riktigt samma som tidigare. F�r och Efternamn �r uppdelat i olika f�lt.
    PartOfTable_users[] sproc_GetAllUsersInList() {
        String sql = "SELECT user_id, last_name, first_name from users ORDER BY last_name";
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new PartOfTable_users( rs );
            }
        } );
        return (PartOfTable_users[])queryResult.toArray( new PartOfTable_users[queryResult.size()] );
    }

    // todo: d�p om sysdata_value s� man f�rst�r vad detta �r f�r n�got...
    int sproc_GetCurrentSessionCounter() {
        Integer type_id = new Integer( 1 );
        return Integer.parseInt( getSysdata( type_id ) );
    }

    // todo: returnera date objekt i st�llet? �ndra typen i databasen?
    String sproc_GetCurrentSessionCounterDate() {
        Integer type_id = new Integer( 2 );
        return getSysdata( type_id );
    }

    private String getSysdata( Integer type_id ) {
        String sql = " SELECT sysdata_value FROM sys_data WHERE type_id  = ? ";
        Object[] paramValues = new Object[]{type_id};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return rs.getString( "sysdata_value" );
            }
        } );
        return (String)queryResult.get( 0 );
    }

    int sproc_SetSessionCounterValue( int sysdata_value ) {
        return updateSysdata( new Integer( 1 ), new Integer( sysdata_value ) );
    }

    int sproc_SetSessionCounterDate( String counterDate ) {
        return updateSysdata( new Integer( 2 ), counterDate );
    }

    private int updateSysdata( Integer type_id, Object sysdata_value ) {
        String sql = " UPDATE sys_data SET sysdata_value = ? WHERE type_id  = ? ";
        Object[] paramValues = new Object[]{sysdata_value, type_id};
        int rowCount = sqlProcessor.executeUpdate( sql, paramValues );
        return rowCount;
    }

    static class Table_section {
        int section_id;
        String section_name;

        Table_section() {}

        Table_section( ResultSet rs ) throws SQLException {
            section_id = rs.getInt( "section_id" );
            section_name = rs.getString( "section_name" );
        }
    }

    Table_section sproc_SectionGetInheritId( int parent_meta_id ) {
        String sql = "SELECT s.section_id, s.section_name " +
            "FROM sections s, meta_section ms, meta m " +
            "where m.meta_id=ms.meta_id and m.meta_id= ? and ms.section_id=s.section_id";
        Object[] paramValues = new Object[]{ new Integer(parent_meta_id)};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_section( rs );
            }
        } );
        if( 0 == queryResult.size() ) {
            return null;
        } else {
            return (Table_section)queryResult.get(0);
        }
    }

    int sproc_SectionChangeAndDeleteCrossref( int new_section_id, int old_section_id ) {
        int rowCount = 0;

        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        Integer oldSectionId = new Integer(old_section_id);
        try {
            rowCount += sectionDelete( transaction, oldSectionId );
            String sql = "update meta_section set section_id = ? where section_id = ? ";
            Object[] paramValues = new Object[]{ new Integer(new_section_id), oldSectionId };
            rowCount += transaction.executeUpdate( sql, paramValues );
            transaction.commit();
        } catch( SQLException ex ) {
            transaction.rollback();
            log.warn( "Failure in sproc_SectionChangeAndDeleteCrossref", ex );
        }
        return rowCount;
    }

    Table_section[] sproc_SectionGetAll() {
        String sql = "SELECT section_id, section_name FROM sections";
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_section( rs );
            }
        } );
        return (Table_section[])queryResult.toArray( new Table_section[queryResult.size()] );
    }

    static class Table_section_count extends Table_section {
        private int doc_count;

        Table_section_count( ResultSet rs ) throws SQLException {
            super( rs );
            doc_count = rs.getInt( "doc_count" );
        }
    }

    Table_section_count[] sproc_SectionGetAllCount() {
        String sql = "select s.section_id, s.section_name, count(meta_id) AS doc_count " +
            "from sections s left join meta_section ms on s.section_id = ms.section_id " +
            "group by s.section_name, s.section_id order by section_name";
        ArrayList queryResult = sqlProcessor.executeQuery( sql, null, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_section_count( rs );
            }
        } );
        return (Table_section_count[])queryResult.toArray( new Table_section_count[queryResult.size()] );
    }

    /**
     * Gets the number of docs that is connected to that section_id
    */
    int sproc_SectionCount( int section_id ) {
        String sql = "select count(meta_id) AS meta_id_count from meta_section where section_id= ? ";
        Object[] paramValues = new Object[]{ new Integer( section_id )};
        ArrayList queryResult = sqlProcessor.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Integer(rs.getInt("meta_id_count"));
            }
        } );
        return ((Integer)queryResult.get(0)).intValue();
    }

    int sproc_SectionDelete( int section_id ) {
        int rowCount = 0;
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        try {
            Integer sectionId = new Integer( section_id );
            rowCount += sectionDelete( transaction, sectionId );
            transaction.commit();
        } catch( SQLException ex ) {
            transaction.rollback();
            log.warn( "sproc_SectionDelete failed", ex );
        }
        return rowCount;
    }

    private int sectionDelete( SQLProcessor.SQLTransaction transaction, Integer sectionId ) throws SQLException {
        int rowCount = deleteFrom_meta_section( transaction, sectionId );
        rowCount += deleteFrom_sections( transaction, sectionId );
        return rowCount;
    }

    private int deleteFrom_meta_section( SQLProcessor.SQLTransaction transaction, Integer sectionId ) throws SQLException {
        String sql = "DELETE FROM meta_section WHERE section_id = ? ";
        Object[] paramValues = new Object[]{sectionId};
        return transaction.executeUpdate( sql, paramValues );
    }

    private int deleteFrom_sections( SQLProcessor.SQLTransaction transaction, Integer sectionId ) throws SQLException {
        String sql = "DELETE FROM sections WHERE section_id = ? ";
        Object[] paramValues = new Object[]{sectionId};
        return transaction.executeUpdate( sql, paramValues );
    }

    int sproc_SectionChangeName( Table_section sectionData ) {
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        int rowCount = 0;
        try {
            rowCount = changeSection( transaction, sectionData );
            transaction.commit();
        } catch( SQLException ex ) {
            transaction.rollback();
            log.warn( "failed in sproc_SectionChangeName ", ex );
        }
        return rowCount;
    }

    private int changeSection( SQLProcessor.SQLTransaction transaction, Table_section sectionData ) throws SQLException {
        String sql = "UPDATE sections set section_name= ? WHERE section_id = ? ";
        Object[] paramValues = new Object[]{sectionData.section_name, new Integer( sectionData.section_id )};
        return transaction.executeUpdate( sql, paramValues );
    }

    int sproc_SectionAdd( Table_section sectionData ) {
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        int rowCount = 0;
        try {
            Table_section textDocsToBeCopied = selectFrom_section( transaction, new Integer( sectionData.section_id ) );
            if( null == textDocsToBeCopied ) {
                rowCount = insertInto_sections( transaction, sectionData );
            } else {
                rowCount = changeSection( transaction, sectionData );
            }
            transaction.commit();
        } catch( SQLException ex ) {
            transaction.rollback();
            log.warn( "failed in sproc_SectionAdd ", ex );
        }
        return rowCount;
    }

    private Table_section selectFrom_section( SQLProcessor.SQLTransaction transaction, Integer section_id ) {
        String sql = "SELECT section_id, section_name FROM sections WHERE section_id = ? ";
        Object[] paramValues =   new Object[]{ section_id };
        ArrayList queryResult = transaction.executeQuery( sql, paramValues, new SQLProcessor.ResultProcessor() {
            Object mapOneRowFromResultsetToObject( ResultSet rs ) throws SQLException {
                return new Table_section( rs );
            }
        } );
        if( 0 == queryResult.size() ) {
            return null;
        } else {
            return (Table_section)queryResult.get(0);
        }
    }

    private int insertInto_sections( SQLProcessor.SQLTransaction transaction, Table_section tableData )  throws SQLException {
        String sql = " INSERT INTO sections ( section_id, section_name ) VALUES (?,?)";
        Object[] paramValues = new Object[]{new Integer( tableData.section_id ), tableData.section_name};
        return transaction.executeUpdate( sql, paramValues );
    }

    /**
     * Lets insert the crossreferences but first we deleta all oldones for this meta_id
     * @return
     */
    int sproc_SectionAddCrossref( Table_meta_section tableData ) {
        SQLProcessor.SQLTransaction transaction = sqlProcessor.startTransaction();
        int rowCount = 0;
        try {
            rowCount = deleteFrom_meta_section( transaction, new Integer( tableData.meta_id ) );
            rowCount += insertInto_meta_section( transaction, tableData );
            transaction.commit();
        }
        catch( SQLException ex ) {
            transaction.rollback();
            log.warn("Failed in sproc_SectionAddCrossref", ex );
        }
        return rowCount;
    }
}
