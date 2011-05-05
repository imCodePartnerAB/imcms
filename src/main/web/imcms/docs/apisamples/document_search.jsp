<%@ page import="com.imcode.imcms.api.*,
                 imcode.server.Imcms,
                 org.apache.lucene.index.Term,
                 org.apache.lucene.search.BooleanQuery,
                 org.apache.lucene.search.BooleanClause.Occur, 
                 org.apache.lucene.search.RangeQuery,
                 org.apache.lucene.search.Sort,
                 org.apache.lucene.search.SortField,
                 org.apache.lucene.search.TermQuery,
                 java.util.Calendar, java.util.Date, 
                 java.text.DateFormat"
         errorPage="error.jsp"
         contentType="text/html; charset=UTF-8"%>
<%
    DateFormat solrDateFormat = org.apache.solr.common.util.DateUtil.getThreadLocalDateFormat();
	response.setContentType( "text/html; charset=" + Imcms.DEFAULT_ENCODING);
	ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    DocumentService documentService = imcmsSystem.getDocumentService() ;
%><html>
    <head>
        <title>Search Documents</title>
    </head>
    <body>
        <h1>Search for documents</h1>
        <p>
            See syntax at
            <a href="http://lucene.apache.org/java/2_4_1/queryparsersyntax.html">
                http://lucene.apache.org/java/2_4_1/queryparsersyntax.html
            </a>
        </p>
        <h2>Search for documents containing "test" in headline, menutext, textfields, or keywords,
            sorted in order of best hit.</h2>
        <ul>
        <%
            SearchQuery query = new LuceneParsedQuery("test");
            Document[] documents = documentService.search(query);

            if (0 == documents.length) { %>No hits.<% }
            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %></li><%
            } %>
        </ul>

        <h2>Search for documents containing "test" in textfield 1,
            and modified between 2004-01-01 and 2005-01-01, sorted by modified time, newest first.</h2>
        <ul>
        <%
            BooleanQuery luceneQuery = new BooleanQuery();
            luceneQuery.add(new TermQuery(new Term("text1", "test")), Occur.MUST);
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(2004,Calendar.JANUARY,1) ;
            Date startDate = calendar.getTime() ;
            calendar.set(2005, Calendar.JANUARY, 1) ;
            Date endDate = calendar.getTime() ;
            luceneQuery.add(new RangeQuery(new Term("modified_datetime", solrDateFormat.format(startDate)), new Term("modified_datetime", solrDateFormat.format(endDate)), false), Occur.MUST);
            query = new LuceneQuery(luceneQuery);
            query.setSort(new Sort("modified_datetime"));
            documents = documentService.search(query);

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
                    query = new LuceneQuery(new TermQuery(new Term("category_id", ""+category.getId())));
                    query.setSort(new Sort("publication_start_datetime"));
                    documents = documentService.search(query);

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
            query = new LuceneQuery(new TermQuery(new Term("parent_menu_id", "1001_1")));
            query.setSort(new Sort(new SortField[] {new SortField("archived_datetime", true) }));
            documents = documentService.search(query);

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
            luceneQuery = new BooleanQuery();
            luceneQuery.add(new TermQuery(new Term("status", ""+Document.PublicationStatus.NEW)), Occur.MUST) ;
            Term lowerTerm = new Term("publication_start_datetime", solrDateFormat.format(new Date(0)));
            Term upperTerm = new Term("publication_start_datetime", solrDateFormat.format(new Date()));
            luceneQuery.add(new RangeQuery(lowerTerm, upperTerm, false), Occur.MUST);
            query = new LuceneQuery(luceneQuery) ;
            query.setSort(new Sort("publication_start_datetime"));
            documents = documentService.search(query) ;

            if (0 == documents.length) { %>No hits.<% }
            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %> - <%= document.getPublicationStartDatetime() %></li><%
            } %>
        </ul>

    </body>
</html>
