<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int menuIndex = 1 ;
    private String makeLink(int documentId) {
        return "<a href=\"../servlet/GetDoc?meta_id="+ documentId +"\">document "+ documentId +"</a>" ;
    }

%>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.Menu menu = document.getMenu(menuIndex) ;

    Document[] documents = menu.getDocuments() ;
    if (documents.length > 0) {
        Document firstDocument = documents[0] ;
        menu.removeDocument(firstDocument);
        %>Removed the first document (<%= makeLink(firstDocument.getId()) %> with headline <b><%= firstDocument.getHeadline() %></b>)
        from menu <%= menuIndex %> on <%= makeLink(document.getId()) %>.<br><%
    } else {
        %>There are no documents in menu <%= menuIndex %> on <%= makeLink(documentId) %>.<%
    }
%>


