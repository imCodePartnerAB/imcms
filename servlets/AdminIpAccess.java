
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import imcode.external.diverse.*;
import imcode.util.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;

public class AdminIpAccess extends Administrator {

    private String HTML_IP_SNIPPET;
    private String HTML_TEMPLATE;
    private String ADD_IP_TEMPLATE;
    private String WARN_DEL_IP_TEMPLATE;

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // ********** GENERATE THE IP-ACCESS PAGE *********
        // Lets get all IP-accesses from DB
        String[][] multi = imcref.sqlProcedureMulti( "IPAccessesGetAll", new String[0] );

        // Lets build the variables for each record
        Vector tags = new Vector();
        tags.add( "IP_ACCESS_ID" );
        tags.add( "USER_ID" );
        tags.add( "LOGIN_NAME" );
        tags.add( "IP_START" );
        tags.add( "IP_END" );

        // Lets parse each record and put it in a string
        String recs = "";
        int nbrOfRows = multi.length;
        for ( int counter = 0; counter < nbrOfRows; counter++ ) {
            Vector aRecV = new Vector( java.util.Arrays.asList( multi[counter] ) );
            VariableManager vmRec = new VariableManager();
            aRecV.setElementAt( Utility.ipLongToString( Long.parseLong( (String)aRecV.elementAt( 3 ) ) ), 3 );
            aRecV.setElementAt( Utility.ipLongToString( Long.parseLong( (String)aRecV.elementAt( 4 ) ) ), 4 );
            vmRec.merge( tags, aRecV );
            vmRec.addProperty( "RECORD_COUNTER", "" + counter );
            recs += this.createHtml( req, res, vmRec, HTML_IP_SNIPPET );
        }

