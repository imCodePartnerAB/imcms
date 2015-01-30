package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocTextContainer;
import com.imcode.imcms.mapping.container.VersionRef;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Shadowgun on 23.12.2014.
 */
public class TextApiServlet extends HttpServlet {

    private ImcmsServices imcmsServices;

    public TextApiServlet() {
        imcmsServices = Imcms.getServices();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPut(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPut(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {

        UserDomainObject user = Imcms.getUser();
        String content = req.getParameter("content");
        String locale = req.getParameter("locale");
        int docId = Integer.parseInt(req.getParameter("meta"));
        TextDocumentDomainObject doc = imcmsServices.getDocumentMapper().getWorkingDocument(docId);

        TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)
                user.getPermissionSetFor(doc);

        // fixme: v4.
        if (!permissionSet.getEditTexts()) {
            //AdminDoc.adminDoc(documentId, user, request, res, getServletContext)
            return;
        }
        int textNo = Integer.parseInt(req.getParameter("no"));
        VersionRef versionRef = VersionRef.of(docId, DocumentVersion.WORKING_VERSION_NO);
        LoopEntryRef loopEntryRefOpt = null;
        String loopEntryRef;
        if (!StringUtils.isEmpty(loopEntryRef = req.getParameter("loopEntryRef".toLowerCase()))) {
            String[] items = loopEntryRef.split("_", 2);
            if (items.length > 1)
                loopEntryRefOpt = LoopEntryRef.of(Integer.parseInt(items[0]), Integer.parseInt(items[1]));
        }

        try {
            imcmsServices.getDocumentMapper().saveTextDocText(
                    TextDocTextContainer.of(
                            DocRef.of(versionRef, locale),
                            loopEntryRefOpt, textNo,
                            new TextDomainObject(content.trim(),
                                    TextDomainObject.TEXT_TYPE_HTML)
                    ), user);
        } catch (DocumentSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {

    }


}
