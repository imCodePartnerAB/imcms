package imcode.server.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The purpose of this class is to help JDBC drivers that lack Connection Pooling.
 * It does so by registrering with the DriverManager a new driver that has Connection Pooling that
 * in turn calls the non pooled driver.
 */
public class ConnectionPoolForNonPoolingDriver extends ConnectionPool {

    private DataSource dataSource;

    private final static Logger log = Logger.getLogger( imcode.server.db.ConnectionPoolForNonPoolingDriver.class.getName() );

    public ConnectionPoolForNonPoolingDriver( String driverClassName, String dbUrl, String userName, String password,
                                              int maxActiveConnections ) throws Exception {

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName( driverClassName );
        basicDataSource.setUsername( userName );
        basicDataSource.setPassword( password );
        basicDataSource.setUrl( dbUrl );
        basicDataSource.setMaxActive( maxActiveConnections );
        basicDataSource.setPoolPreparedStatements( true );


        dataSource = basicDataSource;

        Connection connection = getConnection();
        logDatabaseData( connection );
        connection.close();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void testConnectionAndLogResultToTheErrorLog() throws SQLException {
        getConnection().close();
        log.info( "Test Connection OK" );
    }

    private static void logDatabaseData( Connection connection ) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        log.info( "Database product version = " + metaData.getDatabaseProductVersion() );
    }

}
