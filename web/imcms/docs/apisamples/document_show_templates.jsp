<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int docId = 1001;
    TextDocument document = documentService.getTextDocument(docId);
    Template docTemplate = document.getTemplate();
%>

<h3>Current template</h3>
TextDocument <%=document.getId()%> is shown with the template named "<%= docTemplate.getName() %>" <br>

<h4>All possible templates to play with this document (for the current logged in user)</h4>
<%
    TemplateService templateService = imcmsSystem.getTemplateService();
    TemplateGroup[] templateGroups = templateService.getTemplatesGroups( document );
    for( int i = 0; i < templateGroups.length; i++ ) {
        TemplateGroup templateGroup = templateGroups[i];%>
        Template group "<%= templateGroup.getName() %>" has the following templates:<br>
        <ul><%
        Template[] templates = templateService.getTemplates( templateGroup );
        for( int k = 0; k < templates.length; k++ ) {
            Template template = templates[k];%>
            <li><%=template.getName()%></li><%
        }%>
        </ul><%
    }

%>


