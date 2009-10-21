<%@ page import="com.imcode.imcms.api.*"%>
<%

    ContentManagementSystem imcms = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcms.getDocumentService();
    CategoryType[] categoryTypes = documentService.getAllCategoryTypes() ;
%>
<html>
    <head>
        <title>Categories and their types</title>
    </head>
    <body>
        All categories that exists in the system:<br>
        <ul>
    <%
        for (int i = 0; i < categoryTypes.length; i++) {
            CategoryType categoryType = categoryTypes[i];
            %>
            <li>Category type name "<%= categoryType.getName() %>", id = <%=categoryType.getId()%>
                <ul>
                    <%
                    Category[] categories = documentService.getAllCategoriesOfType(categoryType);
                    for (int j = 0; j < categories.length; j++) {
                        Category category = categories[j];
                        %><li>Category name "<%= category.getName() %>", id = <%=category.getId()%></li><%
                    }
                    %>
                </ul>
            </li><%
        }
    %>
    </ul>
    </body>
</html>
