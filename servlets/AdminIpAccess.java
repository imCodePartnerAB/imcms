import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.awt.* ;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class AdminIpAccess  extends Administrator {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	String HTML_IP_SNIPPET ;
	String HTML_TEMPLATE ;
	String ADD_IP_TEMPLATE ;
	String WARN_DEL_IP_TEMPLATE ;

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
	String header = "Error in AdminIpAccess." ;
	String msg = "Couldnt create an user object."+ "<BR>" ;
	this.log(header + msg) ;
	AdminError err = new AdminError(req,res,header,msg) ;
	return ;
  }

// ********** GENERATE THE IP-ACCESS PAGE *********
// Lets get all IP-accesses from DB
  RmiLayer rmi = new RmiLayer(user) ;
  String[][] multi= rmi.execProcedureMulti(server, "IPAccessesGetAll") ;
	//   Vector v =  this.getOneRow(multi, 1) ;

 // Lets build the variables for each record
   Vector tags = new Vector() ;
   tags.add("IP_ACCESS_ID") ;
   tags.add("USER_ID") ;
   tags.add("LOGIN_NAME") ;
   tags.add("IP_START") ;
   tags.add("IP_END") ;

	// Lets parse each record and put it in a string
   String recs = "" ;
   int nbrOfRows = getNbrOfRows(multi) ;
  for (int counter = 0 ; counter< nbrOfRows ; counter++) {
	 Vector aRecV = this.getOneRow(multi, counter) ;
	 VariableManager vmRec = new VariableManager() ;
	aRecV.setElementAt(Utility.ipLongToString(Long.parseLong((String)aRecV.elementAt(3))),3);
	aRecV.setElementAt(Utility.ipLongToString(Long.parseLong((String)aRecV.elementAt(4))),4);
	 vmRec.merge(tags, aRecV) ;
	 vmRec.addProperty("RECORD_COUNTER" , "" + counter) ;
	 recs += this.createHtml(req,res,vmRec, HTML_IP_SNIPPET) ;
   }

// Lets generate the html page
  VariableManager vm = new VariableManager() ;
  vm.addProperty("ALL_IP_ACCESSES", recs) ;
  this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
} // End doGet



/**
	POST
**/

