<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentMapper = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    Document document = documentMapper.getDocument(documentId) ;
%>

<h3>Document information</h3>
Headline: "<%=document.getHeadline()%>"<br>
Menu text: "<%=document.getMenuText()%>"<br>
Menu picture: "<%=document.getMenuImageURL()%>"<br>
