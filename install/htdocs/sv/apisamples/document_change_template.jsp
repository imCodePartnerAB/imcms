<%@ page import="com.imcode.imcms.*"%>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    TemplateService templateService = imcmsSystem.getTemplateService();
    int documentId = 1001 ;
    TextDocument document = (TextDocument)documentService.getTextDocument(documentId) ;
    Template currentTemplate = document.getTemplate();
    Template[] templates = templateService.getPossibleTemplates( document );
    int i = 0;
    boolean found = false;
    Template newTemplate = null;
    while( i < templates.length && !found && !currentTemplate.equals(templates[i]) )
        newTemplate = templates[i];
        found = true;
    }
    document.setTemplate( newTemplate );
    documentService.saveChanges( document );
%>
<h3>Changing template on document <%=document.getId()%></h3>
The template the document has assigned, <br>
Before: <%= currentTemplate.getName() %>
After: <%= document.getTemplate() %>