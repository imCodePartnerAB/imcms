
import imcode.external.diverse.VariableManager;
import imcode.server.IMCPoolInterface;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;
import imcode.util.Utility;

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
 * #CONFERENCE_LIST#
 * #CONFERENCE#
 * #FORUM_LIST
 * #FORUM#
 * #DEBAT_LIST#
 * #DEBATE#
 * <p/>
 * stored procedures in use:
 * -
 *
 * @author Jerker Drottenmyr
 * @version 1.02 11 Nov 2000
 */
public class AdminConference extends Administrator {

    private static final String TEMPLATE_CONF = "AdminConference.html";
    private static final String TEMPLATE_LIST_TOOL = "AdminConference_list_tool.html";
    private static final String TEMPLATE_LIST = "AdminConference_list.html";
    private static final String TEMPLATE_CONF_ELEMENT = "AdminConference_list_conf_element.html";
    private static final String TEMPLATE_FORUM_ELEMENT = "AdminConference_list_forum_element.html";
    private static final String ERROR_HEADER = "AdminConference";

    //required date format
    private static final String DATE_FORMATE = "yyyy-MM-dd";

    // lets dispatches all requests to doPost()
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws IOException {
        doPost( request, response );
    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
        String eMailServerMaster = Utility.getDomainPref( "servermaster_email" );

        // Lets validate the session
        if ( checkSession( request, response ) == false ) {
            return;
        }

        // Lets get an user object
        imcode.server.user.UserDomainObject user = getUserObj( request, response ) ;
        if(user == null) {
            sendErrorMessage( imcref, eMailServerMaster, user, ERROR_HEADER, 1, response );
            return ;
        }

        // Lets verify that the user who tries to add a new user is an admin
        if (imcref.checkAdminRights(user) == false) {
            sendErrorMessage( imcref, eMailServerMaster, user, ERROR_HEADER, 2, response );
            return ;
        }

        /* User has right lets do the request */
        VariableManager vm = new VariableManager();

        /* lets get which request to do */
        // generate htmlpage for listing conferences
        if ( request.getParameter( "VIEW_CONF_LIST_TOOL" ) != null ) {
            sendHtml( request, response, vm, TEMPLATE_LIST_TOOL );

            // generate list off conferences
        } else if ( request.getParameter( "VEIW_CONF_LIST" ) != null ) {
            listConferences( request, response, user);

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

    /*
     *
     */
    private void listConferences(HttpServletRequest request, HttpServletResponse response, UserDomainObject user) throws IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
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

            IMCPoolInterface confref = ApplicationServer.getIMCPoolInterface();

            //lets get htmltemplate for conferencerow
            String htmlConferenceElement = imcref.parseDoc( null, TEMPLATE_CONF_ELEMENT, user);
            String htmlForumElement = imcref.parseDoc( null, TEMPLATE_FORUM_ELEMENT, user);

            String[][] listOfConferences = imcref.sqlProcedureMulti( "ListConferences", new String[0] );

            // lets create conferencelist
            StringBuffer conferencesListTag = new StringBuffer();

            Hashtable conferenceTags = new Hashtable();
            Hashtable forumTags = new Hashtable();
            Hashtable debateTags = new Hashtable();

            for ( int i = 0; i < listOfConferences.length; i++ ) {

                String metaId = listOfConferences[i][0];

                String[][] queryResultForum = confref.sqlProcedureMulti( "A_AdminStatistics1", new String[]{ metaId,startDate,endDate,listMode } );

                //lets create forumList for this conference
                StringBuffer forumList = new StringBuffer();

                for ( int j = 0; j < queryResultForum.length; j++ ) {

                    String forumId = queryResultForum[j][0];

                    String[][] arr1 = confref.sqlProcedureMulti( "A_AdminStatistics2", new String[]{metaId, forumId, startDate, endDate, listMode} );
                    String[][] queryResultDebate = arr1;

                    // lets create debatelist for this forum
                    StringBuffer debateList = new StringBuffer();
                    for ( int k = 0; k < queryResultDebate.length; k++ ) {
                        debateTags.put( "DEBATE", queryResultDebate[k][1] );
                        debateTags.put( "DATE", queryResultDebate[k][2] );
                    }

                    forumTags.put( "FORUM", queryResultForum[j][1] );
                    forumTags.put( "DEBATE_LIST", debateList.toString() );
                    forumList.append( ( Parser.parseTags( new StringBuffer( htmlForumElement ), '#', " <>\n\r\t", forumTags, true, 1 ) ).toString() );
                }

                if ( queryResultForum.length > 0 ) {
                    conferenceTags.put( "SERVLET_URL", "" );
                    conferenceTags.put( "META_ID", metaId );
                    conferenceTags.put( "CONFERENCE", listOfConferences[i][1] );
                    conferenceTags.put( "FORUM_LIST", forumList.toString() );
                    conferencesListTag.append( ( Parser.parseTags( new StringBuffer( htmlConferenceElement ), '#', " <>\n\r\t", conferenceTags, true, 1 ) ).toString() );
                }
            }

            //Lets generate the html page
            VariableManager vm = new VariableManager();
            vm.addProperty( "CONFERENCE_LIST", conferencesListTag.toString() );

            this.sendHtml( request, response, vm, TEMPLATE_LIST );

        } else {
            sendErrorMessage( imcref, eMailServerMaster, user, ERROR_HEADER, 10, response );
        }
    }
}
