package com.imcode.imcms;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;

import imcode.server.Imcms;

import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Imcms Context Listener - must be specified first in web.xml.
 */
public class ImcmsContextListener implements ServletContextListener {

    private Logger logger;

	public void contextInitialized(ServletContextEvent evt) {
        ServletContext servletContext = evt.getServletContext();
        File path = new File(servletContext.getRealPath("/"));

        initLogger(servletContext, path);

        Imcms.setPath(path);
        Imcms.setServletContextEvent(evt);

        try {
            logger.info("Starting CMS.");
            Imcms.startCms();
            Imcms.setCmsMode();
        } catch (Exception e) {
            logger.error("Error starting CMS.", e);
            Imcms.setMaintenanceMode();
        }
	}

    
	public void contextDestroyed(ServletContextEvent evt) {
        Imcms.stopCms();
        LogManager.shutdown();
    }


    public void initLogger(ServletContext servletContext, File realContextPath) {
        //File configFile = new File(path, "WEB-INF/classes/log4j.xml");
        File configFile = new File(realContextPath, "WEB-INF/classes/log4j-debug.xml");

        try {
            System.setProperty("com.imcode.imcms.path", realContextPath.toString());
            DOMConfigurator.configure(configFile.toString());
        } catch (RuntimeException e ) {
            // TODO: Provide minimal logging configuration
            throw e;
        }

        logger = Logger.getLogger(getClass());
        logger.info("Servlet Engine: " + servletContext.getServerInfo());

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
            logger.info(systemPropertyName + ": " + System.getProperty(systemPropertyName));
        }
    }

}
