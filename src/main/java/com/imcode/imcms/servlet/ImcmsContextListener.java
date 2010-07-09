package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ImcmsContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        File path = new File(servletContext.getRealPath("/"));

        logEnvironment(servletContext);

        Imcms.setPath(path);
        Imcms.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
    }

    
    public void contextDestroyed(ServletContextEvent event) {}


    public void logEnvironment(ServletContext servletContext) {
        Logger logger = Logger.getLogger(getClass());
        
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
