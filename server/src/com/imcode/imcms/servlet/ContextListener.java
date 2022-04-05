package com.imcode.imcms.servlet;

import com.imcode.imcms.domain.services.core.ServerSettingsChecker;
import imcode.server.Imcms;
import imcode.util.Prefs;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;

public class ContextListener implements ServletContextListener {

    /**
     * Became accessible only after {@link #configureLogging} method calling
     */
    private static Logger log;
	private static final String WEB_APP_PATH_REGEX = "(?i)\\$\\{\\s*WEB_APP_PATH\\s*}";

	public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        final File realPathToWebApp = new File(servletContext.getRealPath("/"));

        File configPath = new File(realPathToWebApp, "WEB-INF/conf");
        Prefs.setConfigPath(configPath);

        configureLogging(realPathToWebApp, configPath);
        ServerSettingsChecker.check();
        logPlatformInfo(servletContext.getServerInfo());

        try {
            Imcms.setPath(realPathToWebApp);
            log.info("imCMS initialized.");
        } catch (RuntimeException e) {
            log.fatal("Failed to initialize imCMS.", e);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.debug("Stopping imCMS.");

        try {
            Imcms.stop();
        } catch (Exception e) {
            log.error("Stopping imCMS failed.", e);
        }

        log.debug("Shutting down logging.");

        try {
            LogManager.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureLogging(File root, File configPath) {
        File configFile = new File(configPath, "log4j.xml");
	    updateLog4jConfigFile(root, configFile);
		Configurator.initialize(null, ConfigurationSource.fromUri(configFile.toURI()));

        log = LogManager.getLogger(ContextListener.class);
        log.info("Logging started");
    }

	private void updateLog4jConfigFile(File webappRealPath, File log4jConfig) {
		try {
			final String webappRootPath = webappRealPath.getCanonicalPath().replaceAll("\\\\", "/");
			final String log4jConfigData = FileUtils
					.readFileToString(log4jConfig, StandardCharsets.UTF_8)
					.replaceAll(WEB_APP_PATH_REGEX, Matcher.quoteReplacement(webappRootPath));

			FileUtils.write(log4jConfig, log4jConfigData);
		} catch (IOException e) {
			throw new RuntimeException("Cannot update log4j configuration file", e);
		}
	}

    private void logPlatformInfo(String serverInfo) {
        log.info("Servlet Engine: " + serverInfo);
        log.info("System properties:");

        for (String name : System.getProperties().stringPropertyNames()) {
            log.info(name + ": " + System.getProperty(name));
        }

        log.info("Environment:");

        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            log.info(entry.getKey() + ": " + entry.getValue());
        }
    }

}
