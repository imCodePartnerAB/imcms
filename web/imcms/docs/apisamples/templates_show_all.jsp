<%@ page import="com.imcode.imcms.api.*"%>
<%

    ContentManagementSystem imcms = (ContentManagementSystem) request.getAttribute(
            RequestConstants.SYSTEM);
    TemplateService templateService = imcms.getTemplateService();
    TemplateGroup[] templateGroups = templateService.getAllTemplateGroups() ;
%>
<html>
    <head>
        <title>Templates and template groups</title>
    </head>
    <body>
        All Template groups and theirs templates:<br>
        <ul>
    <%
        for (int i = 0; i < templateGroups.length; i++) {
            TemplateGroup templateGroup = templateGroups[i];
            %><li>Template group name "<%= templateGroup.getName() %>", id = <%=templateGroup.getId()%><ul><%
            Template[] templates = templateService.getTemplates( templateGroup);
            for (int j = 0; j < templates.length; j++) {
                Template template = templates[j];
                %><li>Template name "<%= template.getName() %>", id = <%=template.getId()%><ul><%
            }
            %></ul></li><%
        }
        %></ul></li><%
    %>
    </ul>
    <br>
    <%
        int templateGroupId = 0;
        int templateId = 1;
    %>
    The template group with id <%= templateGroupId %> has the name "<%= templateService.getTemplateGroupById( templateGroupId ).getName()%>"<br>
    The template with id <%= templateId %> has the name "<%= templateService.getTemplateById( templateId ).getName()%>"<br>
    <br>
    <%
        String templateName = "demo";
    %>
    The template with name "<%= templateName %>" has the id <%= templateService.getTemplate( templateName ).getId()%><br>
    </body>
</html>
