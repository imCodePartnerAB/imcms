import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;

public class ConfAdd extends Conference {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    String HTML_TEMPLATE ;
    String SERVLET_NAME ;  // The name on this servlet

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get all parameters for this servlet
	Properties params = this.getParameters(req) ;
	if (super.checkParameters(req, res, params) == false) {
	    String header = SERVLET_NAME + " servlet. " ;
	    String msg = params.toString() ;
	    ConfError err = new ConfError(req,res,header,1) ;
	    return ;
	}

	// Lets get the user object
	imcode.server.user.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ) {
	    return;
	}

	// Lets detect which addtype we have
	String addType = "" ;
	addType = req.getParameter("ADDTYPE") ;

	// Lets get serverinformation
	String host = req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface confref = IMCServiceRMI.getConfIMCPoolInterface(req) ;

	int metaId = Integer.parseInt( params.getProperty("META_ID") );
	if ( userHasRightToEdit( imcref, metaId, user ) ) {

	    // ********* CANCEL ********
	    if( req.getParameter("CANCEL") != null || req.getParameter("CANCEL.x") != null ) {
		// Lets redirect to the servlet which holds in us.
		res.sendRedirect("ConfDiscView") ;
		return ;
	    }

	    // ********* ADD DISCUSSION ********
	    if(addType.equalsIgnoreCase("DISCUSSION") && ( req.getParameter("ADD") != null || req.getParameter("ADD.x") != null ) ) {

		// Lets add a new discussion to the database
		String aForumId = params.getProperty("FORUM_ID") ;
		String userId = "" ;
		HttpSession session = req.getSession(false) ;
		if (session != null) {
		    userId = (String) session.getAttribute("Conference.user_id") ;
		}

		// Lets get the users reply level
		String levelQ = "A_ConfUsersGetUserLevel "+ params.getProperty("META_ID") ;
		levelQ += ", " + userId ;
		String level = confref.sqlProcedureStr(levelQ) ;
		if(level.equalsIgnoreCase("-1")) {
		    log("An error occured in reading the users level") ;
		    level = "0" ;
		}

		// Lets verify the fields the user have had to write freetext in
		// to verify that the sql questions wont go mad.
		String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER")) ;
		String addText = super.verifySqlText(params.getProperty("ADD_TEXT")) ;

		// Lets check the data size
		if(addText.length() > 32000) {
		    String header = SERVLET_NAME + " servlet. " ;
		    String msg = params.toString() ;
		    ConfError err = new ConfError(req,res,header,74) ;
		    return ;
		}

		// Ok, Lets add the discussion to DB
		String sqlQuest = "A_AddNewDisc " + aForumId + ", " + userId + ", " ;
		sqlQuest += sqlPDelim(addHeader) ;
		sqlQuest += sqlP(addText) + ", " + level ;
		confref.sqlUpdateProcedure(sqlQuest) ;

		// Lets add the new discussion id to the session object
		// Ok, Lets get the last discussion in that forum
		if (session != null) {
		    String latestDiscId = confref.sqlProcedureStr("A_GetLastDiscussionId " +
								  params.getProperty("META_ID") + ", " + aForumId) ;
		    session.setAttribute("Conference.disc_id", latestDiscId) ;
		}


		// Lets redirect to the servlet which holds in us.
		res.sendRedirect("ConfDiscView") ;
		return ;
	    }

	    // ********* ADD REPLY ********
	    // This is a workaround to fix the possibility to use gifs OR submit buttons

	    if(addType.equalsIgnoreCase("REPLY") && ( req.getParameter("ADD") != null || req.getParameter("ADD.x") != null ) ) {

		// Lets add a new Reply to the database
		String discId = params.getProperty("DISC_ID") ;
		String userId = "" ;
		HttpSession session = req.getSession(false) ;
		if (session != null) {
		    userId = (String) session.getAttribute("Conference.user_id") ;
		}

		// Lets get the users reply level
		String levelQ = "A_ConfUsersGetUserLevel "+ params.getProperty("META_ID") ;
		levelQ += ", " + userId ;
		String level = confref.sqlProcedureStr(levelQ) ;
		if(level.equalsIgnoreCase("-1")) {
		    log("An error occured in reading the users level") ;
		    level = "0" ;
		}

		// Lets verify the textfields
		String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER")) ;
		String addText = super.verifySqlText(params.getProperty("ADD_TEXT")) ;

		// Lets check the data size
		if(addText.length() > 32000) {
		    String header = SERVLET_NAME + " servlet. " ;
		    String msg = params.toString() ;
		    ConfError err = new ConfError(req,res,header,74) ;
		    return ;
		}


		// Ok, Lets add the reply
		String sqlQuest = "A_AddReply " + userId + ", " + discId + ", "  ;
		sqlQuest += sqlPDelim(addHeader);
		sqlQuest += sqlP(addText) + ", " + level ;
		confref.sqlUpdateProcedure(sqlQuest) ;

		// Lets redirect to the servlet which holds in us.
		res.sendRedirect("ConfDiscView") ;
		return ;
	    }
	} else {
	    String header = SERVLET_NAME + " servlet. " ;
	    ConfError err = new ConfError( req, res ,header , 100 ) ;
	    return ;
	}

    } // DoPost

    /**
       DoGet
    */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get all parameters for this servlet
	Properties params = this.getParameters(req) ;
	if (super.checkParameters(req, res, params) == false) {
	    String header = SERVLET_NAME + " servlet. " ;
	    String msg = params.toString() ;
	    ConfError err = new ConfError(req,res,header,1) ;
	    return ;
	}

	// Lets get the user object
	imcode.server.user.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	if ( !isUserAuthorized( req, res, user ) ) {
	    return;
	}

	// Lets get serverinformation
	String host = req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	IMCPoolInterface confref = IMCServiceRMI.getConfIMCPoolInterface(req) ;

	int metaId = Integer.parseInt( params.getProperty("META_ID") );
	if ( userHasRightToEdit( imcref, metaId, user ) ) {
	    // Lets Get the session user id
	    // Ok, Lets get the last discussion in that forum
	    String loginUserId = "" ;
	    HttpSession session = req.getSession(false) ;
	    if (session != null) {
		loginUserId = (String) session.getAttribute("Conference.user_id") ;
	    }

	    // Lets get a VariableManager
	    VariableManager vm = new VariableManager() ;

	    // Lets get the users first and last names
	    String sqlName = "A_GetConfLoginNames " + params.getProperty("META_ID") ;
	    sqlName += ", " + loginUserId + ", " +  1 ;
	    String firstName = (String) confref.sqlProcedureStr( sqlName ) ;
	    sqlName = "A_GetConfLoginNames " + params.getProperty("META_ID") ;
	    sqlName += ", " + loginUserId + ", " +  2 ;
	    String lastName = (String) confref.sqlProcedureStr(sqlName) ;

	    vm.addProperty("FIRST_NAME", firstName ) ;
	    vm.addProperty("LAST_NAME",	lastName ) ;
	    vm.addProperty("ADD_TYPE", params.getProperty("ADD_TYPE")) ;

	    // Lets add the current forum name
	    String currForum = "" + confref.sqlProcedureStr("A_GetForumName " + params.getProperty("FORUM_ID")) ;
	    vm.addProperty("CURRENT_FORUM_NAME", currForum) ;

	    // Lets get the addtype and add it to the page
	    String addTypeHeader = "" ;
	    if(params.getProperty("ADD_TYPE").equalsIgnoreCase("REPLY")) {
		ConfError err = new ConfError() ;
		addTypeHeader = err.getErrorMessage(req, 72) ;
	    }
	    else {
		ConfError err = new ConfError() ;
		addTypeHeader = err.getErrorMessage(req, 73) ;
	    }

	    vm.addProperty("ADD_TYPE_HEADER", addTypeHeader) ;

	    // If addtype is reply, then lets get the header for the discussion
	    // from the db and suggest it to the user
	    String discHeader = "" ;
	    if( params.getProperty("ADD_TYPE").equalsIgnoreCase("REPLY") ) {
		String aDiscId = params.getProperty("DISC_ID") ;
		String sqlQ = "A_GetDiscussionHeader " + aDiscId ;
		String arr[] = confref.sqlProcedure(sqlQ) ;
		if( arr != null) {
		    if(arr.length > 0) {
			discHeader = (String) arr[0] ;
		    }
		}
	    }
	    vm.addProperty("DISC_HEADER", discHeader) ;
	    this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
	    //	log("ConfAdd OK") ;
	    return ;
	} else {
	    String header = SERVLET_NAME + " servlet. " ;
	    ConfError err = new ConfError( req, res ,header , 100 ) ;
	    return ;
	}

    } //DoGet

    /**
       Collects all the parameters used by this servlet
    **/

    public Properties getParameters( HttpServletRequest req)
	throws ServletException, IOException {

	// Lets get the standard SESSION metainformation
	Properties params = super.getSessionParameters(req) ;

	// Lets get the EXTENDED SESSION PARAMETERS
	super.getExtSessionParameters(req, params) ;

	// Lets get our REQUESTPARAMETERS
	String addType = (req.getParameter("ADDTYPE")==null) ? "" : (req.getParameter("ADDTYPE")) ;
	String addHeader = (req.getParameter("ADDHEADER")==null) ? "" : (req.getParameter("ADDHEADER")) ;
	String addText = (req.getParameter("ADDTEXT")==null) ? "" : (req.getParameter("ADDTEXT")) ;

	// Alright, these parameters are userdefined text, and if the user hasnt filled something in them
	// then the checkparamters will warn for this. The thing is that we dont care if the
	// user passes a text or not, so lets look if the variable is empty, and if it is
	// just put " " in it!

	// from now on, we do care if the user has added a text or something. If the user hasnt
	// then get defalult values from the errmsg file

	if( addHeader.equals("")) {
	    ConfError err = new ConfError() ;
	    addHeader = err.getErrorMessage(req, 70) ;
	}
	if( addText.equals("")) {
	    ConfError err = new ConfError() ;
	    addText = err.getErrorMessage(req, 71) ;
	}


	params.setProperty("ADD_HEADER", addHeader) ;
	params.setProperty("ADD_TEXT", addText) ;
	params.setProperty("ADD_TYPE", addType) ;

	return params ;
    }


    /**
       Init
    */

    public void init(ServletConfig config)
	throws ServletException {
	super.init(config);
	HTML_TEMPLATE = "Conf_Add.htm" ;
	SERVLET_NAME = "ConfAdd" ;
    }

    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str) {
	super.log(str) ;
	System.out.println(SERVLET_NAME + " " + str ) ;
    }

} // End of class
