<%@ page contentType="text/html; charset=windows-1252" import="com.imcode.imcms.api.*,
java.util.*,
                 java.text.SimpleDateFormat,
                 java.text.DateFormat,
                                                               org.apache.commons.lang.StringUtils" errorPage="error.jsp" %><%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute(RequestConstants.SYSTEM);
    DocumentService documentService = imcmsSystem.getDocumentService() ;
    SearchQuery query ;
%><html>
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
        <h2>Search for documents containing "test" in headline, menutext, textfields, or keywords,
            sorted in order of best hit.</h2>
        <ul>
        <%
            query = new LuceneParsedQuery("test");
            Document[] documents = documentService.search(query);

            if (0 == documents.length) { %>No hits.<% }
            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %></li><%
            } %>
        </ul>

        <%
            int sectionId = 1;
            Section section = documentService.getSection( sectionId );
            if (null != section) {
        %>
        <h2>Search for documents in section "<%= section.getName() %>", sorted by headline and id.</h2>
        <ul>
        <%
            query = new LuceneParsedQuery("section:"+section.getName());
            documents = documentService.search(query);
            Arrays.sort(documents, Document.Comparator.HEADLINE.chain( Document.Comparator.ID )) ;

            if (0 == documents.length) { %>No hits.<% }
            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %></li>
            <% } %>
        </ul>
        <% } %>

        <h2>Search for documents containing "test" in textfield 1,
            and modified between 2004-01-01 and 2005-01-01, sorted by modified time, newest first.</h2>
        <ul>
        <%
            query = new LuceneParsedQuery("+text1:test +modified_datetime:[2003-01-01 TO 2005-01-01]");
            documents = documentService.search(query);
            Arrays.sort(documents, Document.Comparator.MODIFIED_DATETIME.reversed()) ;

            if (0 == documents.length) { %>No hits.<% }
            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %> - <%= document.getModifiedDatetime() %></li>
            <% } %>
        </ul>

        <%
            CategoryType[] categoryTypes = documentService.getAllCategoryTypes() ;
            if (0 != categoryTypes.length) {
                Category[] categories = documentService.getAllCategoriesOfType( categoryTypes[0] ) ;
                if (0 != categories.length) {
                    Category category = categories[0] ; %>
                    <h2>Search for documents in category <%= category.getName() %> (category-id <%= category.getId() %>),
                        sorted by published time.</h2>
                    <ul>
                    <%
                    query = new LuceneParsedQuery("category_id:"+category.getId());
                    documents = documentService.search(query);
                    Arrays.sort(documents, Document.Comparator.PUBLICATION_START_DATETIME.nullsFirst() ) ;

                    if (0 == documents.length) { %>No hits.<% }
                    for ( int i = 0; i < documents.length; i++ ) {
                        Document document = documents[i]; %>
                        <li><%= document.getId() %> - <%= document.getHeadline() %> - <%= document.getPublicationStartDatetime() %></li><%
                    }
                }
            }
        %>
        </ul>

        <h2>Search for documents linked from menu 1 in document 1001,
            sorted by headline and archived date, later archived first.</h2>
        <ul>
        <%
            query = new LuceneParsedQuery("parent_menu_id:1001_1");
            documents = documentService.search(query);
            Arrays.sort(documents, Document.Comparator.HEADLINE.chain(Document.Comparator.ARCHIVED_DATETIME.reversed().nullsFirst())) ;

            if (0 == documents.length) { %>No hits.<% }
            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %> - <%= document.getArchivedDatetime() %></li><%
            } %>
        </ul>

        <h2>Search for new documents that are overdue for publication approval (publication start date),
            sorted with oldest publication start date first.</h2>
        <ul>
        <%
            Date now = new Date() ;
            String todayDateString = new SimpleDateFormat("yyyy-MM-dd").format(now) ;
            query = new LuceneParsedQuery("+status:"+Document.STATUS_NEW+" +publication_start_datetime:[0 TO "+todayDateString+"]") ;
            documents = documentService.search(query) ;
            Arrays.sort(documents, Document.Comparator.PUBLICATION_START_DATETIME) ;

            if (0 == documents.length) { %>No hits.<% }
            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %> - <%= document.getPublicationStartDatetime() %></li><%
            } %>
        </ul>

        <%
            String categoryTypeName = "API-sample Category Type";
            CategoryType categoryType = documentService.getCategoryType( categoryTypeName );
        %>
    </body>
</html>
