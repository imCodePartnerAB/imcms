<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001;
    int menuIndex = 1;
    int documentToBeAddedId = 1002;
%>

<html>
<body>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    TextDocument.Menu menu = document.getMenu(menuIndex) ;

    Document documentToBeAdded = documentService.getDocument(documentToBeAddedId) ;
    if( null != documentToBeAdded ) {
        try {
            menu.addDocument(documentToBeAdded) ;
            documentService.saveChanges( document );
            %>Done. See <a href="document_get_menu.jsp">document_get_menu.jsp</a>.<%
        } catch (DocumentAlreadyInMenuException daim) {
            %>Menu <%= menuIndex %> on document <%= documentId %> already contains document <%= documentToBeAddedId %>.<%
        }
    } else { %>
        No document with id <%= documentToBeAddedId %> exists. Please add a document before running this page again.
  <%}
%>
</body>
</html>
