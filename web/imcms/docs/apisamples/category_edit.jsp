<%@ page import="com.imcode.imcms.api.*"
        contentType="text/html; charset=UTF-8" %>
<%

    ContentManagementSystem imcms = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcms.getDocumentService();
%>
<html>
    <head>
        <title>Edit a category</title>
    </head>
    <body>
        <%
            String categoryTypeName = "API-sample Category Type";
            CategoryType categoryType = documentService.getCategoryType( categoryTypeName ) ;
            if (null == categoryType) {
                %>No category type by the name "<%= categoryTypeName %>".<%
            } else {
                String categoryName = "API-sample category";
                Category category = documentService.getCategory(categoryType, categoryName) ;
                if (null == category) {
                    %>No category by the name "<%= categoryName %>" with category type "<%= categoryTypeName %>".<%
                } else {
                    String newCategoryName = "API-sample category with new name";
                    try{
                        category.setName( newCategoryName);
                        documentService.saveCategory( category );
                        %>
                        Category "<%= categoryName %>" was renamed to "<%= newCategoryName %>"<%
                    } catch( CategoryAlreadyExistsException ex ) {
                        %>A category with the name "<%= newCategoryName %>" already exists.<%
                    }
                }
            }
        %>
    </body>
</html>
