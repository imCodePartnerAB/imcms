package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
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

        Stack history = (Stack)req.getSession().getAttribute( "history" );
        boolean useNextToLastTextDocument = req.getParameter( "top" ) == null;
        int lastTextDocumentId = getLastTextDocumentFromHistory( history, useNextToLastTextDocument, imcref );

        if ( lastTextDocumentId != 0 ) {
            //	history.push(new Integer(meta_id)) ;
            req.getSession().setAttribute( "history", history );
            redirectToDocumentId( req, res, lastTextDocumentId );
        } else {
            redirectToDocumentId( req, res, imcref.getSystemData().getStartDocument() );
        }
    }

    private void redirectToDocumentId( HttpServletRequest request, HttpServletResponse response, int meta_id ) throws IOException {
        DocumentDomainObject document = Imcms.getServices().getDefaultDocumentMapper().getDocument( meta_id ) ;
        response.sendRedirect( Utility.getAbsolutePathToDocument( request, document ) );
    }

    public static int getLastTextDocumentFromHistory( Stack history, boolean useNextToLastTextDocument,
                                                      ImcmsServices imcref ) {
        int meta_id = 0;
        if ( null != history && !history.empty() ) {

            if ( useNextToLastTextDocument ) {
                // pop the first value from the history stack and true it away
                // because that is the current meta_id
                int tmp_meta_id = ( (Integer)history.peek() ).intValue();			// Get the top value
                boolean docTypeIsText = isTextDocument( imcref, tmp_meta_id );

                if ( docTypeIsText ) {			// If we are on a text_doc,
                    meta_id = ( (Integer)history.pop() ).intValue();			// Get the top value. If there are no more text_docs, we need to stay here.
                }
            }

            while ( !history.empty() ) {
                int tmp_meta_id = ( (Integer)history.pop() ).intValue();			// Get the top value
                boolean docTypeIsText = isTextDocument( imcref, tmp_meta_id );

                if ( docTypeIsText ) {
                    meta_id = tmp_meta_id;
                    break;
                }
            }
        }
        return meta_id;
    }

    private static boolean isTextDocument( ImcmsServices imcref, int tmp_meta_id ) {
        int doc_type;
        doc_type = imcref.getDocType( tmp_meta_id );	// Get the doc_type

        boolean docTypeIsText = doc_type == 1 || doc_type == 2;
        return docTypeIsText;
    }
}
