package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class ImcmsInitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        File webappRoot = new File(servletContext.getRealPath("/"));
        Imcms.setPath(webappRoot);
        Imcms.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
    }


    public void contextDestroyed(ServletContextEvent event) {
    }
}
