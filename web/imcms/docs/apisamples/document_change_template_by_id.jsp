<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TemplateService templateService = imcmsSystem.getTemplateService();
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;

    int templateId = 1;
    Template template = templateService.getTemplateById( templateId );

    if( null != template )  {
        document.setTemplate( template );
        // don't forget to save your changes!
        documentService.saveChanges( document );
        %>Done!<%
    } else {
        %>No such template.<%
    }
%>