
import imcode.external.diverse.VariableManager;
import imcode.server.ApplicationServer;
import imcode.server.IMCPoolInterface;
import imcode.server.IMCServiceInterface;
import imcode.util.Parser;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

/**
 * Lists conferences who has debates that has requared dates. (create or modified)
 * <p/>
 * Html template in use:
 * AdminConference.html
 * AdminConference_list_tool.html
 * AdminConference_list.html
 * AdminConference_list_conf_element.html
 * AdminConference_list_debate_element.html
 * Error.html
 * <p/>
 * Html parstags in use:
 * #META_ID#
 * #BILLBOARD_LIST#
 * #BILLBOARD#
 * #SECTION_LIST
 * #SECTION#
 * #DEBAT_LIST#
 * #DEBATE#
 * <p/>
 * stored procedures in use:
 * -
 *
 * @author Jerker Drottenmyr
 * @version 1.02 11 Nov 2000
 */
public class AdminBillBoard extends Administrator { //AdminConference

    private static final String TEMPLATE_CONF = "";//"AdminConference.html";
    private static final String TEMPLATE_LIST_TOOL = "";//"AdminConference_list_tool.html";
    private static final String TEMPLATE_LIST = "";//"AdminConference_list.html";
    private static final String TEMPLATE_CONF_ELEMENT = "";//"AdminConference_list_conf_element.html";
    private static final String TEMPLATE_FORUM_ELEMENT = "";//"AdminConference_list_forum_element.html";
    private static final String ERROR_HEADER = "";//"AdminConference";

    //required date format
    private static final String DATE_FORMATE = "yyyy-MM-dd";

    // lets dispatches all requests to doPost()
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        doPost( request, response );
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String eMailServerMaster = Utility.getDomainPref( "servermaster_email" );

        // lets get ready for errors
        String defaultLanguagePrefix = imcref.getDefaultLanguageAsIso639_2();

        // Lets validate the session
        if ( checkSession( request, response ) == false ) {
            return;
        }

        // Lets get an user object
        imcode.server.user.UserDomainObject user = getUserObj( request, response );
        if ( user == null ) {
            sendErrorMessage( imcref, eMailServerMaster, defaultLanguagePrefix, ERROR_HEADER, 1, response );
            return;
        }

        // Lets verify that the user who tries to add a new user is an admin
        if ( imcref.checkAdminRights( user ) == false ) {
            sendErrorMessage( imcref, eMailServerMaster, defaultLanguagePrefix, ERROR_HEADER, 2, response );
            return;
        }

        /* User has right lets do the request */
        String languagePrefix = user.getLangPrefix();
        VariableManager vm = new VariableManager();

