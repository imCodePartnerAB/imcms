import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;
	
public class AdminCounter extends Administrator {
	String HTML_TEMPLATE ;


/**
		The GET method creates the html page when this side has been
		redirected from somewhere else.
**/

	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
	
		String host 				= req.getHeader("Host") ;
		String server 			= Utility.getDomainPref("adminserver",host) ;
	  
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
	
	// Lets verify that the user who tries to add a new user is an admin  	
	if (super.checkAdminRights(server, user) == false) { 
		  String header = "Error in AdminCounter." ;
		  String msg = "The user is not an administrator."+ "<BR>" ;
	 		this.log(header + msg) ;
	 		AdminError err = new AdminError(req,res,header,msg) ;
	 		return ;
	}
	
 // Lets get the countervalue	
  	RmiLayer imc = new RmiLayer(user) ;
		String counterValue = "" + imc.getCounter(server) ;
 
 // Lets get the servers startdate
		String startDate = imc.getCounterDate(server) ;	
				  
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
	
	String host 				= req.getHeader("Host") ;
	String server 			= Utility.getDomainPref("adminserver",host) ;
  	
 // Lets get an user object  
  imcode.server.User user = super.getUserObj(req,res) ;
  if(user == null) {
  	  String header = "Error in AdminCounter." ;
		  String msg = "Couldnt create an user object."+ "<BR>" ;
	 		this.log(header + msg) ;
	 		AdminError err = new AdminError(req,res,header,msg) ;
	 		return ;
	}

  if (super.checkAdminRights(server, user) == false) { 
		  String header = "Error in AdminCounter." ;
		  String msg = "The user is not an administrator."+ "<BR>" ;
	 		this.log(header + msg) ;
	 		AdminError err = new AdminError(req,res,header,msg) ;
	 		return ;
	}
	
	// Ok, we passed, Lets check what the admin wants to do
	// First, lets prepare the output to the htmlpage
		VariableManager vm = new VariableManager() ;
 		RmiLayer imc = new RmiLayer(user) ;

	// ***** RETURN TO ADMIN MANAGER *****
    if( req.getParameter("CancelCounter") != null) {
			res.sendRedirect(MetaInfo.getServletPath(req) + "AdminManager") ;
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
				
			if (ok) imc.setCounter(server, theUserInt) ;
  		this.doGet(req, res) ;
			return ;
		}
      
  // ***** SET COUNTER DATE *****
		if( req.getParameter("setDate") != null) {
			// Lets get the parameter and validate it
			String date = (req.getParameter("date_value")==null) ? "" : (req.getParameter("date_value")) ;
			boolean ok = imc.setCounterDate(server, date) ;
			if(!ok)	log("Serverdate couldnt be set") ;
			this.doGet(req, res) ;
			return ;
		}
      
/*

	 	// ***** RESET COUNTER *****
	 	if( whichButton.equalsIgnoreCase("ResetCounter")) {
   	  imc.setCounter(0) ;
   	  String msg = "The counter was reseted. New value is:" + imc.getCounter() ;
	 		this.log(msg) ;
			vm.addProperty("STATUS",msg ) ;
 			this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
			return ;
			
 	// ***** RESET COUNTERDATE *****
		} else if( whichButton.equalsIgnoreCase("ResetCounterDate")) {
  	// imc.resetCounter() ;
  		String toDay = super.getDateToday() ;
  		boolean ok = imc.setCounterDate(toDay) ;
  		String msg = "" ;
  		if( ok == true)
   			msg = "The counterdate was reseted successfully." ;
	 		else
	 			msg = "The counterdate couldnt be reseted." ;
	 		msg += " Counterdate is: " + imc.getCounterDate() ;  
			this.log(msg) ;
	 		vm.addProperty("STATUS", msg ) ;
 			this.sendHtml(req,res,vm,HTML_TEMPLATE) ;
			return ;
			
	// ***** SHOW COUNTERDATE *****
		} else if( whichButton.equalsIgnoreCase("GetCounterDate")) {
			String newDate = imc.getCounterDate() ;
  		String msg = "The counter start date is:" + newDate ;
	 		this.log(msg) ;
			vm.addProperty("STATUS",msg ) ;
  		imc = null ;
			this.sendHtml(req,res,vm,HTML_TEMPLATE) ;
			return ;
		
	// ***** SHOW COUNTER *****
		} else if( whichButton.equalsIgnoreCase("GetCounter")) {
  	  	int counter = imc.getCounter() ;
   	 	  String msg = "The counter is: " + counter ;
	 			this.log(msg) ;
				vm.addProperty("STATUS",msg ) ;
  			imc = null ;
				this.sendHtml(req,res,vm,HTML_TEMPLATE) ;
				return ;
	
	// ***** SET COUNTER *****
		} else if( whichButton.equalsIgnoreCase("SetCounter")) {
			// Lets get the parameter and validate it
				Properties props = this.getParameters(req) ;				
				String userVal = props.getProperty("COUNTER_VALUE") ;
				this.log("The user values was: " + userVal) ;
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
				
				if (ok) {
  					boolean wasCounterSet = imc.setCounter(theUserInt) ;
  					if( wasCounterSet == true)
   						msg = "The counter was set successfully. New value is: "
   							 + imc.getCounter() ;
	 					else
	 						msg = "The counter couldnt be set." ;
	 			}
				
				vm.addProperty("STATUS",msg ) ;
  			imc = null ;
				this.sendHtml(req,res,vm,HTML_TEMPLATE) ;
				return ;
					
	// ***** SET COUNTER DATE *****
		} else if( whichButton.equalsIgnoreCase("SetCounterDate")) {
			// Lets get the parameter and validate it
				Properties props = this.getParameters(req) ;				
				String userVal = props.getProperty("COUNTER_VALUE") ;
				this.log("The user date: " + userVal) ;
							
				boolean ok = imc.setCounterDate(userVal) ;
  			String msg = "" ;
  			if( ok == true)
   				msg = "The counterdate was reseted successfully." ;
	 			else
	 				msg = "The counterdate couldnt be reseted." ;
	 			msg += " Counterdate is: " + imc.getCounterDate() ;  
				this.log(msg) ;
	 			vm.addProperty("STATUS", msg ) ;
 				this.sendHtml(req,res,vm,HTML_TEMPLATE) ;
				return ;
		*/	
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
    HTML_TEMPLATE = "AdminCounter.htm" ;	
    /*
    HTML_TEMPLATE = getInitParameter("html_template"); 
  
    if (HTML_TEMPLATE == null ) {
	    Enumeration initParams = getInitParameterNames();
	    System.err.println("The init parameters were: ");
	    while (initParams.hasMoreElements()) {
				System.err.println(initParams.nextElement());
	    }
	    System.err.println("Should have seen one parameter name");
	    throw new UnavailableException (this,
		"Not given a directory to read init files");
		}
	
	this.log("html_template:" + getInitParameter("html_template")) ;
  */
 }


/**
	Log function, will work for both servletexec and Apache
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("AdminCounter: " + str ) ;	
	}

} // End of class