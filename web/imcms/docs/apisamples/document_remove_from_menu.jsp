<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int menuIndex = 1 ;
    private String makeLink(int documentId, HttpServletRequest request) {
        return "<a href=\""+request.getContextPath()+"/servlet/GetDoc?meta_id="+ documentId +"\">document "+ documentId +"</a>" ;
    }

%>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.Menu menu = document.getMenu(menuIndex) ;

    Document[] documents = menu.getDocuments() ;
    if (documents.length > 0) {
        Document firstDocument = documents[0] ;
        menu.removeDocument(firstDocument);
        documentService.saveChanges( document ); // Don't forget to save!
        %>Removed the first document (<%= makeLink(firstDocument.getId(), request) %> with headline "<b><%= firstDocument.getHeadline() %></b>")
        from menu <%= menuIndex %> on <%= makeLink(document.getId(), request) %>.<br><%
    } else {
        %>There are no documents in menu <%= menuIndex %> on <%= makeLink(documentId, request) %>.<%
    }
%>


