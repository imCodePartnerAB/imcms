<%@ page import="com.imcode.imcms.api.*"%>
<%

    ContentManagementSystem imcms = (ContentManagementSystem) request.getAttribute(
            RequestConstants.SYSTEM);
    DocumentService documentService = imcms.getDocumentService();
    CategoryType[] categoryTypes = documentService.getAllCategoryTypes() ;
%>
<html>
    <head>
        <title><? sv/apisamples/categories_show_all.jsp/1 ?></title>
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
                %><li><? sv/apisamples/categories_show_all.jsp/2 ?></li><%
            }
            %></ul></li><%
        }
    %>
    </ul>
    </body>
</html>
