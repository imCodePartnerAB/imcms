<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    Document document = documentService.getDocument(documentId) ;
%>

<h3>Document information</h3>
Headline: "<%=document.getHeadline()%>"<br>
Menu text: "<%=document.getMenuText()%>"<br>
Menu picture: "<%=document.getMenuImageURL()%>"<br>
