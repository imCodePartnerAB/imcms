<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int includeIndex = 1 ;
%>

Include number <%= includeIndex %> in document <%= documentId %> has the content:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    Document includedDocument = document.getInclude(includeIndex) ;
    if (null != includedDocument) {
    %>Document <%= includedDocument.getId() %> which has the headline <%= includedDocument.getHeadline() %><%
    } else {
    %>No include <%= includeIndex %> in document <%= documentId %><%
    }
%>
// end content
