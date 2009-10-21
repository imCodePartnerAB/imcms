<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TemplateService templateService = imcmsSystem.getTemplateService();
    int documentId = 1001 ;
    String templateName = "demo" ;
    TextDocument document = documentService.getTextDocument(documentId) ;
    Template currentTemplate = document.getTemplate();

    Template newTemplate = templateService.getTemplate(templateName) ;

    if( newTemplate == null ){
        %> No template by the name "<%= templateName %>" <%
    } else {
        document.setTemplate( newTemplate );
        // don't forget to save your changes!
        documentService.saveChanges( document ); %>

        <h3>Changing template on document <%=document.getId()%></h3>
        The template the document has assigned, <br>
        Before: <%= currentTemplate.toString()%>
        After: <%= document.getTemplate().toString() %><%
    }
%>
