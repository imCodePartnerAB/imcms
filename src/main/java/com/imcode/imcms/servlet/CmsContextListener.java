package com.imcode.imcms.servlet;

import imcode.util.Prefs;
import imcode.server.Imcms;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import com.imcode.imcms.dao.LanguageDao;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.I18nException;
import com.imcode.imcms.schema.SchemaUpgrade;

/**
 * Cms context listener.
 *
 * Springframework context listener should be initialized first.
 *
 * TODO: Refactor this listener to spring bean.
 */
public class CmsContextListener implements ServletContextListener {

    private Logger logger;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger = Logger.getLogger(getClass());
        // TODO: Refactor dublicated in Imcms.initPrefs
        File configPath = new File(Imcms.getPath(), "WEB-INF/conf");
        Prefs.setConfigPath(configPath);

        ServletContext servletContext =  servletContextEvent.getServletContext();

    	upgradeDatabaseSchema();
        initI18nSupport(servletContext);

    }


    public void contextDestroyed(ServletContextEvent servletContextEvent) {}


    /**
     * Initializes I18N support.
     * Reads languages from the database.
     * Please note that one (and only one) language in the database table i18n_languages must be set as default.
     */
	private void initI18nSupport(ServletContext servletContext) {
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
    private void upgradeDatabaseSchema() {
        // TODO: lock file
        File confXMLFile = new File(Imcms.getPath(), "WEB-INF/conf/schema-upgrade.xml");
        File confXSDFile = new File(Imcms.getPath(), "WEB-INF/conf/schema-upgrade.xsd");
        File scriptsDir = new File(Imcms.getPath(), "WEB-INF/sql");

        final SchemaUpgrade schemaUpgrade = new SchemaUpgrade(confXMLFile, confXSDFile, scriptsDir);

        // todo: replace with datasource get connection.
        HibernateTemplate template = (HibernateTemplate)Imcms.getSpringBean("hibernateTemplate");

        template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                schemaUpgrade.upgrade(session.connection());

                return null;
            }
        });
    }
}