<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int docId = 1001;
    TextDocument document = (TextDocument)documentService.getDocument(docId);
    Template docTemplate = document.getTemplate();
%>

<h3>Current template</h3>
TextDocument <%=document.getId()%> is shown with the template <%= docTemplate.getName() %>

<h4>All possible templtates to play with this document (for the current logged in user)</h4>
<%
    TemplateService templateService = imcmsSystem.getTemplateService();
    TemplateGroup[] templateGroups = templateService.getTemplatesGroups( document );
    for( int i = 0; i < templateGroups.length; i++ ) {
        TemplateGroup templateGroup = templateGroups[i];%>
        <%= templateGroup.getName() %> has the following templates:<br><%
        Template[] templates = templateService.getTemplates( templateGroup );
        for( int k = 0; k < templates.length; k++ ) {
            Template template = templates[k];%>
            <%=template.getName()%><br><%
        }%>
        <br><%
    }

%>


