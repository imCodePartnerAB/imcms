<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>


<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    TextDocument documentToBeIncluded = documentService.createNewTextDocument( documentId, 1 ) ;

    int includeIndexInDocument = 1;
    document.setInclude( includeIndexInDocument, documentToBeIncluded ) ;
%>
Done. Document <%= documentId %> now includes document <%= documentToBeIncluded.getId() %>.
