<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;

    int parentId = 1001;
    int parentMenuNumber = 1;
    TextDocument document = documentService.createNewTextDocument( parentId, parentMenuNumber ) ;
    document.setHeadline( "Testrubrik" );
    documentService.saveChanges(document);
%>
Created document with id "<a href="../servlet/GetDoc?meta_id=<%= document.getId() %>"><%= document.getId() %></a>"
with link from the document with id "<a href="../servlet/GetDoc?meta_id=<%= parentId %>"><%= parentId %></a>".<br>
