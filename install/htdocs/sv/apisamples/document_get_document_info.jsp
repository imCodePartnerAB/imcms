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
    Document document = documentService.getTextDocument(documentId) ;
%>

<h3>Document information</h3>
Headline: "<%=document.getHeadline()%>"<br>
Menu text: "<%=document.getMenuText()%>"<br>
Menu picture: "<%=document.getMenuImageURL()%>"<br>
</body>
</html>
