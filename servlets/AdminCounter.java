import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;

import imcode.server.* ;
import imcode.util.* ;


public class AdminCounter extends Administrator {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    private final static String HTML_TEMPLATE = "AdminCounter.htm" ;

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	// Lets validate the session
	if (super.checkSession(req,res) == false)	return ;

	// Lets get an user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null) {
	    String header = "Error in AdminCounter." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	// Lets verify that the user who tries to add a new user is an admin
	if (imcref.checkAdminRights(user) == false) {
	    String header = "Error in AdminCounter." ;
	    String msg = "The user is not an administrator."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	// Lets get the countervalue
	String counterValue = "" + imcref.getCounter() ;

	// Lets get the servers startdate
	String startDate = imcref.getCounterDate() ;

	// Lets generate the html page
	VariableManager vm = new VariableManager() ;
	//  vm.addProperty("STATUS","..." ) ;
	vm.addProperty("COUNTER_VALUE", counterValue ) ;
	vm.addProperty("DATE_VALUE", startDate ) ;
	this.sendHtml(req,res,vm, HTML_TEMPLATE) ;

    } // End doGet


    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	// Lets validate the session
	if (super.checkSession(req,res) == false) return ;

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	// Lets get an user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null) {
	    String header = "Error in AdminCounter." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	if (imcref.checkAdminRights(user) == false) {
	    String header = "Error in AdminCounter." ;
	    String msg = "The user is not an administrator."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	// Ok, we passed, Lets check what the admin wants to do
	// First, lets prepare the output to the htmlpage
	VariableManager vm = new VariableManager() ;

	// ***** RETURN TO ADMIN MANAGER *****
	if( req.getParameter("CancelCounter") != null) {
	    res.sendRedirect("AdminManager") ;
	    return ;
	}

	// ***** SET COUNTER *****
	if( req.getParameter("setCounter") != null) {
	    // Lets get the parameter and validate it
	    Properties props = this.getParameters(req) ;
	    String userVal = props.getProperty("COUNTER_VALUE") ;
	    //this.log("The user values was: " + userVal) ;
	    String msg = "" ;
	    int theUserInt = 0 ;

	    boolean ok = true ;
	    try {
		if ( userVal.equals(""))  {
		    msg = "Error: a counterValue couldnt be identified!" ;
		    ok = false ;
		}
		theUserInt = Integer.parseInt(userVal) ;

	    } catch (Exception e) {
		msg = "Error: You must write numbers!" ;
		ok = false ;
	    }

	    if (ok) imcref.setCounter(theUserInt) ;
	    this.doGet(req, res) ;
	    return ;
	}

	// ***** SET COUNTER DATE *****
	if( req.getParameter("setDate") != null) {
	    // Lets get the parameter and validate it
	    String date = (req.getParameter("date_value")==null) ? "" : (req.getParameter("date_value")) ;
	    boolean ok = imcref.setCounterDate(date) ;
	    if(!ok)	log("Serverdate couldnt be set") ;
	    this.doGet(req, res) ;
	    return ;
	}

    } // End of doPost


    /**
       Collects the parameters from the request object
    **/

    public Properties getParameters( HttpServletRequest req)
	throws ServletException, IOException {

	Properties reqParams = new Properties() ;
	// Lets get the parameters we know we are supposed to get from the request object
	String counterVal = (req.getParameter("counter_value")==null) ? "" : (req.getParameter("counter_value")) ;
	reqParams.setProperty("COUNTER_VALUE", counterVal) ;

	return reqParams ;
    }


    /**
       Init: Detects paths and filenames.
    */

    public void init(ServletConfig config)
	throws ServletException
    {

	super.init(config);
    }


    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str) {
	super.log(str) ;
	System.out.println("AdminCounter: " + str ) ;
    }

} // End of class
