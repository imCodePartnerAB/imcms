<%@ page import="com.imcode.imcms.api.*,
                 java.util.Date" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;

    int parentId = 1001;
    int menuIndex = 1;
    TextDocument parentDocument = documentService.getTextDocument( parentId );

    TextDocument textDocument = documentService.createNewTextDocument( parentDocument ) ;
    textDocument.setHeadline( "Textdocument created from API" );
    textDocument.setPlainTextField( 1, "Test text field" );
    textDocument.setPublicationStartDatetime( new Date() );
    textDocument.setStatus(Document.STATUS_PUBLICATION_APPROVED);
    documentService.saveChanges( textDocument );

    parentDocument.getMenu( menuIndex ).addDocument( textDocument );
    documentService.saveChanges( parentDocument );
%>
Created a text document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= textDocument.getId() %>"><%= textDocument.getId() %></a>"
with link from the document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= parentId %>"><%= parentId %></a>".<br>

<%
    UrlDocument urlDocument = documentService.createNewUrlDocument( parentDocument );
    urlDocument.setHeadline( "URL-document created from API" );
    urlDocument.setUrl( "www.imcode.com" );
    urlDocument.setPublicationStartDatetime( new Date() );
    urlDocument.setStatus(Document.STATUS_PUBLICATION_APPROVED);
    documentService.saveChanges( urlDocument );

    parentDocument.getMenu( menuIndex ).addDocument( urlDocument );
    documentService.saveChanges( parentDocument );
%>

Created an url document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= urlDocument.getId() %>"><%= urlDocument.getId() %></a>"
with link from the document with id "<a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=<%= parentId %>"><%= parentId %></a>".<br>
