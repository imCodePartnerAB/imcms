<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
%>

The first field in document <%= documentId %> has content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.Menu menu = document.getMenu(1) ;
    out.println(menu.getLinks()) ;
%>
// end content