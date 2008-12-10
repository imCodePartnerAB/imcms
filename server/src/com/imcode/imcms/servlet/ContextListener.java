package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.util.Prefs;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class ContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        final File realPathToWebApp = new File(servletContext.getRealPath("/"));

        File configPath = new File(realPathToWebApp, "WEB-INF/conf");
        Prefs.setConfigPath(configPath);

        configureLogging(servletContext, realPathToWebApp, configPath);

        Logger log = Logger.getLogger(ContextListener.class);
        try {
            Imcms.setPath(realPathToWebApp);
            log.info("imCMS initialized.");
        } catch (RuntimeException e) {
            log.fatal("Failed to initialize imCMS.",e);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Logger log = Logger.getLogger(ContextListener.class);
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

    private void configureLogging(ServletContext servletContext, File root, File configPath) {
        System.setProperty("com.imcode.imcms.path", root.toString());
        File configFile = new File(configPath, "log4j.xml");
        DOMConfigurator.configure(configFile.toString());
        Logger log = Logger.getLogger(ContextListener.class);
        log.info("Logging started");
        logPlatformInfo(servletContext, log);
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

}
