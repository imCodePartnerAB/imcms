import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;
import imcode.server.* ;

//första gången vi kommer hit har vi doGet parametern  action=new

public class ChatManager extends ChatBase{
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;


    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException{

	log("startar doGet");
	RequestDispatcher myDispatcher = req.getRequestDispatcher("StartDoc");

	// Lets validate the session, e.g has the user logged in to Janus?
	if (super.checkSession(req,res) == false)	return ;

	// Lets get the standard parameters and validate them
	Properties params = MetaInfo.getParameters(req) ;
	//if (super.checkParameters(req, res, params) == false) return ;

	// Lets get an user object
	imcode.server.user.User user = super.getUserObj(req,res) ;
	if(user == null) return ;

	int testMetaId = Integer.parseInt( params.getProperty("META_ID") );

	if ( !isUserAuthorized( req, res, testMetaId, user ) ){
	    return;
	}

	String action = req.getParameter("action") ;

	if(action == null){
	    //OBS FIXA FELMEDELANDENA
	    action = "" ;
	    String header = "ChatManager servlet. " ;
	    ChatError err = new ChatError(req,res,header,3) ;
	    log(header + err.getErrorMsg()) ;
	    return ;
	}

	// ********* NEW ********
	//it's here we end up when we creates a new chatlink
	if(action.equalsIgnoreCase("NEW"))
	    {
		//log("Lets add a chat");
		HttpSession session = req.getSession(false) ;
		if (session != null){
		    // log("Ok nu sätter vi metavärdena");
		    session.setAttribute("Chat.meta_id", params.getProperty("META_ID")) ;
		    session.setAttribute("Chat.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
		}

		req.setAttribute("action","NEW");
		myDispatcher = req.getRequestDispatcher("ChatCreator");
		myDispatcher.forward(req,res);
		return ;
	    }

	// ********* VIEW ********
	if(action.equalsIgnoreCase("VIEW")){

	    // Lets get userparameters
	    String metaId = params.getProperty("META_ID") ;
	    String userId = ""+user.getUserId() ;

	    // Lets store  the standard metavalues in his session object
	    HttpSession session = req.getSession(false) ;
	    if (session != null){
		// log("Ok nu sätter vi metavärdena");
		session.setAttribute("Chat.meta_id", params.getProperty("META_ID")) ;
		session.setAttribute("Chat.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
	    }

	    req.setAttribute("login_type","login");
	    myDispatcher = req.getRequestDispatcher("ChatLogin");
	    myDispatcher.forward(req,res);
	    return ;

	} // End of View

	// ********* CHANGE ********
	if(action.equalsIgnoreCase("CHANGE")){
	    req.setAttribute("metadata","meta");
	    myDispatcher = req.getRequestDispatcher("ChangeExternalDoc2");
	    myDispatcher.forward(req,res);
	    return ;
	} // End if



	//************** följande metoder behöver kollas över om de fungerar eller inte *****************
	// ********* STATISTICS OBS. NOT USED IN PROGRAM, ONLY FOR TEST ********
	if(action.equalsIgnoreCase("STATISTICS"))
	    {

		// Lets get serverinformation
		IMCPoolInterface chatref = IMCServiceRMI.getChatIMCPoolInterface(req) ;

		String metaId = req.getParameter("meta_id") ;
		String frDate = req.getParameter("from_date") ;
		String toDate = req.getParameter("to_date") ;
		String mode = req.getParameter("list_mode") ;

		// Lets fix the date stuff
		if( frDate.equals("0")) frDate  = "1991-01-01 00:00" ;
		if( toDate.equals("0")) toDate  = "2070-01-01 00:00" ;
		if( mode == null) mode  = "1" ;

		StringBuffer sql = new StringBuffer() ;
		sql.append("C_AdminStatistics1" + " " + metaId + ", '" + frDate + "', '" );
		sql.append(toDate + "', " + mode) ;

		String[][] arr = ChatManager.getStatistics(chatref, sql.toString()) ;

	    } // End if

    } // End doGet


    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str)
    {
	super.log("ChatManager: " + str ) ;
    }

    /**
       Statistics function. Used By AdminManager system
    **/

    public static String[][] getStatistics (IMCPoolInterface chatref, String sproc)
	throws ServletException, IOException
    {
	String[][] arr = chatref.sqlProcedureMulti(sproc) ;
	return arr ;
    }

} // End of class
