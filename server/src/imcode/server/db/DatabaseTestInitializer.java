package imcode.server.db;

import java.io.File ;
import java.io.IOException ;

public class DatabaseTestInitializer {

    private static final int SQLSERVER_PORT = 1433;
    private static final int MIMER_PORT = 1360;
    private static final int MYSQL_PORT = 3306;

    private static final File FILE_PATH = new File("E:/backuppas/projekt/imcode2003/imCMS/1.3/sql/multipledatabases/") ;
    //private static final File FILE_PATH = new File("/home/kreiger/work/imCMS-MAIN/1.3/sql/multipledatabases") ;

    static DatabaseService static_initMimer() throws IOException {
        DatabaseService dbService = new MimerDatabaseService("localhost", MIMER_PORT, "test", "sysadm", "admin", FILE_PATH );
//        DatabaseService dbService = new MimerDatabaseService("localhost", MIMER_PORT, "imcmstest", "sysadm", "trexus", FILE_PATH );        dbService.setupDatabaseWithTablesAndData();
        dbService.createTestData();
        return dbService;
    }

    static DatabaseService static_initSqlServer() throws IOException {
        DatabaseService dbService = new SQLServerDatabaseService( "localhost", SQLSERVER_PORT, "test", "sa", "sa", FILE_PATH );
//        DatabaseService dbService = new SQLServerDatabaseService( "ratatosk", SQLSERVER_PORT, "kreiger_imcmstest", "sa", "nonac", FILE_PATH );        dbService.setupDatabaseWithTablesAndData();
        dbService.createTestData();
        return dbService;
    }

    static DatabaseService static_initMySql() throws IOException {
        DatabaseService dbService = new MySQLDatabaseServer( "localhost", MYSQL_PORT, "test", "root", "", FILE_PATH );
//        DatabaseService dbService = new MySQLDatabaseServer( "localhost", MYSQL_PORT, "imcmstest", "root", "", FILE_PATH );        dbService.setupDatabaseWithTablesAndData();
        dbService.createTestData();
        return dbService;
    }

}
