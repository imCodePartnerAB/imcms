<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TemplateService templateService = imcmsSystem.getTemplateService();
    int documentId = 1001 ;
    TextDocument document = documentService.getTextDocument(documentId) ;
    Template currentTemplate = document.getTemplate();
    Template[] templates = templateService.getPossibleTemplates( document );

    if (0 == templates.length) {
        %> No possible templates to choose from for this user. <%
    } else {

        // take the first template that's not current
        Template newTemplate = null;
        int i = 0;
        while( newTemplate == null && i < templates.length ){
            if( !templates[i].equals(currentTemplate) ) {
                newTemplate = templates[i];
            }
            i++;
        }

        if( newTemplate != null ){
            document.setTemplate( newTemplate );
        }
        // don't forget to save your changes!
        documentService.saveChanges( document );
        %>
        <h3>Changing template on document <%=document.getId()%></h3>
        The template the document has assigned, <br>
        Before: <%= currentTemplate.toString()%>
        After: <%= document.getTemplate().toString() %>
<% } %>

