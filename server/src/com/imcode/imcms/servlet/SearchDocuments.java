package com.imcode.imcms.servlet;

import imcode.external.diverse.Html;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.SectionDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;
import imcode.util.Utility;
import imcode.util.HttpSessionUtils;
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

public class SearchDocuments extends HttpServlet {

    public final static String PARAM__DOCUMENT_TYPE = "documentType";

    //the templates we uses as default they are stored in template/admin/original folder
    private final static String SEARCH_PAGE_TEMPLATE = "search/search_documents.html";
    private final static String HIT_PAGE_TEMPLATE = "search/search_result.html";
    private final static String NO_HIT_PAGE_TEMPLATE = "search/search_result_no_hit.html";
    private final static String HIT_LINE_TEMPLATE = "search/search_result_list.html";

    private final static String NAV_NEXT_BUTTON = "search/search_nav_next.html";
    private final static String NAV_PREV_BUTTON = "search/search_nav_prev.html";
    private final static String NAV_ACTIVE = "search/search_nav_active.html";
    private final static String NAV_INACTIVE = "search/search_nav_inactive.html";
    private final static String NAV_AHREF = "search/search_nav_ahref.html";

    private final static Logger log = Logger.getLogger(com.imcode.imcms.servlet.SearchDocuments.class.getName());
    public static final String REQUEST_ATTRIBUTE_PARAMETER__SEARCH_DOCUMENTS = "SearchDocuments";
    public static final String PARAM__SHOW_SELECT_LINK = "showSelectLinks";
    public static final String PARAM__CHOSEN_URL = "returningUrl";
    public static final String REQUEST_PARAM_SELECTED_DOCUMENT = "selectedDocumentId";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DocumentFinder documentFinder = getDocumentFinderAndAddItToRequest( request );

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        Utility.setDefaultHtmlContentType(response);
        ServletOutputStream out = response.getOutputStream();

        // Lets get the html file we use as template
        List tags = new ArrayList();
        tags.add("#document_type#");
        tags.add(getDocumentTypeString(request));
        tags.add("#show_select_links#");
        tags.add(getShowSelectLink(request));
        tags.add("#search_hit_list#");
        tags.add("");
        tags.add("#section_list#");
        tags.add(createSectionOptionList(imcref, request));
        tags.add("#noofhits_list#");
        tags.add(createNoOfHitsOptionList(getNoOfHits(request)));

        //ok lets see what folder to get the search-templates from.
        // @show = parameter with the folder name. If we get no parameter lets use folder original.
        UserDomainObject user = Utility.getLoggedOnUser(request);
        String templateStr = imcref.getAdminTemplate(SEARCH_PAGE_TEMPLATE, user, null);

