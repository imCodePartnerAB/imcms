<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentMapper = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    Document document = documentMapper.getDocument(documentId) ;
    document.setHeadline( "Test headline text");
    document.setMenuText( "Test menu text");
    document.setMenuImageURL("Test menu image url");
%>
Done.

