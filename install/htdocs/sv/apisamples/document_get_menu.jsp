<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int menuIndex = 1 ;

%>

The headline of the first document in menu number <%= menuIndex %> on <%= documentId %> is:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.Menu menu = document.getMenu(menuIndex) ;

    Document[] documents = menu.getDocuments() ;
    if (documents.length > 0) {
        %><b><%= menu.getDocuments()[0].getHeadline() %></b><%
    } else {
        %>there are no documents in menu <%= menuIndex %> on <%= documentId %><%
    }
%>
<br>
// end content