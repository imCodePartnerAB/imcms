/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-mar-01
 * Time: 15:46:20
 */
package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.document.BrowserDocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

public class BrowserDocumentComposer extends HttpServlet {

    private static final String URL_I15D_PAGE__BROWSERDOC = "/jsp/docadmin/browser_document.jsp";

    public static final String PARAMETER_BUTTON__ADD_BROWSERS = "add_browsers";
    public static final String PARAMETER_BUTTON__OK = "ok";
    public static final String PARAMETER_BUTTON__CANCEL = "cancel";

    public final static String PARAMETER__BROWSERS = "browsers";
    public final static String PARAMETER_PREFIX__DESTINATION = "destination_";
    public static final String PARAMETER__DEFAULT_DESTINATION = "default_destination";

    public static final String REQUEST_ATTRIBUTE__ADDED_BROWSERS = "addedBrowsers";

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        String action = request.getParameter( DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION ) ;
        BrowserDocumentDomainObject document = (BrowserDocumentDomainObject)DocumentComposer.getObjectFromSessionWithKeyInRequest( request, DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME) ;
        Map addedBrowsers = document.getBrowserDocumentIdMap() ;
        if (null != request.getParameter( PARAMETER_BUTTON__ADD_BROWSERS)) {
            addedBrowsers = getAddedBrowsersFromRequest( request );
        }

        if (DocumentComposer.ACTION__PROCESS_NEW_DOCUMENT_INFORMATION.equalsIgnoreCase( action )
            || DocumentComposer.ACTION__EDIT_BROWSER_DOCUMENT.equalsIgnoreCase( action )
            || null != request.getParameter( PARAMETER_BUTTON__ADD_BROWSERS)) {
            UserDomainObject user = Utility.getLoggedOnUser( request );
            request.setAttribute( REQUEST_ATTRIBUTE__ADDED_BROWSERS, addedBrowsers );
            request.getRequestDispatcher( DocumentComposer.URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2()
                                          + URL_I15D_PAGE__BROWSERDOC ).forward( request, response );
        } else if ( null != request.getParameter( PARAMETER_BUTTON__OK ) ) {
            addedBrowsers = getAddedBrowsersFromRequest( request );
            for ( Iterator iterator = addedBrowsers.keySet().iterator(); iterator.hasNext(); ) {
                BrowserDocumentDomainObject.Browser browser = (BrowserDocumentDomainObject.Browser)iterator.next();
                Integer documentId = (Integer)addedBrowsers.get(browser) ;
                if (null != documentId) {
                    document.setBrowserDocumentId( browser, documentId.intValue() );
                }
            }
            request.getRequestDispatcher( "DocumentComposer" ).forward( request,response );
        } else if ( null != request.getParameter( PARAMETER_BUTTON__CANCEL ) ) {
            response.sendRedirect( "AdminDoc?meta_id="+document.getId() );
        }
    }

    private Map getAddedBrowsersFromRequest( HttpServletRequest request ) {
        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();

        Map addedBrowsers = new HashMap();

        addedBrowsers.put(BrowserDocumentDomainObject.Browser.DEFAULT, null) ;
        String[] addedBrowserIdStrings = request.getParameterValues( PARAMETER__BROWSERS );

        for ( int i = 0; null != addedBrowserIdStrings && i < addedBrowserIdStrings.length; i++ ) {
            int addedBrowserId = Integer.parseInt( addedBrowserIdStrings[i] );
            BrowserDocumentDomainObject.Browser browser = documentMapper.getBrowserById( addedBrowserId );
            addedBrowsers.put( browser, null );
        }

        Map parameterMap = request.getParameterMap();
        for ( Iterator iterator = parameterMap.keySet().iterator(); iterator.hasNext(); ) {
            String parameterName = (String)iterator.next();
            if ( parameterName.startsWith( PARAMETER_PREFIX__DESTINATION ) ) {
                int browserId = Integer.parseInt( StringUtils.substringAfter( parameterName, PARAMETER_PREFIX__DESTINATION ) );
                BrowserDocumentDomainObject.Browser browser = documentMapper.getBrowserById( browserId );
                Integer destinationDocumentId = null;
                try {
                    destinationDocumentId = Integer.valueOf( request.getParameter( parameterName ) );
                } catch ( NumberFormatException ignored ) {
                }
                addedBrowsers.put( browser, destinationDocumentId );
            }
        }

        return addedBrowsers;
    }

}