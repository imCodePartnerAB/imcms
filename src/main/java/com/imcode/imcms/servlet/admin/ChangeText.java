package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChangeText extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int docId = Integer.parseInt(request.getParameter("meta_id"));
        int textNo = Integer.parseInt(request.getParameter("txt"));

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        TextDocumentDomainObject document = documentMapper.getDocument(docId);

        UserDomainObject user = Utility.getLoggedOnUser(request);
        TextDocumentPermissionSetDomainObject permissionSetFor = (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(document);
        if (!permissionSetFor.getEditTexts()) {
            AdminDoc.adminDoc(docId, user, request, response, getServletContext());
            return;
        }

        request.getRequestDispatcher("/WEB-INF/imcms/jsp/docadmin/text/change_text.jsp").forward(request, response);
    }
}