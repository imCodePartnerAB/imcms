package com.imcode.imcms.servlet;

import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.mapping.DocumentMapper;
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

public class BackDoc extends HttpServlet {

    public static DocumentDomainObject getNextToLastTextDocumentFromHistory(Stack<Integer> history, ImcmsServices imcref) {
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument(history.pop()); // remove top document from stack ( this is current text document )

        if (null != history && !history.empty()) {
            while (!history.empty()) {
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
        ImcmsServices imcref = Imcms.getServices();
        Utility.setDefaultHtmlContentType(res);

        Stack<Integer> history = (Stack<Integer>) req.getSession().getAttribute("history");
        DocumentDomainObject lastTextDocument = getNextToLastTextDocumentFromHistory(history, imcref);

        if (null != lastTextDocument) {
            redirectToDocumentId(req, res, lastTextDocument.getId());
        } else {
            DocumentLanguages dls = imcref.getDocumentLanguages();
            Imcms.getUser().getDocGetterCallback().setLanguage(dls.getDefault(), true);
            redirectToDocumentId(req, res, imcref.getSystemData().getStartDocument());
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    private void redirectToDocumentId(HttpServletRequest request, HttpServletResponse response, int meta_id) throws IOException {
        DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(meta_id);
        response.sendRedirect(Utility.getAbsolutePathToDocument(request, document));
    }
}
