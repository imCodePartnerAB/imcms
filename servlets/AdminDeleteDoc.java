import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class AdminDeleteDoc extends Administrator {
    private final static String HTML_TEMPLATE = "AdminDeleteDoc.htm" ;
 
    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
	String host				= req.getHeader("Host") ;
	String server			= Utility.getDomainPref("adminserver",host) ;
  
	// Lets validate the session
	if (super.checkSession(req,res) == false)	return ;

	// Lets get an user object 
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) {
	    String header = "Error in AdminCounter." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}
	
	// Lets verify that this user is an admin	
	if (super.checkAdminRights(server, user) == false) { 
	    String header = "Error in AdminCounter." ;
	    String msg = "The user is not an administrator."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

		
	VariableManager vm = new VariableManager() ;
	super.sendHtml(req,res,vm, HTML_TEMPLATE) ;
			
    } // End doGet

  
    /**
       POST		
    **/	

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
	String host				= req.getHeader("Host") ;
	String server			= Utility.getDomainPref("adminserver",host) ;
  
	// Lets validate the session
	if (super.checkSession(req,res) == false)	return ;
	
	// Lets get an user object  
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) {
	    String header = "Error in AdminCounter." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}
	// Lets check if the user is an admin, otherwise throw him out.
	if (super.checkAdminRights(server, user) == false) { 
	    String header = "Error in AdminCounter." ;
	    String msg = "The user is not an administrator."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}
	

	// Lets check which button was pushed
	//		String whichButton = req.getParameter("adminTask") ;
	//		if(whichButton == null) whichButton = "" ;		
			
	// ******* DELETE DOC **********
  
	if( req.getParameter("DELETE_DOC") != null) {

	    // Lets get the parameters from html page and validate them
	    Properties params = this.getParameters(req) ;
	    if(this.validateParameters(params) == false ) {
		String header = "Error in AdminDeleteDoc." ;
		String msg = "The metaid was not correct. Please add a valid metaid." + "<BR>" ;
		this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header,msg) ;
		return ;
	    }	 
	 
	    // OK, Lets check that the metaid were gonna delete exists in db
	    RmiLayer imc = new RmiLayer(user) ;
	    int metaId = Integer.parseInt(params.getProperty("DEL_META_ID")) ;
	    String findMetaIdSql = "FindMetaId " + metaId ;
	    //	log("SqlQ: " + findMetaIdSql) ;
	    String foundMetaId = imc.execSQlProcedureStr(server, findMetaIdSql) ;
	    log("FoundMetaId: " + foundMetaId) ;
		
	    if( foundMetaId == null) { 
		String header = "Error in AdminUserProps." ;
		String msg = "The metaid " + metaId + " could not be found in db. <BR>" ;
		this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header,msg) ;
		return ;
	    }
				 
	    // Ok, Lets delete the meta id
	    log("Nu försöker vi ta bort ett meta id") ;
	    if( imc.deleteDocAll(server, metaId) == false) { 
		String header = "Error in AdminUserProps." ;
		String msg = "The operation failed to delete metaid: " + metaId + "<BR>" ;
		this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header,msg) ;
		return ;
	    }
	    this.doGet(req,res) ;
	    //this.goAdminUsers(req, res) ;
	    return ;
	}  

	// ******** GO_BACK TO THE MENY ***************		
	if( req.getParameter("GO_BACK") != null) {	
	    String url = MetaInfo.getServletPath(req) ;
	    url += "AdminManager" ; 
	    res.sendRedirect(url) ;
	    return ;
	}	
		
	// ******** UNIDENTIFIED ARGUMENT TO SERVER ********
	this.log("Unidentified argument was sent!") ;
	doGet(req,res) ;
	return ;
    } // end HTTP POST

    /**
       Collects the parameters from the request object 
    **/
	
    public Properties getParameters( HttpServletRequest req) throws ServletException, IOException {
		
	Properties params = new Properties() ;	
	// Lets get the parameters we know we are supposed to get from the request object
	String del_meta_id = (req.getParameter("delete_meta_id")==null) ? "" : (req.getParameter("delete_meta_id")) ;

	params.setProperty("DEL_META_ID", del_meta_id) ;
	
	return params ;
    }
	
    /**
       Collects the parameters from the request object 
    **/
	
    public boolean validateParameters( Properties params) throws ServletException, IOException {
		
	if(super.checkParameters(params) == false) return false ;
	try { 
	    int metaId = Integer.parseInt(params.getProperty("DEL_META_ID"))	;
	} catch(Exception e) { 
	    return false ;
	}
	return true ;
    }	
	
    /**
       Init: Detects paths and filenames.
    */

    public void init(ServletConfig config) throws ServletException {
	
	super.init(config);
	this.log("html_template:" + HTML_TEMPLATE) ;
    }
			
    public void log( String str) {
	super.log(str) ;
	System.out.println("AdminDeleteDoc: " + str ) ;	
    }
	

    /**
       Returns to the adminUsers meny
    */
	
    public void goAdminUsers(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	String servletPath = MetaInfo.getServletPath(req) ;
	res.sendRedirect(servletPath + "AdminManager") ;
    }
	
} // End of class