public void doPost(HttpServletRequest req, HttpServletResponse res)
  throws ServletException, IOException {

  String host 				= req.getHeader("Host") ;
  String server 			= Utility.getDomainPref("adminserver",host) ;


// Lets validate the session
	if (super.checkSession(req,res) == false) return ;

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

// ******* GENERATE THE ADD A NEW IP-ACCESS TO DB **********
  if( req.getParameter("ADD_IP_ACCESS") != null) {

  // Lets get all USERS from DB
	 RmiLayer rmi = new RmiLayer(user) ;
	 String[] usersArr = rmi.execSqlProcedure(server, "GetAllUsersInList") ;
	 Vector usersV  = super.convert2Vector(usersArr) ;
	 Html ht = new Html() ;
	 String usersOption = ht.createHtmlCode("ID_OPTION", "", usersV ) ;

  // Lets generate the html page
	 VariableManager vm = new VariableManager() ;
	 vm.addProperty("USERS_LIST", usersOption  ) ;
	 this.sendHtml(req,res,vm, ADD_IP_TEMPLATE) ;
	 return ;
	}

 // *************** RETURN TO ADMINMANAGER *****************
	if( req.getParameter("CANCEL") != null) {
		res.sendRedirect(MetaInfo.getServletPath(req) + "AdminManager") ;
		return ;
	}

  // ******* RETURN TO THE NORMAL ADMIN IPACCESS PAGE **********
  else if( req.getParameter("CANCEL_ADD_IP") != null || req.getParameter("IP_CANCEL_DELETE") != null ) {
	 res.sendRedirect(MetaInfo.getServletPath(req) + "AdminIpAccess?action=start") ;
	 return ;
  }

  // ******* RETURN TO THE NORMAL ADMIN IPACCESS PAGE **********
  else if( req.getParameter("IP_CANCEL_DELETE") != null ) {
	 res.sendRedirect(MetaInfo.getServletPath(req) + "AdminIpAccess?action=start") ;
	 return ;
  }

// ******* ADD A NEW IP-ACCESS TO DB **********

  else if( req.getParameter("ADD_NEW_IP_ACCESS") != null) {
	log("Now's ADD_IP_ACCESS running") ;

// Lets get the parameters from html page and validate them
	Properties params = this.getAddParameters(req) ;
	//log("PARAMS: " + params.toString()) ;
	params = this.validateParameters(params,req,res) ;
	if(params == null) return ;

// Lets build the users information into a string and add it to db
	String sqlStr = "IPAccessAdd " + params.get("USER_ID") + ", '" ;
	sqlStr += params.get("IP_START") + "', '" + params.get("IP_END") + "'" ;
	log("AddNewIP-access sql: " + sqlStr ) ;

	RmiLayer rmi = new RmiLayer(user) ;
	rmi.execSqlUpdateProcedure(server, sqlStr) ;
	res.sendRedirect(MetaInfo.getServletPath(req) + "AdminIpAccess?action=start") ;
	return ;
	}


 // ******** SAVE AN EXISTING IP-ACCESS TO DB ***************

  else if( req.getParameter("RESAVE_IP_ACCESS") != null) {

	// Lets get all the ip_access id:s
	String[] reSavesIds = this.getEditedIpAccesses(req) ;
	RmiLayer rmi = new RmiLayer(user) ;

	// Lets resave all marked ip-accesses.
		if( reSavesIds != null ) {
			for(int i = 0 ; i < reSavesIds.length ; i++ ){
				log("ResaveId: " +  reSavesIds[i]) ;
		String tmpId = reSavesIds[i] ;
		// Lets get all edited fields for that ip-access
		String ipAccessId = req.getParameter("IP_ACCESS_ID_" + tmpId) ;
		String ipUserId = req.getParameter("IP_USER_ID_" + tmpId) ;
		String ipStart = req.getParameter("IP_START_" + tmpId) ;
		String ipEnd = req.getParameter("IP_END_" + tmpId) ;

		long ipStartInt = Utility.ipStringToLong(ipStart) ;

		long ipEndInt = Utility.ipStringToLong(ipEnd) ;


		String sqlQ = "IPAccessUpdate " + ipAccessId +", "+ ipUserId +", " ;
		sqlQ += ipStartInt +", '"+ ipEndInt ;
		log("IP-ACCESS: " + sqlQ) ;
			rmi.execSqlUpdateProcedure(server, sqlQ) ;
		}
		}

		this.doGet(req, res) ;
	return ;
  }

  // ***** GENERATE THE LAST DELETE IP-ACCESS WARNING PAGE  **********
  else if( req.getParameter("IP_WARN_DELETE") != null ) {

  // Lets get the parameters from html page and validate them
	String[] deleteIds = this.getEditedIpAccesses(req) ;
		HttpSession session = req.getSession(false) ;
	if(session != null ) {
		Enumeration enumNames  = req.getParameterNames() ;
	  while( enumNames.hasMoreElements() ) {
		 String paramName = (String) (enumNames.nextElement()) ;
				 String arr[] = req.getParameterValues(paramName) ;
		 /*if(arr.length == 1) { // its a string
		   log("Det är en sträng: " + arr[0]) ;
		   session.putValue("IP." + paramName, arr[0]) ;
		 } else { // its an array
				log("Det är en array: " + arr[0]) ; */
			session.putValue("IP." + paramName , arr) ;
		// }
	  }
	} else {
		String header = "Delete IP-Access error" ;
		  String msg = "A session could not be created. Please try again + " + "<BR>";
			this.log("Error in IP-access delete") ;
		AdminError err = new AdminError(req,res,header, msg) ;
			return ;
		}

		// Lets generate the last warning html page
		VariableManager vm = new VariableManager() ;
	this.sendHtml(req,res,vm, WARN_DEL_IP_TEMPLATE) ;
	return ;
  }


 // ******** DELETE A IP ACCESS FROM DB ***************
 else if( req.getParameter("DEL_IP_ACCESS") != null) {
		HttpSession session = req.getSession(false) ;
	if(session != null ) {
		log("Ok, ta bort en Ip-access: " + session.toString()) ;
	  //String arr[] = session.getValueNames() ;
	  // for(int i = 0 ; i < arr.length ; i++ ) {
	  // 	log("session: " + arr[i]) ;
	  // }

	  String[] deleteIds = (String[]) session.getValue("IP.EDIT_IP_ACCESS") ;
	  //log("Det här fick vi: " + deleteIds.toString()) ;
	  // String[] deleteIds = (String[]) obj ;
	 // log("Antal IP-accesser att ta bort: " + deleteIds.length) ;
	 // log("Första IP-access ID: " + deleteIds[0]) ;
			RmiLayer rmi = new RmiLayer(user) ;

		// Lets resave all marked ip-accesses.
			if( deleteIds != null ) {
				for(int i = 0 ; i < deleteIds.length ; i++ ){
			String tmpId = "IP.IP_ACCESS_ID_" + deleteIds[i] ;
			String[] tmpArr = (String[]) session.getValue(tmpId) ;
			String ipAccessId = tmpArr[0] ;
		  String sqlQ = "IPAccessDelete " + ipAccessId ;
			log("IP-Delete: " + sqlQ) ;
				rmi.execSqlUpdateProcedure(server, sqlQ) ;
			}
			}
	} else {
		String header = "Delete IP-Access error" ;
		  String msg = "A session could not be accessed. Please try again + " + "<BR>";
			this.log("Error in IP-access delete") ;
		AdminError err = new AdminError(req,res,header, msg) ;
			return ;
		}
	this.doGet(req, res) ;
	return ;
  }

} // end HTTP POST



	/**
	 Returns all fields for a post with. if were trying to get a row outside the
   available nbr of rows, an emty vector is returned
	**/



