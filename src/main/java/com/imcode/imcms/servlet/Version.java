package com.imcode.imcms.servlet;

import com.imcode.db.DatabaseException;
import com.imcode.imcms.db.Schema;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Move version info to the manifest file
 */
public class Version extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(Version.class);

    private final static String VERSION_FILE = "/WEB-INF/version.txt";
    private final static String DB_SCHEMA_FILE = "schema.xml";

    public static String getImcmsVersion(ServletContext servletContext) {
        try {
            try (Reader in = new InputStreamReader(servletContext.getResourceAsStream(VERSION_FILE))) {
                return IOUtils.toString(in).trim();
            }
        } catch (Exception e) {
            final String errMessage = "Error reading imcms version.";
            logger.error(errMessage, e);
            return errMessage;
        }
    }

    public static String getRequiredDbVersion() {
        try {
            return Schema.fromInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(DB_SCHEMA_FILE)).getVersion().toString();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser(req);

        final ServletContext servletContext = getServletContext();
        String imcmsVersion = getImcmsVersion(servletContext);
        String serverInfo = servletContext.getServerInfo();
        String databaseProductNameAndVersion = getDatabaseProductNameAndVersion();
        String javaVersion = getJavaVersion();

        res.setHeader("Cache-Control", "no-store");
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println(imcmsVersion);

        if (user.isSuperAdmin()) {
            out.println(javaVersion);
            out.println(serverInfo);
            out.println("Required DB schema version: " + StringUtils.defaultString(getRequiredDbVersion(), "N/A"));
            out.println(databaseProductNameAndVersion);
        }
    }

    private String getJavaVersion() {
        return System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
    }

    private String getDatabaseProductNameAndVersion() {
        return Imcms.getServices().getDatabase().execute(connection -> {
            try {
                DatabaseMetaData metaData = connection.getConnection().getMetaData();
                return metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();
            } catch (SQLException e) {
                throw DatabaseException.fromSQLException("", e);
            }
        });
    }
}
