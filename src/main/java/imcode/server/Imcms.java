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
import java.util.List;
import java.util.HashMap;

import javax.sql.DataSource;
import javax.servlet.ServletContext;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Session;
import org.hibernate.HibernateException;

import com.imcode.db.DataSourceDatabase;
import com.imcode.db.Database;
import com.imcode.imcms.db.DefaultProcedureExecutor;
import com.imcode.imcms.util.l10n.CachingLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessageProvider;
import com.imcode.imcms.schema.SchemaUpgrade;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nException;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.servlet.ImcmsMode;
import com.imcode.imcms.servlet.ImcmsFilter;

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
    private static ServletContext servletContext;
    private static ImcmsMode mode = ImcmsMode.MAINTENANCE;
    private static ImcmsFilter imcmsFilter;
    private static Exception cmsStartupEx;

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
            beforeStart();


            
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
        imcmsFilter.updateDelegateFilter();

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


    // clear
    public static void setCmsStartupEx(Exception cmsStartupEx) {
        Imcms.cmsStartupEx = cmsStartupEx;
    }


    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static void setServletContext(ServletContext servletContext) {
        Imcms.servletContext = servletContext;
    }


	public static Object getSpringBean(String beanName) {
		return webApplicationContext.getBean(beanName);
	}


    public static void beforeStart() {
        File configPath = new File(path, "WEB-INF/conf");
        Prefs.setConfigPath(configPath);

    	upgradeDatabaseSchema();
        initI18nSupport();        
    }

    public static Map<String, I18nLanguage> getI18nHosts() {
        return i18nHosts;
    }

    public static void setI18nHosts(Map<String, I18nLanguage> i18nHosts) {
        Imcms.i18nHosts = i18nHosts;
    }



    /**
     * Initializes I18N support.
     * Reads languages from the database.
     * Please note that one (and only one) language in the database table i18n_languages must be set as default.
     */
	private static void initI18nSupport() {
    	logger.info("Initializing i18n support.");

    	LanguageDao languageDao = (LanguageDao) Imcms.getSpringBean("languageDao");
    	List<I18nLanguage> languages = languageDao.getAllLanguages();

    	if (languages.size() == 0) {
    		String msg = "I18n configuration error. Database table i18n_languages must contain at least one record.";
    		logger.fatal(msg);
    		throw new I18nException(msg);
    	}

        int defaultLanguageRecordCount = CollectionUtils.countMatches(languages, new Predicate() {
            public boolean evaluate(Object language) {
                return ((I18nLanguage)language).isDefault();
            }
        });

        if (defaultLanguageRecordCount == 0) {
    		String msg = "I18n configuration error. Default language is not set.";
    		logger.fatal(msg);
    		throw new I18nException(msg);
        } else if (defaultLanguageRecordCount > 1) {
            String msg = "I18n configuration error. Only one language must be set default.";
            logger.fatal(msg);
            throw new I18nException(msg);
        }

        I18nLanguage defaultLanguage = languageDao.getDefaultLanguage();

    	I18nSupport.setDefaultLanguage(defaultLanguage);
    	I18nSupport.setLanguages(languages);

    	servletContext.setAttribute("defaultLanguage", defaultLanguage);
    	servletContext.setAttribute("languages", languages);

        // Read "virtual" hosts mapped to languages.
    	String prefix = "i18n.host.";
    	int prefixLength = prefix.length();
        Properties properties = Imcms.getServerProperties();

        Map<String, I18nLanguage> i18nHosts = new HashMap<String, I18nLanguage>();
        Imcms.setI18nHosts(i18nHosts);

    	for (Map.Entry entry: properties.entrySet()) {
    		String key = (String)entry.getKey();

    		if (!key.startsWith(prefix)) {
    			continue;
    		}

			String languageCode = key.substring(prefixLength);
			String value = (String)entry.getValue();

    		logger.info("I18n configurtion: language code [" + languageCode + "] mapped to host(s) [" + value + "].");

			I18nLanguage language = I18nSupport.getByCode(languageCode);

			if (language == null) {
				String msg = "I18n configuration error. Language with code [" + languageCode + "] is not defined in database.";
        		logger.fatal(msg);
        		throw new I18nException(msg);
			}

			String hosts[] = value.split("[ \\t]*,[ \\t]*");

			for (String host: hosts) {
				i18nHosts.put(host.trim(), language);
			}
    	}
	}



    /**
     * Upgrades database schema if necessary.
     */
    private static void upgradeDatabaseSchema() {
        File confXMLFile = new File(Imcms.getPath(), "WEB-INF/conf/schema-upgrade.xml");
        File confXSDFile = new File(Imcms.getPath(), "WEB-INF/conf/schema-upgrade.xsd");
        File scriptsDir = new File(Imcms.getPath(), "WEB-INF/sql");

        if (!confXMLFile.isFile()) {
            throw new RuntimeException("Schema upgrade XML file '" + confXMLFile.getAbsolutePath() + "' does not exist.");
        }


        if (!confXSDFile.isFile()) {
            throw new RuntimeException("Schema upgrade XSD file '" + confXSDFile.getAbsolutePath() + "' does not exist.");
        }


        if (!scriptsDir.isDirectory()) {
            throw new RuntimeException("Schema diff scripts dir '" + scriptsDir.getAbsolutePath() + "' does not exist.");
        }


        String xml = SchemaUpgrade.validateAndGetContent(confXMLFile, confXSDFile);
        final SchemaUpgrade schemaUpgrade = new SchemaUpgrade(xml, scriptsDir);

        // todo: replace with datasource get connection.
        HibernateTemplate template = (HibernateTemplate)Imcms.getSpringBean("hibernateTemplate");

        template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                schemaUpgrade.upgrade(session.connection());

                return null;
            }
        });
    }

    public static ImcmsFilter getImcmsFilter() {
        return imcmsFilter;
    }

    public static void setImcmsFilter(ImcmsFilter imcmsFilter) {
        Imcms.imcmsFilter = imcmsFilter;
    }

    public static WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    public static void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        Imcms.webApplicationContext = webApplicationContext;
    }
}