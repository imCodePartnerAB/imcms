<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001;
    int menuIndexInDocument = 1;
    int documentToBeAddedId = 1003;
%>

<html>
<body>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    TextDocument.Menu menu = document.getMenu(menuIndexInDocument) ;

    TextDocument documentToBeAdded = documentService.getTextDocument(documentToBeAddedId) ;

    try {
        menu.addDocument(documentToBeAdded) ;
        %>Done. See <a href="../servlet/GetDoc?meta_id=<%= documentId %>">document <%= documentId %></a>.<%
    } catch (DocumentAlreadyInMenuException daim) {
        %>Menu <%= menuIndexInDocument %> on document <%= documentId %> already contains document <%= documentToBeAddedId %>.<%
    }
%>
</body>
</html>
