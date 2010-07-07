package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ImcmsContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        File path = new File(servletContext.getRealPath("/"));

        Imcms.setPath(path);
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

}
