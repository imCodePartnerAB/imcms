<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    Document document = documentService.getTextDocument(documentId) ;
%>

<h3>Document information</h3>
<pre>
Headline: "<%=document.getHeadline()%>"
Menu text: "<%=document.getMenuText()%>"
Menu picture: "<%=document.getMenuImageURL()%>"
Language: "<%=document.getLanguage()%>"
Creator: <a href="mailto:<%=document.getCreator().getEmailAddress()%>"><%=document.getCreator().getLoginName()%></a>
</pre>
