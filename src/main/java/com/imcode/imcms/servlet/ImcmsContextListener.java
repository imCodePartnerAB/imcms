package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.util.Prefs;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.WebApplicationContext;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.I18nException;
import com.imcode.imcms.util.SchemaVersionChecker;
import com.imcode.imcms.util.SchemaVersionCheckerException;

/**
 * Initializes system.
 */
public class ImcmsContextListener implements ServletContextListener {

    private Logger logger;

	/**
	 * Detects real and configuration paths of the application.
	 * Logs environment and application properties.
	 */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        final File realPathToWebApp = new File(servletContext.getRealPath("/"));

        File configPath = new File(realPathToWebApp, "WEB-INF/conf");
        Prefs.setConfigPath(configPath);

        System.setProperty("com.imcode.imcms.path", realPathToWebApp.toString());

        logger = Logger.getLogger(ImcmsContextListener.class);

        logger.info("Logging started");
        logPlatformInfo(servletContext, logger);
        
        try {
            Imcms.setPath(realPathToWebApp);
            logger.info("imCMS initialized.");
        } catch (RuntimeException e) {
            logger.fatal("Failed to initialize imCMS.",e);
            throw e;
        }

    	initSpringframework(servletContext);
    	initI18nSupport(servletContext);
    	checkSchemaVersion(servletContext);

        ImcmsServices service = Imcms.getServices();
    }

    /**
     * Stops the application and shuts down logging.
     * 
     * TODO: investigate why logging is being shut-down - is this really necessary?.    
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Logger log = Logger.getLogger(ImcmsContextListener.class);
        log.debug("Stopping imCMS.");
        try {
            Imcms.stop();
        } catch(Exception e) {
            log.error("Stopping imCMS failed.", e);
        }
        log.debug("Shutting down logging.");
        try {
            LogManager.shutdown();
        } catch (Exception e) {
            System.err.println(e);
        }
        try {
            LogFactory.releaseAll();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void logPlatformInfo(ServletContext application, Logger log) {

        log.info("Servlet Engine: " + application.getServerInfo());
        String[] systemPropertyNames = new String[] {
                "java.version",
                "java.vendor",
                "java.class.path",
                "os.name",
                "os.arch",
                "os.version",
        };
        for ( int i = 0; i < systemPropertyNames.length; i++ ) {
            String systemPropertyName = systemPropertyNames[i];
            log.info(systemPropertyName + ": " + System.getProperty(systemPropertyName));
        }

    }


    /**
     * Initializes I18N support.
     * Reads languages from the database.
     * Please note that one (and only one) language in the database table i18n_languages must be set as default.
     */
	private void initI18nSupport(ServletContext servletContext) {
    	logger.info("Initializing i18n support.");

    	LanguageDao languageDao = (LanguageDao) Imcms.getServices().getSpringBean("languageDao");
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

        // TODO: create const for "imcms.i18n.hosts"
        Map<String, I18nLanguage> i18nHosts = new HashMap<String, I18nLanguage>();
        servletContext.setAttribute("imcms.i18n.hosts", i18nHosts);
        
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


    private void checkSchemaVersion(ServletContext servletContext) {
    	SchemaVersionChecker checker = (SchemaVersionChecker)Imcms.getServices()
    		.getSpringBean("schemaVersionChecker");

    	try {
    		String expectedSchemaVersion = Imcms.getServerProperties().getProperty("db.schema.version", "");
    		checker.checkSchemaVersion(expectedSchemaVersion);
    	} catch (SchemaVersionCheckerException e) {
    		logger.fatal(e);
    		throw e;
    	}
    }

    
    private void initSpringframework(ServletContext servletContext) {
    	logger.info("Initializing springframework web application context.");
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(
    			servletContext);

    	Imcms.webApplicationContext = wac; 
    }


    /**
     * Upgrades database schema.
     */
    /*
    private void upgradeDatabaseSchemaIfNecessary() {
        try {
            RT.load("com/imcode/imcms/schema_upgrade");
        } catch (Exception e) {
            throw new RuntimeException("Clojure RT.", e);    
        }

        Var var = RT.var("com.imcode.imcms.schema-upgrade", "upgrade");
        var.invoke(dataSource);
    }
    */
}