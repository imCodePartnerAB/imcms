
import imcode.external.diverse.MetaInfo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The class used to generate login pages, and administrate users page
 *
 * TEMPLATES: The following html files and fragments are used by this servlet.
 *	BillBoard_Login_Error.htm
 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson REBUILD TO BillBoardLogin BY Peter Östergren
 *
 */

public class BillBoardLogin extends BillBoard {//ConfLogin


    //String CREATE_HTML = "BillBoard_Add_User.htm" ;   // The create new user page Conf_Add_User.htm
    String LOGIN_ERROR_HTML = "BillBoard_Login_Error.htm";  // The error Conf_Login_Error.htm
    // page used for specialized messages to user
    //String ADMIN1_HTML = "BillBoard_admin_user.htm" ;//Conf_admin_user.htm
    //String ADMIN2_HTML = "BillBoard_admin_user_resp.htm" ;//Conf_admin_user_resp.htm
    //String ADD_USER_OK_HTML = "BillBoard_Login_add_ok.htm" ;//Conf_Login_add_ok.htm



    public void init( ServletConfig config )
            throws ServletException {
        super.init( config );
        //	test = new Vector();
    }

    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {
        //log("START BillBoardLogin doGet");

        // Lets validate the session, e.g has the user logged in to Janus?
        if ( super.checkSession( req, res ) == false ) return;

        // Lets get the standard parameters and validate them
        MetaInfo.Parameters params = super.getBillBoardSessionParameters( req );

        // Lets get the user object
        imcode.server.User user = super.getUserObj( req, res );
        if ( user == null ) return;

        int testMetaId = params.getMetaId() ;
        if ( !isUserAuthorized( req, res, testMetaId, user ) ) {
            return;
        }


        String userId = "" + user.getUserId();
        if ( !super.prepareUserForBillBoard( req, res, params, userId ) ) {
            log( "Error in prepareUserFor Conf" );
        }
        return;
    } // End doGet

    /**
     <PRE>
     Parameter	Händelse	parameter värde
     login_type	Utförs om login_type OCH submit har skickats. Verifierar inloggning i konferensen.	LOGIN
     login_type	Adderar en användare in i Janus user db och till konferensens db	ADD_USER
     login_type	Sparar en användares användarnivå till konferens db	SAVE_USER
     Reacts on the actions sent.

     PARAMETERS:
     login_type : Flag used to detect selected acion. Case insensitive

     Expected values
     LOGIN : Verifies a user login to the conference
     ADD_USER : Adds a new user in the db
     SAVE_USER	: Saves a users level to the db

     </PRE>
     **/

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {
        //log("START BillBoardLogin doPost");

        // Lets validate the session, e.g has the user logged in to Janus?
        if ( super.checkSession( req, res ) == false ) return;

        // Lets get the standard parameters and validate them
        MetaInfo.Parameters params = super.getBillBoardSessionParameters( req );
        if ( true == false ) return;

        // Lets get the user object
        imcode.server.User user = super.getUserObj( req, res );
        if ( user == null ) return;

        int testMetaId = params.getMetaId();
        if ( !isUserAuthorized( req, res, testMetaId, user ) ) {
            return;
        }

        // Lets get the loginType
        String loginType = ( req.getParameter( "login_type" ) == null ) ? "" : ( req.getParameter( "login_type" ) );

        // Ok, the user wants to login
        if ( loginType.equalsIgnoreCase( "login" ) /* && req.getParameter("submit") != null */ ) {
            //log("Ok, nu försöker vi verifiera logga in!") ;
            String userId = "" + user.getUserId();

            //  Lets update the users sessionobject with a a ok login to the conference
            //	Send him to the manager with the ability to get in
            //log("Ok, nu förbereder vi användaren på att logga in") ;
            if ( !super.prepareUserForBillBoard( req, res, params, userId ) ) {
                log( "Error in prepareUserFor Conf" );
            }
            return;
        }

        // ***** RETURN TO ADMIN MANAGER *****
        if ( loginType.equalsIgnoreCase( "GoBack" ) ) {
            res.sendRedirect( "BillBoardLogin?login_type=admin_user" );
            return;
        }
    } // end HTTP POST


    /**
     Log function, will work for both servletexec and Apache
     **/

    public void log( String msg ) {
        super.log( "BillBoardLogin: " + msg );

    }
} // End class
