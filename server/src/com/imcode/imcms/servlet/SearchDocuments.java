package com.imcode.imcms.servlet;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.SectionDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.HttpSessionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SearchDocuments extends HttpServlet {

    public final static String PARAM__DOCUMENT_TYPE = "documentType";

    private final static Logger log = Logger.getLogger( com.imcode.imcms.servlet.SearchDocuments.class.getName() );
    public static final String REQUEST_ATTRIBUTE_PARAMETER__SEARCH_DOCUMENTS = "SearchDocuments";
    public static final String PARAM__SHOW_SELECT_LINK = "showSelectLinks";
    public static final String PARAM__CHOSEN_URL = "returningUrl";
    public static final String REQUEST_PARAM_SELECTED_DOCUMENT = "selectedDocumentId";
    public static final String REQUEST_PARAMETER__SECTION_ID = "section_id";
    public static final String REQUEST_PARAMETER__DOCUMENTS_PER_PAGE = "num";
    public static final String REQUEST_PARAMETER__QUERY = "q";
    private static final int DEFAULT_DOCUMENTS_PER_PAGE = 20;
    public static final String REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX = "start";
    public static final String REQUEST_PARAMETER__SEARCH_BUTTON = "search";
    private static final String REQUEST_ATTRIBUTE_PARAMETER__SEARCH_DOCUMENTS_PAGE = "SearchDocumentsPage";

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        DocumentMapper documentMapper = imcref.getDocumentMapper();

        UserDomainObject user = Utility.getLoggedOnUser( request );

        SearchDocumentsPage searchDocumentsPage = new SearchDocumentsPage();

        try {
            int sectionId = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__SECTION_ID ) );
            SectionDomainObject section = documentMapper.getSectionById( sectionId );
            searchDocumentsPage.setSection( section );
        } catch ( NumberFormatException nfe ) {
        }

        int documentsPerPage = NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__DOCUMENTS_PER_PAGE ), DEFAULT_DOCUMENTS_PER_PAGE );
        searchDocumentsPage.setDocumentsPerPage( documentsPerPage );

        int firstDocumentIndex = NumberUtils.stringToInt( request.getParameter( REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX ) );
        searchDocumentsPage.setFirstDocumentIndex( firstDocumentIndex );

        Integer documentTypeId = null;
        try {
            documentTypeId = new Integer( request.getParameter( PARAM__DOCUMENT_TYPE ) );
        } catch ( NumberFormatException nfe ) {
        }

        searchDocumentsPage.setQuery( StringUtils.defaultString( request.getParameter( REQUEST_PARAMETER__QUERY ) ) );
        if ( StringUtils.isNotBlank( searchDocumentsPage.getQuery() ) || null != request.getParameter( REQUEST_PARAMETER__SEARCH_BUTTON ) ) {
            DocumentDomainObject[] searchResults = searchDocuments( searchDocumentsPage.getQuery(), searchDocumentsPage.getSection(), documentTypeId, user );
            searchDocumentsPage.setDocumentsFound( searchResults );
        }

        searchDocumentsPage.forward( request, response );
    }

    private DocumentDomainObject[] searchDocuments( String searchString, SectionDomainObject section,
                                                    Integer documentType, UserDomainObject user ) throws IOException {

        DocumentIndex index = ApplicationServer.getIMCServiceInterface().getDocumentMapper().getDocumentIndex();
        BooleanQuery query = new BooleanQuery();
        if ( null != searchString && !"".equals( searchString.trim() ) ) {
            try {
                Query textQuery = index.parseLucene( searchString );
                query.add( textQuery, true, false );
            } catch ( ParseException e ) {
                log.warn( e.getMessage() + " in search-string " + searchString );
            }
        }

        if ( null != section ) {
            Query sectionQuery = new TermQuery( new Term( "section", section.getName().toLowerCase() ) );
            query.add( sectionQuery, true, false );
        }

        if ( null != documentType ) {
            Query documentTypeQuery = new TermQuery( new Term( "doc_type_id", "" + documentType ) );
            query.add( documentTypeQuery, true, false );
        }

        return index.search( query, user );
    }

    public static class SearchDocumentsPage {

        public static final String REQUEST_ATTRIBUTE__PAGE = "page";

        private SectionDomainObject section;
        private DocumentDomainObject[] documentsFound;
        private int firstDocumentIndex;
        private int documentsPerPage;
        private String query;
        private String selectDocumentUrlPrefix;

        private void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
            UserDomainObject user = Utility.getLoggedOnUser( request );
            request.getRequestDispatcher( "/imcms/" + user.getLanguageIso639_2() + "/jsp/search_documents.jsp" ).forward( request, response );
        }

        public SectionDomainObject getSection() {
            return section;
        }

        public DocumentDomainObject[] getDocumentsFound() {
            return documentsFound;
        }

        public void setDocumentsFound( DocumentDomainObject[] documentsFound ) {
            this.documentsFound = documentsFound;
        }

        public int getFirstDocumentIndex() {
            return firstDocumentIndex;
        }

        public int getDocumentsPerPage() {
            return documentsPerPage;
        }

        public void setQuery( String query ) {
            this.query = query;
        }

        public void setDocumentsPerPage( int documentsPerPage ) {
            this.documentsPerPage = documentsPerPage;
        }

        public void setSection( SectionDomainObject section ) {
            this.section = section;
        }

        public String getQuery() {
            return query;
        }

        public void setFirstDocumentIndex( int firstDocumentIndex ) {
            this.firstDocumentIndex = firstDocumentIndex;
        }

        public String getSelectDocumentUrlPrefix() {
            return selectDocumentUrlPrefix;
        }
    }

} // End class
