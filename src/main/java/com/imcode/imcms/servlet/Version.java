package com.imcode.imcms.servlet;

import com.imcode.imcms.db.Schema;
import com.vaadin.sass.internal.util.StringUtil;
import imcode.server.Imcms;

import java.io.*;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.imcode.db.DatabaseCommand;
import com.imcode.db.DatabaseConnection;
import com.imcode.db.DatabaseException;
import org.apache.commons.lang3.StringUtils;

/**
 * Move version info to the manifest file
 */
public class Version extends HttpServlet {

    private final static String VERSION_FILE = "/WEB-INF/version.txt";

    private final static String DB_SCHEMA_FILE = "schema.xml";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String imcmsVersion = getImcmsVersion();
        String serverInfo = getServletContext().getServerInfo();
        String databaseProductNameAndVersion = getDatabaseProductNameAndVersion();
        String javaVersion = getJavaVersion();

        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println(imcmsVersion);
        out.println(javaVersion);
        out.println(serverInfo);
        out.println("Required DB schema version: " + StringUtils.defaultString(getRequiredDbVersion(), "N/A"));
        out.println(databaseProductNameAndVersion);
    }

    private String getJavaVersion() {
        return System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
    }

    private String getImcmsVersion() {
        try {
            Reader in = new InputStreamReader(getServletContext().getResourceAsStream(VERSION_FILE));
            try {
                return "imCMS " + IOUtils.toString(in).trim();
            } finally {
                in.close();
            }
        } catch (Exception npe) {
            return "imCMS";
        }
    }

    private String getRequiredDbVersion() {
        try {
            return Schema.load(getClass().getClassLoader().getResource(DB_SCHEMA_FILE)).version().toString();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static String getImcmsVersion(ServletContext servletContext) {
        try {
            Reader in = new InputStreamReader(servletContext.getResourceAsStream(VERSION_FILE));
            try {
                return "imCMS " + IOUtils.toString(in).trim();
            } finally {
                in.close();
            }
        } catch (Exception npe) {
            return "imCMS";
        }
    }

    private String getDatabaseProductNameAndVersion() {
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
}
