<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<head>
<title>Delete a role named "Test role"</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    int textFieldIndexInDocument = 1;
    String newHtmlText = "<h2>Quotations</h2>";
    document.setHtmlTextField( textFieldIndexInDocument, newHtmlText ) ;

    textFieldIndexInDocument = 2;
    String newPlainText = "If we knew what it was we were doing, it would not be called research, would it? /Albert Einstein";
    document.setPlainTextField( textFieldIndexInDocument, newPlainText ) ;
%>
Done.