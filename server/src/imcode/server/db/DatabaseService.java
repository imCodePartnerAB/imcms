package imcode.server.db;

import imcode.server.db.sql.*;
import imcode.server.test.Log4JConfiguredTestCase;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    SQLProcessorNoTransaction noTransactionSqlProcessor;
    private Logger log;
    private int defaultTransactionRetries;

    DatabaseService( Logger log, int defaultTransactionRetries ) {
        this.log = log;
        this.defaultTransactionRetries = defaultTransactionRetries;
    }

    /**
     * Used to thest multithreading within methoc getNextUniquePrimaryKey
     * To run this test make sure to uncomment the commented lines in the above method.
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        Log4JConfiguredTestCase.initLog4J();

        DatabaseService[] databaseServices = new DatabaseService[]{
            DatabaseTestInitializer.static_initMySql(),
            DatabaseTestInitializer.static_initSqlServer(),
            DatabaseTestInitializer.static_initMimer()
        };

        for( int i = 0; i < databaseServices.length; i++ ) {
            final DatabaseService databaseService = databaseServices[i];
            for( int k = 0; k < 2; k++ ) {
                Thread testTread = new Thread( new Runnable() {
                    public void run() {
                        System.out.println( "Started one thread" );
                        System.out.println( "key = " + databaseService.getNextUniquePrimaryKey( "meta", "meta_id" ) );
                    }
                } );
                testTread.start();
            }
            System.out.println( "Finnished datbaseservice " + String.valueOf( i ) );
            Thread.currentThread().sleep( 1500 );
        }
    }

    private static class PrimaryKeyTransactionContent implements TransactionContent {
        private Integer newPrimaryKey;
        private SQLTransaction transaction;
        private String table_name;
        private String column_name;

        PrimaryKeyTransactionContent( SQLTransaction transaction, String table_name, String column_name ) {

            this.transaction = transaction;
            this.table_name = table_name;
            this.column_name = column_name;
        }

        public void execute() throws SQLException {
            String sql = "UPDATE unique_keys SET key_value = key_value + 1 WHERE table_name = ? AND column_name = ? ";
            Object[] paramValues = new Object[]{table_name, column_name};
            transaction.executeUpdate( sql, paramValues );

            /* This code fragment is used when testing concurrent read/writes, see the main method.
            try {
                System.out.println("Updated key, now sleep for a feew seconds");
                Thread.currentThread().sleep( 1000 );
            } catch( InterruptedException ex ){}// newver happends
            */
            sql = "SELECT key_value FROM unique_keys WHERE table_name = ? AND column_name = ? ";
            paramValues = new Object[]{table_name, column_name};
            ArrayList queryResult = transaction.executeQuery( sql, paramValues, new ResultProcessor() {
                public Object mapOneRow( ResultSet rs ) throws SQLException {
                    int id = rs.getInt( "key_value" );
                    return new Integer( id );
                }
            } );

            newPrimaryKey = (Integer)queryResult.get( 0 );
        }

        public Integer getNewPrimaryKey() {
            return newPrimaryKey;
        }
    }

    private Integer getNextUniquePrimaryKey( final String table_name, final String column_name ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        PrimaryKeyTransactionContent transactionContent = new PrimaryKeyTransactionContent( transaction, table_name, column_name );
        transaction.executeAndCommit( transactionContent );
        return transactionContent.getNewPrimaryKey();
    }

    /**
     * @deprecated Use the methods on this class directly instead
     */
    public ConnectionPool getConnectionPool() {
        return noTransactionSqlProcessor.getConnectionPool();
    }

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

    void initConnectionPoolAndSQLProcessor( String serverName, String jdbcDriver, String serverUrl, String user, String password, Integer maxConnectionCount ) {
        try {
            ConnectionPool connectionPool = new ConnectionPoolForNonPoolingDriver( serverName, jdbcDriver, serverUrl, user, password, maxConnectionCount.intValue() );
            noTransactionSqlProcessor = new SQLProcessorNoTransaction( connectionPool );
        } catch( Exception ex ) {
            log.fatal( "Couldn't initialize connection pool: serverName :' " + serverName + "', jdbcDriver : '" + jdbcDriver + "', serverUrl : " + serverUrl + "', user : '" + user + "', login_password :' " + password + "'" );
            log.fatal( ex );
        }
    }

    void setupDatabaseWithTablesAndData( File pathToScriptFiles ) {
        try {
            // I tried to use batchUpdate but for the current Mimer driver that only works for SELECT, INSERT, UPDATE,
            // and DELETE operations and this method is also used for create table and drop table commands. /Hasse
            // noTransactionSqlProcessor.executeBatchUpdate( con, (String[])commands.toArray( new String[commands.size()] ) );

            ArrayList commands = readCommandsFromFile( new File( pathToScriptFiles, DROP_TABLES ) );
            for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
                final String command = (String)iterator.next();
                final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
                transaction.executeAndCommit( new TransactionContent() {
                    public void execute() throws SQLException {
                        transaction.executeUpdate( command, null );
                    }
                } );
            }

            commands = readCommandsFromFile( new File( pathToScriptFiles, CREATE_TABLES ) );
            final ArrayList filteredCommands = filterCreateCommands( commands );
            // I tried to use batchUpdate but for the current Mimer driver that only works for SELECT, INSERT, UPDATE,
            // and DELETE operations and this method is also used for create table and drop table commands (Hasse):
            for( Iterator iterator = filteredCommands.iterator(); iterator.hasNext(); ) {
                final String command = (String)iterator.next();
                final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
                transaction.executeAndCommit( new TransactionContent() {
                    public void execute() throws SQLException {
                        transaction.executeUpdate( command, null );
                    }
                } );
            }
            commands = readCommandsFromFile( new File( pathToScriptFiles, INSERT_TYPE_DATA ) );
            noTransactionSqlProcessor.executeBatchUpdate( (String[])commands.toArray( new String[commands.size()] ) );
            /* Remove privious line and Uncomment the comment below if you need better error messages
            for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
                String command = (String)iterator.next();
                noTransactionSqlProcessor.executeUpdate( command, null );
            }*/

            commands = readCommandsFromFile( new File( pathToScriptFiles, INSERT_DEFAULT_DATA ) );
            commands = filterInsertCommands( commands );
            noTransactionSqlProcessor.executeBatchUpdate( (String[])commands.toArray( new String[commands.size()] ) );
            /* Remove privious line and Uncomment the comment below if you need better error messages
            for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
                String command = (String)iterator.next();
                noTransactionSqlProcessor.executeUpdate( command, null );
            }*/
        } catch( IOException ex ) {
            log.fatal( "Couldn't open a file ", ex );
        }
    }

    void createTestData( final File pathToScriptFiles ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                ArrayList commands = null;
                try {
                    commands = readCommandsFromFile( new File( pathToScriptFiles, ADITIONAL_TEST_DATA ) );
                } catch( IOException ex ) {
                    log.error( "Failure in method createTestData ", ex );
                }
                for( Iterator iterator = commands.iterator(); iterator.hasNext(); ) {
                    String command = (String)iterator.next();
                    transaction.executeUpdate( command, null );
                }
            }
        } );
    }

    private ArrayList readCommandsFromFile( File sqlScriptingFile ) throws IOException {
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

    public static class Table_users {
        public int user_id;
        public String login_name;
        public String login_password;
        public String first_name;
        public String last_name;
        public String title;
        public String company;
        public String address;
        public String city;
        public String zip;
        public String country;
        public String county_council;
        public String email;
        public boolean external;
        private int last_page;
        private int archive_mode;
        public int lang_id;
        public int user_type;
        public boolean active;
        public Timestamp create_date;

        Table_users( int user_id, String login_name, String login_password, String first_name, String last_name,
                     String title, String company, String address, String city, String zip, String country,
                     String county_council, String email, boolean external, int last_page, int archive_mode,
                     int lang_id, int user_type, boolean active, Timestamp create_date ) {
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
            external = rs.getInt( "external" ) == 1;
            last_page = rs.getInt( "last_page" );
            archive_mode = rs.getInt( "archive_mode" );
            lang_id = rs.getInt( "lang_id" );
            user_type = rs.getInt( "user_type" );
            active = (rs.getInt( "active" ) == 1);
            create_date = rs.getTimestamp( "create_date" );
        }
    }

    public Table_users[] sproc_GetAllUsers_OrderByLastName() {
        String sql = "SELECT user_id,login_name,login_password,first_name,last_name,title,company,address,city,zip," +
            "country,county_council,email,external,last_page,archive_mode,lang_id,user_type,active,create_date " +
            "FROM users ORDER BY last_name";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_users( rs );
            }
        } );
        return (Table_users[])queryResult.toArray( new Table_users[queryResult.size()] );
    }

    /**
     *
     * @param userData The user id is not needed to be set, it is generated within this method.
     * @return
     */
    public int sproc_AddNewuser( final Table_users userData ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Integer newUserId = getNextUniquePrimaryKey( "users", "user_id" );
                String sql = "INSERT INTO users (user_id, login_name, login_password, first_name, last_name, title, " +
                    "company, address, city, zip, country, county_council, email, external, last_page, archive_mode, " +
                    "lang_id, user_type, active, create_date ) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                Object[] paramValues = new Object[]{newUserId, userData.login_name,
                                                    userData.login_password, userData.first_name, userData.last_name,
                                                    userData.title, userData.company, userData.address, userData.city,
                                                    userData.zip, userData.country, userData.county_council, userData.email,
                                                    new Integer( userData.external ? 1 : 0 ), new Integer( 1001 ), new Integer( 0 ),
                                                    new Integer( userData.lang_id ), new Integer( userData.user_type ),
                                                    new Integer( userData.active ? 1 : 0 ), userData.create_date};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    // todo: flytta in detta i addNewUser istället, och se till att det fungerar concurrently.
    // todo: kolla att det inte är highest+1 som förväntas.
    public int sproc_getHighestUserId() {
        throw new Error( "Dont use this!" );
    }

    public int sproc_updateUser( final Table_users userData ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "UPDATE users SET " + "login_name = ?, " + "login_password = ?, " + "first_name = ?, " +
                    "last_name = ?, " + "title = ?, " + "company = ?, " + "address =  ?, " + "city = ?, " + "zip = ?, " +
                    "country = ?, " + "county_council =?, " + "email = ?, " + "user_type = ?, " + "active = ?, " +
                    "lang_id = ? " + "WHERE user_id = ?";
                Object[] paramValues = new Object[]{userData.login_name, userData.login_password, userData.first_name,
                                                    userData.last_name, userData.title, userData.company, userData.address,
                                                    userData.city, userData.zip, userData.country, userData.county_council,
                                                    userData.email, new Integer( userData.user_type ),
                                                    new Integer( userData.active ? 1 : 0 ), new Integer( userData.lang_id ),
                                                    new Integer( userData.user_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public static class Table_user_types {
        private int user_type;
        private String type_name;

        public Table_user_types( ResultSet rs ) throws SQLException {
            user_type = rs.getInt( "user_type" );
            type_name = rs.getString( "type_name" );
        }
    }

    /*
    Used to generate a list with all type of users. Used from AdminUserProps
    */
    public Table_user_types[] sproc_GetUserTypes( String lang_prefix ) {
        String sql = "SELECT DISTINCT user_type, type_name FROM user_types WHERE lang_prefix = ? ";
        Object[] paramValues = new Object[]{lang_prefix};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_user_types( rs );
            }
        } );
        return (Table_user_types[])queryResult.toArray( new Table_user_types[queryResult.size()] );
    }

    /*
    This function adds a new phone numbers to the db. Used by AdminUserProps
    */
    public int sproc_phoneNbrAdd( final int userId, final String number, final int phoneType ) {
        final SQLTransaction transcation = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transcation.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Integer userId1 = new Integer( userId );
                Integer phoneType1 = new Integer( phoneType );
                String tableName = "phones";
                String primaryKeyColumnName = "phone_id";
                Integer newPhoneId = getNextUniquePrimaryKey( tableName, primaryKeyColumnName );
                String sql = "INSERT INTO phones ( phone_id , number , user_id, phonetype_id ) VALUES ( ? , ?, ?, ? )";
                Object[] paramValues = new Object[]{newPhoneId, number, userId1, phoneType1};
                transcation.executeUpdate( sql, paramValues );
            }
        } );
        return transcation.getRowCount();
    }

    // todo: ta bort från samtliga forreign key ställen (och inte bara från user_roles_crossref)? phones,
    // todo: user_flags_crossref, user_rights, useradmin_role_crossref
    // todo: Or Split into two, depending on how it is used.
    public int sproc_delUser( final int user_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sqlUserRoles = "DELETE FROM user_roles_crossref WHERE user_id = " + user_id;
                transaction.executeUpdate( sqlUserRoles, null );

                String sqlUsers = "DELETE FROM users WHERE user_id = " + user_id;
                transaction.executeUpdate( sqlUsers, null );
            }
        } );
        return transaction.getRowCount();
    }

    /**
     * Add role a Useradmin have administration rights on user with that roles.
     * A useradmin is only allowed to administrate users with those roles.
     * @param user_id The user id for the user, that user should have the role Useradmin (1)
     * @param role_id The role of other users that this user should hav (new) permissions to administrate.
     * @return 1 if succed, otherwise 0.
     */
    public int sproc_AddUseradminPermissibleRoles( final int user_id, final int role_id ) {
        final SQLTransaction transcation = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transcation.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "INSERT INTO useradmin_role_crossref (user_id, role_id ) " +
                    "VALUES ( ?, ? )";
                Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( role_id )};
                transcation.executeUpdate( sql, paramValues );
            }
        } );
        return transcation.getRowCount();
    }

    /**
     * Adds a role to a particular user
     */
    public int sproc_AddUserRole( final int user_id, final int role_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                // Lets check if the role already exists
                String sqlSelect = "SELECT role_id FROM user_roles_crossref WHERE user_id = ? AND role_id = ? ";
                Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( role_id )};
                ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sqlSelect, paramValues, new ResultProcessor() {
                    public Object mapOneRow( ResultSet rs ) throws SQLException {
                        return new Integer( rs.getInt( "role_id" ) );
                    }
                } );

                if( queryResult.isEmpty() ) {
                    String sqlInsert = "INSERT INTO user_roles_crossref(user_id, role_id) VALUES( ? , ? )";
                    transaction.executeUpdate( sqlInsert, paramValues );
                }
            }
        } );
        return transaction.getRowCount();
    }

    public int sproc_ChangeUserActiveStatus( final int user_id, final boolean active ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "UPDATE users SET active = ? WHERE user_id = ? ";
                Integer activeInteger = new Integer( active ? 1 : 0 );
                Integer userIdInteger = new Integer( user_id );
                Object[] paramValues = new Object[]{activeInteger, userIdInteger};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    /**
     * Because different databses treats upper/lower case differently this method makes a
     * ignoreCases  match.
     * @param userName
     * @return
     */
    // todo döp om denna till, userExists eller nåt
    public boolean sproc_FindUserName( String userName ) {
        String sql = "SELECT login_name FROM users WHERE LOWER(login_name) = ? ";
        Object[] paramValues = new Object[]{userName.toLowerCase()};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return rs.getString( "login_name" );
            }
        } );
        return queryResult.size() == 1;
    }

    // todo döp om till deleteAllPhonenumbersForUser eller nåt
    // todo klumpa ihop med delete userses?
    public int sproc_DelPhoneNr( final int user_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "DELETE FROM phones WHERE user_id = ? ";
                Object[] paramValues = new Object[]{new Integer( user_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public int sproc_PhoneNbrDelete( final int phone_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "DELETE FROM phones WHERE phone_id = ? ";
                Object[] paramValues = new Object[]{new Integer( phone_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public int sproc_PhoneNbrUpdate( final int user_id, final int phone_id, final String number, final int phonetype_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "UPDATE phones SET number = ?, phonetype_id = ? " +
                    "WHERE user_id = ? AND phone_id = ? ";
                Object[] paramValues = new Object[]{number, new Integer( phonetype_id ),
                                                    new Integer( user_id ), new Integer( phone_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public String sproc_GetPhonetypeName( int phonetype_id, int lang_id ) {
        String sql = "SELECT typename FROM phonetypes " +
            "WHERE phonetype_id = ? AND lang_id = ? ";
        Object[] paramValues = new Object[]{new Integer( phonetype_id ), new Integer( lang_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return rs.getString( 1 );
            }
        } );
        return (String)queryResult.get( 0 );
    }

    public static class Table_phonetypes {
        private int phonetype_id;
        private String typename;

        Table_phonetypes( ResultSet rs ) throws SQLException {
            phonetype_id = rs.getInt( "phonetype_id" );
            typename = rs.getString( "typename" );
        }
    }

    public Table_phonetypes[] sproc_GetPhonetypes_ORDER_BY_phonetype_id( int lang_id ) {
        String sql = " SELECT  phonetype_id, typename FROM phonetypes WHERE lang_id = ? ORDER BY phonetype_id";
        Object[] paramValues = new Object[]{new Integer( lang_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_phonetypes( rs );
            }
        } );
        return (Table_phonetypes[])queryResult.toArray( new Table_phonetypes[queryResult.size()] );
    }

    public static class Table_phone {
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
    // todo: Warning, this method used to trim the phone numer result, not any longer...
    public Table_phone[] sproc_GetUserPhones( int user_id ) {
        String sql = "SELECT p.phone_id, p.number FROM users u , phones p " +
            "WHERE u.user_id = p.user_id AND u.user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_phone( rs );
            }
        } );
        return (Table_phone[])queryResult.toArray( new Table_phone[queryResult.size()] );
    }

    public static class JoinedTables_phones_phonetypes {
        private int phone_id;
        public String number;
        private int user_id;
        public int phonetype_id;
        private String typename;

        JoinedTables_phones_phonetypes( ResultSet rs ) throws SQLException {
            phone_id = rs.getInt( "phone_id" );
            number = rs.getString( "number" );
            user_id = rs.getInt( "user_id" );
            phonetype_id = rs.getInt( "phonetype_id" );
            typename = rs.getString( "typename" );
        }
    }

    // todo: Do we realy need to return user_id?
    // todo: This should be able to be used instead of sproc_GetUserPhones, why not?
    public JoinedTables_phones_phonetypes[] sproc_GetUserPhoneNumbers( int user_id ) {
        String sql = "SELECT phones.phone_id, phones.number, phones.user_id, phones.phonetype_id, phonetypes.typename " +
            "FROM phones " +
            "INNER JOIN users ON phones.user_id = users.user_id " +
            "INNER JOIN phonetypes ON phones.phonetype_id = phonetypes.phonetype_id AND users.lang_id = phonetypes.lang_id " +
            "WHERE phones.user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new JoinedTables_phones_phonetypes( rs );
            }
        } );
        return (JoinedTables_phones_phonetypes[])queryResult.toArray( new JoinedTables_phones_phonetypes[queryResult.size()] );
    }

    public int sproc_DocumentDelete( final int meta_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Object[] paramValues = new Object[]{new Integer( meta_id )};
                transaction.executeUpdate( "DELETE FROM meta_classification WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM childs WHERE to_meta_id = 	?", paramValues );
                transaction.executeUpdate( "DELETE FROM childs WHERE meta_id =	?", paramValues );
                transaction.executeUpdate( "DELETE FROM text_docs WHERE meta_id = 	?", paramValues );
                transaction.executeUpdate( "DELETE FROM texts WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM images WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM roles_rights WHERE meta_id = ?", paramValues );
                // This table dosen't exist in the databse, but the following line were fount in
                // the sproc.
                //                transaction.executeUpdate( "DELETE FROM user_rights WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM url_docs WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM browser_docs WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM fileupload_docs WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM frameset_docs WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM new_doc_permission_sets_ex WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM new_doc_permission_sets WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM doc_permission_sets_ex WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM doc_permission_sets WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM includes WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM meta_section WHERE meta_id = ?", paramValues );
                transaction.executeUpdate( "DELETE FROM meta WHERE meta_id = ?", paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    // todo: Döp om till documentExixts eller nåt...
    public boolean sproc_FindMetaId( int meta_id ) {
        String sql = "SELECT meta_id FROM meta WHERE meta_id = ?";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
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
     */
    // todo: Testa denna!!! Och gå igenom nogrant!!!
    // todo: Ska inte ändringsdatumen uppdateras i denna också?
    public int sproc_AddExistingDocToMenu( final int meta_id, final int existing_meta_id, final int doc_menu_no ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {

                // test if this is the first child
                String sqlLinksCount = "SELECT COUNT(*) FROM childs WHERE meta_id = ?  AND menu_sort = ? ";
                Object[] paramValuesLinksCount = new Object[]{new Integer( meta_id ), new Integer( doc_menu_no )};
                ArrayList countResult = noTransactionSqlProcessor.executeQuery( sqlLinksCount, paramValuesLinksCount, new ResultProcessor() {
                    public Object mapOneRow( ResultSet rs ) throws SQLException {
                        return new Integer( rs.getInt( 1 ) );
                    }
                } );
                Integer countItem = (Integer)countResult.get( 0 );

                int manualSortOrder = 500;
                if( countItem.intValue() > 0 ) {// update manual_sort_order
                    String sqlSortOrder = "SELECT MAX(manual_sort_order) FROM childs WHERE meta_id = ? AND menu_sort = ?";
                    Object[] paramValuesSortOrder = new Object[]{new Integer( meta_id ), new Integer( doc_menu_no )};
                    ArrayList sortOrderResult = noTransactionSqlProcessor.executeQuery( sqlSortOrder, paramValuesSortOrder, new ResultProcessor() {
                        public Object mapOneRow( ResultSet rs ) throws SQLException {
                            return new Integer( rs.getInt( 1 ) );
                        }
                    } );
                    manualSortOrder = ((Integer)sortOrderResult.get( 0 )).intValue() + 10;
                }

                //- test if child already exist in this menu. If not, then we will add the child to the menu.
                String sqlThisLinksCount = "SELECT COUNT(*) FROM childs WHERE meta_id = ? AND to_meta_id = ? AND menu_sort = ?";
                Object[] paramValuesThisLinksCount = new Object[]{new Integer( meta_id ), new Integer( existing_meta_id ), new Integer( doc_menu_no )};
                ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sqlThisLinksCount, paramValuesThisLinksCount, new ResultProcessor() {
                    public Object mapOneRow( ResultSet rs ) throws SQLException {
                        return new Integer( rs.getInt( 1 ) );
                    }
                } );

                Integer thisCountItem = (Integer)queryResult.get( 0 );
                if( thisCountItem.intValue() == 0 ) {
                    String sql = "INSERT INTO childs( meta_id, to_meta_id, menu_sort, manual_sort_order) VALUES( ?, ?, ?, ? )";
                    Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( existing_meta_id ), new Integer( doc_menu_no ), new Integer( manualSortOrder )};
                    transaction.executeUpdate( sql, paramValues );

                    sql = "UPDATE meta SET date_modified = ? WHERE meta_id = ?";
                    paramValues = new Object[]{new Timestamp( new java.util.Date().getTime() ), new Integer( meta_id )};
                    transaction.executeUpdate( sql, paramValues );
                }
            }
        } );
        return transaction.getRowCount();
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
    // todo: döp om till getDocsForUser eller nåt.
    // todo: Tog bort i första raden ett DISTINCT från COUNT(DISTINCT c.meta_id) till COUNT(c.meta_id), fundera igenom om det gör något?
    public PartOfTable_document[] sproc_getDocs( int user_id, int start, int end ) {
        String sql = "SELECT DISTINCT m.meta_id, COUNT(c.meta_id) parentcount, meta_headline, doc_type FROM meta m " +
            "LEFT JOIN childs c ON c.to_meta_id = m.meta_id " +
            "LEFT JOIN roles_rights rr  ON rr.meta_id = m.meta_id AND rr.set_id < 4 " +
            "JOIN user_roles_crossref urc ON urc.user_id = ? AND ( urc.role_id = 0 OR ( urc.role_id = rr.role_id ) OR m.shared = 1 ) " +
            "WHERE m.activate = 1 AND m.meta_id > (?-1) AND m.meta_id < (?+1) " +
            "GROUP BY m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id " +
            "ORDER BY m.meta_id";
        Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( start ), new Integer( end )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new PartOfTable_document( rs );
            }
        } );
        return (PartOfTable_document[])queryResult.toArray( new PartOfTable_document[queryResult.size()] );
    }

    public static class JoinedTables_meta_childs {
        public int to_meta_id;
        public int menu_sort;
        public int manual_sort_order;
        public int doc_type;
        public boolean archive;
        public String target;
        public Timestamp date_created;
        public Timestamp date_modified;
        public String meta_headline;
        public String meta_text;
        public String meta_image;
        public String frame_name;
        public Timestamp activated_datetime;
        public Timestamp archived_datetime;
        public String filename;

        JoinedTables_meta_childs( ResultSet rs ) throws SQLException {
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
    // todo WARNING, i anropande kod måste en förändring ske!
    // todo Den bortkommenterade reden nedan beräknar om man har rätt att editera eller ej.
    // todo Se till att göra den kollen på annat sätt efteråt för varje dokument.
    public JoinedTables_meta_childs[] sproc_getChilds( int meta_id, int user_id ) {
        Integer sortOrder = getMenuSortOrder( meta_id );
        String sql =
            "SELECT to_meta_id, c.menu_sort,manual_sort_order, doc_type," +
            "  archive,target, date_created, date_modified," +
            "  meta_headline,meta_text,meta_image,frame_name," +
            "  activated_datetime,archived_datetime," +
            //            "  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1))," +
            "fd.filename " +
            "FROM  childs c " +
            "JOIN meta m " +
            "   ON m.meta_id = c.to_meta_id " + // meta.meta_id corresponds to childs.to_meta_id
            "   AND  m.activate > 0 " + // Only include the documents that are active in the meta table
            "   AND  c.meta_id = ? " + // Only include documents that are children to this particular meta_id
            "LEFT JOIN roles_rights rr " + // We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin
            "   ON c.to_meta_id = rr.meta_id " + // Only include rows with the documents we are interested in
            "LEFT JOIN doc_permission_sets dps " + // Include the permission_sets
            "   ON c.to_meta_id = dps.meta_id " + // for each document
            "   AND dps.set_id = rr.set_id " + // and only the sets for the roles we are interested in
            "   AND dps.permission_id > 0 " + // and only the sets that have any permission
            "JOIN user_roles_crossref urc " + // This table tells us which users have which roles
            "   ON urc.user_id = ? " + // Only include the rows with the user we are interested in...
            "   AND ( " +
            "      rr.role_id = urc.role_id " + //  Include rows where the users roles match the roles that have permissions on the documents
            "   or urc.role_id = 0" + // and also include the rows that tells us this user is a superadmin
            "      or ( " +
            "         m.show_meta != 0 " + //  and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
            "      ) " +
            "   ) " +
            "LEFT JOIN fileupload_docs fd " +
            "   ON fd.meta_id = c.to_meta_id " +
            "GROUP BY to_meta_id, c.menu_sort,manual_sort_order, doc_type, archive,target, date_created, date_modified, " +
            "meta_headline,meta_text,meta_image,frame_name, activated_datetime,archived_datetime, fd.filename ";
        Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( user_id )};

        if( sortOrder.intValue() == 3 ) {
            sql += "order by  menu_sort,c.manual_sort_order desc";
        } else if( sortOrder.intValue() == 2 ) {
            sql += "order by  menu_sort,convert (varchar,date_modified,120) desc";
        } else if( sortOrder.intValue() == 1 ) {
            sql += "order by  menu_sort,meta_headline";
        }

        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new JoinedTables_meta_childs( rs );
            }
        } );
        return (JoinedTables_meta_childs[])queryResult.toArray( new JoinedTables_meta_childs[queryResult.size()] );
    }

    private Integer getMenuSortOrder( int meta_id ) {
        String sql = "SELECT sort_order FROM text_docs WHERE meta_id = ?";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "sort_order" ) );
            }
        } );
        return (Integer)queryResult.get( 0 );
    }

    /**
     * Detects if a user is administrator or not
     *
     * @return role_ids
     */
    // In the databse sproc it returned the user_id, role_id, but the code that used it
    // assumed it was just roles. And only one role.
    // So I changed this to return only role_id's.
    // But I think that it should return a boolean true or fals if it is a
    public boolean sproc_CheckAdminRights( int user_id ) {
        String sql = "SELECT roles.role_id FROM users " +
            "INNER JOIN user_roles_crossref " +
            "ON users.user_id = user_roles_crossref.user_id " +
            "INNER JOIN roles " +
            "ON user_roles_crossref.role_id = roles.role_id " +
            "WHERE roles.role_id = 0 AND users.user_id = ?";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "role_id" ) );
            }
        } );
        return queryResult.size() != 0;
    }

    /**
     * @deprecated
     * @param userId
     * @param admin_role
     * @return
     */
    // todo, se till att denna inte anropas direkt utan att man anropar metoder isUserAdmin och isSuperAdmin istället. med admin_role satt till 1 resp 2.
    public boolean sproc_checkUserAdminrole( int user_id, int admin_role ) {
        String sql = "SELECT admin_role FROM user_roles_crossref " +
            "INNER JOIN roles " +
            "ON user_roles_crossref.role_id = roles.role_id " +
            "WHERE (user_roles_crossref.user_id = ? ) AND (roles.admin_role = ? )";
        Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( admin_role )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "admin_role" ) );
            }
        } );
        return queryResult.size() == 1;
    }

    // todo: Döp om till hasUserSharePemissionForDocument
    public boolean sproc_CheckUserDocSharePermission( int user_id, int meta_id ) {
        String sql = "SELECT m.meta_id FROM meta m " +
            "JOIN user_roles_crossref urc ON urc.user_id = ? AND m.meta_id = ? " +
            "LEFT JOIN roles_rights rr ON rr.meta_id = m.meta_id AND rr.role_id = urc.role_id " +
            "WHERE ( shared = 1 OR	rr.set_id < 3 OR urc.role_id = 0 ) ";
        Object[] parameterValues = new Object[]{new Integer( user_id ), new Integer( meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, parameterValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "meta_id" ) );
            }
        } );
        return queryResult.size() == 1;
    }

    /**
     * @deprecated
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
    public int sproc_DelUserRoles( int user_id, int role_id ) {
        int rowCount = 0;
        if( role_id == -1 ) {
            rowCount += deleteFrom_user_roles( user_id );
        } else {
            rowCount += deleteFrom_user_roles( user_id, role_id );
        }
        return rowCount;
    }

    private int deleteFrom_user_roles( final int user_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "DELETE FROM user_roles_crossref WHERE user_id = ? ";
                Object[] paramValues = new Object[]{new Integer( user_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    private int deleteFrom_user_roles( final int user_id, final int role_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "DELETE FROM user_roles_crossref WHERE user_id = ? AND role_id = ? ";
                Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( role_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public String sproc_GetFileName( int metaId ) {
        String sql = "SELECT filename FROM fileupload_docs WHERE meta_id = ?";
        Object[] paramValues = new Object[]{new Integer( metaId )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return rs.getString( "filename" );
            }
        } );
        return (String)(queryResult.size() > 0 ? queryResult.get( 0 ) : null);
    }

    public Integer sproc_GetDocType( int meta_id ) {
        String sql = "SELECT doc_type FROM meta WHERE meta_id = ?";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "doc_type" ) );
            }
        } );
        return (Integer)(queryResult.isEmpty() ? null : queryResult.get( 0 ));
    }

    public class Table_doc_types {
        public int doc_type;
        public String type;

        Table_doc_types( ResultSet rs ) throws SQLException {
            doc_type = rs.getInt( "doc_type" );
            type = rs.getString( "type" );
        }
    }

    public Table_doc_types[] sproc_GetDocTypes( String lang_prefix ) {
        String sql = "SELECT doc_type,type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type";
        Object[] paramValues = new Object[]{lang_prefix};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_doc_types( rs );
            }
        } );
        return (Table_doc_types[])queryResult.toArray( new Table_doc_types[queryResult.size()] );
    }

    public class ExtgendedTable_doc_types extends Table_doc_types {
        int permission_data;

        public ExtgendedTable_doc_types( ResultSet rs ) throws SQLException {
            super( rs );
            Object o = rs.getObject( "permission_data" );
            if( o == null ) {
                permission_data = -1;
            } else {
                permission_data = rs.getInt( "permission_data" );
            }
        }
    }

    /**
     Retrieves a list of all doc-types, with a indicator of wether a particular permission-set may use it.
     The permission-set must still have the "Create document"-permission set, though. ( Not checked in this proc )
     Column 1: The doc-type
     Column 2: The name of the doc-type
     Column 3: permission_data, > -1 if this set_id may use this.
     */
    // todo: "ORDER BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type" borttagen, sortering får göras vid behov i den anropande koden.
    ExtgendedTable_doc_types[] sproc_GetDocTypesWithPermissions( int meta_id, int set_id, String lang_prefix ) {
        String sql = "SELECT doc_type,type, dpse.permission_data " +
            "FROM doc_types dt " +
            "LEFT JOIN doc_permission_sets_ex dpse " +
            "ON dpse.permission_data = dt.doc_type " +
            "AND dpse.meta_id = ? " +
            "AND dpse.set_id = ? " +
            "AND dpse.permission_id = 8 " +
            "WHERE dt.lang_prefix = ? ";
        Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( set_id ), lang_prefix};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new ExtgendedTable_doc_types( rs );
            }
        } );
        return (ExtgendedTable_doc_types[])queryResult.toArray( new ExtgendedTable_doc_types[queryResult.size()] );
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

    // todo: copyPrefix is never used, check this!
    int[] sproc_copyDocs( int parent_meta_id, int parent_menu_id, int user_id, int[] childrensMetaIds, String copyPrefix ) {

        ArrayList fileDocs = new ArrayList();
        ArrayList notFileDocs = new ArrayList();

        for( int i = 0; i < childrensMetaIds.length; i++ ) {
            int meta_id = childrensMetaIds[i];
            if( selectFrom_meta_isFileDoc( meta_id ) ) {
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

    boolean selectFrom_meta_isFileDoc( int meta_id ) {
        String sql = "SELECT meta_id FROM meta WHERE meta_id = ? AND doc_type = 8";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
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
        int doc_type = sproc_GetDocType( meta_id_to_add.intValue() ).intValue();
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
    public Table_doc_types[] sproc_GetDocTypesForUser( int user_id, int meta_id, String lang_prefix ) {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_doc_types( rs );
            }
        } );
        return (Table_doc_types[])queryResult.toArray( new Table_doc_types[queryResult.size()] );
    }

    public static class Table_meta {
        public int meta_id;
        private String description;
        public int doc_type;
        public String meta_headline;
        public String meta_text;
        public String meta_image;
        private int owner_id;
        private int permissions;
        private int shared;
        private int expand;
        private int show_meta;
        private int help_text_id;
        public boolean archive;
        private int status_id;
        private String lang_prefix;
        private String classification;
        public Timestamp date_created;
        public Timestamp date_modified;
        private int sort_position;
        private int menu_position;
        private int disable_search;
        public String target;
        private String frame_name;
        private int activate;
        public Timestamp activated_datetime;
        public Timestamp archived_datetime;

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
            archive = rs.getInt( "archive" ) == 1;
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

    public Table_meta sproc_GetDocumentInfo( int meta_id ) {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_meta( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_meta)queryResult.get( 0 );
        }
    }

    private void insertInto_meta( SQLTransaction transaction, Table_meta tableData ) {
        String sql = "INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, " +
            "permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, " +
            "date_created, date_modified, sort_position, menu_position, disable_search, target, frame_name, activate, " +
            "activated_datetime, archived_datetime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.description, new Integer( tableData.doc_type ),
                                            tableData.meta_headline, tableData.meta_text, tableData.meta_image,
                                            new Integer( tableData.owner_id ), new Integer( tableData.permissions ),
                                            new Integer( tableData.shared ), new Integer( tableData.expand ), new Integer( tableData.show_meta ),
                                            new Integer( tableData.help_text_id ), new Integer( tableData.archive ? 1 : 0 ),
                                            new Integer( tableData.status_id ), tableData.lang_prefix, tableData.classification,
                                            tableData.date_created, tableData.date_modified, new Integer( tableData.sort_position ),
                                            new Integer( tableData.menu_position ), new Integer( tableData.disable_search ),
                                            tableData.target, tableData.frame_name, new Integer( tableData.activate ),
                                            tableData.activated_datetime,
                                            (null == tableData.archived_datetime) ? (Object)new SQLTypeNull( Types.TIMESTAMP ) : tableData.archived_datetime
        };
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_text_docs {
        private int meta_id;
        public int template_id;
        public int group_id;
        public int sort_order;
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

    // todo: this dosen't return the simple_name anymore.
    public Table_text_docs sproc_GetTextDocData( int meta_id ) {
        return selectFrom_text_docs( new Integer( meta_id ) );
    }

    private Table_text_docs selectFrom_text_docs( Integer meta_id ) {
        String sql = "SELECT meta_id, template_id, group_id, sort_order, default_template_1, default_template_2 " +
            "FROM text_docs WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_text_docs( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_text_docs)queryResult.get( 0 );
        }
    }

    private void insertInto_text_docs( SQLTransaction transaction, Table_text_docs tableData ) {
        String sql = "INSERT INTO text_docs ( meta_id, template_id, group_id, sort_order, default_template_1, default_template_2 ) " +
            "VALUES ( ?,?,?,?,?,? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.template_id ),
                                            new Integer( tableData.group_id ), new Integer( tableData.sort_order ),
                                            new Integer( tableData.default_template_1 ), new Integer( tableData.default_template_2 )};
        transaction.executeUpdate( sql, paramValues );
    };

    public static class Table_url_docs {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_url_docs( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_url_docs)queryResult.get( 0 );
        }
    }

    private void insertInto_url_docs( SQLTransaction transaction, Table_url_docs tableData ) {
        String sql = "INSERT INTO url_docs (meta_id, frame_name, target, url_ref, url_txt, lang_prefix ) " +
            "VALUES ( ?, ?, ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.frame_name, tableData.target,
                                            tableData.url_ref, tableData.url_txt, tableData.lang_prefix};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_browser_docs {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_browser_docs( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_browser_docs)queryResult.get( 0 );
        }
    }

    static class JoinedTables_meta_browser_docs {
        private int to_meta_id;
        String meta_headline;

        JoinedTables_meta_browser_docs( ResultSet rs ) throws SQLException {
            to_meta_id = rs.getInt( "to_meta_id" );
            meta_headline = rs.getString( "meta_headline" );
        }
    }

    public JoinedTables_meta_browser_docs[] sproc_getBrowserDocChilds( int meta_id, int user_id ) {
        String sql = "SELECT DISTINCT to_meta_id, meta_headline FROM browser_docs bd JOIN meta m " +
            "ON  bd.to_meta_id = m.meta_id AND bd.meta_id = ? " +
            "LEFT JOIN roles_rights rr ON rr.meta_id = m.meta_id AND rr.set_id < 4 " +
            "JOIN user_roles_crossref urc ON urc.user_id = ? AND (urc.role_id = 0 OR urc.role_id = rr.role_id OR m.shared = 1)" +
            "WHERE m.activate = 1 ORDER BY to_meta_id";
        Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( user_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new JoinedTables_meta_browser_docs( rs );
            }
        } );
        return (JoinedTables_meta_browser_docs[])queryResult.toArray( new JoinedTables_meta_browser_docs[queryResult.size()] );
    }

    private void insertInto_browser_docs( SQLTransaction transaction, Table_browser_docs tableData ) {
        String sql = "INSERT INTO browser_docs ( meta_id, to_meta_id, browser_id ) VALUES (?,?,?)";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.to_meta_id ),
                                            new Integer( tableData.browser_id )};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_frameset_docs {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_frameset_docs( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_frameset_docs)queryResult.get( 0 );
        }
    }

    private void insertInto_frameset_docs( SQLTransaction transaction, Table_frameset_docs tableData ) {
        String sql = "INSERT INTO frameset_docs ( meta_id, frame_set ) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.frame_set};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_fileupload_docs {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_fileupload_docs( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_fileupload_docs)queryResult.get( 0 );
        }
    }

    private void insertInto_fileupload_docs( SQLTransaction transaction, Table_fileupload_docs tableData ) {
        String sql = "INSERT INTO fileupload_docs ( meta_id, filename, mime ) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), tableData.filename, tableData.mime};
        transaction.executeUpdate( sql, paramValues );
    }

    public class Table_texts {
        public int meta_id;
        public int name;
        public String text;
        public int type;
        private int counter;

        Table_texts( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            name = rs.getInt( "name" );
            text = rs.getString( "text" );
            type = rs.getInt( "type" );
            counter = rs.getInt( "counter" );
        }
    }

    /**
     Retrieve a text with type
     */
    // Todo: Denna returnerade i orginalutförandet endast en del av datan som finns för rad i tabellen texts,
    // todo: ok att returnera hela på detta sätt?
    public Table_texts sproc_GetText( int meta_id, int name ) {
        Table_texts[] allTextInDocument = selectFrom_texts( new Integer( meta_id ) );
        for( int i = 0; i < allTextInDocument.length; i++ ) {
            Table_texts table_texts = allTextInDocument[i];
            if( table_texts.name == name ) {
                return table_texts;
            }
        }
        return null;
    }

    public Table_texts[] sproc_GetTexts( int meta_id ) {
        return selectFrom_texts( new Integer( meta_id ) );
    }

    private Table_texts[] selectFrom_texts( Integer meta_id ) {
        String sql = "SELECT meta_id, name, text, type, counter FROM texts WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_texts( rs );
            }
        } );
        return (Table_texts[])queryResult.toArray( new Table_texts[queryResult.size()] );
    }

    private void insertInto_texts( SQLTransaction transaction, Table_texts tableData ) {
        String sql = "INSERT INTO texts ( meta_id, name, text, type, counter ) VALUES ( ?, ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.name ), tableData.text,
                                            new Integer( tableData.type ), new Integer( tableData.counter )};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_images {
        private int meta_id;
        public int width;
        public int height;
        public int border;
        public int v_space;
        public int h_space;
        public int name;
        public String image_name;
        public String target;
        public String target_name;
        public String align;
        public String alt_text;
        public String low_scr;
        public String imgurl;
        public String linkurl;

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
    // todo: ej heller inehåller resultatet den formaterade varianten av name: '#img'+convert(varchar(5), name)+'#'
    public Table_images[] sproc_getImages( int meta_id ) {
        return selectFrom_images( new Integer( meta_id ) );
    }

    private Table_images[] selectFrom_images( Integer meta_id ) {
        String sql = "SELECT meta_id, width, height, border, v_space, h_space, name, image_name, target, target_name, " +
            "align, alt_text, low_scr, imgurl, linkurl FROM images WHERE meta_id = ?";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_images( rs );
            }
        } );
        return (Table_images[])queryResult.toArray( new Table_images[queryResult.size()] );
    }

    private void insertInto_images( SQLTransaction transaction, Table_images tableData ) {
        String sql = "INSERT INTO images (meta_id, width, height, border, v_space, h_space, name, image_name, target, target_name," +
            " align, alt_text, low_scr, imgurl, linkurl ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.width ),
                                            new Integer( tableData.height ), new Integer( tableData.border ),
                                            new Integer( tableData.v_space ), new Integer( tableData.h_space ),
                                            new Integer( tableData.name ), tableData.image_name, tableData.target,
                                            tableData.target_name, tableData.align, tableData.alt_text, tableData.low_scr,
                                            tableData.imgurl, tableData.linkurl};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_includes {
        private int meta_id;
        public int include_id;
        public int included_meta_id;

        Table_includes( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            include_id = rs.getInt( "include_id" );
            included_meta_id = rs.getInt( "included_meta_id" );
        }
    }

    public Table_includes[] sproc_GetIncludes( int meta_id ) {
        return selectFrom_includes( new Integer( meta_id ) );
    }

    private Table_includes[] selectFrom_includes( Integer meta_id ) {
        String sql = "SELECT meta_id, include_id, included_meta_id FROM includes WHERE meta_id = ? ORDER BY include_id";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_includes( rs );
            }
        } );
        return (Table_includes[])queryResult.toArray( new Table_includes[queryResult.size()] );
    }

    private void insertInto_includes( SQLTransaction transaction, Table_includes tableData ) {
        String sql = "INSERT INTO includes ( meta_id, include_id, included_meta_id ) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.include_id ),
                                            new Integer( tableData.included_meta_id )};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_doc_permission_sets {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_doc_permission_sets( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_doc_permission_sets)queryResult.get( 0 );
        }
    }

    private void insertInto_doc_permission_sets( SQLTransaction transaction, Table_doc_permission_sets tableData ) {
        String sql = "INSERT INTO doc_permission_sets ( meta_id, set_id, permission_id ) VALUES( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id )};
        transaction.executeUpdate( sql, paramValues );
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_new_doc_permission_sets( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_new_doc_permission_sets)queryResult.get( 0 );
        }
    }

    private void insertInto_new_doc_permission_sets( SQLTransaction transaction, Table_new_doc_permission_sets tableData ) {
        String sql = "INSERT INTO new_doc_permission_sets (meta_id, set_id, permission_id) VALUES ( ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id )};
        transaction.executeUpdate( sql, paramValues );
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_doc_permission_sets_ex( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_doc_permission_sets_ex)queryResult.get( 0 );
        }
    }

    private void insertInto_doc_permission_sets_ex( SQLTransaction transaction, Table_doc_permission_sets_ex tableData ) {
        String sql = "INSERT INTO doc_permission_sets_ex (meta_id, set_id, permission_id, permission_data) VALUES ( ?, ?, ?, ? ) ";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id ), new Integer( tableData.permission_data )};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_new_doc_permission_sets_ex {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_new_doc_permission_sets_ex( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_new_doc_permission_sets_ex)queryResult.get( 0 );
        }
    }

    private void insertInto_new_doc_permission_sets_ex( SQLTransaction transaction, Table_new_doc_permission_sets_ex tableData ) {
        String sql = "INSERT INTO new_doc_permission_sets_ex (meta_id, set_id, permission_id, permission_data) VALUES ( ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.set_id ), new Integer( tableData.permission_id ), new Integer( tableData.permission_data )};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_roles_rights {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_roles_rights( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_roles_rights)queryResult.get( 0 );
        }
    }

    private void insertInto_roles_rights( SQLTransaction transaction, Table_roles_rights tableData ) {
        String sql = "INSERT INTO roles_rights (role_id, meta_id, set_id) VALUES ( ?, ?, ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.role_id ), new Integer( tableData.meta_id ), new Integer( tableData.set_id )};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_meta_classification {
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
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_meta_classification( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_meta_classification)queryResult.get( 0 );
        }
    }

    private void insertInto_meta_classification( SQLTransaction transaction, Table_meta_classification tableData ) {
        String sql = "INSERT INTO meta_classification (meta_id, class_id) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.class_id )};
        transaction.executeUpdate( sql, paramValues );
    }

    public static class Table_meta_section {
        int meta_id;
        int section_id;

        Table_meta_section() {
        }

        Table_meta_section( ResultSet rs ) throws SQLException {
            meta_id = rs.getInt( "meta_id" );
            section_id = rs.getInt( "section_id" );
        }
    }

    private Table_meta_section selectFrom_meta_section( Integer meta_id ) {
        String sql = "SELECT meta_id, section_id FROM meta_section WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{meta_id};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_meta_section( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_meta_section)queryResult.get( 0 );
        }
    }

    private void insertInto_meta_section( SQLTransaction transaction, Table_meta_section tableData ) {
        String sql = "INSERT INTO meta_section ( meta_id, section_id ) VALUES ( ?, ? )";
        Object[] paramValues = new Object[]{new Integer( tableData.meta_id ), new Integer( tableData.section_id )};
        transaction.executeUpdate( sql, paramValues );
    }

    private int copyDocument( final Integer meta_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Integer nextFreeMetaId = getNextUniquePrimaryKey( "meta", "meta_id" );
                Table_meta metaToBeCopied = selectFrom_meta( meta_id );
                metaToBeCopied.meta_id = nextFreeMetaId.intValue();
                insertInto_meta( transaction, metaToBeCopied );

                Table_text_docs textDocsToBeCopied = selectFrom_text_docs( meta_id );
                if( null != textDocsToBeCopied ) {
                    textDocsToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_text_docs( transaction, textDocsToBeCopied );
                }

                Table_url_docs urlDocsToBeCopied = selectFrom_url_docs( meta_id );
                if( null != urlDocsToBeCopied ) {
                    urlDocsToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_url_docs( transaction, urlDocsToBeCopied );
                }

                Table_browser_docs browserDocsToBeCopied = selectFrom_browser_docs( meta_id );
                if( null != browserDocsToBeCopied ) {
                    browserDocsToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_browser_docs( transaction, browserDocsToBeCopied );
                }

                Table_frameset_docs framesetDocsToBeCopied = selectFrom_frameset_docs( meta_id );
                if( null != framesetDocsToBeCopied ) {
                    framesetDocsToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_frameset_docs( transaction, framesetDocsToBeCopied );
                }

                Table_fileupload_docs fileUploadDocsToBeCopied = selectFrom_fileupload_docs( meta_id );
                if( null != fileUploadDocsToBeCopied ) {
                    fileUploadDocsToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_fileupload_docs( transaction, fileUploadDocsToBeCopied );
                }

                Table_texts[] textsToBeCopied = selectFrom_texts( meta_id );
                for( int i = 0; i < textsToBeCopied.length; i++ ) {
                    Table_texts textToBeCopied = textsToBeCopied[i];
                    textToBeCopied.meta_id = meta_id.intValue();
                    Integer newCounterValue = getNextUniquePrimaryKey( "texts", "counter" );
                    textToBeCopied.counter = newCounterValue.intValue();
                    insertInto_texts( transaction, textToBeCopied );
                }

                Table_images[] imagesToBeCopied = selectFrom_images( meta_id );
                for( int i = 0; i < imagesToBeCopied.length; i++ ) {
                    Table_images imageToBeCopied = imagesToBeCopied[i];
                    imageToBeCopied.meta_id = meta_id.intValue();
                    insertInto_images( transaction, imageToBeCopied );
                }

                Table_includes[] includesToBeCopied = selectFrom_includes( meta_id );
                for( int i = 0; i < includesToBeCopied.length; i++ ) {
                    Table_includes table_includes = includesToBeCopied[i];
                    table_includes.meta_id = nextFreeMetaId.intValue();
                    insertInto_includes( transaction, table_includes );
                }

                Table_doc_permission_sets docPermissionSetsToBeCopied = selectFrom_doc_permission_sets( meta_id );
                if( null != docPermissionSetsToBeCopied ) {
                    docPermissionSetsToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_doc_permission_sets( transaction, docPermissionSetsToBeCopied );
                }

                Table_new_doc_permission_sets newDocPermissionSetsToBeCopied = selectFrom_new_doc_permission_sets( meta_id );
                if( null != newDocPermissionSetsToBeCopied ) {
                    newDocPermissionSetsToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_new_doc_permission_sets( transaction, newDocPermissionSetsToBeCopied );
                }

                Table_doc_permission_sets_ex docPermissionsSetsExToBeCopied = selectFrom_doc_permission_sets_ex( meta_id );
                if( null != docPermissionsSetsExToBeCopied ) {
                    docPermissionsSetsExToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_doc_permission_sets_ex( transaction, docPermissionsSetsExToBeCopied );
                }

                Table_new_doc_permission_sets_ex newDocPermissionSetsExToBeCopied = selectFrom_new_doc_permission_sets_ex( meta_id );
                if( null != newDocPermissionSetsExToBeCopied ) {
                    newDocPermissionSetsExToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_new_doc_permission_sets_ex( transaction, newDocPermissionSetsExToBeCopied );
                }

                Table_roles_rights rolesRightsToBeCopied = selectFrom_roles_rights( meta_id );
                if( null != rolesRightsToBeCopied ) {
                    rolesRightsToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_roles_rights( transaction, rolesRightsToBeCopied );
                }

                Table_meta_classification metaClassificationToBeCopied = selectFrom_meta_classification( meta_id );
                if( null != metaClassificationToBeCopied ) {
                    metaClassificationToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_meta_classification( transaction, metaClassificationToBeCopied );
                }

                Table_meta_section metaSectionToBeCopied = selectFrom_meta_section( meta_id );
                if( null != metaSectionToBeCopied ) {
                    metaSectionToBeCopied.meta_id = nextFreeMetaId.intValue();
                    insertInto_meta_section( transaction, metaSectionToBeCopied );
                }
            }
        } );
        return transaction.getRowCount();
    }

    public int sproc_deleteInclude( final int meta_id, final int include_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "DELETE FROM includes WHERE meta_id = ? AND include_id = ? ";
                Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( include_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public Table_users sproc_GetUserType( int meta_id ) {
        return selectFrom_users( new Integer( meta_id ) );
    }

    Table_users selectFrom_users( Integer user_id ) {
        String sql = "SELECT user_id,login_name,login_password,first_name,last_name,title,company,address,city,zip,country,county_council,email,external,last_page,archive_mode,lang_id,user_type,active,create_date FROM users " +
            "WHERE user_id = ? ";
        Object[] paramValues = new Object[]{user_id};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_users( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_users)queryResult.get( 0 );
        }
    }

    public String[] sproc_GetUserRoles( int user_id ) {
        String sql = "SELECT role_name FROM roles, user_roles_crossref " +
            "WHERE roles.role_id = user_roles_crossref.role_id AND user_roles_crossref.user_id = ?";
        Object paramValues[] = new Object[]{new Integer( user_id )};
        List roleNames = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                String role_name = rs.getString( "role_name" );
                return role_name;
            }
        } );

        return (String[])roleNames.toArray( new String[roleNames.size()] );
    }

    // todo: Denna returnerar lang_id istället för lang prefix
    // todo: anropa även sproc_GetLangPrefixFromId för denna information
    public Table_users sproc_GetUserInfo( int user_id ) {
        return selectFrom_users( new Integer( user_id ) );
    }

    public static class Table_languages {
        private String lang_prefix;
        private String user_prefix;
        private String language;

        Table_languages( ResultSet rs ) throws SQLException {
            lang_prefix = rs.getString( "lang_prefix" );
            user_prefix = rs.getString( "user_prefix" );
            language = rs.getString( "language" );
        }
    }

    public Table_languages[] sproc_getLanguages() {
        String sql = "SELECT lang_prefix, user_prefix, language FROM languages order by language";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_languages( rs );
            }
        } );
        return (Table_languages[])queryResult.toArray( new Table_languages[queryResult.size()] );
    }

    /** Get the users preferred language. Used by the administrator functions.
     * Begin with getting the users langId from the userobject.
     */
    public Table_lang_prefixes sproc_GetLangPrefixFromId( int lang_id ) {
        return selectFrom_lang_prefixes( new Integer( lang_id ) );
    }

    public static class Table_lang_prefixes {
        private int lang_id;
        public String lang_prefix;

        Table_lang_prefixes( ResultSet rs ) throws SQLException {
            lang_id = rs.getInt( "lang_id" );
            lang_prefix = rs.getString( "lang_prefix" );
        }
    }

    private Table_lang_prefixes selectFrom_lang_prefixes( Integer lang_id ) {
        String sql = "SELECT lang_id, lang_prefix FROM lang_prefixes WHERE lang_id = ? ";
        Object[] paramValues = new Object[]{lang_id};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_lang_prefixes( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_lang_prefixes)queryResult.get( 0 );
        }
    }

    public int sproc_GetRoleIdByRoleName( String role_name ) {
        String sql = "SELECT role_id FROM roles WHERE role_name like ? ";
        Object[] paramValues = new Object[]{role_name};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "role_id" ) );
            }
        } );
        return ((Integer)queryResult.get( 0 )).intValue();
    }

    static class JoinedTables_langprefixes_language {
        private int lang_id;
        private String language;

        JoinedTables_langprefixes_language( ResultSet rs ) throws SQLException {
            lang_id = rs.getInt( "lang_id" );
            language = rs.getString( "language" );
        }
    }

    public JoinedTables_langprefixes_language[] sproc_GetLanguageList( String lang_prefix ) {
        String sql = "SELECT lp.lang_id , lang.language FROM lang_prefixes lp, languages lang " +
            "WHERE lp.lang_prefix = lang.lang_prefix AND lang.user_prefix = ? ";
        Object[] paramValues = new Object[]{lang_prefix};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new JoinedTables_langprefixes_language( rs );
            }
        } );
        return (JoinedTables_langprefixes_language[])queryResult.toArray( new JoinedTables_langprefixes_language[queryResult.size()] );
    }

    public static class Table_templates {
        private int template_id;
        public String simple_name;

        Table_templates( ResultSet rs ) throws SQLException {
            template_id = rs.getInt( "template_id" );
            simple_name = rs.getString( "simple_name" );
        }
    }

    public Table_templates selectFrom_templates( int template_id ) {
        String sql = "SELECT template_id, simple_name FROM templates WHERE template_id = ? ";
        Object[] paramValues = new Object[]{new Integer( template_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_templates( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_templates)queryResult.get( 0 );
        }
    }

    public Table_templates[] sproc_getTemplates() {
        String sql = "SELECT template_id, simple_name FROM templates";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_templates( rs );
            };
        } );
        return (Table_templates[])queryResult.toArray( new Table_templates[queryResult.size()] );
    }

    public Table_templates[] sproc_GetTemplatesInGroup( int groupId ) {
        String sql = "SELECT t.template_id,simple_name FROM templates t " +
            "JOIN templates_cref c ON  t.template_id = c.template_id " +
            "WHERE c.group_id = ? " +
            "ORDER BY simple_name";
        Object[] paramValues = new Object[]{new Integer( groupId )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_templates( rs );
            }
        } );
        return (Table_templates[])queryResult.toArray( new Table_templates[queryResult.size()] );
    }

    public int sproc_GetTemplateId( String simple_name ) {
        String sql = "SELECT template_id FROM templates WHERE simple_name = ? ";
        Object[] paramValues = new Object[]{simple_name};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                int template_id = rs.getInt( "template_id" );
                return new Integer( template_id );
            }
        } );
        return ((Integer)queryResult.get( 0 )).intValue();
    }

    public String sproc_GetUserPassword( int user_id ) {
        String sql = "SELECT login_password FROM users WHERE user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return rs.getString( "login_password" );
            }
        } );
        if( queryResult.isEmpty() ) {
            return "";// todo: borde det inte vara null istället?
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

    public PartOfTable_roles[] sproc_getUserRoleIds( int user_id ) {
        String sql = "SELECT roles.role_id, role_name FROM roles, user_roles_crossref WHERE roles.role_id = user_roles_crossref.role_id AND user_roles_crossref.user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new PartOfTable_roles( rs );
            }
        } );
        return (PartOfTable_roles[])queryResult.toArray( new PartOfTable_roles[queryResult.size()] );
    }

    public static class PartOfTable_users {
        public int user_id;
        private String first_name;
        private String last_name;

        PartOfTable_users( ResultSet rs ) throws SQLException {
            user_id = rs.getInt( "user_id" );
            first_name = rs.getString( "first_name" );
            last_name = rs.getString( "last_name" );
        }
    }

    // todo: Denna returnerar inte riktigt samma som tidigare. För och Efternamn är uppdelat i olika fält.
    public PartOfTable_users[] sproc_GetUsersWhoBelongsToRole( int role_id ) {
        String sql = "SELECT u.user_id, u.last_name, u.first_name FROM user_roles_crossref us " +
            "JOIN users u ON us.user_id = u.user_id WHERE role_id = ? ORDER BY last_name";
        Object[] paramValues = new Object[]{new Integer( role_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new PartOfTable_users( rs );
            }
        } );
        return (PartOfTable_users[])queryResult.toArray( new PartOfTable_users[queryResult.size()] );
    }

    /**
     This function is used from AdminIpAcces servlet to generate a list
     */
    // todo: Denna returnerar inte riktigt samma som tidigare. För och Efternamn är uppdelat i olika fält.
    public PartOfTable_users[] sproc_GetAllUsersInList() {
        String sql = "SELECT user_id, last_name, first_name FROM users ORDER BY last_name";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new PartOfTable_users( rs );
            }
        } );
        return (PartOfTable_users[])queryResult.toArray( new PartOfTable_users[queryResult.size()] );
    }

    public int sproc_StartDocGet() {
        Integer type_id = new Integer( 0 );
        return Integer.parseInt( selectFrom_sys_data( type_id ) );
    }

    public int sproc_StartDocSet( int meta_id ) {
        return update_sys_data( new Integer( 0 ), new Integer( meta_id ) );
    }

    public int sproc_GetCurrentSessionCounter() {
        Integer type_id = new Integer( 1 );
        return Integer.parseInt( selectFrom_sys_data( type_id ) );
    }

    public int sproc_SetSessionCounterValue( int sysdata_value ) {
        return update_sys_data( new Integer( 1 ), new Integer( sysdata_value ) );
    }

    public String sproc_GetCurrentSessionCounterDate() {
        Integer type_id = new Integer( 2 );
        return selectFrom_sys_data( type_id );
    }

    public int sproc_SetSessionCounterDate( String counterDate ) {
        return update_sys_data( new Integer( 2 ), counterDate );
    }

    public String sproc_SystemMessageGet() {
        Integer type_id = new Integer( 3 );
        return selectFrom_sys_data( type_id );
    }

    public int sproc_SystemMessageSet( String newMsg ) {
        return update_sys_data( new Integer( 3 ), newMsg );
    }

    public String sproc_ServerMasterGet_name() {
        Integer type_id = new Integer( 4 );
        return selectFrom_sys_data( type_id );
    }

    public String sproc_ServerMasterGet_address() {
        Integer type_id = new Integer( 5 );
        return selectFrom_sys_data( type_id );
    }

    public int sproc_ServerMasterSet_name( String name ) {
        return update_sys_data( new Integer( 4 ), name );
    }

    public int sproc_ServerMasterSet_address( String address ) {
        return update_sys_data( new Integer( 5 ), address );
    }

    public String sproc_WebMasterGet_name() {
        Integer type_id = new Integer( 6 );
        return selectFrom_sys_data( type_id );
    }

    public String sproc_WebMasterGet_address() {
        Integer type_id = new Integer( 7 );
        return selectFrom_sys_data( type_id );
    }

    public int sproc_WebMasterSet_name( String name ) {
        return update_sys_data( new Integer( 6 ), name );
    }

    public int sproc_WebMasterSet_address( String address ) {
        return update_sys_data( new Integer( 7 ), address );
    }

    private String selectFrom_sys_data( Integer type_id ) {
        String sql = " SELECT sysdata_value FROM sys_data WHERE type_id  = ? ";
        Object[] paramValues = new Object[]{type_id};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return rs.getString( "sysdata_value" );
            }
        } );
        return (String)queryResult.get( 0 );
    }

    private int update_sys_data( final Integer type_id, final Object sysdata_value ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = " UPDATE sys_data SET sysdata_value = ? WHERE type_id  = ? ";
                Object[] paramValues = new Object[]{sysdata_value, type_id};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public static class Table_section {
        public int section_id;
        public String section_name;

        Table_section() {
        }

        Table_section( ResultSet rs ) throws SQLException {
            section_id = rs.getInt( "section_id" );
            section_name = rs.getString( "section_name" );
        }
    }

    public Table_section sproc_SectionGetInheritId( int parent_meta_id ) {
        String sql = "SELECT s.section_id, s.section_name " +
            "FROM sections s, meta_section ms, meta m " +
            "WHERE m.meta_id=ms.meta_id AND m.meta_id= ? AND ms.section_id=s.section_id";
        Object[] paramValues = new Object[]{new Integer( parent_meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_section( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_section)queryResult.get( 0 );
        }
    }

    public int sproc_SectionChangeAndDeleteCrossref( final int new_section_id, final int old_section_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Integer oldSectionId = new Integer( old_section_id );
                Integer newSectionId = new Integer( new_section_id );
                deleteFrom_section( transaction, oldSectionId );
                String sql = "UPDATE meta_section SET section_id = ? WHERE section_id = ? ";
                Object[] paramValues = new Object[]{newSectionId, oldSectionId};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public Table_section[] sproc_SectionGetAll() {
        String sql = "SELECT section_id, section_name FROM sections";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_section( rs );
            }
        } );
        return (Table_section[])queryResult.toArray( new Table_section[queryResult.size()] );
    }

    public static class Table_section_count extends Table_section {
        private int doc_count;

        Table_section_count( ResultSet rs ) throws SQLException {
            super( rs );
            doc_count = rs.getInt( "doc_count" );
        }
    }

    public Table_section_count[] sproc_SectionGetAllCount() {
        String sql = "SELECT s.section_id, s.section_name, COUNT(meta_id) AS doc_count " +
            "FROM sections s " +
            "LEFT JOIN meta_section ms ON s.section_id = ms.section_id " +
            "GROUP BY s.section_name, s.section_id order by section_name";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_section_count( rs );
            }
        } );
        return (Table_section_count[])queryResult.toArray( new Table_section_count[queryResult.size()] );
    }

    /**
     * Gets the number of docs that is connected to that section_id
     */
    public int sproc_SectionCount( int section_id ) {
        String sql = "SELECT COUNT(meta_id) AS meta_id_count FROM meta_section WHERE section_id= ? ";
        Object[] paramValues = new Object[]{new Integer( section_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "meta_id_count" ) );
            }
        } );
        return ((Integer)queryResult.get( 0 )).intValue();
    }

    public int sproc_SectionDelete( final int section_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Integer sectionId = new Integer( section_id );
                deleteFrom_section( transaction, sectionId );
            }
        } );
        return transaction.getRowCount();
    }

    private void deleteFrom_section( SQLTransaction transaction, Integer sectionId ) {
        deleteFrom_meta_section( transaction, sectionId );
        deleteFrom_sections( transaction, sectionId );
    }

    private void deleteFrom_meta_section( SQLTransaction transaction, Integer sectionId ) {
        String sql = "DELETE FROM meta_section WHERE section_id = ? ";
        Object[] paramValues = new Object[]{sectionId};
        transaction.executeUpdate( sql, paramValues );
    }

    private void deleteFrom_sections( SQLTransaction transaction, Integer sectionId ) {
        String sql = "DELETE FROM sections WHERE section_id = ? ";
        Object[] paramValues = new Object[]{sectionId};
        transaction.executeUpdate( sql, paramValues );
    }

    public int sproc_SectionChangeName( final Table_section sectionData ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                update_sections( transaction, sectionData );
            }
        } );
        return transaction.getRowCount();
    }

    private void update_sections( SQLTransaction transaction, Table_section sectionData ) {
        String sql = "UPDATE sections SET section_name= ? WHERE section_id = ? ";
        Object[] paramValues = new Object[]{sectionData.section_name, new Integer( sectionData.section_id )};
        transaction.executeUpdate( sql, paramValues );
    }

    public int sproc_SectionAdd( final Table_section sectionData ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Table_section textDocsToBeCopied = selectFrom_section( transaction, new Integer( sectionData.section_id ) );
                if( null == textDocsToBeCopied ) {
                    insertInto_sections( transaction, sectionData );
                } else {
                    update_sections( transaction, sectionData );
                }
            }
        } );
        return transaction.getRowCount();
    }

    private Table_section selectFrom_section( SQLTransaction transaction, Integer section_id ) throws SQLException {
        String sql = "SELECT section_id, section_name FROM sections WHERE section_id = ? ";
        Object[] paramValues = new Object[]{section_id};
        ArrayList queryResult = transaction.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_section( rs );
            }
        } );

        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_section)queryResult.get( 0 );
        }
    }

    private void insertInto_sections( SQLTransaction transaction, Table_section tableData ) {
        String sql = " INSERT INTO sections ( section_id, section_name ) VALUES (?,?)";
        Object[] paramValues = new Object[]{new Integer( tableData.section_id ), tableData.section_name};
        transaction.executeUpdate( sql, paramValues );
    }

    /**
     * Lets insert the crossreferences but first we deleta all oldones for this meta_id
     * @return
     */
    public int sproc_SectionAddCrossref( final Table_meta_section tableData ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                deleteFrom_meta_section( transaction, new Integer( tableData.meta_id ) );
                insertInto_meta_section( transaction, tableData );
            }
        } );
        return transaction.getRowCount();
    }

    public class Table_roles {
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

    public Table_roles[] sproc_GetAllRoles_but_user() {
        String sql = "SELECT role_id, role_name, permissions, admin_role FROM roles WHERE role_name <> 'Users' ORDER BY role_name";
        Object[] paramValues = null;

        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_roles( rs );
            }
        } );
        return (Table_roles[])queryResult.toArray( new Table_roles[queryResult.size()] );
    }

    /**
     * This function is when an administrator tries to add a new roleName.
     * The system searches for the rolename and returns the the id it exists otherwize -1
     */
    public int sproc_RoleFindName( String role_name ) {
        String sql = "SELECT r.role_id FROM roles r WHERE r.role_name = ? ";
        Object[] paramValues = new Object[]{role_name};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "role_id" ) );
            }
        } );
        if( queryResult.isEmpty() ) {
            return -1;
        } else {
            return ((Integer)queryResult.get( 0 )).intValue();
        }
    }

    /**
     *  Adds a new role
     */
    public int sproc_RoleAddNew( final String role_name ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Integer newRoleId = getNextUniquePrimaryKey( "roles", "role_id" );
                String sql = "INSERT INTO roles ( role_id , role_name, permissions, admin_role ) VALUES( ?, ?, ?, ? )";
                Object[] paramValues = new Object[]{newRoleId, role_name, new Integer( 0 ), new Integer( 0 )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    /**
     * Deletes an role from the role table. Used by the AdminRoles servlet
     */
    public int sproc_RoleDelete( final int role_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "DELETE FROM roles_rights WHERE role_id = ? ";
                Object[] paramValues = new Object[]{new Integer( role_id )};
                transaction.executeUpdate( sql, paramValues );

                sql = "DELETE FROM user_roles_crossref WHERE role_id = ? ";
                transaction.executeUpdate( sql, paramValues );

                sql = "DELETE FROM roles WHERE role_id = ? ";
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    /**
     * Get data for a user by his login_name. Used for login.
     * @param login_name
     * @return null if no user found
     */
    public Table_users sproc_GetUserByLogin( String login_name ) {
        Integer user_id = selectFrom_users_getUserIdFromLoginName( login_name );
        if( user_id == null ) {
            return null;
        }

        return selectFrom_users( user_id );
    }

    // todo: Denna returnerar lang_id istället för lang prefix
    // todo: anropa även sproc_GetLangPrefixFromId för denna information
    private Integer selectFrom_users_getUserIdFromLoginName( String login_name ) {
        String sql = "SELECT user_id FROM users WHERE login_name = ? ";
        Object[] paramValues = new Object[]{login_name};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "user_id" ) );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Integer)queryResult.get( 0 );
        }
    }

    public static class Table_templategroups {
        int group_id;
        String group_name;

        public Table_templategroups( ResultSet rs ) throws SQLException {
            group_id = rs.getInt( "group_id" );
            group_name = rs.getString( "group_name" );
        }
    }

    public Table_templategroups[] sproc_getTemplategroups() {
        String sql = "SELECT group_id,group_name FROM templategroups order by group_name";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_templategroups( rs );
            }
        } );
        return (Table_templategroups[])queryResult.toArray( new Table_templategroups[queryResult.size()] );
    }

    /**
     *
     * Nice query that fetches all templategroups a user may use in a document,
     * for easy insertion into an html-option-list, no less!
     */
    public Table_templategroups[] sproc_GetTemplateGroupsForUser( int meta_id, int user_id ) {
        String sql = "SELECT distinct group_id,group_name FROM templategroups dt " +
            "JOIN  user_roles_crossref urc ON urc.user_id = ? " +
            "LEFT JOIN roles_rights rr ON rr.meta_id = ? AND rr.role_id = urc.role_id " +
            "LEFT JOIN doc_permission_sets dps ON dps.meta_id = rr.meta_id AND dps.set_id = rr.set_id " +
            "LEFT JOIN doc_permission_sets_ex dpse ON dpse.permission_data = dt.group_id " +
            "AND (dpse.permission_id & dps.permission_id) > 0 AND dpse.meta_id = rr.meta_id " +
            "AND dpse.set_id = rr.set_id AND dpse.permission_id = 524288 " + // = Change template
            "WHERE dpse.permission_data IS NOT NULL OR rr.set_id = 0 OR urc.role_id = 0 ORDER BY dt.group_name";
        Object[] paramValues = new Object[]{new Integer( user_id ), new Integer( meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_templategroups( rs );
            }
        } );
        return (Table_templategroups[])queryResult.toArray( new Table_templategroups[queryResult.size()] );
    }

    public int sproc_UpdateParentsDateModified( final int meta_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Integer meta_id1 = new Integer( meta_id );
                Integer result;
                String sql = "SELECT meta_id FROM childs WHERE to_meta_id = ? ";
                Object[] paramValues = new Object[]{meta_id1};
                ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
                    public Object mapOneRow( ResultSet rs ) throws SQLException {
                        return new Integer( rs.getInt( "meta_id" ) );
                    }
                } );
                if( queryResult.isEmpty() ) {
                    result = null;
                } else {
                    result = (Integer)queryResult.get( 0 );
                }
                Integer parent_meta_id = result;

                if( null != parent_meta_id ) {
                    sql = "UPDATE meta SET date_modified = ? WHERE meta_id = ? ";
                    paramValues = new Object[]{new Timestamp( new java.util.Date().getTime() ), parent_meta_id};
                    transaction.executeUpdate( sql, paramValues );
                }
            }
        } );
        return transaction.getRowCount();
    }

    // todo: Christoffer varför är ip_start och ip_end flyttal? Det verkar inte vara så i koden, vad är det för något?
    public static class Table_ip_accesses {
        int ip_access_id;
        int user_id;
        long ip_start;
        long ip_end;

        public Table_ip_accesses() {
        }

        public Table_ip_accesses( ResultSet rs ) throws SQLException {
            ip_access_id = rs.getInt( "ip_access_id" );
            user_id = rs.getInt( "user_id" );
            ip_start = rs.getLong( "ip_start" );
            ip_end = rs.getLong( "ip_end" );
        }
    }

    /**
     * Lets get all IPaccesses from db. Used  by the AdminIpAccesses
     */
    // todo: denna returnerar endast users som finns kvar i systemet. Gamla user_ids försvinner, ska det vara så?
    public Table_ip_accesses[] sproc_IPAccessesGetAll() {
        String sql = "SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end " +
            "FROM ip_accesses ip, users usr WHERE ip.user_id = usr.user_id";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_ip_accesses( rs );
            }
        } );
        return (Table_ip_accesses[])queryResult.toArray( new Table_ip_accesses[queryResult.size()] );
    }

    /**
     * This function adds a new ip-access to the db. Used by AdminManager
     * @param user_id
     * @param ip_start
     * @param ip_end
     * @return
     */
    // todo: Borde inte ip_access_id vara en primary key, och user_id en forreign?
    public int sproc_IPAccessAdd( final int user_id, final long ip_start, final long ip_end ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                Integer new_ip_access_id = getNextUniquePrimaryKey( "ip_accesses", "ip_access_id" );
                String sql = "INSERT INTO ip_accesses ( ip_access_id, user_id , ip_start , ip_end ) VALUES (?,?,?,?)";
                Object[] paramValues = new Object[]{new_ip_access_id, new Integer( user_id ), new Long( ip_start ), new Long( ip_end )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    /**
     * Updates the IPaccess table
     */
    public int sproc_IPAccessUpdate( final Table_ip_accesses tableData ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "UPDATE ip_accesses SET user_id = ? , ip_start = ?, ip_end = ? WHERE ip_access_id = ? ";
                Object[] paramValues = new Object[]{new Integer( tableData.user_id ), new Double( tableData.ip_start ),
                                                    new Long( tableData.ip_end ), new Long( tableData.ip_access_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    /**
     * Deletes an Ip-access for a user. Used by the AdminIpAccess servlet
     */
    public int sproc_IPAccessDelete( final int ip_access_id ) {
        final SQLTransaction transaction = noTransactionSqlProcessor.createNewTransaction( Connection.TRANSACTION_SERIALIZABLE, defaultTransactionRetries );
        transaction.executeAndCommit( new TransactionContent() {
            public void execute() throws SQLException {
                String sql = "DELETE FROM ip_accesses WHERE ip_access_id = ? ";
                Object[] paramValues = new Object[]{new Integer( ip_access_id )};
                transaction.executeUpdate( sql, paramValues );
            }
        } );
        return transaction.getRowCount();
    }

    public static class JoinedTables_sort_by_display_name {
        String sort_by_type;
        String display_name;

        public JoinedTables_sort_by_display_name( ResultSet rs ) throws SQLException {
            sort_by_type = rs.getString( "sort_by_type" );
            display_name = rs.getString( "display_name" );
        }
    }

    /**
     * This sproc is used by the GetExistingDoc servlet, it takes the lang id string as argument and returns
     * the sortorder options  display text for that language.
     * Example: SortOrder_GetExistingDocs 'se'.
     */
    public JoinedTables_sort_by_display_name[] sproc_SortOrder_GetExistingDocs( String lang_prefix ) {
        String sql = "SELECT sType.sort_by_type , display.display_name FROM lang_prefixes lang " +
            "INNER JOIN display_name display ON display.lang_id = lang.lang_id AND lang.lang_prefix = ? " +
            "INNER JOIN sort_by sType ON sType.sort_by_id = display.sort_by_id";
        Object[] paramValues = new Object[]{lang_prefix};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new JoinedTables_sort_by_display_name( rs );
            }
        } );
        return (JoinedTables_sort_by_display_name[])queryResult.toArray( new JoinedTables_sort_by_display_name[queryResult.size()] );
    }

    // getUserPermissionSetForDocument() and isRestricted1MorePriviligedThanRestricted2ForDocument
    // replaces the sproc GetUserPermissionSet
    public boolean isRestricted1MorePriviligedThanRestricted2ForDocument( int meta_id ) {
        String sql = "SELECT permissions FROM meta WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Integer( rs.getInt( "permissions" ) );
            }
        } );
        int permissionBitvector = ((Integer)queryResult.get( 0 )).intValue();
        return 1 == (1 & permissionBitvector);
    }

    public static class JoinedTables_permissions {
        public int set_id;
        public int permission_id;

        public JoinedTables_permissions() {
        }

        public JoinedTables_permissions( ResultSet rs ) throws SQLException {
            set_id = rs.getInt( "set_id" );
            permission_id = rs.getInt( "permission_id" );
        }
    }

    /*
     Finds out what is the most privileged permission_set a user has for a document.
     Column 1: The users most privileged set_id
     Column 2: The users permission-set for this set_id

     set_id's:
     0 - most privileged (full rights)
     1 & 2 - misc. They may be equal, and 1 may have permission to modify 2.
     3 - only read rights
     4 - least privileged (no rights)
    */

    // getUserPermissionSetForDocument() and isRestricted1MorePriviligedThanRestricted2ForDocument
    // replaces the sproc GetUserPermissionSet
    public JoinedTables_permissions
        getUserPermissionSetForDocument( int meta_id, int user_id ) {

        // Check if user is superadmin
        if( sproc_CheckAdminRights( user_id ) ) {
            JoinedTables_permissions result = new JoinedTables_permissions();
            result.set_id = 0;
            result.permission_id = 0;
            return result;
        }

        String sql = "SELECT rr.set_id,dps.permission_id " +
            "FROM user_roles_crossref urc " +
            "JOIN roles_rights rr " +
            "ON urc.role_id = rr.role_id " +
            "AND urc.user_id = ? " +
            "AND rr.meta_id = ? " +
            "LEFT JOIN doc_permission_sets dps " +
            "ON dps.meta_id = ? " +
            "AND dps.set_id = rr.set_id " +
            "ORDER BY rr.set_id";
        Integer metaId = new Integer( meta_id );
        Object[] paramValues = new Object[]{new Integer( user_id ), metaId, metaId};

        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new JoinedTables_permissions( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (JoinedTables_permissions)queryResult.get( 0 );
        }
    }

    public class Table_polls {
        int id;
        public Integer name;
        int description;
        public int meta_id;
        public int popup_freq;
        public int set_cookie;
        public int hide_result;
        int confirmation_text;
        public Integer email_recipients;
        public int email_from;
        public int email_subject;
        public int result_template;

        public Table_polls( ResultSet rs ) throws SQLException {
            id = rs.getInt( "id" );
            name = (Integer)rs.getObject( "name" );
            description = rs.getInt( "description" );
            meta_id = rs.getInt( "meta_id" );
            popup_freq = rs.getInt( "popup_freq" );
            set_cookie = rs.getInt( "set_cookie" );
            hide_result = rs.getInt( "hide_result" );
            confirmation_text = rs.getInt( "confirmation_text" );
            email_recipients = (Integer)rs.getObject( "email_recipients" );
            email_from = rs.getInt( "email_from" );
            email_subject = rs.getInt( "email_subject" );
            result_template = rs.getInt( "result_template" );
        }
    }

    public Table_polls[] sproc_Poll_GetAll() {
        String sql = "SELECT id, name, description, meta_id, popup_freq, " +
            "set_cookie, hide_result, confirmation_text, " +
            "email_recipients, email_from, email_subject, result_template FROM polls";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_polls( rs );
            }
        } );
        return (Table_polls[])queryResult.toArray( new Table_polls[queryResult.size()] );
    }

    public static class Table_poll_answers {
        int id;
        int question_id;
        int text_id;
        int option_number;
        int answer_count;
        int option_point;

        public Table_poll_answers( ResultSet rs ) throws SQLException {
            id = rs.getInt( "id" );
            question_id = rs.getInt( " question_id" );
            text_id = rs.getInt( " text_id" );
            option_number = rs.getInt( " option_number" );
            answer_count = rs.getInt( " answer_count" );
            option_point = rs.getInt( " option_point" );
        }
    }

    /**
     * Get all answer for one question
     */
    Table_poll_answers[] sproc_Poll_GetAllAnswers( int question_id ) {
        String sql = "SELECT id, question_id, text_id, option_number, answer_count, option_point " +
            "FROM poll_answers WHERE question_id = ? ORDER BY option_number";
        Object[] paramValues = new Object[]{new Integer( question_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_poll_answers( rs );
            }
        } );
        return (Table_poll_answers[])queryResult.toArray( new Table_poll_answers[queryResult.size()] );
    }

    public class Table_poll_questions {
        int id;
        int poll_id;
        int question_number;
        int text_id;

        public Table_poll_questions( ResultSet rs ) throws SQLException {
            id = rs.getInt( "id!" );
            poll_id = rs.getInt( "poll_id!" );
            question_number = rs.getInt( "question_number!" );
            text_id = rs.getInt( "text_id!" );
        }
    }

    /**
     * Get all questions for one poll
     * @param poll_id
     * @return
     */
    Table_poll_questions[] sproc_Poll_GetAllQuestions( int poll_id ) {
        String sql = "SELECT id, poll_id, question_number, text_id FROM poll_questions WHERE poll_id = ? ORDER BY question_number";
        Object[] paramValues = new Object[]{new Integer( poll_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_poll_questions( rs );
            }
        } );
        return (Table_poll_questions[])queryResult.toArray( new Table_poll_questions[queryResult.size()] );
    }

    /**
     * Get one answer option for a question
     */
    Table_poll_answers sproc_Poll_GetAnswer( int question_id, int option_no ) {
        String sql = "SELECT * FROM poll_answers WHERE question_id = ? AND option_number = ? ";
        Object[] paramValues = new Object[]{new Integer( question_id ), new Integer( option_no )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_poll_answers( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_poll_answers)queryResult.get( 0 );
        }
    }

    /*
    * Get all data for a poll by meta_id
    */
    public Table_polls sproc_Poll_GetOne( int meta_id ) {
        String sql = "SELECT id, name, description, meta_id, popup_freq, set_cookie, hide_result, confirmation_text, email_recipients, email_from, email_subject, result_template FROM polls WHERE meta_id = ? ";
        Object[] paramValues = new Object[]{new Integer( meta_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_polls( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_polls)queryResult.get( 0 );
        }
    }

    /*
    * Get a question by meta_id and question numbe
    */
    Table_poll_answers sproc_Poll_GetQuestion( int poll_id, int question_number ) {
        String sql = "SELECT id, poll_id, question_number, text_id FROM poll_questions WHERE poll_id = ? AND question_number = ? ";
        Object[] paramValues = new Object[]{new Integer( poll_id ), new Integer( question_number )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_poll_answers( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_poll_answers)queryResult.get( 0 );
        }
    }

    public class JoinedTables_meta_childs2 {
        int to_meta_id;
        String meta_headline;

        public JoinedTables_meta_childs2( ResultSet rs ) throws SQLException {
            to_meta_id = rs.getInt( "to_meta_id" );
            meta_headline = rs.getString( "meta_headline" );
        }
    }

    JoinedTables_meta_childs2[] sproc_getMenuDocChilds( int meta_id, int user_id ) {
        String sql = "SELECT DISTINCT to_meta_id, meta_headline FROM childs c " +
            "JOIN meta m " +
            "ON c.to_meta_id = m.meta_id " +
            "AND c.meta_id = ? " +
            "LEFT JOIN roles_rights rr " +
            "ON rr.meta_id = m.meta_id " +
            "AND rr.set_id < 4 " +
            "JOIN user_roles_crossref urc " +
            "ON urc.user_id = ? " +
            "AND (  urc.role_id = 0 " +
            "OR urc.role_id = rr.role_id " +
            "OR  m.shared = 1) " +
            "WHERE m.activate = 1 ORDER BY to_meta_id";
        Object[] paramValues = new Object[]{new Integer( meta_id ), new Integer( user_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new JoinedTables_meta_childs2( rs );
            }
        } );
        return (JoinedTables_meta_childs2[])queryResult.toArray( new JoinedTables_meta_childs2[queryResult.size()] );
    }

    public class Table_readrunner_user_data {
        int user_id;
        int uses;
        int max_uses;
        int max_uses_warning_threshold;
        Timestamp expiry_date;
        int expiry_date_warning_threshold;
        int expiry_date_warning_sent;

        public Table_readrunner_user_data( ResultSet rs ) throws SQLException {
            user_id = rs.getInt( "user_id" );
            uses = rs.getInt( "uses" );
            max_uses = rs.getInt( "max_uses" );
            max_uses_warning_threshold = rs.getInt( "max_uses_warning_threshold" );
            expiry_date = rs.getTimestamp( "expiry_date" );
            expiry_date_warning_threshold = rs.getInt( "expiry_date_warning_threshold" );
            expiry_date_warning_sent = rs.getInt( "expiry_date_warning_sent" );
        }
    }

    /**
     * Return readrunner-user-data for one user.
     * @param user_id The id of the user
     * @return    Returns one row with the following columns:
     uses                          INT      The number of times the user have used readrunner.
     max_uses                      INT      Maximum allowed amount of uses.
     max_uses_warning_threshold    INT      Percentage threshold at which the user will be warned about expiry.
     expiry_date                   DATETIME The last date the user may use readrunner.
     expiry_date_warning_threshold INT      Threshold of days before expiry_date at which the user will be warned about expiry.
     expiry_date_warning_sent      INT      Whether a expiry-date-warning has been sent or not.
     */
    Table_readrunner_user_data sproc_GetReadrunnerUserDataForUser( int user_id ) {
        String sql = "SELECT user_id, uses, max_uses, max_uses_warning_threshold, expiry_date_warning_threshold, " +
            "expiry_date_warning_sent FROM readrunner_user_data WHERE user_id = ? ";
        Object[] paramValues = new Object[]{new Integer( user_id )};
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, paramValues, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_readrunner_user_data( rs );
            }
        } );
        if( queryResult.isEmpty() ) {
            return null;
        } else {
            return (Table_readrunner_user_data)queryResult.get( 0 );
        }
    }

    class Table_unique_keys {
        String table_name;
        String column_name;
        int key_value;

        Table_unique_keys( ResultSet rs ) throws SQLException {
            table_name = rs.getString( "table_name" );
            column_name = rs.getString( "column_name" );
            key_value = rs.getInt( "key_value" );
        }
    }

    Table_unique_keys[] getAllUniqueKeys() {
        String sql = "SELECT table_name, column_name, key_value FROM unique_keys";
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                return new Table_unique_keys( rs );
            }
        } );
        return (Table_unique_keys[])queryResult.toArray( new Table_unique_keys[queryResult.size()] );
    }

    int getMaxKeyValue( String table_name, String column_name ) {
        String sql = "SELECT MAX(" + column_name + ") AS max_value FROM " + table_name;
        ArrayList queryResult = noTransactionSqlProcessor.executeQuery( sql, null, new ResultProcessor() {
            public Object mapOneRow( ResultSet rs ) throws SQLException {
                int max_value = rs.getInt( "max_value" );
                return new Integer( max_value );
            }
        } );
        return ((Integer)queryResult.get( 0 )).intValue();
    }
}
