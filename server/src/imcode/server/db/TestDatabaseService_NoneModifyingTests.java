package imcode.server.db;

public class TestDatabaseService_NoneModifyingTests extends TestDatabaseService {
    DatabaseService sqlServer;
    DatabaseService mimer;
    DatabaseService mySql;

    public TestDatabaseService_NoneModifyingTests() throws Exception {
        if( mimer == null ) {
            initAllDatabases();
        }
    }

    private static void static_assertEquals( Object[] ref, Object[] one, Object[] another ) {
        static_assertEquals( ref, one );
        static_assertEquals( ref, another );
    }

    private static void static_assertEquals( Object[] oneArr, Object[] anotherArr ) {
        if( oneArr == null ) {
            assertNotNull( anotherArr );
        } else if( anotherArr == null ) {
            fail( "The second array is null, but not the first oneArr" );
        } else {
            assertTrue( oneArr != anotherArr );
            assertEquals( oneArr.length, anotherArr.length );
            for( int i = 0; i < oneArr.length; i++ ) {
                Object one = oneArr[i];
                Object another = anotherArr[i];
                assertTrue( one != another );
                assertEquals( one, another );
            }
        }
    }

    public void testSameResultFromSprocGetAllRoles() {
        DatabaseService.Table_roles[] ref = mimer.sprocGetAllRoles();
        DatabaseService.Table_roles[] one = sqlServer.sprocGetAllRoles();
        DatabaseService.Table_roles[] another = mySql.sprocGetAllRoles();
        assertEquals( 2, ref.length );
        assertEquals( 2, one.length );
        assertEquals( 2, another.length );
        static_assertEquals( ref, one, another );
    }

    public void testSameResultFromSprocGetAllUsers() {
        DatabaseService.Table_users[] ref = mimer.sprocGetAllUsers();
        DatabaseService.Table_users[] one = sqlServer.sprocGetAllUsers();
        DatabaseService.Table_users[] another = mySql.sprocGetAllUsers();
        assertEquals( 2, ref.length );
        assertEquals( 2, one.length );
        assertEquals( 2, another.length );
        static_assertEquals(  ref, one, another );
    }

    public void testSameResultFromSprocGetTamplatesInGroup() {
        DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupZero = mimer.sprocGetTemplatesInGroup(0);
        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupZero = sqlServer.sprocGetTemplatesInGroup(0);
        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupZero = mySql.sprocGetTemplatesInGroup(0);

        assertEquals( 1, mimerTemplatesInGroupZero.length );
        assertEquals( 1, sqlServerTemplatesInGroupZero.length );
        assertEquals( 1, mySQLTemplatesInGroupZero.length );
        DatabaseService.ViewTemplateGroup templateGroupZero = new DatabaseService.ViewTemplateGroup( 1, "Start" );
        assertEquals( templateGroupZero, mimerTemplatesInGroupZero[0] );
        assertEquals( templateGroupZero, sqlServerTemplatesInGroupZero[0] );
        assertEquals( templateGroupZero, mySQLTemplatesInGroupZero[0] );

        DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupOne = mimer.sprocGetTemplatesInGroup(1);
        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupOneo = sqlServer.sprocGetTemplatesInGroup(1);
        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupOne = mySql.sprocGetTemplatesInGroup(1);
        assertEquals(mimerTemplatesInGroupOne.length, sqlServerTemplatesInGroupOneo.length );
        assertEquals(mimerTemplatesInGroupOne.length, mySQLTemplatesInGroupOne.length );

        DatabaseService.ViewTemplateGroup[] mimerTemplatesInGroupTwo = mimer.sprocGetTemplatesInGroup(2);
        DatabaseService.ViewTemplateGroup[] sqlServerTemplatesInGroupTwo = sqlServer.sprocGetTemplatesInGroup(2);
        DatabaseService.ViewTemplateGroup[] mySQLTemplatesInGroupTwo = mySql.sprocGetTemplatesInGroup(2);
        assertEquals( mimerTemplatesInGroupTwo.length, sqlServerTemplatesInGroupTwo.length );
        assertEquals( mimerTemplatesInGroupTwo.length, mySQLTemplatesInGroupTwo.length );
    }

    protected void initAllDatabases() throws Exception {
        initMySql();
        initSqlServer();
        initMimer();
    }

    private void initMimer() throws Exception {
        mimer = new DatabaseService( DatabaseService.MIMER, TestDatabaseService.DB_HOST, TestDatabaseService.MIMER_PORT, TestDatabaseService.MIMMER_DATABASE_NAME, TestDatabaseService.MIMMER_DATABASE_USER, TestDatabaseService.MIMMER_DATABASE_PASSWORD );
        mimer.initializeDatabase();
    }

    private void initSqlServer() throws Exception {
        sqlServer = new DatabaseService( DatabaseService.SQL_SERVER, TestDatabaseService.DB_HOST, TestDatabaseService.SQLSERVER_PORT, TestDatabaseService.SQLSERVER_DATABASE_NAME, TestDatabaseService.SQLSERVE_DATABASE_USER, TestDatabaseService.SQLSERVE_DATABASE_PASSWORD );
        sqlServer.initializeDatabase();
    }

    private void initMySql() throws Exception {
        mySql = new DatabaseService( DatabaseService.MY_SQL, TestDatabaseService.DB_HOST, TestDatabaseService.MYSQL_PORT, TestDatabaseService.MYSQL_DATABASE_NAME, TestDatabaseService.MYSQL_DATABASE_USER, TestDatabaseService.MYSQL_DATABASE_PASSWORD );
        mySql.initializeDatabase();
    }
}
