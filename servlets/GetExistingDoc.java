
import imcode.external.diverse.Html;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentIndex;
import imcode.server.document.DocumentMapper;
import imcode.util.DateHelper;
import imcode.util.Parser;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.document.DateField;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Templates in use by this servlet:
 * existing_doc.html     = the startpage
 * existing_doc_hit.html = One record hit html
 * existing_doc_res.html = summary page for all hits on the search
 */

public class GetExistingDoc extends HttpServlet {

    private static Logger log = Logger.getLogger( GetExistingDoc.class.getName() );

    /**
     * doPost()
     */
    public void service( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();

        imcode.server.user.UserDomainObject user;
        String values[];
        int existing_meta_id;

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        // Lets get the meta_id for the page were adding stuff to
        String tmpMetaIdS = req.getParameter( "meta_id_value" );

        int meta_id;

        try {
            meta_id = Integer.parseInt( tmpMetaIdS );
        } catch ( NumberFormatException exc ) {
            log.warn( "No meta id could be found. Check the template" );
            return;
        }

        // Get the session
        HttpSession session = req.getSession( true );

        // Does the session indicate this user already logged in?
        Object done = session.getAttribute( "logon.isDone" );  // marker object
        user = (imcode.server.user.UserDomainObject)done;

        if ( done == null ) {
            // No logon.isDone means he hasn't logged in.
            // Save the request URL as the true target and redirect to the login page.
            String scheme = req.getScheme();
            String serverName = req.getServerName();
            int p = req.getServerPort();
            String port = ( p == 80 ) ? "" : ":" + p;
            res.sendRedirect( scheme + "://" + serverName + port + start_url );
            return;
        }

        // Lets get the doc_menu_number
        int doc_menu_no;
        try {
            doc_menu_no = Integer.parseInt( req.getParameter( "doc_menu_no" ) );
        } catch ( NumberFormatException ex ) {
            log.error( "\"doc_menu_no\" not found in GetExistingDoc.", ex );
            return;
        }

        StringBuffer searchResults;

        if ( ( req.getParameter( "cancel" ) != null ) || ( req.getParameter( "cancel.x" ) != null ) ) {
            String tempstring = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( tempstring != null ) {
                out.write( tempstring );
            }
            return;
        } else if ( ( req.getParameter( "search" ) != null ) || ( req.getParameter( "search.x" ) != null ) ) {
            // SEARCH
            // Lets do a search among existing documents.
            // Lets collect the parameters and build a sql searchstring
            final DocumentIndex documentIndex = imcref.getDocumentMapper().getDocumentIndex();
            BooleanQuery query = new BooleanQuery();
            String searchString = req.getParameter( "searchstring" );
            String searchPrep = req.getParameter( "search_prep" );
            try {
                if ( "or".equalsIgnoreCase( searchPrep ) ) {
                    addStringToQuery( documentIndex, searchString, query );
                } else {
                    String[] searchStrings = searchString.split( "\\s+" );
                    for ( int i = 0; i < searchStrings.length; i++ ) {
                        String string = searchStrings[i];
                        addStringToQuery( documentIndex, string, query );
                    }
                }
            } catch ( org.apache.lucene.queryParser.ParseException pe ) {
                log.warn( "Bad query: " + searchString, pe );
            }

            // Lets build a comma separetad string with the doctypes
            String docTypes[] = req.getParameterValues( "doc_type" );
            BooleanQuery docTypesQuery = new BooleanQuery() ;
            for ( int i = 0; null != docTypes && i < docTypes.length; i++ ) {
                String docType = docTypes[i];
                docTypesQuery.add( new TermQuery( new Term( "doc_type_id", docType ) ), false, false );
            }
            query.add( docTypesQuery, true, false );

            DateFormat dateFormat = new SimpleDateFormat( DateHelper.DATE_FORMAT_STRING );
            Date startDate = null ;
            Date endDate = null ;
            try {
                String startDateString = req.getParameter( "start_date" );
                startDate = dateFormat.parse( startDateString );
            } catch ( ParseException ignored ) { }
            try {
                String endDateString = req.getParameter( "end_date" );
                endDate = dateFormat.parse( endDateString );
            } catch ( ParseException ignored ) { }

            if (null != startDate || null != endDate) {
                String[] wantedDateFields = req.getParameterValues( "include_doc" ) ;
                for ( int i = 0; null != wantedDateFields && i < wantedDateFields.length; i++ ) {
                    String wantedDateField = wantedDateFields[i];
                    String wantedIndexDateField = null ;
                    if ("created".equalsIgnoreCase( wantedDateField )) {
                        wantedIndexDateField = "created_datetime" ;
                    } else if ("changed".equalsIgnoreCase( wantedDateField )) {
                        wantedIndexDateField = "modified_datetime" ;
                    } else {
                        continue ;
                    }
                    Term startDateTerm = null != startDate ? new Term( wantedIndexDateField, DateField.dateToString( startDate )) : null ;
                    Term endDateTerm = null != endDate ? new Term( wantedIndexDateField, DateField.dateToString( addOneDayToDate( endDate ))) : null ;
                    RangeQuery dateRangeQuery = new RangeQuery( startDateTerm, endDateTerm, true);
                    query.add(dateRangeQuery,true,false) ;
                }
            }

            String sortBy = req.getParameter( "sortBy" );

            // Lets get the language prefix
            String langPrefix = user.getLangPrefix();

            // Lets check that the sortby option is valid by run the method
            // "SortOrder_GetExistingDocs 'lang_prefix' wich will return
            // an array with all the document types. By adding the key-value pair
            // array into an hashtable and check if the sortorder exists in the hashtable.
            // we are able to determine if the sortorder is okay.

            // Lets fix the sortby list, first get the displaytexts from the database
            String[][] sortOrder = imcref.sqlProcedureMulti( "SortOrder_GetExistingDocs", new String[]{langPrefix} );
            Hashtable sortOrderHash = convert2Hashtable( sortOrder );
            if ( sortOrderHash.containsKey( sortBy ) == false ) {
                sortBy = "meta_id";
            }

            Vector sortOrderV = new Vector();
            for ( int i = 0; i < sortOrder.length; i++ ) {
                sortOrderV.add( sortOrder[i][0] );
                sortOrderV.add( sortOrder[i][1] );
            }

            log.debug("Query: "+query) ;
            DocumentDomainObject[] searchResultDocuments = documentIndex.search( query, user );
            Vector outVector = new Vector();

            // Lets get the resultpage fragment used for an result
            String oneRecHtmlSrc = imcref.parseDoc( null, "existing_doc_hit.html", user );

            // Lets get all document types and put them in a hashTable
            String[][] allDocTypesArray = imcref.getDocumentTypesInList( langPrefix );
            Hashtable allDocTypesHash = convert2Hashtable( allDocTypesArray );

            // Lets parse the searchresults
            searchResults = parseSearchResults( oneRecHtmlSrc, searchResultDocuments );

            // Lets get the surrounding resultpage fragment used for all the result
            // and parse all the results into this summarize html template for all the results
            Vector tmpV = new Vector();
            tmpV.add( "#searchResults#" );
            tmpV.add( searchResults.toString() );
            searchResults.replace( 0, searchResults.length(), imcref.parseDoc( tmpV, "existing_doc_res.html", user ) );

            // Lets parse out hidden fields
            outVector.add( "#meta_id#" );
            outVector.add( "" + meta_id );
            outVector.add( "#doc_menu_no#" );
            outVector.add( "" + doc_menu_no );

            // Lets get the searchstring and add it to the page
            outVector.add( "#searchstring#" );
            String searchStr = ( req.getParameter( "searchstring" ) == null )
                               ? "" : ( req.getParameter( "searchstring" ) );
            outVector.add( searchStr );

            // Lets get the date used in the html page, otherwise, use todays date
            String startDateStr = ( req.getParameter( "start_date" ) == null )
                                  ? "" : ( req.getParameter( "start_date" ) );
            String endDateStr = ( req.getParameter( "end_date" ) == null ) ? "" : ( req.getParameter( "end_date" ) );

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
                Hashtable selectedDocTypes = new Hashtable( docTypes.length );
                for ( int i = 0; i < docTypes.length; i++ ) {
                    selectedDocTypes.put( docTypes[i], docTypes[i] );
                }

                // Lets get all possible values of for the documenttypes from database
                for ( int i = 0; i < allDocTypesArray.length; i++ ) {
                    outVector.add( "#checked_" + allDocTypesArray[i][0] + "#" );
                    if ( selectedDocTypes.containsKey( allDocTypesArray[i][0] ) ) {
                        outVector.add( "checked" );
                    } else {
                        outVector.add( "" );
                    }
                }

            }

            // Lets take care of the created, changed boxes.
            // first, getallchecked values and put them in a hashtable
            String[] includeDocs = req.getParameterValues( "include_doc" );
            if ( includeDocs == null ) {
                includeDocs = new String[0];
            }

            Hashtable selectedIncludeDocs = new Hashtable( includeDocs.length );
            for ( int i = 0; i < includeDocs.length; i++ ) {
                selectedIncludeDocs.put( includeDocs[i], includeDocs[i] );
            }

            // Lets create an array with all possible values.
            // in this case just changed resp. created
            String[] allPossibleIncludeDocsValues = {"created", "changed"};
            for ( int i = 0; i < allPossibleIncludeDocsValues.length; i++ ) {
                outVector.add( "#include_check_" + allPossibleIncludeDocsValues[i] + "#" );
                if ( selectedIncludeDocs.containsKey( allPossibleIncludeDocsValues[i] ) ) {
                    outVector.add( "checked" );
                } else {
                    outVector.add( "" );
                }
            }


            // Lets take care of the search_prep condition, eg and / or
            // first, getallchecked values and put them in a hashtable
            String[] searchPrepArr = req.getParameterValues( "search_prep" );
            if ( searchPrepArr == null ) {
                searchPrepArr = new String[0];
            }

            Hashtable selectedsearchPrep = new Hashtable( searchPrepArr.length );
            for ( int i = 0; i < searchPrepArr.length; i++ ) {
                selectedsearchPrep.put( searchPrepArr[i], searchPrepArr[i] );
            }
            // Lets create an array with all possible values.
            // in this case just changed resp. created
            String[] allPossibleSearchPreps = {"and", "or"};
            for ( int i = 0; i < allPossibleSearchPreps.length; i++ ) {
                outVector.add( "#search_prep_check_" + allPossibleSearchPreps[i] + "#" );
                if ( selectedsearchPrep.containsKey( allPossibleSearchPreps[i] ) ) {
                    outVector.add( "checked" );
                } else {
                    outVector.add( "" );
                }
            }

            String sortOrderStr = Html.createHtmlOptionList( sortBy, sortOrderV );
            outVector.add( "#sortBy#" );
            outVector.add( sortOrderStr );

            outVector.add( "#searchResults#" );
            outVector.add( searchResults.toString() );

            // Send page to browser
            // htmlOut = imcref.parseDoc( htmlOut, outVector);
            String htmlOut = imcref.parseDoc( outVector, "existing_doc.html", user );
            out.write( htmlOut );
            return;
        } else {
            // ************** Lets add a document ***********************
            user.put( "flags", new Integer( 262144 ) );

            // get the seleced existing docs
            values = req.getParameterValues( "existing_meta_id" );
            if ( values == null ) {
                values = new String[0];
            }

            // Lets loop through all the selected existsing meta ids and add them to the current menu
            try {
                for ( int m = 0; m < values.length; m++ ) {
                    existing_meta_id = Integer.parseInt( values[m] );

                    // Fetch all doctypes from the db and put them in an option-list
                    // First, get the doc_types the current user may use.
                    String[] user_dt = imcref.sqlProcedure( "GetDocTypesForUser",
                                                            new String[]{
                                                                "" + meta_id, "" + user.getUserId(),
                                                                user.getLangPrefix()
                                                            } );
                    HashSet user_doc_types = new HashSet();

                    // I'll fill a HashSet with all the doc-types the current user may use,
                    // for easy retrieval.
                    for ( int i = 0; i < user_dt.length; i += 2 ) {
                        user_doc_types.add( user_dt[i] );
                    }

                    int doc_type = DocumentMapper.sqlGetDocTypeFromMeta( imcref, existing_meta_id );

                    // Add the document in menu if user is admin for the document OR the document is shared.
                    boolean sharePermission = imcref.checkUserDocSharePermission( user, existing_meta_id );
                    if ( user_doc_types.contains( "" + doc_type ) && sharePermission ) {
                        try {
                            imcref.addExistingDoc( meta_id, user, existing_meta_id, doc_menu_no );
                        } catch ( DocumentMapper.DocumentAlreadyInMenuException e ) {
                            //ok, already in menu
                        }
                    }

                } // End of for loop
            } catch ( NumberFormatException ex ) {
                String tempstring = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
                if ( tempstring != null ) {
                    out.write( tempstring );
                }
                return;
            }

            String tempstring = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( tempstring != null ) {
                out.write( tempstring );
            }
        }
    }

    private Date addOneDayToDate( Date date ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        calendar.add( Calendar.DATE, 1 );
        return calendar.getTime() ;
    }

    private void addStringToQuery( final DocumentIndex documentIndex, String string, BooleanQuery query )
            throws org.apache.lucene.queryParser.ParseException {
        Query textQuery = documentIndex.parseLucene( string );
        query.add( textQuery, true, false );
    }

    /**
     * Returns the variables used to parse one row in the resultset from the
     * search page
     */
    private static Vector getSearchHitVector() {
        Vector vector = new Vector();
        vector.add( "#meta_id#" );
        vector.add( "#doc_type#" );
        vector.add( "#meta_headline#" );
        vector.add( "#meta_text#" );
        vector.add( "#date_created#" );
        vector.add( "#date_modified#" );
        vector.add( "#date_activated#" );
        vector.add( "#date_archived#" );
        vector.add( "#archive#" );
        vector.add( "#shared#" );
        vector.add( "#show_meta#" );
        vector.add( "#disable_search#" );
        vector.add( "#doc_count" );
        return vector;
    }

    /**
     * Local helpmehtod
     * Takes an array as argument and creates an hashtable the information.
     * Expects that the first element will be the key and the next element in the
     * array will be the value.
     */

    private static Hashtable convert2Hashtable( String[][] arr ) {

        Hashtable h = new Hashtable();
        for ( int i = 0; i < arr.length; i++ ) {
            h.put( arr[i][0], arr[i][1] );
        }
        return h;
    }

    /**
     * Local helpmehtod
     * Parses all the searchhits and returns an StringBuffer
     */

    private static StringBuffer parseSearchResults( String oneRecHtmlSrc,
                                                    DocumentDomainObject[] searchResultDocuments ) {
        StringBuffer searchResults = new StringBuffer( 1024 );
        int docTypeIndex = 1;  // Index of where the doctype id is placed in one record array

        // Lets parse the searchresults
        Vector oneRecVariables = GetExistingDoc.getSearchHitVector();
        for ( int i = 0; i < searchResultDocuments.length; i++ ) {
            DocumentDomainObject document = searchResultDocuments[i];

            DateFormat dateFormat = new SimpleDateFormat( DateHelper.DATETIME_SECONDS_FORMAT_STRING );
            String[] data = {
                "#meta_id#", String.valueOf( document.getMetaId() ),
                "#doc_type#", String.valueOf( document.getDocumentType() ),
                "#meta_headline#", document.getHeadline(),
                "#meta_text#", document.getText(),
                "#date_created#", formatDate( dateFormat, document.getCreatedDatetime() ),
                "#date_modified#", formatDate( dateFormat, document.getModifiedDatetime() ),
                "#date_activated#", formatDate( dateFormat, document.getActivatedDatetime() ),
                "#date_archived#", formatDate( dateFormat, document.getArchivedDatetime() ),
                "#archive#", document.isArchived() ? "1" : "0",
            };

            searchResults.append( Parser.parseDoc( oneRecHtmlSrc, data ) );
        }
        return searchResults;
    }

    private static String formatDate( DateFormat dateFormat, Date datetime ) {
        return null != datetime ? dateFormat.format( datetime ) : "&nbsp;";
    }

} // End class