/**
  Collects the parameters from the request object for the add function
**/
public Properties getAddParameters( HttpServletRequest req) throws ServletException, IOException {

  Properties ipInfo = new Properties() ;
  // Lets get the parameters we know we are supposed to get from the request object
  String user_id = (req.getParameter("USER_ID")==null) ? "" : (req.getParameter("USER_ID").trim()) ;
  String ipStart = (req.getParameter("IP_START")==null) ? "" : (req.getParameter("IP_START").trim()) ;
  String ipEnd = (req.getParameter("IP_END")==null) ? "" : (req.getParameter("IP_END").trim()) ;

	long ipStartInt = Utility.ipStringToLong(ipStart) ;

	long ipEndInt = Utility.ipStringToLong(ipEnd) ;

  ipInfo.setProperty("USER_ID", user_id) ;
  ipInfo.setProperty("IP_START", String.valueOf(ipStartInt)) ;
  ipInfo.setProperty("IP_END", String.valueOf(ipEndInt)) ;
  return ipInfo ;
}

/**
	Collects the parameters used to delete a reply
**/

	public String[] getEditedIpAccesses( HttpServletRequest req )
		throws ServletException, IOException {

	// Lets get the standard discussion_id to delete
		String[] replyId = (req.getParameterValues("EDIT_IP_ACCESS")) ;
		return replyId ;
	}


/**
	Returns a Properties, containing the user information from the html page. if Something
	failes, a error page will be generated and null will be returned.
*/

public Properties validateParameters(Properties aPropObj, HttpServletRequest req,
	HttpServletResponse res) throws ServletException, IOException {

		if(checkParameters(aPropObj) == false) {
		  String header = "Checkparameters error" ;
			String msg = "Samtliga fält var inte korrekt ifyllda." + "<BR>";
		  this.log("Error in checkingparameters") ;
			AdminError err = new AdminError(req,res,header, msg) ;
		  return null;
		}
		return aPropObj ;

} // end checkParameters



/**
	Init: Detects paths and filenames.
*/

public void init(ServletConfig config) throws ServletException {
  super.init(config);
  HTML_TEMPLATE = "AdminIpAccess.htm";
  HTML_IP_SNIPPET = "AdminIpAccessList.htm";
  ADD_IP_TEMPLATE = "AdminIpAccess_Add.htm";
	WARN_DEL_IP_TEMPLATE = "AdminIpAccess_Delete2.htm" ;
}

public void log( String str) {
	super.log(str) ;
	System.out.println("AdminIpAccess: " + str ) ;
}


} // End of class