package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.DocumentPageFlow;
import imcode.server.WebAppGlobalConstants;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.MultipartHttpServletRequest;
import imcode.util.Utility;
import org.apache.commons.lang.NotImplementedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DocumentPageFlowDispatcher extends HttpServlet {

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    public void doPost( HttpServletRequest r, HttpServletResponse response ) throws ServletException, IOException {
        r.setCharacterEncoding( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
        MultipartHttpServletRequest request = new MultipartHttpServletRequest( r );

        UserDomainObject user = Utility.getLoggedOnUser( request );

        DocumentPageFlow pageFlow = DocumentPageFlow.fromRequest( request );
        if ( null != pageFlow ) {
            DocumentDomainObject document = pageFlow.getDocument();
            if ( null != document && user.canEdit( document ) ) {
                pageFlow.dispatch( request, response );

                if ( !response.isCommitted() ) {
                    throw new NotImplementedException( pageFlow.getClass() );
                }
            }
        } else {
            Utility.redirectToStartDocument( request, response );
        }
    }

}