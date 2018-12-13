package com.imcode.imcms.servlet;

import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import imcode.server.Imcms;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class Version extends HttpServlet {

    private final static String VERSION_FILE = "/WEB-INF/version.txt";

    public static String getJavaVersion() {
        return System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
    }

    public static String getImcmsVersion(ServletContext servletContext) {
        try {
            try (Reader in = new InputStreamReader(servletContext.getResourceAsStream(VERSION_FILE))) {
                return "imCMS " + IOUtils.toString(in).trim();
            }
        } catch (Exception npe) {
            return "imCMS";
        }
    }

    public static String getDatabaseProductNameAndVersion() {
        return (String) Imcms.getServices().getDatabase().execute(new DatabaseCommand() {
            public Object executeOn(DatabaseConnection connection) throws DatabaseException {
                try {
                    DatabaseMetaData metaData = connection.getConnection().getMetaData();
                    return metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();
                } catch (SQLException e) {
                    throw DatabaseException.fromSQLException("", e);
                }
            }
        });
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String imcmsVersion = getImcmsVersion(getServletContext());
        String serverInfo = getServletContext().getServerInfo();
        String databaseProductNameAndVersion = getDatabaseProductNameAndVersion();
        String javaVersion = getJavaVersion();

        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println(imcmsVersion);
        out.println(javaVersion);
        out.println(serverInfo);
        out.println(databaseProductNameAndVersion);
    }
}
