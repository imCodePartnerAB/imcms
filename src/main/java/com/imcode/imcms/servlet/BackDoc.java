package com.imcode.imcms.servlet;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Deprecated
public class BackDoc extends HttpServlet {

    private static final long serialVersionUID = 416759093238960093L;

    public static DocumentDomainObject getNextToLastTextDocumentFromHistory(DocumentHistory history, ImcmsServices imcmsServices) {
        final DocumentMapper documentMapper = imcmsServices.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument(history.pop());
        // remove top document from history ( this is current text document )

        if (!history.isEmpty()) {
            while (!history.isEmpty()) {
                document = documentMapper.getDocument(history.pop());
                if (Utility.isTextDocument(document)) {
                    break;
                }
            }
        }

        return document;
    }

    /**
     * doGet()
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Utility.setDefaultHtmlContentType(res);

        final ImcmsServices imcmsServices = Imcms.getServices();
        final DocumentHistory history = DocumentHistory.from(req.getSession());
        final DocumentDomainObject lastTextDocument = getNextToLastTextDocumentFromHistory(history, imcmsServices);

        if (null != lastTextDocument) {
            redirectToDocumentId(req, res, lastTextDocument.getId());
        } else {
            Imcms.getUser().getDocGetterCallback().setLanguage(Imcms.getServices().getLanguageService().getDefaultLanguage());
            redirectToDocumentId(req, res, imcmsServices.getSystemData().getStartDocument());
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    private void redirectToDocumentId(HttpServletRequest request, HttpServletResponse response, int meta_id) throws IOException {
        final DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(meta_id);
        response.sendRedirect(Utility.getAbsolutePathToDocument(request, document));
    }
}
