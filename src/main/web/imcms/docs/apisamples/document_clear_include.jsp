<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    int includeIndexInDocument = 1;

    document.setInclude(includeIndexInDocument,null) ;
    documentService.saveChanges( document );

%>
Done.
