import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;


/**
 * The class is the link between imCMS and the billboard-system
 * Commands: NEW, VIEW, CHANGE, STATISTICS
 * OBS STATISTICS is not done yet sense I dont realy know what it will do
 *
 *
 * TEMPLATES: The following html files and fragments are used by this servlet.
 *	BillBoard_Login_Error.htm
 *
 *
 * Html parstags in use:
 * -
 *
 * stored procedures in use:
 * B_AdminStatistics1
 *
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson REBUILD TO BillBoardLogin BY Peter Östergren
 *
 */



public class BillBoardManager extends BillBoard	  //ConfManager
{
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;
    String HTML_TEMPLATE ;

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get the standard parameters and validate them
	Properties params = MetaInfo.getParameters(req) ;
	if (super.checkParameters(req, res, params) == false) return ;

	// Lets get an user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null) return ;

	int testMetaId = Integer.parseInt( params.getProperty("META_ID") );
	if ( !isUserAuthorized( req, res, testMetaId, user ) )
	    {
		return;
	    }

	String action = req.getParameter("action") ;
	//log("ConfManager is in action...") ;
	if(action == null)
	    {
		action = "" ;
		String header = "BillBoardManager servlet. " ;
		BillBoardError err = new BillBoardError(req,res,header,3) ;
		log(header + err.getErrorMsg()) ;
		return ;
	    }

	// ********* NEW ********
	if(action.equalsIgnoreCase("NEW"))
	    {
		//log("Lets add a billBoard");
		HttpSession session = req.getSession(false) ;
		if (session != null)
		    {
			// log("Ok nu sätter vi metavärdena");
			session.setAttribute("BillBoard.meta_id", params.getProperty("META_ID")) ;
			session.setAttribute("BillBoard.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
			session.setAttribute("BillBoard.cookie_id", params.getProperty("COOKIE_ID")) ;
		    }

		res.sendRedirect("BillBoardCreator?action=NEW") ;
		return ;
	    }

	// ********* VIEW ********
	if(action.equalsIgnoreCase("VIEW"))
	    {

		// Lets get userparameters
		String metaId = params.getProperty("META_ID") ;
		String userId = ""+user.getUserId() ;

		// Lets detect which type of user we got
		String loginType = user.getLoginType() ;

		// We got 3 usertypes: 0= specialusers, 1=normal, 2=confernce
		// We got 3 logintypes: "Extern"=web users, "ip_access"= people from a certain ip nbr
		// and "verify" = people who has logged into the system

		if(! loginType.equalsIgnoreCase("VERIFY"))
		    {
			// Lets store  the standard metavalues in his session object
			HttpSession session = req.getSession(false) ;
			if (session != null)
			    {
				// log("Ok nu sätter vi metavärdena");
				session.setAttribute("BillBoard.meta_id", params.getProperty("META_ID")) ;
				session.setAttribute("BillBoard.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
				session.setAttribute("BillBoard.cookie_id", params.getProperty("COOKIE_ID")) ;
				session.setAttribute("BillBoard.viewedDiscList", new Properties()) ;
				//log("OK, nu sätter vi viewedDiscList") ;
			    }

			String loginPage = "BillBoardLogin?login_type=login" ;
			res.sendRedirect(loginPage) ;
			return ;
		    }

		//  Lets update the users sessionobject with a with a ok login to the conference
		//	Send him to the manager with the ability to get in
		if(!super.prepareUserForBillBoard(req, res, params, userId) )
		    {
			log("Error in prepareUserFor Conf" ) ;
		    }


		return ;
	    } // End of View

	// ********* CHANGE ********
	if(action.equalsIgnoreCase("CHANGE"))
	    {
		String url = "ChangeExternalDoc2?"
		    + MetaInfo.passMeta(params) + "&metadata=meta" ;
		res.sendRedirect(url) ;
		return ;
	    } // End if

	// ********* STATISTICS OBS. NOT USED IN PROGRAM, ONLY FOR TEST ********
	if(action.equalsIgnoreCase("STATISTICS"))
	    {

		// Lets get serverinformation

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
		IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface(req) ;

		String metaId = req.getParameter("meta_id") ;
		String frDate = req.getParameter("from_date") ;
		String toDate = req.getParameter("to_date") ;
		String mode = req.getParameter("list_mode") ;

		// Lets fix the date stuff
		if( frDate.equals("0")) frDate  = "1991-01-01 00:00" ;
		if( toDate.equals("0")) toDate  = "2070-01-01 00:00" ;
		if( mode == null) mode  = "1" ;

		StringBuffer sql = new StringBuffer() ;
		sql.append("B_AdminStatistics1" + " " + metaId + ", '" + frDate + "', '" );
		sql.append(toDate + "', " + mode) ;
		String[][] arr = BillBoardManager.getStatistics(billref, sql.toString()) ;

	    } // End if


    } // End doGet


    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String msg)
    {
	super.log("BillBoardManager: " + msg) ;
    }

    /**
       Statistics function. Used By AdminManager system
    **/

    public static String[][] getStatistics (IMCPoolInterface billref,String sproc)
	throws ServletException, IOException
    {

	String[][] arr = billref.sqlProcedureMulti(sproc) ;
	return arr ;
    }
} // End of class
