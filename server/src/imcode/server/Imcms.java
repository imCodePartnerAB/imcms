package imcode.server;

import imcode.server.db.Database;
import imcode.server.db.impl.DefaultDatabase;
import imcode.server.db.impl.DefaultProcedureExecutor;
import imcode.util.Prefs;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Properties;

public class Imcms {

    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    private final static Logger log = Logger.getLogger( imcode.server.Imcms.class.getName() );
    private static ImcmsServices services ;
    private static BasicDataSource apiDataSource ;
    private static BasicDataSource dataSource;

    private Imcms() {}

    public synchronized static ImcmsServices getServices() {
        if (null == services) {
            services = createServices();
        }
        return services;
    }

    private synchronized static ImcmsServices createServices() {
        Properties serverprops = getServerProperties();
        dataSource = createDataSource( serverprops );
        QueryRunner queryRunner = new QueryRunner( dataSource );
        Database database = new DefaultDatabase( queryRunner, new DefaultProcedureExecutor(queryRunner) );
        DefaultImcmsServices services = new DefaultImcmsServices( database, serverprops );
        return services ;
    }

    public synchronized static DataSource getApiDataSource() {
        if (null == apiDataSource) {
            Properties serverprops = getServerProperties();
            apiDataSource = createDataSource( serverprops );
        }
        return apiDataSource ;
    }

    private static Properties getServerProperties() {
        try {
            return Prefs.getProperties( SERVER_PROPERTIES_FILENAME );
        } catch ( IOException e ) {
            log.fatal( "Failed to initialize imCMS", e);
            throw new UnhandledException(e) ;
        }
    }

    private static BasicDataSource createDataSource( Properties props ) {

        String jdbcDriver = props.getProperty( "JdbcDriver" );
        String jdbcUrl = props.getProperty( "JdbcUrl" );
        String user = props.getProperty( "User" );
        String password = props.getProperty( "Password" );
        int maxConnectionCount = Integer.parseInt( props.getProperty( "MaxConnectionCount" ) );

        log.debug( "JdbcDriver = " + jdbcDriver );
        log.debug( "JdbcUrl = " + jdbcUrl );
        log.debug( "User = " + user );
        log.debug( "MaxConnectionCount = " + maxConnectionCount );

        return createDataSource( jdbcDriver, jdbcUrl, user, password, maxConnectionCount );
    }

    public synchronized static void restart() {
        try {
            apiDataSource.close();
            dataSource.close();
        } catch ( SQLException e ) {
            throw new UnhandledException( e );
        }
        Prefs.flush();
        services = createServices() ;
    }

    private static void logDatabaseVersion( BasicDataSource basicDataSource ) throws SQLException {
        Connection connection = basicDataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        log.info( "Database product version = " + metaData.getDatabaseProductVersion() );
        connection.close();
    }

    public static BasicDataSource createDataSource( String jdbcDriver, String jdbcUrl,
                                               String user, String password,
                                               int maxConnectionCount ) {
        try {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName( jdbcDriver );
            basicDataSource.setUsername( user );
            basicDataSource.setPassword( password );
            basicDataSource.setUrl( jdbcUrl );

            basicDataSource.setMaxActive( maxConnectionCount );
            basicDataSource.setMaxIdle( maxConnectionCount );

            logDatabaseVersion(basicDataSource);

            return basicDataSource ;
        } catch ( Exception ex ) {
            log.fatal( "Failed to create connection pool. Url: " + jdbcUrl + " Driver: " + jdbcDriver, ex );
            throw new RuntimeException( ex );
        }
    }

}
