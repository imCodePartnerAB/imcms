<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    document.setHeadline( "Test headline text");
    document.setMenuText( "Test menu text");
    document.setMenuImageURL("Test menu image url");

    //Language english = Language.getLanguageByISO639_1( "en" );
    Language english = Language.getLanguageByISO639_2( "eng" );

    document.setLanguage( english );

    // don't forget to save your changes!
    documentService.saveChanges( document );
%>
Done changing the headline, menutext, menuimageurl, and language of
document <a href="../servlet/GetDoc?meta_id=<%= documentId %>"><%= documentId %></a>.
