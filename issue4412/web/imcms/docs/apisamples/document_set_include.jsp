<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>
<html>
<body>
<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    TextDocument documentToBeIncluded = documentService.createNewTextDocument( document ) ;
    documentService.saveChanges( documentToBeIncluded );

    int includeIndexInDocument = 1;
    document.setInclude( includeIndexInDocument, documentToBeIncluded ) ;
    documentService.saveChanges( document );
%>
Done. Document <%= documentId %> now includes document <%= documentToBeIncluded.getId() %>.
</body>
</html>