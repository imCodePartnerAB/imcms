<%@ page import="com.imcode.imcms.api.*"%>
<%

    ContentManagementSystem imcms = (ContentManagementSystem) request.getAttribute(
            RequestConstants.SYSTEM);
    DocumentService documentService = imcms.getDocumentService();
    CategoryType[] categoryTypes = documentService.getAllCategoryTypes() ;
%>
<html>
    <head>
        <title>All categories</title>
    </head>
    <body>
        <ul>
    <%
        for (int i = 0; i < categoryTypes.length; i++) {
            CategoryType categoryType = categoryTypes[i];
            %><li><%= categoryType.getName() %><ul><%
            Category[] categories = documentService.getAllCategoriesOfType(categoryType);
            for (int j = 0; j < categories.length; j++) {
                Category category = categories[j];
                %><li><%= category.getName() + " - " + category.getDescription() + " (ID: " + category.getId() + ")" %></li><%
            }
            %></ul></li><%
        }
    %>
    </ul>
    </body>
</html>
