<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    int textFieldIndexInDocument = 1;
    String newHtmlText = "";
    document.setHtmlTextField( textFieldIndexInDocument, newHtmlText ) ;

    textFieldIndexInDocument = 2;
    String newPlainText = "";
    document.setPlainTextField( textFieldIndexInDocument, newPlainText ) ;

    documentService.saveChanges( document );
%>
Done.