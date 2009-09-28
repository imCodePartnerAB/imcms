package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.flow.Page;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.index.DefaultQueryParser;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.SimpleDocumentQuery;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetExistingDoc extends HttpServlet {

    private final static Logger LOG = Logger.getLogger( GetExistingDoc.class.getName() );
    private static final String ONE_SEARCH_HIT = "existing_doc_hit.html";
    private static final String SEARCH_RESULTS = "existing_doc_res.html";
    private static final String ADMIN_TEMPLATE_EXISTING_DOC = "existing_doc.html";

    private static final String SORT_BY_LANGUAGE_KEY_PREFIX = "templates/sv/existing_doc.html/sort_by/" ;

    private static final Object[][] SORT_ORDERS_ARRAY = new Object[][]{
        {"meta_headline", new LocalizedMessage( SORT_BY_LANGUAGE_KEY_PREFIX+"headline") },
        {"meta_id", new LocalizedMessage( SORT_BY_LANGUAGE_KEY_PREFIX+"id") },
        {"doc_type", new LocalizedMessage( SORT_BY_LANGUAGE_KEY_PREFIX+"type") },
        {"date_modified", new LocalizedMessage( SORT_BY_LANGUAGE_KEY_PREFIX+"modified_datetime") },
        {"date_created", new LocalizedMessage( SORT_BY_LANGUAGE_KEY_PREFIX+"created_datetime") },
        {"archived_datetime", new LocalizedMessage( SORT_BY_LANGUAGE_KEY_PREFIX+"archived_datetime") },
        {"publication_start_datetime", new LocalizedMessage( SORT_BY_LANGUAGE_KEY_PREFIX+"published_datetime") },
    };
    static final Map SORT_ORDERS_MAP = Utility.getMapViewOfObjectPairArray( SORT_ORDERS_ARRAY) ;

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        Utility.setDefaultHtmlContentType( res );
        Writer out = res.getWriter();

        DocumentMapper documentMapper = imcref.getDocumentMapper();
        TextDocumentDomainObject parentDocument = (TextDocumentDomainObject)documentMapper.getDocument( Integer.parseInt( req.getParameter( "meta_id_value" ) ) );

        // Lets get the doc_menu_number
        int menuIndex = Integer.parseInt( req.getParameter( "doc_menu_no" ) );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        MenuEditPage menuEditPage = (MenuEditPage) Page.fromRequest(req);
        if ( req.getParameter( "cancel" ) != null || req.getParameter( "cancel.x" ) != null ) {
            menuEditPage.forward(req, res);
        } else if ( req.getParameter( "search" ) != null || req.getParameter( "search.x" ) != null ) {
            // SEARCH
            // Lets do a search among existing documents.
            // Lets collect the parameters and build a sql searchstring
            final DocumentIndex index = documentMapper.getDocumentIndex();
            BooleanQuery query = new BooleanQuery();
            String searchString = req.getParameter( "searchstring" );
            String searchPrep = req.getParameter( "search_prep" );
            try {
                if ( "or".equalsIgnoreCase( searchPrep ) ) {
                    addStringToQuery(searchString, query );
                } else {
                    String[] searchStrings = searchString.split( "\\s+" );
                    for ( String string : searchStrings ) {
                        addStringToQuery(string, query);
                    }
                }
            } catch ( org.apache.lucene.queryParser.ParseException pe ) {
                LOG.debug( "Bad query: " + searchString, pe );
            }

            String[] docTypes = req.getParameterValues( "doc_type" );
            addDocTypesToQuery( docTypes, query );

            DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );
            Date startDate = null;
            try {
                String startDateString = req.getParameter( "start_date" );
                startDate = dateFormat.parse( startDateString );
            } catch ( ParseException ignored ) {
            }
            Date endDate = null;
            try {
                String endDateString = req.getParameter( "end_date" );
                endDate = dateFormat.parse( endDateString );
            } catch ( ParseException ignored ) {
            }

            addDateRangesToQuery( startDate, endDate, req, query );

            String sortBy = req.getParameter( "sortBy" );

            // Lets get the language prefix
            String langPrefix = user.getLanguageIso639_2();

            // Lets fix the sortby list, first get the displaytexts from the database
            Set sortOrderSet = SORT_ORDERS_MAP.keySet() ;
            if ( !sortOrderSet.contains( sortBy ) ) {
                sortBy = "meta_id";
            }

            List sortOrderV = new ArrayList();
            for ( int i = 0; i < SORT_ORDERS_ARRAY.length; i++ ) {
                sortOrderV.add( SORT_ORDERS_ARRAY[i][0] );
                sortOrderV.add( ((LocalizedMessage)SORT_ORDERS_ARRAY[i][1]).toLocalizedString( user ) );
            }

            LOG.debug( "Query: " + query );
            List searchResultDocuments = index.search( new SimpleDocumentQuery(query), user );

            if ( 0 == searchResultDocuments.size() ) {
                if ( StringUtils.isNumeric(searchString) ) {
                    int documentId = Integer.parseInt(searchString) ;
                    DocumentDomainObject document = documentMapper.getDocument(documentId);
                    if (canAddToMenu(user, parentDocument, document)) {
                        addDocumentToMenu(document, parentDocument, user, menuEditPage);
                        redirectBackToMenu( req, res, menuEditPage );
                        return ;
                    }
                }
            } else if ( 1 == searchResultDocuments.size() ) {
                DocumentDomainObject onlyDocumentFound = (DocumentDomainObject) searchResultDocuments.get(0);
                if ( searchString.equals( "" + onlyDocumentFound.getId() ) && canAddToMenu(user, parentDocument, onlyDocumentFound) ) {
                    addDocumentToMenu( onlyDocumentFound, parentDocument, user, menuEditPage);
                    redirectBackToMenu( req, res, menuEditPage );
                    return;
                }
            }
            createSearchResultsPage( imcref, user, langPrefix, searchResultDocuments, parentDocument, menuIndex, req, startDate,
                                     dateFormat, endDate, docTypes, sortBy, sortOrderV, out, menuEditPage);

        } else {
            addDocumentsFromRequestToMenu( user, req, imcref, parentDocument, menuEditPage);
            redirectBackToMenu( req, res, menuEditPage );
        }
    }

    private class DocumentDomainObjectComparator implements Comparator {

        private String sortBy;

        private DocumentDomainObjectComparator( String sortBy ) {
            this.sortBy = sortBy;
        }

        public int compare( Object o1, Object o2 ) {
            DocumentDomainObject d1 = (DocumentDomainObject)o1;
            DocumentDomainObject d2 = (DocumentDomainObject)o2;
            if ( "meta_headline".equalsIgnoreCase( sortBy ) ) {
                return d1.getHeadline().compareToIgnoreCase( d2.getHeadline() );
            } else if ( "doc_type".equalsIgnoreCase( sortBy ) ) {
                return d1.getDocumentTypeId() - d2.getDocumentTypeId();
            } else if ( "date_modified".equalsIgnoreCase( sortBy ) ) {
                return -1 * Utility.compareDatesWithNullFirst( d1.getModifiedDatetime(), d2.getModifiedDatetime() );
            } else if ( "date_created".equalsIgnoreCase( sortBy ) ) {
                return -1 * Utility.compareDatesWithNullFirst( d1.getCreatedDatetime(), d2.getCreatedDatetime() );
            } else if ( "date_activated".equalsIgnoreCase( sortBy ) ) {
                return Utility.compareDatesWithNullFirst( d1.getPublicationStartDatetime(), d2.getPublicationStartDatetime() );
            } else {
                return d1.getId() - d2.getId();
            }
        }

    }

    private void addDocumentsFromRequestToMenu(UserDomainObject user, HttpServletRequest req,
                                               ImcmsServices imcref, TextDocumentDomainObject parentDocument,
                                               MenuEditPage menuEditPage) {
        req.getSession().setAttribute( "flags", new Integer( ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_MENUS ) );

        // get the seleced existing docs
        String[] values = req.getParameterValues( "existing_meta_id" );
        if ( values == null ) {
            values = new String[0];
        }

        DocumentMapper documentMapper = imcref.getDocumentMapper();

        // Lets loop through all the selected existsing meta ids and add them to the current menu
        for ( String value : values ) {
            int existingDocumentId = Integer.parseInt(value);

            DocumentDomainObject existingDocument = documentMapper.getDocument(existingDocumentId);
            // Add the document in menu if user is admin for the document OR the document is shared.
            addDocumentToMenu(existingDocument, parentDocument, user, menuEditPage);

        }
        try {
            menuEditPage.save(user);
        } catch ( DocumentSaveException e ) {
            throw new UnhandledException(e);
        }
    }

    private void redirectBackToMenu(HttpServletRequest req, HttpServletResponse res,
                                    MenuEditPage page) throws IOException, ServletException {
        page.forward(req, res);
    }

    private void addDocumentToMenu(DocumentDomainObject document, TextDocumentDomainObject parentDocument,
                                   UserDomainObject user, MenuEditPage menuEditPage) {
        boolean canAddToMenu = canAddToMenu(user, parentDocument, document);
        if ( canAddToMenu ) {
            menuEditPage.getMenu().addMenuItem( new MenuItemDomainObject(new DirectDocumentReference(document)) );
        }
    }

    private boolean canAddToMenu(UserDomainObject user, TextDocumentDomainObject parentDocument,
                                 DocumentDomainObject document) {
        Set allowedDocumentTypeIds = ( (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(parentDocument) ).getAllowedDocumentTypeIds() ;
        boolean sharePermission = user.canAddDocumentToAnyMenu( document );
        return sharePermission && (allowedDocumentTypeIds.isEmpty() || allowedDocumentTypeIds.contains(new Integer(document.getDocumentTypeId())));
    }

    private void createSearchResultsPage(ImcmsServices imcref, UserDomainObject user,
                                         String langPrefix, List searchResultDocuments,
                                         TextDocumentDomainObject parentDocument,
                                         int doc_menu_no, HttpServletRequest req, Date startDate,
                                         DateFormat dateFormat, Date endDate, String[] docTypes, String sortBy,
                                         List sortOrderV, Writer out, MenuEditPage page) throws IOException {
        Comparator searchResultsComparator = new DocumentDomainObjectComparator( sortBy );
        Collections.sort( searchResultDocuments, searchResultsComparator );

        List outVector = new ArrayList();

        // Lets get the resultpage fragment used for an result
        String oneRecHtmlSrc = imcref.getAdminTemplate( ONE_SEARCH_HIT, user, null );

        // Lets get all document types and put them in a hashTable
        String[][] allDocTypesArray = imcref.getAllDocumentTypes( langPrefix );
        Map allDocTypesHash = convertToMap( allDocTypesArray );

        // Lets parse the searchresults
        StringBuffer searchResults = parseSearchResults(oneRecHtmlSrc, searchResultDocuments, allDocTypesHash, user, req);

        // Lets get the surrounding resultpage fragment used for all the result
        // and parse all the results into this summarize html template for all the results
        List tmpV = new ArrayList();
        tmpV.add( "#searchResults#" );
        tmpV.add( searchResults.toString() );
        searchResults.replace( 0, searchResults.length(), imcref.getAdminTemplate( SEARCH_RESULTS, user, tmpV ) );

        // Lets parse out hidden fields
        outVector.add( "#meta_id#" );
        outVector.add( "" + parentDocument.getId() );
        outVector.add( "#doc_menu_no#" );
        outVector.add( "" + doc_menu_no );
        outVector.add( "#page#" );
        outVector.add( page.getSessionAttributeName() );

        // Lets get the searchstring and add it to the page
        outVector.add( "#searchstring#" );
        String searchStr = req.getParameter( "searchstring" ) == null ? "" : req.getParameter( "searchstring" );
        outVector.add( searchStr );

        outVector.add( "#start_date#" );
        if ( startDate == null ) {
            outVector.add( "" );
        } else {
            outVector.add( dateFormat.format( startDate ) );
        }

        outVector.add( "#end_date#" );
        outVector.add( dateFormat.format( endDate ) );

        if ( docTypes != null ) {
            // Lets take care of the document types. Get those who were selected
            // and select those again in the page to send back to the user.
            // First, put them in an hashtable for easy access.
            Map selectedDocTypes = new HashMap( docTypes.length );
            for ( String docType : docTypes ) {
                selectedDocTypes.put(docType, docType);
            }

            // Lets get all possible values of for the documenttypes from database
            for ( String[] docType : allDocTypesArray ) {
                outVector.add("#checked_" + docType[0] + "#");
                if ( selectedDocTypes.containsKey(docType[0]) ) {
                    outVector.add("checked");
                } else {
                    outVector.add("");
                }
            }

        }

        // Lets take care of the created, changed boxes.
        // first, getallchecked values and put them in a hashtable
        String[] includeDocs = req.getParameterValues( "include_doc" );
        if ( includeDocs == null ) {
            includeDocs = new String[0];
        }

        Set selectedIncludeDocs = new HashSet( includeDocs.length );
        for ( String includeDoc : includeDocs ) {
            selectedIncludeDocs.add(includeDoc);
        }

        // Lets create an array with all possible values.
        // in this case just changed resp. created
        String[] allPossibleIncludeDocsValues = {"created", "changed"};
        for ( String allPossibleIncludeDocsValue : allPossibleIncludeDocsValues ) {
            outVector.add("#include_check_" + allPossibleIncludeDocsValue + "#");
            if ( selectedIncludeDocs.contains(allPossibleIncludeDocsValue) ) {
                outVector.add("checked");
            } else {
                outVector.add("");
            }
        }

        // Lets take care of the search_prep condition, eg and / or
        // first, getallchecked values and put them in a hashtable
        String[] searchPrepArr = req.getParameterValues( "search_prep" );
        if ( searchPrepArr == null ) {
            searchPrepArr = new String[0];
        }

        Map selectedsearchPrep = new HashMap( searchPrepArr.length );
        for ( String aSearchPrepArr : searchPrepArr ) {
            selectedsearchPrep.put(aSearchPrepArr, aSearchPrepArr);
        }
        // Lets create an array with all possible values.
        // in this case just changed resp. created
        String[] allPossibleSearchPreps = {"and", "or"};
        for ( String allPossibleSearchPrep : allPossibleSearchPreps ) {
            outVector.add("#search_prep_check_" + allPossibleSearchPrep + "#");
            if ( selectedsearchPrep.containsKey(allPossibleSearchPrep) ) {
                outVector.add("checked");
            } else {
                outVector.add("");
            }
        }

        String sortOrderStr = Html.createOptionList( sortOrderV, sortBy );
        outVector.add( "#sortBy#" );
        outVector.add( sortOrderStr );

        outVector.add( "#searchResults#" );
        outVector.add( searchResults.toString() );

        // Send page to browser
        // htmlOut = imcref.replaceTagsInStringWithData( htmlOut, outVector);
        String htmlOut = imcref.getAdminTemplate( ADMIN_TEMPLATE_EXISTING_DOC, user, outVector );
        out.write( htmlOut );
    }

    private void addDocTypesToQuery( String[] docTypes, BooleanQuery query ) {
        BooleanQuery docTypesQuery = new BooleanQuery();
        for ( int i = 0; null != docTypes && i < docTypes.length; i++ ) {
            String docType = docTypes[i];
            docTypesQuery.add( new TermQuery( new Term( "doc_type_id", docType ) ), false, false );
        }
        query.add( docTypesQuery, true, false );
    }

    private void addDateRangesToQuery( Date startDate, Date endDate, HttpServletRequest req, BooleanQuery query ) {
        if ( null != startDate || null != endDate ) {
            String[] wantedDateFields = req.getParameterValues( "include_doc" );
            for ( int i = 0; null != wantedDateFields && i < wantedDateFields.length; i++ ) {
                String wantedDateField = wantedDateFields[i];
                String wantedIndexDateField;
                if ( "created".equalsIgnoreCase( wantedDateField ) ) {
                    wantedIndexDateField = "created_datetime";
                } else if ( "changed".equalsIgnoreCase( wantedDateField ) ) {
                    wantedIndexDateField = "modified_datetime";
                } else {
                    continue;
                }
                Term startDateTerm = null != startDate
                                     ? new Term( wantedIndexDateField, DateField.dateToString( startDate ) ) : null;
                Term endDateTerm = null != endDate
                                   ? new Term( wantedIndexDateField,
                                               DateField.dateToString( addOneDayToDate( endDate ) ) )
                                   : null;
                RangeQuery dateRangeQuery = new RangeQuery( startDateTerm, endDateTerm, true );
                query.add( dateRangeQuery, true, false );
            }
        }
    }

    private Date addOneDayToDate( Date date ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        calendar.add( Calendar.DATE, 1 );
        return calendar.getTime();
    }

    private void addStringToQuery(String string, BooleanQuery query)
            throws org.apache.lucene.queryParser.ParseException {
        Query textQuery = new DefaultQueryParser().parse( string );
        query.add( textQuery, true, false );
    }

    /**
     * Local helpmehtod
     * Takes an array as argument and creates an hashtable the information.
     * Expects that the first element will be the key and the next element in the
     * array will be the value.
     */

    private static Map convertToMap( String[][] arr ) {

        Map h = new HashMap();
        for ( String[] anArr : arr ) {
            h.put(anArr[0], anArr[1]);
        }
        return h;
    }

    /**
     * Local helpmehtod
     * Parses all the searchhits and returns an StringBuffer
     */

    private static StringBuffer parseSearchResults(String oneRecHtmlSrc,
                                                   List<DocumentDomainObject> searchResultDocuments, Map docTypesHash, UserDomainObject user, HttpServletRequest request) {
        StringBuffer searchResults = new StringBuffer( 1024 );

        for ( DocumentDomainObject document : searchResultDocuments ) {

            DateFormat dateFormat = new SimpleDateFormat(DateConstants.DATETIME_FORMAT_STRING);
            String alias = document.getAlias();
            int meta_id = document.getId();
            String[] data = {
                    "#alias#", alias  ,
                    "#meta_id#", meta_id+"",
                    "#page_name#", document.getName() ,
                    "#status#", Html.getStatusIconTemplate(document, user),
                    "#doc_type#", (String) docTypesHash.get("" + document.getDocumentTypeId()),
                    "#meta_headline#", document.getHeadline() ,
                    "#meta_text#", document.getMenuText(),
                    "#date_created#", formatDate(dateFormat, document.getCreatedDatetime()),
                    "#date_modified#", formatDate(dateFormat, document.getModifiedDatetime()),
                    "#date_activated#", formatDate(dateFormat, document.getPublicationStartDatetime()),
                    "#date_archived#", formatDate(dateFormat, document.getArchivedDatetime()),
                    "#archive#", document.isArchived() ? "1" : "0",
            };

            searchResults.append(Parser.parseDoc(oneRecHtmlSrc, data));
        }
        return searchResults;
    }

    private static String formatDate( DateFormat dateFormat, Date datetime ) {
        return null != datetime ? dateFormat.format( datetime ) : "&nbsp;";
    }

} // End class
