import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;

public class ConfManager extends Conference {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;
    String HTML_TEMPLATE ;

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get the standard parameters and validate them
	Properties params = super.getParameters(req) ;
	if (super.checkParameters(req, res, params) == false) return ;

	// Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	int testMetaId = Integer.parseInt( params.getProperty("META_ID") );
	if ( !isUserAuthorized( req, res, testMetaId, user ) ) {
	    return;
	}

	String action = req.getParameter("action") ;
	//log("ConfManager is in action...") ;
	if(action == null) {
	    action = "" ;
	    String header = "ConfManager servlet. " ;
	    ConfError err = new ConfError(req,res,header,3) ;
	    log(header + err.getErrorMsg()) ;
	    return ;
	}

	// ********* NEW ********
	if(action.equalsIgnoreCase("NEW")) {
	    log("Lets add a conference");
	    HttpSession session = req.getSession(false) ;
	    if (session != null) {
		// log("Ok nu sätter vi metavärdena");
		session.setAttribute("Conference.meta_id", params.getProperty("META_ID")) ;
		session.setAttribute("Conference.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
		session.setAttribute("Conference.cookie_id", params.getProperty("COOKIE_ID")) ;
	    }

	    String url = "ConfCreator?action=NEW" ;
	    //log("Redirect till:" + url) ;
	    res.sendRedirect(url) ;
	    return ;
	}

	// ********* VIEW ********
	if(action.equalsIgnoreCase("VIEW")) {

	    // Lets get userparameters
	    String metaId = params.getProperty("META_ID") ;
	    String userId = "" + user.getUserId() ;

	    // Lets detect which type of user we got
	    String loginType = user.getLoginType() ;

	    // We got 3 usertypes: 0= specialusers, 1=normal, 2=confernce
	    // We got 3 logintypes: "Extern"=web users, "ip_access"= people from a certain ip nbr
	    // and "verify" = people who has logged into the system

	    if(! loginType.equalsIgnoreCase("VERIFY")) {
		// Lets store  the standard metavalues in his session object
		HttpSession session = req.getSession(false) ;
		if (session != null) {
		    // log("Ok nu sätter vi metavärdena");
		    session.setAttribute("Conference.meta_id", params.getProperty("META_ID")) ;
		    session.setAttribute("Conference.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
		    session.setAttribute("Conference.cookie_id", params.getProperty("COOKIE_ID")) ;
		    session.setAttribute("Conference.viewedDiscList", new Properties()) ;
		    log("OK, nu sätter vi viewedDiscList") ;
		}

		String loginPage = "ConfLogin?login_type=login" ;
		//log("Redirect till:" + loginPage) ;
		res.sendRedirect(loginPage) ;
		return ;
	    }

	    log("Ok, användaren har loggat in, förbered honom för konferensen" ) ;
	    //  Lets update the users sessionobject with a with a ok login to the conference
	    //	Send him to the manager with the ability to get in
	    if(!super.prepareUserForConf(req, res, params, userId) ) {
		log("Error in prepareUserFor Conf" ) ;
	    }


	    return ;
	} // End of View

	// ********* CHANGE ********
	if(action.equalsIgnoreCase("CHANGE")) {
	    String url = "ChangeExternalDoc2?"
		+ MetaInfo.passMeta(params) + "&metadata=meta" ;
	    res.sendRedirect(url) ;
	    return ;
	} // End if

	// ********* STATISTICS OBS. NOT USED IN PROGRAM, ONLY FOR TEST ********
	if(action.equalsIgnoreCase("STATISTICS")) {

	    // Lets get serverinformation
	    IMCPoolInterface confref = IMCServiceRMI.getConfIMCPoolInterface(req) ;

	    String metaId = req.getParameter("meta_id") ;
	    String frDate = req.getParameter("from_date") ;
	    String toDate = req.getParameter("to_date") ;
	    String mode = req.getParameter("list_mode") ;

	    // Lets fix the date stuff
	    if( frDate.equals("0")) frDate  = "1991-01-01 00:00" ;
	    if( toDate.equals("0")) toDate  = "2070-01-01 00:00" ;
	    if( mode == null) mode  = "1" ;

	    StringBuffer sql = new StringBuffer() ;
	    sql.append("AdminStatistics1" + " " + metaId + ", '" + frDate + "', '" );
	    sql.append(toDate + "', " + mode) ;
	    String[][] arr = ConfManager.getStatistics(confref, sql.toString()) ;
	} // End if


    } // End doGet


    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str) {
	super.log(str) ;
	System.out.println("ConfManager: " + str ) ;
    }

    /**
       Statistics function. Used By AdminManager system
    **/

    public static String[][] getStatistics (IMCPoolInterface confref,String sproc)
	throws ServletException, IOException {

	String[][] arr = confref.sqlProcedureMulti(sproc) ;
	return arr ;
    }

} // End of class
