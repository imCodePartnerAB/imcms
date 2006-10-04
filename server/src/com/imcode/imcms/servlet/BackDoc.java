package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Stack;

import com.imcode.imcms.mapping.DocumentMapper;

public class BackDoc extends HttpServlet {

    /**
     * doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        Utility.setDefaultHtmlContentType( res );

        Stack history = (Stack)req.getSession().getAttribute( "history" );
        DocumentDomainObject lastTextDocument = getNextToLastTextDocumentFromHistory( history, imcref );

        if (null != lastTextDocument ) {
            req.getSession().setAttribute( "history", history );
            redirectToDocumentId( req, res, lastTextDocument.getId() );
        } else {
            redirectToDocumentId( req, res, imcref.getSystemData().getStartDocument() );
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req,res);
    }

    private void redirectToDocumentId( HttpServletRequest request, HttpServletResponse response, int meta_id ) throws IOException {
        DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument( meta_id ) ;
        response.sendRedirect( Utility.getAbsolutePathToDocument( request, document ) );
    }

    public static DocumentDomainObject getNextToLastTextDocumentFromHistory( Stack history, ImcmsServices imcref ) {

        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument((Integer) history.pop() ); // remove top document from stack ( this is current text document )

        if ( null != history && !history.empty() ) {

            while ( !history.empty() ) {
                document = documentMapper.getDocument( (Integer)history.pop() );
                if ( isTextDocument( document ) ) {
                    break;
                }
            }
        }
        return document;
    }

    private static boolean isTextDocument(DocumentDomainObject document) {
        return DocumentTypeDomainObject.TEXT == document.getDocumentType();
    }

}
