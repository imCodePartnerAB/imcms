<%@ page import="com.imcode.imcms.api.*,
java.util.*" errorPage="error.jsp" %>
<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    SearchQuery query ;
%>

<html>
    <head>
        <title>Search Documents</title>
    </head>
    <body>
        <h1>Search for documents</h1>
        <p>
            See syntax at
            <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">
                http://jakarta.apache.org/lucene/docs/queryparsersyntax.html
            </a>
        </p>
        <h2>Search for documents containing "test" in headline, menutext, textfields, or keywords.</h2>
        <ul>
        <%
            query = new LuceneParsedQuery("test");
            Document[] documents = documentService.search(query);

            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %></li><%
            } %>
        </ul>

        <%
            int sectionId = 1;
            Section section = documentService.getSection( sectionId );
        %>
        <h2>Search for documents in section "<%= section.getName() %>".</h2>
        <ul>
        <%
            query = new LuceneParsedQuery("section:"+section.getName());
            documents = documentService.search(query);

            for ( int i = 0; i < documents.length; i++ ) {
            Document document = documents[i]; %>
            <li><%= document.getId() %> - <%= document.getHeadline() %></li>
        <% } %>
        </ul>

        <h2>Search for documents containing "test" in textfield 1, and modified between 2004-01-01 and 2005-01-01.</h2>
        <ul>
        <%
            query = new LuceneParsedQuery("+text1:test +modified_datetime:[2003-01-01 TO 2005-01-01]");
            documents = documentService.search(query);

            for ( int i = 0; i < documents.length; i++ ) {
            Document document = documents[i]; %>
            <li><%= document.getId() %> - <%= document.getHeadline() %></li>
        <% } %>
        </ul>

        <% CategoryType[] categoryTypes = documentService.getAllCategoryTypes() ;
            if (0 != categoryTypes.length) {
                Category[] categories = documentService.getAllCategoriesOfType( categoryTypes[0] ) ;
                if (0 != categories.length) {
                    Category category = categories[0] ; %>
                    <h2>Search for documents in category <%= category.getName() %> with category-id <%= category.getId() %>.</h2>
                    <ul>
                    <%
                    query = new LuceneParsedQuery("category_id:"+category.getId());
                    documents = documentService.search(query);

                    for ( int i = 0; i < documents.length; i++ ) {
                        Document document = documents[i]; %>
                        <li><%= document.getId() %> - <%= document.getHeadline() %></li><%
                    }
                }
            }
        %>
        </ul>
    </body>
</html>