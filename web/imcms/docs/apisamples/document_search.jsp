<%@ page import="com.imcode.imcms.api.*,
                 imcode.server.Imcms,
                 org.apache.lucene.document.DateTools,
                 org.apache.lucene.index.Term, org.apache.lucene.search.BooleanClause.Occur"
         errorPage="error.jsp"

         contentType="text/html; charset=UTF-8"%>
<%@ page import="org.apache.lucene.search.*" %>
<%@ page import="org.apache.lucene.util.BytesRef" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%
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
            <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">
                http://jakarta.apache.org/lucene/docs/queryparsersyntax.html
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

        <%
            int sectionId = 1;
            Section section = documentService.getSection( sectionId );
            if (null != section) {
        %>
        <h2>Search for documents in section "<%= section.getName() %>", sorted by headline and id.</h2>
        <ul>
        <%
            query = new LuceneQuery(new TermQuery(new Term("section", section.getName())));
            query.setSort(new Sort(
                    new SortField("meta_headline_keyword", SortField.Type.STRING),
                    new SortField("meta_id", SortField.Type.STRING)
            ));
            documents = documentService.search(query);

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
            BooleanQuery.Builder luceneQueryBuilder = new BooleanQuery.Builder();
            luceneQueryBuilder.add(new TermQuery(new Term("text1", "test")), Occur.MUST);
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(2004,Calendar.JANUARY,1) ;
            Date startDate = calendar.getTime() ;
            calendar.set(2005, Calendar.JANUARY, 1) ;
            Date endDate = calendar.getTime() ;
            final TermRangeQuery termRangeQuery = new TermRangeQuery(
                    "modified_datetime",
                    new BytesRef(DateTools.dateToString(startDate, DateTools.Resolution.MINUTE)),
                    new BytesRef(DateTools.dateToString(endDate, DateTools.Resolution.MINUTE)),
                    false, false
            );
            luceneQueryBuilder.add(termRangeQuery, Occur.MUST);
            query = new LuceneQuery(luceneQueryBuilder.build());
            query.setSort(new Sort(new SortField("modified_datetime", SortField.Type.STRING)));
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
                    query.setSort(new Sort(new SortField("publication_start_datetime", SortField.Type.STRING)));
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
            query.setSort(new Sort(new SortField("meta_headline_keyword", SortField.Type.STRING), new SortField("archived_datetime", SortField.Type.STRING, true)));
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
            luceneQueryBuilder = new BooleanQuery.Builder();
            luceneQueryBuilder.add(new TermQuery(new Term("status", ""+Document.PublicationStatus.NEW)), Occur.MUST) ;
            final BytesRef lowerRef = new BytesRef("0");
            final BytesRef upperRef = new BytesRef(DateTools.dateToString(new Date(), DateTools.Resolution.MINUTE));
            luceneQueryBuilder.add(new TermRangeQuery("publication_start_datetime", lowerRef, upperRef, false, false), Occur.MUST);
            query = new LuceneQuery(luceneQueryBuilder.build()) ;
            query.setSort(new Sort(new SortField("publication_start_datetime", SortField.Type.STRING)));
            documents = documentService.search(query) ;

            if (0 == documents.length) { %>No hits.<% }
            for ( int i = 0; i < documents.length; i++ ) {
                Document document = documents[i]; %>
                <li><%= document.getId() %> - <%= document.getHeadline() %> - <%= document.getPublicationStartDatetime() %></li><%
            } %>
        </ul>

    </body>
</html>
