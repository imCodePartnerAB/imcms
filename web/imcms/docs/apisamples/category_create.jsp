<%@ page import="com.imcode.imcms.api.*"%>
<%

    ContentManagementSystem imcms = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcms.getDocumentService();
%>
<html>
    <head>
        <title>Create a new Category Type and a Category</title>
    </head>
    <body>
        <%
            String categoryTypeName = "API-sample Category Type";
            try{
                CategoryType newCategoryType = documentService.createNewCategoryType( categoryTypeName, 1 );
                Category newCategory = documentService.createNewCategory( "API-sample category", "A description", "", newCategoryType );%>
                A new Category type "<%= newCategoryType.getName() %>" was created, including one category with name "<%= newCategory.getName() %>"<%
            } catch( CategoryAlreadyExistsException ex ) {
                %>A category type with that name already exists.<%
            } catch( CategoryTypeAlreadyExistsException ex ) {
                %>A category with that name already exists within the category type name "<%=categoryTypeName %>". <%
            }
        %>
    </body>
</html>
