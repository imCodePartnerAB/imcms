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
import java.util.Map;

import javax.sql.DataSource;
import javax.servlet.ServletContextEvent;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

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
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.servlet.CmsContextListener;

/**
 * Shared fields and mathods; can not be instantiated.
 */
public class Imcms {
	
    private static final String SERVER_PROPERTIES_FILENAME = "server.properties";
    public static final String ASCII_ENCODING = "US-ASCII";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String DEFAULT_ENCODING = UTF_8_ENCODING;

    private static Logger logger = Logger.getLogger(Imcms.class);

    /** Cms services. */
    private static ImcmsServices cmsServices;

    private static BasicDataSource apiDataSource;
    private static BasicDataSource dataSource;

    /** Imcms full deployment path. */
    private static File path;


    // TODO: begin refactor
    /*
     * Temp workaround. Servlet Context Event is required to initialize Cms and Spring context listeners.
     */
    private static ServletContextEvent servletContextEvent;
    private static ImcmsMode mode = ImcmsMode.MAINTENANCE;
    private static ImcmsFilter imcmsFilter;
    private static Exception cmsStartupEx;

    private static CmsContextListener CmsContextListener;
    private static ContextLoaderListener springContextLoaderListener;
    private static Map<String, I18nLanguage> i18nHosts;

    /** Springframework web application context. */
    public static WebApplicationContext webApplicationContext;
    // TODO: end refactor 


	/** When running in cms mode a user bound to a current thread in the CmsFilter. */
	private final static ThreadLocal<UserDomainObject> cmsUsers = new ThreadLocal<UserDomainObject>();


    /** Can not be instantiated directly. */
    private Imcms() {}

    /**
     * Returns application services.
     */
    public static ImcmsServices getServices() {
        // TODO: assign some proxy implementation - null ex might be thrown.
        return cmsServices;
    }

    /**
     * TODO: Refactor.
     */
    public static void startCms() throws StartupException {
        setCmsStartupEx(null);

        try {
            initCmsPrefs();

            springContextLoaderListener = new ContextLoaderListener();
            springContextLoaderListener.contextInitialized(servletContextEvent);
            webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext());

            CmsContextListener = new CmsContextListener();
            CmsContextListener.contextInitialized(servletContextEvent);

            cmsServices = createApplicationServices();
        } catch (Exception e) {
            logger.error(e, e);
            setCmsStartupEx(e);

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
        logger.debug("Creating main DataSource.");
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
            logger.debug("Creating API DataSource.");
            apiDataSource = createDataSource(serverprops);
        }
        return apiDataSource;
    }

    public static Properties getServerProperties() {
        try {
            return Prefs.getProperties(SERVER_PROPERTIES_FILENAME);
        } catch ( IOException e ) {
            logger.fatal("Failed to initialize imCMS", e);
            throw new UnhandledException(e);
        }
    }

    private static BasicDataSource createDataSource(Properties props) {

        String jdbcDriver = props.getProperty("JdbcDriver");
        String jdbcUrl = props.getProperty("JdbcUrl");
        String user = props.getProperty("User");
        String password = props.getProperty("Password");
        int maxConnectionCount = Integer.parseInt(props.getProperty("MaxConnectionCount"));

        logger.debug("JdbcDriver = " + jdbcDriver);
        logger.debug("JdbcUrl = " + jdbcUrl);
        logger.debug("User = " + user);
        logger.debug("MaxConnectionCount = " + maxConnectionCount);

        return createDataSource(jdbcDriver, jdbcUrl, user, password, maxConnectionCount);
    }

    public synchronized static void restartCms() {
        stopCms();
        startCms();
    }


    // TODO - print stack trace to imcms logger not app logger.
    public static void stopCms() {
        if ( null != apiDataSource ) {
            try {
                logger.debug("Closing API DataSource.");
                apiDataSource.close();
            } catch ( SQLException e ) {
                logger.error(e, e);
            }
        }
        
        if ( null != dataSource ) {
            try {
                logger.debug("Closing main DataSource.");
                dataSource.close();
            } catch ( SQLException e ) {
                logger.error(e, e);
            }
        }
        
        Prefs.flush();

        try {
            if (springContextLoaderListener != null)
                springContextLoaderListener.contextDestroyed(servletContextEvent);
        } catch (Exception e) {
           logger.error(e, e);
        }

        cmsServices = null;
    }

    private static void logDatabaseVersion(BasicDataSource basicDataSource) throws SQLException {
        Connection connection = basicDataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        logger.info("Database product version = " + metaData.getDatabaseProductVersion());
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
            logger.fatal(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    public static class StartupException extends RuntimeException {

        public StartupException(String message, Exception e) {
            super(message, e) ;
        }
    }
    
    public static void setUser(UserDomainObject user) {
    	cmsUsers.set(user);
    }
    
    public static UserDomainObject getUser() {
    	return cmsUsers.get();
    }


    public static ImcmsMode setMode(ImcmsMode mode) {
        Imcms.mode = mode;

        if (imcmsFilter != null) {
            imcmsFilter.updateDelegateFilter();
        }

        return mode;
    }

    public static ImcmsMode setMaintenanceMode() {
        return setMode(ImcmsMode.MAINTENANCE);
    }


    public static ImcmsMode setCmsMode() {
        return setMode(ImcmsMode.CMS);
    }

    public static ImcmsMode getMode() {
        return mode;
    }


    /**
     * Invoked by ImcmsFilter when its init method is called.
     *
     * @param imcmsFilter
     */
    public static void setImcmsFilter(ImcmsFilter imcmsFilter) {
        Imcms.imcmsFilter = imcmsFilter;
    }

    public static Exception getCmsStartupEx() {
        return cmsStartupEx;
    }

    // clear
    public static void setCmsStartupEx(Exception cmsStartupEx) {
        Imcms.cmsStartupEx = cmsStartupEx;
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


    public static void initCmsPrefs() {
        File configPath = new File(path, "WEB-INF/conf");
        Prefs.setConfigPath(configPath);        
    }


    public static ServletContextEvent getServletContextEvent() {
        return servletContextEvent;
    }

    public static void setServletContextEvent(ServletContextEvent servletContextEvent) {
        Imcms.servletContextEvent = servletContextEvent;
    }

    public static Map<String, I18nLanguage> getI18nHosts() {
        return i18nHosts;
    }

    public static void setI18nHosts(Map<String, I18nLanguage> i18nHosts) {
        Imcms.i18nHosts = i18nHosts;
    }
}