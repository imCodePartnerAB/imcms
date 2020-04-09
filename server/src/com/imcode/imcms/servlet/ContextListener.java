package com.imcode.imcms.servlet;

import com.imcode.imcms.domain.services.core.ServerSettingsChecker;
import imcode.server.Imcms;
import imcode.util.Prefs;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.Map;

public class ContextListener implements ServletContextListener {

    /**
     * Became accessible only after {@link #configureLogging} method calling
     */
    private static Logger log;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        final File realPathToWebApp = new File(servletContext.getRealPath("/"));

        File configPath = new File(realPathToWebApp, "WEB-INF/conf");
        Prefs.setConfigPath(configPath);

        configureLogging(realPathToWebApp.toString(), configPath);
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
        try {
            LogFactory.releaseAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureLogging(String root, File configPath) {
        System.setProperty("com.imcode.imcms.path", root);
        File configFile = new File(configPath, "log4j.xml");
        DOMConfigurator.configure(configFile.toString());

        log = Logger.getLogger(ContextListener.class);
        log.info("Logging started");
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
