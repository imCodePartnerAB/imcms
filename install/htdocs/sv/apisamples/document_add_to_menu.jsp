<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int DOCUMENT_ID = 1001;
    int MENU_INDEX_IN_DOCUMENT = 1;
    int DOCUMENT_TO_BE_ADDED_ID = 1002;
%>

<html>
<body>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    TextDocument document = documentService.getTextDocument(DOCUMENT_ID) ;

    TextDocument.Menu menu = document.getMenu(MENU_INDEX_IN_DOCUMENT) ;

    TextDocument documentToBeAdded = documentService.getTextDocument(DOCUMENT_TO_BE_ADDED_ID) ;
    if( null != documentToBeAdded ) {
        try {
            menu.addDocument(documentToBeAdded) ;
            %>Done. See <a href="../servlet/GetDoc?meta_id=<%= DOCUMENT_ID %>">document <%= DOCUMENT_ID %></a>.<%
        } catch (DocumentAlreadyInMenuException daim) {
            %>Menu <%= MENU_INDEX_IN_DOCUMENT %> on document <%= DOCUMENT_ID %> already contains document <%= DOCUMENT_TO_BE_ADDED_ID %>.<%
        }
    } else { %>
        No document with id <%= DOCUMENT_TO_BE_ADDED_ID %> exists. Please add a document before running this page again.
  <%}
%>
</body>
</html>
