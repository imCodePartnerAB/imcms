<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    document.setHeadline( "Test headline text");
    document.setMenuText( "Test menu text");
    document.setMenuImageURL("Test menu image url");

    // don't forget to save your changes!
    documentService.saveChanges( document );
%>
Done.

