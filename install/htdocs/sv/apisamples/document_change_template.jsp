<%@ page import="com.imcode.imcms.*"%>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentMapper = imcmsSystem.getDocumentService();
    int docId = 1001;
    TextDocument document = (TextDocument)documentMapper.getDocument(docId);
    Template template = document.getTemplate();
%>
TextDocument <%=document.getId()%> is shown with the template <%= template.getName() %>
<h3>All possible templtates to play with </h3>
<%
    TemplateService templateService = imcmsSystem.getTemplateService();
    Template[] templateGroups = templateService.getAllTemplatesGroups( document );
    for( int i = 0; i < templateGroups.length; i++ ) {
        Template temp = templateGroups[i];%>
        <%= temp.getName() %><%
    }
%>


