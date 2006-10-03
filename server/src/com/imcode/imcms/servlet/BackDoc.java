package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Stack;

public class BackDoc extends HttpServlet {

    /**
     * doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        // Find the start-page

        Utility.setDefaultHtmlContentType( res );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        Stack history = (Stack)user.get( "history" );
        boolean useNextToLastTextDocument = req.getParameter( "top" ) == null;
        int lastTextDocumentId = getLastTextDocumentFromHistory( history, useNextToLastTextDocument, imcref );

        if ( lastTextDocumentId != 0 ) {
            //	history.push(new Integer(meta_id)) ;
            user.put( "history", history );
            redirectToDocumentId( req, res, lastTextDocumentId );
        } else {
            redirectToDocumentId( req, res, imcref.getSystemData().getStartDocument() );
        }
    }

    private void redirectToDocumentId( HttpServletRequest request, HttpServletResponse response, int meta_id ) throws IOException {
        DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument( meta_id ) ;
        response.sendRedirect( Utility.getAbsolutePathToDocument( request, document ) );
    }

    public static int getLastTextDocumentFromHistory( Stack history, boolean useNextToLastTextDocument,
                                                      ImcmsServices imcref ) {
        int meta_id = 0;
        if ( null != history && !history.empty() ) {

            if ( useNextToLastTextDocument ) {
                // pop the first value from the history stack and true it away
                // because that is the current meta_id
                int tmp_meta_id = ( (Integer)history.peek() ).intValue();	// Get the top value
                if ( isTextDocument( imcref, tmp_meta_id ) ) {			    // If we are on a text_doc,
                    meta_id = ( (Integer)history.pop() ).intValue();		// Get the top value. If there are no more text_docs, we need to stay here.
                }
            }

            while ( !history.empty() ) {
                int tmp_meta_id = ( (Integer)history.pop() ).intValue();			// Get the top value
                if ( isTextDocument( imcref, tmp_meta_id ) ) {
                    meta_id = tmp_meta_id;
                    break;
                }
            }
        }
        return meta_id;
    }

    private static boolean isTextDocument( ImcmsServices imcref, int tmp_meta_id ) {
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( tmp_meta_id );
        return DocumentTypeDomainObject.TEXT == document.getDocumentType();
    }
}
