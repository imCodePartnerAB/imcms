package com.imcode.imcms.servlet.admin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 Adds a new document to a menu.
 Shows an empty metadata page, which calls SaveNewMeta
 */
public class AddDoc extends HttpServlet {

    static final String SESSION__DATA__IDENTIFIER = "AddDoc.session.data";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DocumentInformation.NewDocumentParentInformation newDocumentParentInformation = new DocumentInformation.NewDocumentParentInformation( request );

        DocumentInformation.addObjectToSessionAndSetSessionAttributeNameInRequest( "newDocumentParentInformation", newDocumentParentInformation,request, DocumentInformation.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);
        request.getRequestDispatcher( "/servlet/DocumentInformation" ).forward( request, response );
        return ;
    }
}
