package com.imcode.imcms.servlet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Matcher;

public class ImcmsLog4jConfigListener implements ServletContextListener {

    // all occurrences of ${com.imcode.imcms.path} must be replaced with real WEB_APP root path.
    private static String WEBAPP_ROOT_RE = "(?i)\\$\\{\\s*com\\.imcode\\.imcms\\.path\\s*\\}";

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        File webappRoot = new File(servletContext.getRealPath("/"));
        File log4jConfFile = new File(webappRoot, "WEB-INF/conf/log4j.xml");

        if (!log4jConfFile.exists()) {
            throw new RuntimeException(String.format("Log4j configuration file %s does not exists.", log4jConfFile));
        }

        try {
            String webappRootPath = webappRoot.getCanonicalPath().replaceAll("\\\\", "/");
            String log4jConf = FileUtils
                    .readFileToString(log4jConfFile, "utf-8")
                    .replaceAll(WEBAPP_ROOT_RE, Matcher.quoteReplacement(webappRootPath));

            Reader log4jConfReader = new StringReader(log4jConf);

            new DOMConfigurator().doConfigure(log4jConfReader, LogManager.getLoggerRepository());
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading log4.xml file", e);
        }

        logEnvironment(servletContext, Logger.getLogger(getClass()));
    }


    public void contextDestroyed(ServletContextEvent event) {
        LogManager.shutdown();
    }


    public void logEnvironment(ServletContext servletContext, Logger logger) {
        logger.info("Servlet Engine: " + servletContext.getServerInfo());
        logger.info("System properties:");
        for (String name : System.getProperties().stringPropertyNames()) {
            logger.info("\t" + name + ": " + System.getProperty(name));
        }

        logger.info("Environment:");
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            logger.info("\t" + entry.getKey() + ": " + entry.getValue());
        }
    }
}
