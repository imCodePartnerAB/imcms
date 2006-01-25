package com.imcode.imcms.flow;

import imcode.server.Imcms;
import imcode.server.document.BrowserDocumentDomainObject;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EditBrowserDocumentPageFlow extends EditDocumentPageFlow {

    public final static String REQUEST_PARAMETER__BROWSERS = "browsers";
    public final static String REQUEST_PARAMETER_PREFIX__DESTINATION = "destination_";
    public static final String URL_I15D_PAGE__BROWSERDOC = "/jsp/docadmin/browser_document.jsp";
    public static final String REQUEST_PARAMETER__ADD_BROWSERS_BUTTON = "add_browsers";
    public static final String REQUEST_ATTRIBUTE__ADDED_BROWSERS = "addedBrowsers";

    public EditBrowserDocumentPageFlow( BrowserDocumentDomainObject document,
                                        DispatchCommand returnCommand,
                                        SaveDocumentCommand saveDocumentCommand ) {
        super( document, returnCommand, saveDocumentCommand );
    }

    protected void dispatchFromEditPage( HttpServletRequest request, HttpServletResponse response, String page ) throws IOException, ServletException {
        Map addedBrowsers = getAddedBrowsersFromRequest( request, Imcms.getServices().getDocumentMapper() );
        if ( null != request.getParameter( REQUEST_PARAMETER__ADD_BROWSERS_BUTTON ) ) {
            forwardToBrowserDocumentPageWithBrowsersMap( request, response, addedBrowsers );
        }
    }

    protected void dispatchOkFromEditPage( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        Map addedBrowsers = getAddedBrowsersFromRequest( request, Imcms.getServices().getDocumentMapper() );
        Utility.removeNullValuesFromMap( addedBrowsers );
        BrowserDocumentDomainObject browserDocument = (BrowserDocumentDomainObject)document;
        browserDocument.setBrowserDocuments( addedBrowsers );
    }

    protected void dispatchToFirstPage( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        BrowserDocumentDomainObject browserDocument = (BrowserDocumentDomainObject)document;
        forwardToBrowserDocumentPageWithBrowsersMap( request, response, browserDocument.getBrowserDocumentIdMap() );
    }

    public void forwardToBrowserDocumentPageWithBrowsersMap( HttpServletRequest request,
                                                             HttpServletResponse response, Map browsersMap ) throws ServletException, IOException {
        UserDomainObject user = Utility.getLoggedOnUser( request );
        request.setAttribute( REQUEST_ATTRIBUTE__ADDED_BROWSERS, browsersMap );
        request.getRequestDispatcher( URL_I15D_PAGE__PREFIX + user.getLanguageIso639_2() + URL_I15D_PAGE__BROWSERDOC ).forward( request, response );
    }

    Map getAddedBrowsersFromRequest( HttpServletRequest request, DocumentMapper documentMapper ) {

        Map addedBrowsers = new HashMap();

        addedBrowsers.put( BrowserDocumentDomainObject.Browser.DEFAULT, null );
        String[] addedBrowserIdStrings = request.getParameterValues( REQUEST_PARAMETER__BROWSERS );

        for ( int i = 0; null != addedBrowserIdStrings && i < addedBrowserIdStrings.length; i++ ) {
            int addedBrowserId = Integer.parseInt( addedBrowserIdStrings[i] );
            BrowserDocumentDomainObject.Browser browser = documentMapper.getBrowserById( addedBrowserId );
            addedBrowsers.put( browser, null );
        }

        Map parameterMap = request.getParameterMap();
        for ( Iterator iterator = parameterMap.keySet().iterator(); iterator.hasNext(); ) {
            String parameterName = (String)iterator.next();
            if ( parameterName.startsWith( REQUEST_PARAMETER_PREFIX__DESTINATION ) ) {
                int browserId = Integer.parseInt( StringUtils.substringAfter( parameterName, REQUEST_PARAMETER_PREFIX__DESTINATION ) );
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
