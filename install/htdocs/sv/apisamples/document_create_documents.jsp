<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;

    int parentId = 1001;
    int parentMenuNumber = 1;
    TextDocument textDocument = documentService.createNewTextDocument( parentId, parentMenuNumber ) ;
    textDocument.setHeadline( "Test headline" );
    textDocument.setPlainTextField( 1, "Test text field" );
    documentService.saveChanges(textDocument);
%>
Created a text document with id "<a href="../servlet/GetDoc?meta_id=<%= textDocument.getId() %>"><%= textDocument.getId() %></a>"
with link from the document with id "<a href="../servlet/GetDoc?meta_id=<%= parentId %>"><%= parentId %></a>".<br>

<%
    UrlDocument urlDocument = documentService.createNewUrlDocument( parentId, parentMenuNumber );
    urlDocument.setUrl( "www.imcode.com");
    documentService.saveChanges( urlDocument );
%>

Created an url document with id "<a href="../servlet/GetDoc?meta_id=<%= urlDocument.getId() %>"><%= urlDocument.getId() %></a>"
with link from the document with id "<a href="../servlet/GetDoc?meta_id=<%= parentId %>"><%= parentId %></a>".<br>

