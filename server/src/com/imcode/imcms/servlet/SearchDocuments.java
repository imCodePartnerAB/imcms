package com.imcode.imcms.servlet;

import imcode.external.diverse.Html;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentIndex;
import imcode.server.document.SectionDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;
import imcode.util.Utility;
import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Search documents
 */
public class SearchDocuments extends HttpServlet {

    //the templates we uses as default they are stored in template/admin/original folder
    private final static String SEARCH_PAGE_TEMPLATE = "search_documents.html";
    private final static String HIT_PAGE_TEMPLATE = "search_result.html";
    private final static String NO_HIT_PAGE_TEMPLATE = "search_result_no_hit.html";
    private final static String HIT_LINE_TEMPLATE = "search_result_list.html";

    private final static String NAV_NEXT_BUTTON = "search_nav_next.html";
    private final static String NAV_PREV_BUTTON = "search_nav_prev.html";
    private final static String NAV_ACTIVE = "search_nav_active.html";
    private final static String NAV_INACTIVE = "search_nav_inactive.html";
    private final static String NAV_AHREF = "search_nav_ahref.html";

    private final static Logger log = Logger.getLogger( "SearchDocuments" );

    /**
     * doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        //this is the params we can get from the browser
        String searchString = req.getParameter( "question_field" ) == null ? "" : req.getParameter( "question_field" );
        String startNr = req.getParameter( "starts" ) == null ? "0" : req.getParameter( "starts" );
        String hitsAtTime = req.getParameter( "no_of_hits" ) == null ? "15" : req.getParameter( "no_of_hits" );
        String prev_search = req.getParameter( "prev_search" ) == null ? "" : req.getParameter( "prev_search" );

        String sectionParameter = req.getParameter( "section" );
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        SectionDomainObject section = sectionParameter == null
                                      ? null
                                      : imcref.getDocumentMapper().getSectionById(
                                              Integer.parseInt( sectionParameter ) );

        // Lets save searchstring typed by user
        String originalSearchString = searchString;

        // If user hit navigation button lets save prev settings
        if ( req.getParameter( "hitsNo" ) != null ) {
            originalSearchString = prev_search;
        }

        //ok the rest of params we need to set up search sql
        DocumentDomainObject[] searchResults;
        int hits = 0;
        //the counter to tell vere in the hitarr to start
        int startNrInt = 0;
        try {
            startNrInt = Integer.parseInt( startNr );
        } catch ( NumberFormatException nfe ) {
            //do nothing lets start at 0
        }

        //the counter to tell how many hits to show
        int noOfHit = 1000;
        try {
            noOfHit = Integer.parseInt( hitsAtTime );
        } catch ( NumberFormatException nfe ) {
            //do nothing lets start at 0
        }

        UserDomainObject user = Utility.getLoggedOnUser( req );
        HttpSession session = req.getSession();

        //check if nex or prev butons was selected or if we must do a new search i db
        if ( req.getParameter( "next_button" ) != null ) {
            noOfHit = Integer.parseInt( req.getParameter( "hitsNo" ) );
            startNrInt = Integer.parseInt( req.getParameter( "startNr" ) );
            searchResults = (DocumentDomainObject[])session.getAttribute( "search_hit_list" );
            if ( searchResults == null ) {
                res.sendRedirect( "StartDoc" );
            }
        } else if ( req.getParameter( "prev_button" ) != null ) {
            noOfHit = Integer.parseInt( req.getParameter( "hitsNo" ) );
            startNrInt = Integer.parseInt( req.getParameter( "startNr" ) ) - ( noOfHit + noOfHit );
            searchResults = (DocumentDomainObject[])session.getAttribute( "search_hit_list" );
            if ( searchResults == null ) {
                res.sendRedirect( "StartDoc" );
            }
        } else {

            searchResults = searchDocuments( searchString,
                                             section,
                                             user );
            session.setAttribute( "search_hit_list", searchResults );
        }

        if ( searchResults != null ) {
            hits = searchResults.length;
        }



        //the no_of_hits list
        String noofhits_option_list = "";
        String selected_hitsToShow = req.getParameter( "no_of_hits" );
        if ( selected_hitsToShow == null ) {
            selected_hitsToShow = "10";
        }
        if ( req.getParameter( "hitsNo" ) != null ) {
            selected_hitsToShow = req.getParameter( "prev_hitsToShow" );
        }

        for ( int i = 10; i <= 1000; i *= 10 ) {
            noofhits_option_list += "<option value=\"" + i + "\" "
                                    + ( i == Integer.parseInt( selected_hitsToShow ) ? "selected" : "" )
                                    + ">"
                                    + i
                                    + "</option>";
        }


        //the sections list
        String[] all_sections = imcref.sqlProcedure( "SectionGetAll", new String[0] );
        String section_option_list = "";
        String selected_sectionToShow = req.getParameter( "section" );
        String strSectionArry = "\'";

        for ( int i = 0; i < all_sections.length; i += 2 ) {
            strSectionArry += all_sections[i + 1];
            if ( i < all_sections.length - 2 ) {
                strSectionArry += "\',\'";
            }
        }
        strSectionArry += "\'";

        if ( req.getParameter( "hitsNo" ) != null ) {
            selected_sectionToShow = req.getParameter( "prev_sectionToShow" );
        }
        if ( all_sections != null ) {
            List onlyTemp = new ArrayList();
            for ( int i = 0; i < all_sections.length; i++ ) {
                onlyTemp.add( all_sections[i] );
            }
            section_option_list = Html.createOptionList( selected_sectionToShow, onlyTemp );
        }


        //parses the result page to send back

        //ok lets see what folder to get the search-templates from.
        // @show = parameter with the folder name. If we get no parameter lets use folder original.
        String oneRecHtmlSrc, resultHtmlSrc, noHitHtmlStr, returnStr;
        String show = req.getParameter( "show" );

        String langPrefix = user.getLanguageIso639_2();
        String templatePath = langPrefix + "/admin/search/";

        if ( show == null ) {
            show = "original"; // default folder for search-templates
        }

        String nextTextTemplate = imcref.getSearchTemplate( templatePath + show + "/" + NAV_NEXT_BUTTON );
        String prevTextTemplate = imcref.getSearchTemplate( templatePath + show + "/" + NAV_PREV_BUTTON );
        String activeTemplate = imcref.getSearchTemplate( templatePath + show + "/" + NAV_ACTIVE );
        String inActiveTemplate = imcref.getSearchTemplate( templatePath + show + "/" + NAV_INACTIVE );
        String ahrefTemplate = imcref.getSearchTemplate( templatePath + show + "/" + NAV_AHREF );
        oneRecHtmlSrc = imcref.getSearchTemplate( templatePath + show + "/" + HIT_LINE_TEMPLATE );
        resultHtmlSrc = imcref.getSearchTemplate( templatePath + show + "/" + HIT_PAGE_TEMPLATE );
        noHitHtmlStr = imcref.getSearchTemplate( templatePath + show + "/" + NO_HIT_PAGE_TEMPLATE );
        //Fix kolla att ingen mall är null om så returnera alla hitts i en lång lista



        //lets set up the <-prev- 1 2 .. -next-> stuff
        boolean nextButtonOn = false;
        boolean prevButtonOn = false;
        int hitPages;
        StringBuffer buttonsSetupHtml = new StringBuffer( "" );

        if ( hits > 0 ) {
            if ( startNrInt + noOfHit < hits ) {
                //ok we need to light the nextButton
                nextButtonOn = true;
            }
            if ( startNrInt - noOfHit >= 0 ) {
                //ok we need the prev button
                prevButtonOn = true;
            }
            //now we need to count the number of hit-pages
            hitPages = hits / noOfHit;
            if ( hits % noOfHit != 0 ) {
                hitPages++;
            }


            //ok this is a tricky part to set up the html for the next button and so on
            //lets start with the prev button
            if ( prevButtonOn ) {
                String[] prevArrOn = {
                    "#nexOrPrev#", "0", "#startNr#", startNrInt - noOfHit + "", "#value#", prevTextTemplate
                };
                buttonsSetupHtml.append( Parser.parseDoc( ahrefTemplate, prevArrOn ) + "\n" );
            } else {
                String[] prevArrOff = {"#value#", prevTextTemplate};
                buttonsSetupHtml.append( Parser.parseDoc( inActiveTemplate, prevArrOff ) + "\n" );
            }
            //ok now we must do some looping to add all the hit page numbers
            for ( int y = 0; y < hitPages; y++ ) {
                //lets see if its the choosen one
                if ( ( y * noOfHit ) == startNrInt ) {
                    String[] pageActive = {"#value#", ( y + 1 ) + ""};
                    buttonsSetupHtml.append( Parser.parseDoc( activeTemplate, pageActive ) + "\n" );
                } else {
                    String[] pageInactive = {
                        "#nexOrPrev#", "1", "#startNr#", ( y * noOfHit ) + "", "#value#", ( y + 1 ) + ""
                    };
                    buttonsSetupHtml.append( Parser.parseDoc( ahrefTemplate, pageInactive ) + "\n" );
                }
            }
            //lets do the nextButton
            if ( nextButtonOn ) {
                String[] nextArrOn = {
                    "#nexOrPrev#", "1", "#startNr#", ( startNrInt + noOfHit ) + "", "#value#", nextTextTemplate
                };
                buttonsSetupHtml.append( Parser.parseDoc( ahrefTemplate, nextArrOn ) + "\n" );
            } else {
                String[] nextArrOff = {"#value#", nextTextTemplate};
                buttonsSetupHtml.append( Parser.parseDoc( inActiveTemplate, nextArrOff ) + "\n" );
            }
        }//end (hits > 0)

        StringBuffer buff = SearchDocuments.parseSearchResults( oneRecHtmlSrc, searchResults, startNrInt, noOfHit );
        //if there isnt any hitts lets add the no hit message
        if ( buff.length() == 0 ) {
            buff.append( noHitHtmlStr );
        }
        Vector tags = new Vector();
        tags.add( "#search_list#" );
        tags.add( buff.toString() );
        tags.add( "#nrhits#" );
        tags.add( "" + hits );
        tags.add( "#searchstring#" );
        tags.add( originalSearchString );
        tags.add( "#page_buttons#" );
        tags.add( buttonsSetupHtml.toString() );
        tags.add( "#hitsNo#" );
        tags.add( noOfHit + "" );
        tags.add( "#section_list#" );
        tags.add( section_option_list );
        tags.add( "#noofhits_list#" );
        tags.add( noofhits_option_list );
        tags.add( "#hitsToShow#" );
        tags.add( selected_hitsToShow );
        tags.add( "#sectionToShow#" );
        tags.add( selected_sectionToShow );
        tags.add( "#sectionArry#" );
        tags.add( strSectionArry );

        returnStr = Parser.parseDoc( resultHtmlSrc, (String[])tags.toArray( new String[tags.size()] ) );



        //now lets send it to browser
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();
        out.print( returnStr );
        out.flush();
        out.close();
        return;
    } // End of doPost

    private DocumentDomainObject[] searchDocuments( String searchString,
                                                    SectionDomainObject section, UserDomainObject user )
            throws IOException {
        DocumentIndex documentIndex = ApplicationServer.getIMCServiceInterface().getDocumentMapper().getDocumentIndex();
        BooleanQuery query = new BooleanQuery();
        if ( null != searchString && !"".equals( searchString.trim() ) ) {
            try {
                Query textQuery = documentIndex.parseLucene( searchString );
                query.add( textQuery, true, false );
            } catch ( ParseException e ) {
                log.warn( e.getMessage() + " in search-string " + searchString );
            }
        }

        if ( null != section ) {
            Query sectionQuery = new TermQuery( new Term( "section", section.getName().toLowerCase() ) );
            query.add( sectionQuery, true, false );
        }

        return documentIndex.search( query, user );
    }

    /**
     * doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();

        //ok lets see what folder to get the search-templates from.
        // @show = parameter with the folder name. If we get no parameter lets use folder original.
        UserDomainObject user = Utility.getLoggedOnUser( req );
        String langPrefix = user.getLanguageIso639_2();
        String templatePath = langPrefix + "/admin/search/";
        String show = req.getParameter( "show" );
        if ( show == null ) {
            show = "original"; // default folder for search-templates
        }

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String templateStr = imcref.getSearchTemplate( templatePath + show + "/" + SEARCH_PAGE_TEMPLATE );


        //the no_of_hits list
        String noofhits_option_list = "";
        String selected = req.getParameter( "no_of_hits" );
        if ( selected == null ) {
            selected = "10";
        }

        for ( int i = 10; i < 101; i += 10 ) {
            noofhits_option_list += "<option value=\"" + i + "\" "
                                    + ( i == Integer.parseInt( selected ) ? "selected" : "" )
                                    + ">"
                                    + i
                                    + "</option>";
        }

        //the sections list
        String[] all_sections = imcref.sqlProcedure( "SectionGetAll", new String[0] );
        String section_option_list = "";
        selected = req.getParameter( "section" );
        if ( all_sections != null ) {
            Vector onlyTemp = new Vector();
            for ( int i = 0; i < all_sections.length; i++ ) {

                onlyTemp.add( all_sections[i] );
            }
            section_option_list = Html.createOptionList( selected, onlyTemp );
        }


        //	String originalSearchString = req.getParameter("question_field") == null? "":req.getParameter("question_field") ;

        // Lets get the html file we use as template
        Vector tags = new Vector();
        tags.add( "#search_hit_list#" );
        tags.add( "" );
        tags.add( "#section_list#" );
        tags.add( section_option_list );
        tags.add( "#noofhits_list#" );
        tags.add( noofhits_option_list );
        //	tags.add("#searchstring#");		tags.add(originalSearchString);


        out.print( Parser.parseDoc( templateStr, (String[])tags.toArray( new String[tags.size()] ) ) );
        out.flush();
        out.close();
        return;
    } // End of doGet

    private static StringBuffer parseSearchResults( String oneRecHtmlSrc,
                                                    DocumentDomainObject[] searchResults, int startValue,
                                                    int numberToParse ) {
        StringBuffer htmlSearchResults = new StringBuffer( "" );
        int stop = startValue + numberToParse;
        if ( stop >= searchResults.length ) {
            stop = searchResults.length;
        }
        // Lets parse the searchresults
        for ( int i = startValue; i < stop; i++ ) {
            DocumentDomainObject document = searchResults[i];
            String[] oneHitTags = SearchDocuments.getSearchHitTags( i, document );

            htmlSearchResults.append( Parser.parseDoc( oneRecHtmlSrc, oneHitTags ) );
        }
        return htmlSearchResults;
    }

    /**
     * Returns all possible variables that might be used when parse the oneRecLine to the
     * search page
     */
    private static String[] getSearchHitTags( int searchHitIndex, DocumentDomainObject document ) {
        return new String[]{
            "#meta_id#", "" + document.getId(),
            "#doc_type#", "" + document.getDocumentTypeId(),
            "#meta_headline#", document.getHeadline(),
            "#meta_text#", document.getMenuText(),
            "#date_created#", "" + ObjectUtils.defaultIfNull( document.getCreatedDatetime(), "&nbsp;" ),
            "#date_modified#", "" + ObjectUtils.defaultIfNull( document.getModifiedDatetime(), "&nbsp;" ),
            "#date_activated#", "" + ObjectUtils.defaultIfNull( document.getPublicationStartDatetime(), "&nbsp;" ),
            "#date_archived#", "" + ObjectUtils.defaultIfNull( document.getArchivedDatetime(), "&nbsp;" ),
            "#archive#", document.isArchived() ? "1" : "0",
            "#shared#", "0",
            "#show_meta#", "0",
            "#disable_search#", "1",
            "#meta_image#", document.getMenuImage(),
            "#hit_nbr#", "" + ( 1 + searchHitIndex )
        };
    }

} // End class
