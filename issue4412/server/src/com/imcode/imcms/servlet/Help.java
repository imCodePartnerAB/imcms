package com.imcode.imcms.servlet;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

public class Help extends HttpServlet{

    private final static Logger log = Logger.getLogger( GetDoc.class.getName() );

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException {
        doGet( req, res );
    }

    /**
     * doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        String helpDocName = req.getParameter("name");
        String lang = req.getParameter("lang") ;
        Properties helpProp ;
        int helpdoc;

        try{
            helpProp = loadProperties("/WEB-INF/help/helpdoc_" + lang + ".properties");

            try{
                helpdoc = Integer.parseInt(helpProp.getProperty(helpDocName));
                res.sendRedirect( helpdoc + "" );
            }catch(NumberFormatException e) {
                log.error("Help link error, help doc name: " + helpDocName + ",  no corresponding meta_id found.");
                res.sendError(HttpStatus.SC_NOT_FOUND);
            }
        }catch(NullPointerException e ) {
            log.error("Help link error, help doc name: " + helpDocName +  ", 'lang' parameter is wrong.");
            res.sendError(HttpStatus.SC_NOT_FOUND);
        }

    }

    private Properties loadProperties( String path ) throws IOException {
        final InputStream resourceAsStream = getServletConfig().getServletContext().getResourceAsStream(path);
        Properties properties = new Properties();
        properties.load(resourceAsStream);
        return properties;
    }
}