        // Lets generate the html page
        VariableManager vm = new VariableManager();
        vm.addProperty( "ALL_IP_ACCESSES", recs );
        this.sendHtml( req, res, vm, HTML_TEMPLATE );
    } // End doGet

    /**
     * POST
     */

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {


        // Lets check if the user is an admin, otherwise throw him out.
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( imcref.checkAdminRights( user ) == false ) {
            String header = "Error in AdminCounter.";
            String msg = "The user is not an administrator." + "<BR>";
            this.log( header + msg );
            new AdminError( req, res, header, msg );
            return;
        }

        // ******* GENERATE THE ADD A NEW IP-ACCESS TO DB **********
        if ( req.getParameter( "ADD_IP_ACCESS" ) != null ) {

            // Lets get all USERS from DB
            String[] usersArr = imcref.sqlProcedure( "GetAllUsersInList", new String[0] );
            Vector usersV = new Vector( java.util.Arrays.asList( usersArr ) );
            String usersOption = Html.createHtmlOptionList( "", usersV );

            // Lets generate the html page
            VariableManager vm = new VariableManager();
            vm.addProperty( "USERS_LIST", usersOption );
            this.sendHtml( req, res, vm, ADD_IP_TEMPLATE );
            return;
        }

        // *************** RETURN TO ADMINMANAGER *****************
        if ( req.getParameter( "CANCEL" ) != null ) {
            res.sendRedirect( "AdminManager" );
            return;
        }

        // ******* RETURN TO THE NORMAL ADMIN IPACCESS PAGE **********
        else if ( req.getParameter( "CANCEL_ADD_IP" ) != null || req.getParameter( "IP_CANCEL_DELETE" ) != null ) {
            res.sendRedirect( "AdminIpAccess?action=start" );
            return;
        }

        // ******* RETURN TO THE NORMAL ADMIN IPACCESS PAGE **********
        else if ( req.getParameter( "IP_CANCEL_DELETE" ) != null ) {
            res.sendRedirect( "AdminIpAccess?action=start" );
            return;
        }

        // ******* ADD A NEW IP-ACCESS TO DB **********

        else if ( req.getParameter( "ADD_NEW_IP_ACCESS" ) != null ) {
            log( "Now's ADD_IP_ACCESS running" );

            // Lets get the parameters from html page and validate them
            Properties params = this.getAddParameters( req );
            //log("PARAMS: " + params.toString()) ;
            params = this.validateParameters( params, req, res );
            if ( params == null ) return;

            imcref.sqlUpdateProcedure( "IPAccessAdd",
                                       new String[]{params.getProperty( "USER_ID" ), params.getProperty( "IP_START" ), params.getProperty( "IP_END" )} );
            res.sendRedirect( "AdminIpAccess?action=start" );
            return;
        }


        // ******** SAVE AN EXISTING IP-ACCESS TO DB ***************

        else if ( req.getParameter( "RESAVE_IP_ACCESS" ) != null ) {

            // Lets get all the ip_access id:s
            String[] reSavesIds = this.getEditedIpAccesses( req );

            // Lets resave all marked ip-accesses.
            if ( reSavesIds != null ) {
                for ( int i = 0; i < reSavesIds.length; i++ ) {
                    log( "ResaveId: " + reSavesIds[i] );
                    String tmpId = reSavesIds[i];
                    // Lets get all edited fields for that ip-access
                    String ipAccessId = req.getParameter( "IP_ACCESS_ID_" + tmpId );
                    String ipUserId = req.getParameter( "IP_USER_ID_" + tmpId );
                    String ipStart = req.getParameter( "IP_START_" + tmpId );
                    String ipEnd = req.getParameter( "IP_END_" + tmpId );

                    long ipStartInt = Utility.ipStringToLong( ipStart );

                    long ipEndInt = Utility.ipStringToLong( ipEnd );

                    imcref.sqlUpdateProcedure( "IPAccessUpdate", new String[]{ipAccessId, ipUserId, "" + ipStartInt, "" + ipEndInt} );
                }
            }

            this.doGet( req, res );
            return;
        }

        // ***** GENERATE THE LAST DELETE IP-ACCESS WARNING PAGE  **********
        else if ( req.getParameter( "IP_WARN_DELETE" ) != null ) {

            // Lets get the parameters from html page and validate them
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                Enumeration enumNames = req.getParameterNames();
                while ( enumNames.hasMoreElements() ) {
                    String paramName = (String)( enumNames.nextElement() );
                    String arr[] = req.getParameterValues( paramName );
                    session.setAttribute( "IP." + paramName, arr );
                }
            } else {
                String header = "Delete IP-Access error";
                String msg = "A session could not be created. Please try again + " + "<BR>";
                this.log( "Error in IP-access delete" );
                new AdminError( req, res, header, msg );
                return;
            }

            // Lets generate the last warning html page
            VariableManager vm = new VariableManager();
            this.sendHtml( req, res, vm, WARN_DEL_IP_TEMPLATE );
            return;
        }


        // ******** DELETE A IP ACCESS FROM DB ***************
        else if ( req.getParameter( "DEL_IP_ACCESS" ) != null ) {
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                log( "Ok, ta bort en Ip-access: " + session.toString() );

                String[] deleteIds = (String[])session.getAttribute( "IP.EDIT_IP_ACCESS" );

                // Lets resave all marked ip-accesses.
                if ( deleteIds != null ) {
                    for ( int i = 0; i < deleteIds.length; i++ ) {
                        String tmpId = "IP.IP_ACCESS_ID_" + deleteIds[i];
                        String[] tmpArr = (String[])session.getAttribute( tmpId );
                        String ipAccessId = tmpArr[0];
                        imcref.sqlUpdateProcedure( "IPAccessDelete", new String[]{ipAccessId} );
                    }
                }
            } else {
                String header = "Delete IP-Access error";
                String msg = "A session could not be accessed. Please try again + " + "<BR>";
                this.log( "Error in IP-access delete" );
                new AdminError( req, res, header, msg );
                return;
            }
            this.doGet( req, res );
            return;
        }

    } // end HTTP POST

    /**
     * Collects the parameters from the request object for the add function
     */
    private Properties getAddParameters( HttpServletRequest req ) {

        Properties ipInfo = new Properties();
        // Lets get the parameters we know we are supposed to get from the request object
        String user_id = ( req.getParameter( "USER_ID" ) == null ) ? "" : ( req.getParameter( "USER_ID" ).trim() );
        String ipStart = ( req.getParameter( "IP_START" ) == null ) ? "" : ( req.getParameter( "IP_START" ).trim() );
        String ipEnd = ( req.getParameter( "IP_END" ) == null ) ? "" : ( req.getParameter( "IP_END" ).trim() );

        long ipStartInt = Utility.ipStringToLong( ipStart );

        long ipEndInt = Utility.ipStringToLong( ipEnd );

        ipInfo.setProperty( "USER_ID", user_id );
        ipInfo.setProperty( "IP_START", String.valueOf( ipStartInt ) );
        ipInfo.setProperty( "IP_END", String.valueOf( ipEndInt ) );
        return ipInfo;
    }

    /**
     * Collects the parameters used to delete a reply
     */

    private String[] getEditedIpAccesses( HttpServletRequest req ) {

        // Lets get the standard discussion_id to delete
        String[] replyId = ( req.getParameterValues( "EDIT_IP_ACCESS" ) );
        return replyId;
    }

    /**
     * Returns a Properties, containing the user information from the html page. if Something
     * failes, a error page will be generated and null will be returned.
     */

    private Properties validateParameters( Properties aPropObj, HttpServletRequest req, HttpServletResponse res ) throws IOException {

        if ( checkParameters( aPropObj ) == false ) {
            String header = "Checkparameters error";
            String msg = "Samtliga fält var inte korrekt ifyllda." + "<BR>";
            this.log( "Error in checkingparameters" );
            new AdminError( req, res, header, msg );
            return null;
        }
        return aPropObj;

    } // end checkParameters

    /**
     * Init: Detects paths and filenames.
     */

    public void init( ServletConfig config ) throws ServletException {
        super.init( config );
        HTML_TEMPLATE = "AdminIpAccess.htm";
        HTML_IP_SNIPPET = "AdminIpAccessList.htm";
        ADD_IP_TEMPLATE = "AdminIpAccess_Add.htm";
        WARN_DEL_IP_TEMPLATE = "AdminIpAccess_Delete2.htm";
    }

    public void log( String str ) {
        super.log( str );
        System.out.println( "AdminIpAccess: " + str );
    }

} // End of class
