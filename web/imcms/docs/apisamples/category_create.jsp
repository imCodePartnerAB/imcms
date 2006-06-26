<%@ page import="com.imcode.imcms.api.*"
        contentType="text/html; charset=UTF-8" %>
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
            CategoryType categoryType ;
            try{
                categoryType = documentService.createNewCategoryType( categoryTypeName, 1 );
                %>Created category type with name "<%= categoryTypeName %>".<br><%
            } catch( CategoryTypeAlreadyExistsException ex ) {
                categoryType = documentService.getCategoryType( categoryTypeName ) ;
            }
            String categoryName = "API-sample category";
            Category newCategory = new Category( categoryName, categoryType );
            try {
                documentService.saveCategory( newCategory );
                %>Created category with name "<%= newCategory.getName() %>" and type "<%= categoryType.getName() %>".<%
            } catch( CategoryAlreadyExistsException ex ) {
                %>A category with the name "<%= categoryName %>" already exists within the category type name "<%=categoryTypeName %>". <%
            }
        %>
    </body>
</html>
