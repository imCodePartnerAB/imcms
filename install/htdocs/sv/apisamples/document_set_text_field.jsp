<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>


<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    int textFieldIndexInDocument = 1;
    String newHtmlText = "<a href=\"../login/\">Log in!</a><br><a href=\"../apisamples/\">API-samples.</a>";
    document.setHtmlTextField( textFieldIndexInDocument, newHtmlText ) ;

    textFieldIndexInDocument = 2;
    String newPlainText = "If we knew what it was we were doing, it would not be called research, would it? -- Albert Einstein";
    document.setPlainTextField( textFieldIndexInDocument, newPlainText ) ;
%>
Done.