        out.print(Parser.parseDoc(templateStr, (String[]) tags.toArray(new String[tags.size()])));
        out.flush();
        out.close();
    }

    private DocumentFinder getDocumentFinderAndAddItToRequest( HttpServletRequest request ) {
        DocumentFinder documentFinder = DocumentFinder.getInstance( request );
        HttpSessionUtils.addObjectToSessionAndSetSessionAttributeNameInRequest( documentFinder, request, DocumentFinder.REQUEST_ATTRIBUTE_PARAMETER__SEARCH_DOCUMENTS );
        return documentFinder;
    }

    private String getShowSelectLink(HttpServletRequest req) {
        String showSelectLink = req.getParameter(PARAM__SHOW_SELECT_LINK);
        showSelectLink = null == showSelectLink ? "false" : showSelectLink;
        return showSelectLink;
    }

    private String getDocumentTypeString(HttpServletRequest req) {
        String documentTypeToSearch = req.getParameter(PARAM__DOCUMENT_TYPE);
        documentTypeToSearch = null == documentTypeToSearch ? "" : documentTypeToSearch;
        return documentTypeToSearch;
    }

    private String createSectionOptionList(IMCServiceInterface imcref, HttpServletRequest req) {
        String selected;
        String[] all_sections = imcref.sqlProcedure("SectionGetAll", new String[0]);
        String section_option_list = "";
        selected = req.getParameter("section");
        if (all_sections != null) {
            List onlyTemp = new ArrayList();
            for (int i = 0; i < all_sections.length; i++) {

                onlyTemp.add(all_sections[i]);
            }
            section_option_list = Html.createOptionList(selected, onlyTemp);
        }
        return section_option_list;
    }

    private String createNoOfHitsOptionList(String selected) {
        String noofhits_option_list = "";
        for (int i = 10; i < 101; i += 10) {
            noofhits_option_list += "<option value=\"" + i + "\" "
                    + (i == Integer.parseInt(selected) ? "selected" : "")
                    + ">"
                    + i
                    + "</option>";
        }
        return noofhits_option_list;
    }

    private String getNoOfHits(HttpServletRequest req) {
        String GET_PARAM_NO_OF_HITS = "no_of_hits";
        String selected = req.getParameter(GET_PARAM_NO_OF_HITS);
        if (selected == null) {
            selected = "10";
        }
        return selected;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DocumentFinder documentFinder = getDocumentFinderAndAddItToRequest( request );

        //this is the params we can get from the browser
        String searchString = request.getParameter("question_field") == null ? "" : request.getParameter("question_field");
        String startNr = request.getParameter("starts") == null ? "0" : request.getParameter("starts");
        String hitsAtTime = request.getParameter("no_of_hits") == null ? "15" : request.getParameter("no_of_hits");
        String prev_search = request.getParameter("prev_search") == null ? "" : request.getParameter("prev_search");

        String sectionParameter = request.getParameter("section");
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        SectionDomainObject section = sectionParameter == null
                ? null
                : imcref.getDocumentMapper().getSectionById(Integer.parseInt(sectionParameter));

        // Lets save searchstring typed by user
        String originalSearchString = searchString;

        // If user hit navigation button lets save prev settings
        if (request.getParameter("hitsNo") != null) {
            originalSearchString = prev_search;
        }

        //ok the rest of params we need to set up search sql
        DocumentDomainObject[] searchResults;
        int hits = 0;
        //the counter to tell vere in the hitarr to start
        int startNrInt = 0;
        try {
            startNrInt = Integer.parseInt(startNr);
        } catch (NumberFormatException nfe) {
            //do nothing lets start at 0
        }

        //the counter to tell how many hits to show
        int noOfHit = 1000;
        try {
            noOfHit = Integer.parseInt(hitsAtTime);
        } catch (NumberFormatException nfe) {
            //do nothing lets start at 0
        }

        UserDomainObject user = Utility.getLoggedOnUser(request);
        HttpSession session = request.getSession();

        //check if nex or prev butons was selected or if we must do a new search i db
        if (request.getParameter("next_button") != null) {
            noOfHit = Integer.parseInt(request.getParameter("hitsNo"));
            startNrInt = Integer.parseInt(request.getParameter("startNr"));
            searchResults = (DocumentDomainObject[]) session.getAttribute("search_hit_list");
            if (searchResults == null) {
                response.sendRedirect("StartDoc");
            }
        } else if (request.getParameter("prev_button") != null) {
            noOfHit = Integer.parseInt(request.getParameter("hitsNo"));
            startNrInt = Integer.parseInt(request.getParameter("startNr")) - (noOfHit + noOfHit);
            searchResults = (DocumentDomainObject[]) session.getAttribute("search_hit_list");
            if (searchResults == null) {
                response.sendRedirect("StartDoc");
            }
        } else {
            searchResults = searchDocuments(searchString, section, getDocumentTypeString(request), user);
            session.setAttribute("search_hit_list", searchResults);
        }

        if (searchResults != null) {
            hits = searchResults.length;
        }



        //the no_of_hits list
        String noofhits_option_list = "";
        String selected_hitsToShow = request.getParameter("no_of_hits");
        if (selected_hitsToShow == null) {
            selected_hitsToShow = "10";
        }
        if (request.getParameter("hitsNo") != null) {
            selected_hitsToShow = request.getParameter("prev_hitsToShow");
        }

        for (int i = 10; i <= 1000; i *= 10) {
            noofhits_option_list += "<option value=\"" + i + "\" "
                    + (i == Integer.parseInt(selected_hitsToShow) ? "selected" : "")
                    + ">"
                    + i
                    + "</option>";
        }


        //the sections list
        String[] all_sections = imcref.sqlProcedure("SectionGetAll", new String[0]);
        String section_option_list = "";
        String selected_sectionToShow = request.getParameter("section");
        String strSectionArry = "\'";

        for (int i = 0; i < all_sections.length; i += 2) {
            strSectionArry += all_sections[i + 1];
            if (i < all_sections.length - 2) {
                strSectionArry += "\',\'";
            }
        }
        strSectionArry += "\'";

        if (request.getParameter("hitsNo") != null) {
            selected_sectionToShow = request.getParameter("prev_sectionToShow");
        }
        if (all_sections != null) {
            List onlyTemp = new ArrayList();
            for (int i = 0; i < all_sections.length; i++) {
                onlyTemp.add(all_sections[i]);
            }
            section_option_list = Html.createOptionList(selected_sectionToShow, onlyTemp);
        }


        //parses the result page to send back

        //ok lets see what folder to get the search-templates from.
        // @show = parameter with the folder name. If we get no parameter lets use folder original.
        String oneRecHtmlSrc, resultHtmlSrc, noHitHtmlStr, returnStr;

        String nextTextTemplate = imcref.getAdminTemplate(NAV_NEXT_BUTTON, user, null);
        String prevTextTemplate = imcref.getAdminTemplate(NAV_PREV_BUTTON, user, null);
        String activeTemplate = imcref.getAdminTemplate(NAV_ACTIVE, user, null);
        String inActiveTemplate = imcref.getAdminTemplate(NAV_INACTIVE, user, null);
        String ahrefTemplate = imcref.getAdminTemplate(NAV_AHREF, user, null);
        oneRecHtmlSrc = imcref.getAdminTemplate(HIT_LINE_TEMPLATE, user, null);
        resultHtmlSrc = imcref.getAdminTemplate(HIT_PAGE_TEMPLATE, user, null);
        noHitHtmlStr = imcref.getAdminTemplate(NO_HIT_PAGE_TEMPLATE, user, null);

        //lets set up the <-prev- 1 2 .. -next-> stuff
        boolean nextButtonOn = false;
        boolean prevButtonOn = false;
        int hitPages;
        StringBuffer buttonsSetupHtml = new StringBuffer("");

        if (hits > 0) {
            if (startNrInt + noOfHit < hits) {
                //ok we need to light the nextButton
                nextButtonOn = true;
            }
            if (startNrInt - noOfHit >= 0) {
                //ok we need the prev button
                prevButtonOn = true;
            }
            //now we need to count the number of hit-pages
            hitPages = hits / noOfHit;
            if (hits % noOfHit != 0) {
                hitPages++;
            }


            //ok this is a tricky part to set up the html for the next button and so on
            //lets start with the prev button
            if (prevButtonOn) {
                String[] prevArrOn = {
                    "#nexOrPrev#", "0", "#startNr#", startNrInt - noOfHit + "", "#value#", prevTextTemplate
                };
                buttonsSetupHtml.append(Parser.parseDoc(ahrefTemplate, prevArrOn) + "\n");
            } else {
                String[] prevArrOff = {"#value#", prevTextTemplate};
                buttonsSetupHtml.append(Parser.parseDoc(inActiveTemplate, prevArrOff) + "\n");
            }
            //ok now we must do some looping to add all the hit page numbers
            for (int y = 0; y < hitPages; y++) {
                //lets see if its the choosen one
                if ((y * noOfHit) == startNrInt) {
                    String[] pageActive = {"#value#", (y + 1) + ""};
                    buttonsSetupHtml.append(Parser.parseDoc(activeTemplate, pageActive) + "\n");
                } else {
                    String[] pageInactive = {
                        "#nexOrPrev#", "1", "#startNr#", (y * noOfHit) + "", "#value#", (y + 1) + ""
                    };
                    buttonsSetupHtml.append(Parser.parseDoc(ahrefTemplate, pageInactive) + "\n");
                }
            }
            //lets do the nextButton
            if (nextButtonOn) {
                String[] nextArrOn = {
                    "#nexOrPrev#", "1", "#startNr#", (startNrInt + noOfHit) + "", "#value#", nextTextTemplate
                };
                buttonsSetupHtml.append(Parser.parseDoc(ahrefTemplate, nextArrOn) + "\n");
            } else {
                String[] nextArrOff = {"#value#", nextTextTemplate};
                buttonsSetupHtml.append(Parser.parseDoc(inActiveTemplate, nextArrOff) + "\n");
            }
        }//end (hits > 0)

        boolean showSelectLink = Boolean.valueOf( getShowSelectLink(request) ).booleanValue();
        StringBuffer buff = SearchDocuments.parseSearchResults(oneRecHtmlSrc, searchResults, startNrInt, noOfHit, showSelectLink, documentFinder.getForwardReturnUrl() );
        //if there isnt any hitts lets add the no hit message
        if (buff.length() == 0) {
            buff.append(noHitHtmlStr);
        }
        List tags = new ArrayList();
        tags.add("#document_type#");
        tags.add(getDocumentTypeString(request));
        tags.add("#show_select_links#");
        tags.add(getShowSelectLink(request));
        tags.add("#search_list#");
        tags.add(buff.toString());
        tags.add("#nrhits#");
        tags.add("" + hits);
        tags.add("#searchstring#");
        tags.add(originalSearchString);
        tags.add("#page_buttons#");
        tags.add(buttonsSetupHtml.toString());
        tags.add("#hitsNo#");
        tags.add(noOfHit + "");
        tags.add("#section_list#");
        tags.add(section_option_list);
        tags.add("#noofhits_list#");
        tags.add(noofhits_option_list);
        tags.add("#hitsToShow#");
        tags.add(selected_hitsToShow);
        tags.add("#sectionToShow#");
        tags.add(selected_sectionToShow);
        tags.add("#sectionArry#");
        tags.add(strSectionArry);

        returnStr = Parser.parseDoc(resultHtmlSrc, (String[]) tags.toArray(new String[tags.size()]));



        //now lets send it to browser
        Utility.setDefaultHtmlContentType(response);
        ServletOutputStream out = response.getOutputStream();
        out.print(returnStr);
        out.flush();
        out.close();
    } // End of doPost

    private DocumentDomainObject[] searchDocuments(String searchString, SectionDomainObject section,
                                                   String documentType, UserDomainObject user) throws IOException {

        DocumentIndex reindexingIndex = ApplicationServer.getIMCServiceInterface().getDocumentMapper().getDocumentIndex();
        BooleanQuery query = new BooleanQuery();
        if (null != searchString && !"".equals(searchString.trim())) {
            try {
                Query textQuery = reindexingIndex.parseLucene(searchString);
                query.add(textQuery, true, false);
            } catch (ParseException e) {
                log.warn(e.getMessage() + " in search-string " + searchString);
            }
        }

        if (null != section) {
            Query sectionQuery = new TermQuery(new Term("section", section.getName().toLowerCase()));
            query.add(sectionQuery, true, false);
        }

        if ( !"".equals(documentType)) {
            Query sectionQuery = new TermQuery(new Term("doc_type_id", documentType));
            query.add(sectionQuery, true, false);
        }

        return reindexingIndex.search(query, user);
    }


    private static StringBuffer parseSearchResults(String oneRecHtmlSrc,
                                                   DocumentDomainObject[] searchResults, int startValue,
                                                   int numberToParse, boolean showSelectLink, String selectStr ) {
        StringBuffer htmlSearchResults = new StringBuffer("");
        int stop = startValue + numberToParse;
        if (stop >= searchResults.length) {
            stop = searchResults.length;
        }
        // Lets parse the searchresults
        for (int i = startValue; i < stop; i++) {
            DocumentDomainObject document = searchResults[i];
            String selectHref = "";
            if( showSelectLink ) {
                selectHref = selectStr + "&" + REQUEST_PARAM_SELECTED_DOCUMENT + "=" + document.getId();
            }
            String[] oneHitTags = SearchDocuments.getSearchHitTags(i, document, selectHref );

            htmlSearchResults.append(Parser.parseDoc(oneRecHtmlSrc, oneHitTags));
        }
        return htmlSearchResults;
    }

    /**
     * Returns all possible variables that might be used when parse the oneRecLine to the
     * search page
     */
    private static String[] getSearchHitTags(int searchHitIndex, DocumentDomainObject document, String chooseUrl) {
        return new String[]{
            "#meta_id#", "" + document.getId(),
            "#doc_type#", "" + document.getDocumentTypeId(),
            "#meta_headline#", document.getHeadline(),
            "#meta_text#", document.getMenuText(),
            "#date_created#", "" + ObjectUtils.defaultIfNull(document.getCreatedDatetime(), "&nbsp;"),
            "#date_modified#", "" + ObjectUtils.defaultIfNull(document.getModifiedDatetime(), "&nbsp;"),
            "#date_activated#", "" + ObjectUtils.defaultIfNull(document.getPublicationStartDatetime(), "&nbsp;"),
            "#date_archived#", "" + ObjectUtils.defaultIfNull(document.getArchivedDatetime(), "&nbsp;"),
            "#archive#", document.isArchived() ? "1" : "0",
            "#shared#", "0",
            "#show_meta#", "0",
            "#disable_search#", "1",
            "#meta_image#", document.getMenuImage(),
            "#hit_nbr#", "" + (1 + searchHitIndex),
            "#select_href#", chooseUrl
        };
    }

} // End class
