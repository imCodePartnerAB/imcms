package imcode.server;

import imcode.server.user.UserDomainObject;
import imcode.util.CachingFileLoader;
import imcode.util.Prefs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import javax.servlet.ServletContextEvent;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.Database;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import com.imcode.imcms.ImcmsMode;
import com.imcode.imcms.ImcmsFilter;
import com.imcode.imcms.servlet.ApplicationContextListener;

public class Imcms {
	
    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

    private final static Logger LOG = Logger.getLogger(Imcms.class.getName());

    /** Application services. */
    private static ImcmsServices services;

    private static BasicDataSource apiDataSource;
    private static BasicDataSource dataSource;

    /** Imcms full deployment path. */
    private static File path;


    // TODO: begin refactor
    public static ServletContextEvent servletContextEvent;
    private static ImcmsMode mode = ImcmsMode.MAINTENANCE;
    private static ImcmsFilter filter;
    private static Exception appStartupEx;

    private static ApplicationContextListener applicationContextListener;
    private static ContextLoaderListener springContextLoaderListener;

    /** Springframework web application context. */
    public static WebApplicationContext webApplicationContext;
    // TODO: end refactor 


	/** When running in application mode a user bound to a current thread in the ApplicationFilter. */
	private final static ThreadLocal<UserDomainObject> users = new ThreadLocal<UserDomainObject>();


    /**
     * Can not be instantiated directly;
     */
    private Imcms() {}

    /**
     * Returns application services.
     */
    public static ImcmsServices getServices() {
        // TODO: assign some proxy implementation - null ex might be thrown.
        return services;
    }

