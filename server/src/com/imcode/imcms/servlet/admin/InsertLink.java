package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.servlet.DocumentFinder;
import com.imcode.imcms.flow.DispatchCommand;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import imcode.server.document.DocumentDomainObject;

public class InsertLink extends HttpServlet {

    private static final String REQUEST_ATTRIBUTE__DOCUMENT_ID = InsertLink.class.getName()+".documentId";
    private static final String LINK_RETURN_PATH = "/imcms/xinha/plugins/ImcmsIntegration/return_link.jsp";

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        DocumentFinder documentFinder = new DocumentFinder();

        final DocumentRetrievalCommand documentRetrievalCommand = new DocumentRetrievalCommand();
        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                DocumentDomainObject document = documentRetrievalCommand.getDocument();
                request.setAttribute(REQUEST_ATTRIBUTE__DOCUMENT_ID, null != document ? ""+document.getId() : "");
                request.getRequestDispatcher(LINK_RETURN_PATH).forward(request, response);
            }
        };
        documentFinder.setCancelCommand(returnCommand);
        documentFinder.setSelectDocumentCommand(documentRetrievalCommand);
        documentFinder.forward(request, response);
    }

    public static String getLink(HttpServletRequest request) {
        return (String) request.getAttribute(REQUEST_ATTRIBUTE__DOCUMENT_ID);
    }

    private static class DocumentRetrievalCommand implements DocumentFinder.SelectDocumentCommand {

        private DocumentDomainObject document;

        public void selectDocument(DocumentDomainObject document) {
            this.document = document;
        }

        public DocumentDomainObject getDocument() {
            return document;
        }
    }
}
