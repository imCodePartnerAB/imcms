<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%!
    int documentId = 1001 ;
    int menuIndex = 1 ;
    private String makeLink(Document document) throws NoPermissionException {
        return "<a href=\"../servlet/GetDoc?meta_id="+ document.getId() +"\">document "+ document.getId() + "</a> with headline <b>"+document.getHeadline()+"</b>" ;
    }
%>

The documents in menu number <%= menuIndex %> on <%= documentId %> is:<br>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TextDocument document = documentService.getTextDocument(documentId) ;
    TextDocument.Menu menu = document.getMenu(menuIndex) ;

    Document[] documents = menu.getDocuments() ;
    if (documents.length > 0) {
        for ( int i = 0; i < documents.length; i++ ) {
            Document linkedDocument = documents[i];
            %><%= makeLink(linkedDocument) %><br><%
        }
    } else {
        %>there are no documents in menu <%= menuIndex %> on <%= documentId %>.<%
    }
%>

