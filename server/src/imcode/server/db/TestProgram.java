package imcode.server.db;

import java.sql.*;
import java.util.Collection;
import java.util.ArrayList;

public class TestProgram {
    public static void main( String[] args ) throws Exception, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String driverClassName = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        String serverName = "localhost";
        String serverPort = "1433";
        String dbUrl = "jdbc:microsoft:sqlserver://" + serverName + ":" + serverPort;

        NonPoolingDriverDBConnectionManager cm = new NonPoolingDriverDBConnectionManager( "xxx", driverClassName, dbUrl, "sa", "sa", 20 );
        Connection conn = cm.getConnection();
        System.out.println( "jorå");
        int metaId = 1001;
        testCallToStoredProcedure( conn, "GetTextDocData", "1001" );
        conn.close();
        System.out.println( "jorå2");


        try {
            NonPoolingDriverDBConnectionManager cm2 = new NonPoolingDriverDBConnectionManager( "yyy", driverClassName, dbUrl, null, null, 20 );
            System.out.println( "jorå3");
            Connection conn2 = cm.getConnection();
            System.out.println( "jorå4");
            testCallToStoredProcedure( conn2, "GetTextDocData", "1001" );
            System.out.println( "jorå5");
        } catch( Exception ex ) {
            //ok
        }


        System.out.println( "jorå6");
        Connection conn3 = cm.getConnection();
        testCallToStoredProcedure( conn3, "GetTextDocData", "1001" );
        conn3.close();
    }

    static void testCallToStoredProcedure( Connection con, String procName, String param1 ) throws SQLException {
        CallableStatement cs = con.prepareCall("{call " + procName + "(?) }");
        cs.setString( 1, param1 );
        ResultSet rs = cs.executeQuery();
        while( rs.next() ) {
            String str = rs.getString( 1 );
            System.out.println( str );
        }
    }

    static String[] getTabelNames( DatabaseMetaData metaData ) throws SQLException {
        String[] types = {"TABLE"};
        ResultSet resultSet = metaData.getTables( null, null, "%", types );
        Collection c = new ArrayList();
        while( resultSet.next() ) {
            String tableName = resultSet.getString( 3 );
            c.add( tableName );
        }
        return (String[])c.toArray( new String[c.size()] );
    }

    static String[] getStoredProcedureNames( Connection conn ) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getProcedures( null, null, "%" );
        Collection procedureNames = new ArrayList();
        while( rs.next() ) {
            String dbProcedureName = rs.getString( 3 );
            procedureNames.add( dbProcedureName );
        }

        return (String[])procedureNames.toArray( new String[procedureNames.size()]);
    }

    static void log( String[] strArr ) {
        for( int i = 0; i < strArr.length; i++ ) {
            String str = strArr[i];
            log( str );
        }
    }

    static void log( String message ) {
        System.out.println( message );
    }
}