package imcode.server.db;

public class TestDatabaseService_NoneModifyingTests extends TestDatabaseService {

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
                System.out.println( "one: " + one.toString() );
                System.out.println( "another: " + another.toString() );
            }
        }
    }

    public void testSameResultFromSprocGetAllRoles() {
        static_assertEquals( mimer.sprocGetAllRoles(), sqlServer.sprocGetAllRoles(), mySql.sprocGetAllRoles() );
    }

    public void testSameResultFromSprocGetAllUsers() {
        static_assertEquals(  mimer.sprocGetAllUsers(), sqlServer.sprocGetAllUsers(), mySql.sprocGetAllUsers() );
    }

    public void testSameResultFromSprocGetTamplatesInGroup() {
        assertEquals( mimer.sprocGetTemplatesInGroup(0), sqlServer.sprocGetTemplatesInGroup(0) );
        assertEquals( mimer.sprocGetTemplatesInGroup(0), mySql.sprocGetTemplatesInGroup(0) );

        assertEquals( mimer.sprocGetTemplatesInGroup(1), sqlServer.sprocGetTemplatesInGroup(1) );
        assertEquals( mimer.sprocGetTemplatesInGroup(1), mySql.sprocGetTemplatesInGroup(1) );

        assertEquals( mimer.sprocGetTemplatesInGroup(2), sqlServer.sprocGetTemplatesInGroup(2) );
        assertEquals( mimer.sprocGetTemplatesInGroup(2), mySql.sprocGetTemplatesInGroup(2) );
    }
}
