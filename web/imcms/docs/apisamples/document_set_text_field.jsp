<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<body>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    int textFieldIndexInDocument = 1;
    String newHtmlText = "<a href=\""+request.getContextPath()+"/login/\">Log in!</a><br><a href=\""+request.getContextPath()+"/imcms/docs/apisamples/\">API-samples.</a>";
    document.setHtmlTextField( textFieldIndexInDocument, newHtmlText ) ;

    textFieldIndexInDocument = 2;
    String newPlainText = "If we knew what it was we were doing, it would not be called research, would it? -- Albert Einstein";
    document.setPlainTextField( textFieldIndexInDocument, newPlainText ) ;

    documentService.saveChanges( document );
%>
Done. See <a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= documentId %>">document <%= documentId %></a>.
</body>
</html>
