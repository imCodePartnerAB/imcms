<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentMapper = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    Document document = documentMapper.getDocument(documentId) ;
    document.setHeadline( "test test ");
//    document.setMenuText(String);
//    document.setMenuImageURL(String);
%>

