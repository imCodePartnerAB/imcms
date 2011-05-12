package com.imcode.imcms.servlet;

import com.imcode.imcms.api.DefaultDocGetterCallback;
import com.imcode.imcms.api.DocGetterCallback;
import com.imcode.imcms.api.Params;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.util.Utility;

import java.io.IOException;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.mapping.DocumentMapper;

public class BackDoc extends HttpServlet {

    public static class HistoryElement {
        public final Integer docId;

        public final DocGetterCallback docGetterCallback;

        public HistoryElement(Integer docId, DocGetterCallback docGetterCallback) {
            this.docId = docId;
            this.docGetterCallback = docGetterCallback;
        }
    }

    /**
     * doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();
        Utility.setDefaultHtmlContentType( res );

        Stack<HistoryElement> history = (Stack<HistoryElement>)req.getSession().getAttribute( "history" );
        DocumentDomainObject lastTextDocument = getNextToLastTextDocumentFromHistory( history, imcref );

        if (null != lastTextDocument ) {
            redirectToDocumentId( req, res, lastTextDocument.getId() );
        } else {
            Params params = new Params(Imcms.getUser(), Imcms.getI18nSupport().getDefaultLanguage(), Imcms.getI18nSupport().getDefaultLanguage());
            DocGetterCallback callback = new DefaultDocGetterCallback(params);
            Imcms.getUser().setDocGetterCallback(callback);
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

    // todo: refactor
    public static DocumentDomainObject getNextToLastTextDocumentFromHistory( Stack<HistoryElement> history, ImcmsServices imcref ) {
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        HistoryElement he = history.pop();
        Imcms.getUser().setDocGetterCallback(he.docGetterCallback);
        DocumentDomainObject document = documentMapper.getDocument(he.docId); // remove top document from stack ( this is current text document )

        while (!history.empty()) {
            he = history.pop();
            Imcms.getUser().setDocGetterCallback(he.docGetterCallback);
            document = documentMapper.getDocument(he.docId);
            if (isTextDocument( document)) {
                break;
            }
        }

        return document;
    }

    private static boolean isTextDocument(DocumentDomainObject document) {
        return DocumentTypeDomainObject.TEXT == document.getDocumentType();
    }

}
