package imcode.server.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The purpose of this class is to help JDBC drivers that lack Connection Pooling.
 * It does so by registrering with the DriverManager a new driver that has Connection Pooling that
 * in turn calls the non pooled driver.
 */
public class ConnectionPoolForNonPoolingDriver extends ConnectionPool {

    private BasicDataSource dataSource;

    private final static Logger log = Logger.getLogger( ConnectionPoolForNonPoolingDriver.class );

    public ConnectionPoolForNonPoolingDriver( String driverClassName, String dbUrl, String userName, String password,
                                              int maxActiveConnections ) throws Exception {

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName( driverClassName );
        basicDataSource.setUsername( userName );
        basicDataSource.setPassword( password );
        basicDataSource.setUrl( dbUrl );

        basicDataSource.setMaxActive( maxActiveConnections );
        basicDataSource.setMaxIdle( maxActiveConnections );

        basicDataSource.setPoolPreparedStatements( true );

        dataSource = basicDataSource;

        logDatabaseVersion();
    }

    private void logDatabaseVersion() throws SQLException {
        Connection connection = getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        log.info( "Database product version = " + metaData.getDatabaseProductVersion() );
        connection.close();
    }

    public synchronized Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
