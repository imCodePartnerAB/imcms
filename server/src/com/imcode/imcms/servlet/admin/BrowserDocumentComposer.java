/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-mar-01
 * Time: 15:46:20
 */
package com.imcode.imcms.servlet.admin;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.BrowserDocumentDomainObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class BrowserDocumentComposer extends HttpServlet {

    public static final String PARAMETER_BUTTON__ADD_BROWSERS = "add_browsers";
    public static final String PARAMETER_BUTTON__OK = "ok";
    public static final String PARAMETER_BUTTON__CANCEL = "cancel";

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if (null != request.getParameter( PARAMETER_BUTTON__ADD_BROWSERS )) {
            request.getRequestDispatcher( DocumentComposer.URL_I15D_PAGE__BROWSERDOC ).forward( request, response );            
        } else if (null != request.getParameter( PARAMETER_BUTTON__OK )) {

        } else if (null != request.getParameter( PARAMETER_BUTTON__CANCEL )) {

        }
    }

}