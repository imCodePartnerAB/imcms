package com.imcode.imcms.servlet;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;

public class ImcmsLog4j2ConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        File webappRoot = new File(servletContext.getRealPath("/"));
        Logger logger = initLog4j(webappRoot);

        logEnvironment(servletContext, logger);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        LogManager.shutdown();
    }

    Logger initLog4j(File webappRoot) {
        // all occurrences of ${com.imcode.imcms.path} must be replaced with real WEB_APP root path.
	    final String WEBAPP_ROOT_RE = "(?i)\\$\\{\\s*com\\.imcode\\.imcms\\.path\\s*\\}";
	    final File log4jConfFile = new File(webappRoot, "WEB-INF/conf/log4j2.xml");

        if (!log4jConfFile.exists()) {
            throw new RuntimeException(String.format("Log4j configuration file %s does not exists.", log4jConfFile));
        }

        try {
	        final String webappRootPath = webappRoot.getCanonicalPath().replaceAll("\\\\", "/");
	        final String log4jConfigData = FileUtils
			        .readFileToString(log4jConfFile, "utf-8")
			        .replaceAll(WEBAPP_ROOT_RE, Matcher.quoteReplacement(webappRootPath));
	        FileUtils.write(log4jConfFile, log4jConfigData, Charset.defaultCharset());

	        Configurator.reconfigure(log4jConfFile.toURI());
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading log4j2.xml file", e);
        }

        return LogManager.getLogger(getClass());
    }


    void logEnvironment(ServletContext servletContext, Logger logger) {
        logger.info("Servlet Engine: " + servletContext.getServerInfo());
        logger.info("System properties:");
        System.getProperties().forEach((key, value) -> logger.info("\t" + key + ": " + value));

        logger.info("Environment:");
        System.getenv().forEach((key, value) -> logger.info("\t" + key + ": " + value));
    }
}