        /* lets get which request to do */
        // generate htmlpage for listing conferences
        if ( request.getParameter( "VIEW_CONF_LIST_TOOL" ) != null ) {
            sendHtml( request, response, vm, TEMPLATE_LIST_TOOL );

            // generate list off conferences
        } else if ( request.getParameter( "VEIW_CONF_LIST" ) != null ) {
            listConferences( request, response, languagePrefix );

            // go to AdminManager
        } else if ( request.getParameter( "CANCEL" ) != null ) {
            Utility.redirect( request, response, "AdminManager" );

            // go to htmlpage for listing conferences
        } else if ( request.getParameter( "CANCEL_CONF_LIST" ) != null ) {
            Utility.redirect( request, response, "AdminConference" );

            // go to AdminConference page
        } else {
            sendHtml( request, response, vm, TEMPLATE_CONF );
        }

    }

    /**
     * check for right date form
     */
    private boolean isDateInRightFormat( String date ) {

        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat( DATE_FORMATE );

        try {
            formatter.parse( date );
        } catch ( ParseException e ) {
            return false;

        }

        return true;
    }

    private void listConferences( HttpServletRequest request, HttpServletResponse response, String languagePrefix )
            throws IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String eMailServerMaster = Utility.getDomainPref( "servermaster_email" );
        boolean noErrors = true;

        /*
         * 0 = all date !not in use
         * 1 = create date
         * 2 = modified date
         */
        String listMode = request.getParameter( "LISTMOD" );
        String startDate = request.getParameter( "START_DATE" );
        String endDate = request.getParameter( "END_DATE" );

        /* lets see if any errors in requared fields or if some is missing */
        try {
            if ( listMode != null ) {
                int mode = Integer.parseInt( listMode );
                if ( !( mode == 1 || mode == 2 ) ) {
                    noErrors = false;
                }
            } else {
                noErrors = false;
            }
        } catch ( NumberFormatException e ) {
            noErrors = false;
        }

        if ( startDate != null ) {
            if ( startDate.length() > 0 ) {
                if ( !isDateInRightFormat( startDate ) ) {
                    noErrors = false;
                }
            } else {
                startDate = "0"; // Stored Procedure expects 0 then no startDate
            }
        } else {
            noErrors = false; // no startDate field submited
        }

        if ( endDate != null ) {
            if ( endDate.length() > 0 ) {
                if ( !isDateInRightFormat( endDate ) ) {
                    noErrors = false;
                }
            } else {
                endDate = "0"; // Stored Procedure expects 0 then no endDate
            }
        } else {
            noErrors = false; // no endDate field submited
        }

        // lets generate response page
        if ( noErrors ) {

            IMCPoolInterface billref = ApplicationServer.getIMCPoolInterface();

            //lets get htmltemplate for conferencerow
            String htmlConferenceElement = imcref.parseDoc( null, TEMPLATE_CONF_ELEMENT, languagePrefix );
            String htmlForumElement = imcref.parseDoc( null, TEMPLATE_FORUM_ELEMENT, languagePrefix );

            String[][] listOfBillBoards = imcref.sqlQueryMulti( "ListBillBoards", new String[0] );

            // lets create conferencelist
            StringBuffer conferencesListTag = new StringBuffer();

            Hashtable billBoardTags = new Hashtable();
            Hashtable forumTags = new Hashtable();
            Hashtable debateTags = new Hashtable();

            for ( int i = 0; i < listOfBillBoards.length; i++ ) {

                String metaId = listOfBillBoards[i][0];
                String[][] arr = billref.sqlProcedureMulti( "B_AdminStatistics1", new String[]{metaId, startDate, endDate, listMode} );
                String[][] queryResultForum = arr;

                //lets create sectionList for this conference
                StringBuffer sectionList = new StringBuffer();

                for ( int j = 0; j < queryResultForum.length; j++ ) {

                    String forumId = queryResultForum[j][0];
                    String[][] arr1 = billref.sqlProcedureMulti( "B_AdminStatistics2", new String[]{metaId, forumId, startDate, endDate, listMode} );
                    String[][] queryResultDebate = arr1;

                    // lets create debatelist for this forum
                    StringBuffer debateList = new StringBuffer();
                    for ( int k = 0; k < queryResultDebate.length; k++ ) {
                        debateTags.put( "DEBATE", queryResultDebate[k][1] );
                        debateTags.put( "DATE", queryResultDebate[k][2] );
                    }

                    forumTags.put( "SECTION", queryResultForum[j][1] );
                    forumTags.put( "DEBATE_LIST", debateList.toString() );
                    sectionList.append( ( Parser.parseTags( new StringBuffer( htmlForumElement ), '#', " <>\n\r\t", (java.util.Map)forumTags, true, 1 ) ).toString() );
                }

                if ( queryResultForum.length > 0 ) {
                    billBoardTags.put( "SERVLET_URL", "" );
                    billBoardTags.put( "META_ID", metaId );
                    billBoardTags.put( "BILLBOARD", listOfBillBoards[i][1] );
                    billBoardTags.put( "SECTION_LIST", sectionList.toString() );
                    conferencesListTag.append( ( Parser.parseTags( new StringBuffer( htmlConferenceElement ), '#', " <>\n\r\t", (java.util.Map)billBoardTags, true, 1 ) ).toString() );
                }
            }

            //Lets generate the html page
            VariableManager vm = new VariableManager();
            vm.addProperty( "BILLBOARD_LIST", conferencesListTag.toString() );

            this.sendHtml( request, response, vm, TEMPLATE_LIST );

        } else {
            sendErrorMessage( imcref, eMailServerMaster, languagePrefix, ERROR_HEADER, 10, response );
        }
    }
}
