<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>


<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    int menuIndexInDocument = 1;
    TextDocument.Menu menu = document.getMenu(menuIndexInDocument) ;

    menu.addDocument(document) ;

%>