    /**
     * TODO: Refactor.
     */
    public static void startApplication() throws StartupException {
        setAppStartupEx(null);
        
        try {
            springContextLoaderListener = new ContextLoaderListener();
            springContextLoaderListener.contextInitialized(servletContextEvent);
            webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext());

            applicationContextListener = new ApplicationContextListener();
            applicationContextListener.contextInitialized(servletContextEvent);

            services = createApplicationServices();
        } catch (Exception e) {
            setAppStartupEx(e);

            try {
                if (springContextLoaderListener != null)
                    springContextLoaderListener.contextDestroyed(servletContextEvent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            throw new StartupException("" +
                    "Application could not be started. Please see the log file in WEB-INF/logs/ for details.", e);
        }
    }

    public static void setPath(File path) {
        Imcms.path = path;
    }

    public static File getPath() {
        return path;
    }


    private static ImcmsServices createApplicationServices() throws Exception {
        Properties serverprops = getServerProperties();
        LOG.debug("Creating main DataSource.");
        Database database = createDatabase(serverprops);
        LocalizedMessageProvider localizedMessageProvider = new CachingLocalizedMessageProvider(new ImcmsPrefsLocalizedMessageProvider());
                
        final CachingFileLoader fileLoader = new CachingFileLoader();
        return new DefaultImcmsServices(database, serverprops, localizedMessageProvider, fileLoader, new DefaultProcedureExecutor(database, fileLoader));
    }

    private static Database createDatabase(Properties serverprops) {
        dataSource = createDataSource(serverprops);
        return new DataSourceDatabase(dataSource);
    }

    public synchronized static DataSource getApiDataSource() {
        if ( null == apiDataSource ) {
            Properties serverprops = getServerProperties();
            LOG.debug("Creating API DataSource.");
            apiDataSource = createDataSource(serverprops);
        }
        return apiDataSource;
    }

    public static Properties getServerProperties() {
        try {
            return Prefs.getProperties(SERVER_PROPERTIES_FILENAME);
        } catch ( IOException e ) {
            LOG.fatal("Failed to initialize imCMS", e);
            throw new UnhandledException(e);
        }
    }

    private static BasicDataSource createDataSource(Properties props) {

        String jdbcDriver = props.getProperty("JdbcDriver");
        String jdbcUrl = props.getProperty("JdbcUrl");
        String user = props.getProperty("User");
        String password = props.getProperty("Password");
        int maxConnectionCount = Integer.parseInt(props.getProperty("MaxConnectionCount"));

        LOG.debug("JdbcDriver = " + jdbcDriver);
        LOG.debug("JdbcUrl = " + jdbcUrl);
        LOG.debug("User = " + user);
        LOG.debug("MaxConnectionCount = " + maxConnectionCount);

        return createDataSource(jdbcDriver, jdbcUrl, user, password, maxConnectionCount);
    }

    public synchronized static void restartApplication() {
        stopApplication();
        startApplication();
    }

    public static void stopApplication() {
        if ( null != apiDataSource ) {
            try {
                LOG.debug("Closing API DataSource.");
                apiDataSource.close();
            } catch ( SQLException e ) {
                LOG.error(e, e);
            }
        }
        if ( null != dataSource ) {
            try {
                LOG.debug("Closing main DataSource.");
                dataSource.close();
            } catch ( SQLException e ) {
                LOG.error(e, e);
            }
        }
        
        Prefs.flush();

        try {
            if (springContextLoaderListener != null)
                springContextLoaderListener.contextDestroyed(servletContextEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        services = null;
    }

    private static void logDatabaseVersion(BasicDataSource basicDataSource) throws SQLException {
        Connection connection = basicDataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        LOG.info("Database product version = " + metaData.getDatabaseProductVersion());
        connection.close();
    }

    public static BasicDataSource createDataSource(String jdbcDriver, String jdbcUrl,
                                                   String user, String password,
                                                   int maxConnectionCount) {
        try {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(jdbcDriver);
            basicDataSource.setUsername(user);
            basicDataSource.setPassword(password);
            basicDataSource.setUrl(jdbcUrl);

            basicDataSource.setMaxActive(maxConnectionCount);
            basicDataSource.setMaxIdle(maxConnectionCount);
            basicDataSource.setDefaultAutoCommit(true);
            basicDataSource.setPoolPreparedStatements(true);
            basicDataSource.setTestOnBorrow(true);
            basicDataSource.setValidationQuery("select 1");

            logDatabaseVersion(basicDataSource);

            return basicDataSource;
        } catch ( SQLException ex ) {
            String message = "Could not connect to database "+ jdbcUrl + " with driver " + jdbcDriver + ": "+ex.getMessage()+" Error code: "
                             + ex.getErrorCode() + " SQL GroupData: " + ex.getSQLState();
            LOG.fatal(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    public static class StartupException extends RuntimeException {

        public StartupException(String message, Exception e) {
            super(message, e) ;
        }
    }
    
    public static void setUser(UserDomainObject user) {
    	users.set(user);
    }
    
    public static UserDomainObject getUser() {
    	return users.get();
    }


    public static ImcmsMode setMode(ImcmsMode mode) {
        Imcms.mode = mode;

        if (filter != null) {
            filter.updateDelegateFilter();
        }

        return mode;
    }

    public static ImcmsMode setMaintenanceMode() {
        return setMode(ImcmsMode.MAINTENANCE);
    }


    public static ImcmsMode setApplicationMode() {
        return setMode(ImcmsMode.APPLICATION);
    }

    public static ImcmsMode getMode() {
        return mode;
    }

    public static void setFilter(ImcmsFilter filter) {
        Imcms.filter = filter;
    }

    public static Exception getAppStartupEx() {
        return appStartupEx;
    }

    public static void setAppStartupEx(Exception appStartupEx) {
        Imcms.appStartupEx = appStartupEx;
    }

    /*
    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static void setServletContext(ServletContext servletContext) {
        Imcms.servletContext = servletContext;
    }
    */

	public static Object getSpringBean(String beanName) {
		if (webApplicationContext == null) {
			//log.error("WebApplicationContext is not set.");
			throw new NullPointerException("WebApplicationContext is not set.");
		}

		return webApplicationContext.getBean(beanName);
	}
}
