package imcode.server.db;

import java.io.File ;
import java.io.IOException ;

public class DatabaseTestInitializer {

    private static final Integer SQLSERVER_PORT = new Integer(1433);
    private static final Integer MIMER_PORT = new Integer(1360);
    private static final Integer MYSQL_PORT = new Integer(3306);

    static final File FILE_PATH = new File("E:/backuppas/projekt/imcode2003/imCMS/1.3/sql/multipledatabases/") ;
    //private static final File FILE_PATH = new File("/home/kreiger/work/imCMS-MAIN/1.3/sql/multipledatabases") ;

    static DatabaseService static_initMimer() throws IOException {
        DatabaseService dbService = new MimerDatabaseService( "localhost", MIMER_PORT, "test", "sysadm", "admin", new Integer(20)  );
        //DatabaseService dbService = new MimerDatabaseService("localhost", MIMER_PORT, "imcmstest", "sysadm", "trexus", new Integer(20) );
        dbService.setupDatabaseWithTablesAndData( FILE_PATH );
        return dbService;
    }

    static DatabaseService static_initSqlServer() throws IOException {
        DatabaseService dbService = new MSSQLDatabaseService( "localhost", SQLSERVER_PORT, "test", "sa", "sa", new Integer(20)   );
        //DatabaseService dbService = new MSSQLDatabaseService( "ratatosk", SQLSERVER_PORT, "kreiger_imcmstest", "sa", "nonac", new Integer(20) );
        dbService.setupDatabaseWithTablesAndData( FILE_PATH );
        return dbService;
    }

    static DatabaseService static_initMySql() throws IOException {
        DatabaseService dbService = new MySQLDatabaseService( "localhost", MYSQL_PORT, "test", "root", "", new Integer(20) );
        //DatabaseService dbService = new MySQLDatabaseService( "localhost", MYSQL_PORT, "imcmstest", "root", "", new Integer(20) );
        dbService.setupDatabaseWithTablesAndData( FILE_PATH );
        return dbService;
    }

}
