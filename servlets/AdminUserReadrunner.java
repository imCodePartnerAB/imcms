import javax.servlet.http.* ;
import javax.servlet.* ;

import java.io.* ;
import java.util.* ;
import java.text.* ;

import imcode.server.* ;
import imcode.server.user.UserDomainObject;
import imcode.util.* ;

import imcode.readrunner.* ;

public class AdminUserReadrunner extends HttpServlet {

	private final static String HTML_RESPONSE_USER = "readrunner/adminreadrunneruser_user.html" ;
	private final static String HTML_RESPONSE_ADMIN = "readrunner/adminreadrunneruser.html" ;

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		UserDomainObject user = null ;

		if( null == (user = Check.userLoggedOn(req,res,imcref.getStartUrl())) ) {
		    // User is not logged on
		    return ;
		}
		
	/*	
		if (!imcref.checkAdminRights(user)) {
		    // User is not superadmin
		    return ;
		}
	*/
		HttpSession session = req.getSession( false );
		String userToChangeId = (String)session.getAttribute("userToChange");
		UserDomainObject userToChange = null;
		if ( userToChangeId != null ) {
			userToChange = imcref.getUserById(Integer.parseInt( userToChangeId ) ) ;
		}
		displayPage(imcref,user,userToChange, res) ;

    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) 
	throws ServletException, IOException {

		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		UserDomainObject user = null ;

		HttpSession session = req.getSession( false );
		
		if( null == (user = Check.userLoggedOn(req,res,imcref.getStartUrl())) ) {
		    // User is not logged on
		    return ;
		}
		
		String userToChangeId = req.getParameter("user_id");
		
		UserDomainObject userToChange = null;
		
		if ( !("").equals(userToChangeId) ){
			userToChange = imcref.getUserById(Integer.parseInt(userToChangeId) ) ;
		}
		

/*
		if (!imcref.checkAdminRights(user)) {
		    // User is not superadmin
		    return ;
		}
*/
		if (null != req.getParameter("cancel")) {
			//	String goback = session.getAttribute("go_back");
		
			if ( null != userToChange ){ 
				res.sendRedirect("AdminUserProps?CHANGE_USER=true") ;
			}else{
				res.sendRedirect("AdminUserProps?ADD_USER=true") ;
			}
		
			return ;
		}
		
		
	
		Date expiryDate = null ;
		try {
		    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
		    expiryDate = dateFormat.parse(req.getParameter("expiry_date")) ;
		} catch (ParseException ignored) {
		    // ignored, no expiry-date is set.
		}

		ReadrunnerUserData rrUserData = new ReadrunnerUserData() ;
		try {
		    rrUserData.setUses                       ( Integer.parseInt(req.getParameter("uses")) ) ;
		    rrUserData.setMaxUses                    ( Integer.parseInt(req.getParameter("max_uses")) ) ;
		    rrUserData.setMaxUsesWarningThreshold    ( Integer.parseInt(req.getParameter("max_uses_warning_threshold")) ) ;
		    rrUserData.setExpiryDate                 ( expiryDate ) ;
		    rrUserData.setExpiryDateWarningThreshold ( Integer.parseInt(req.getParameter("expiry_date_warning_threshold")) ) ;
		 	rrUserData.setExpiryDateWarningSent		 ( false ) ; // Reset expiry-date-warning-sent-flag;
		 
		 //   imcref.setReadrunnerUserData(userToChange, rrUserData) ;
		} catch (NumberFormatException nfe) {
		    throw nfe ;
		}
		session.setAttribute("tempRRUserData", rrUserData);
		if ( null != userToChange ){ 
			res.sendRedirect("AdminUserProps?CHANGE_USER=true") ;
		}else{
			res.sendRedirect("AdminUserProps?ADD_USER=true") ;
		}
	}


    private void displayPage(IMCServiceInterface imcref, UserDomainObject user, UserDomainObject userToChange, HttpServletResponse res)
	throws IOException {

		// check if user is a Useradmin, adminRole = 2
		boolean isUseradmin = imcref.checkUserAdminrole ( user.getUserId(), 2 );

		// check if user is a Superadmin, adminRole = 1
		boolean isSuperadmin = imcref.checkUserAdminrole ( user.getUserId(), 1 );
		
		ReadrunnerUserData rrUserData = new ReadrunnerUserData() ;
		
		String userToChangeId = "";
		
		if ( userToChange != null ){
			rrUserData = imcref.getReadrunnerUserData(userToChange) ;
			userToChangeId = "" + userToChange.getUserId();
		}
		
		if (null == rrUserData) {
		    rrUserData = new ReadrunnerUserData() ;
		}

		String expiryDateString =
		    null != rrUserData.getExpiryDate()
		    ? new SimpleDateFormat("yyyy-MM-dd").format(rrUserData.getExpiryDate())
		    : "" ;

		ArrayList parseList = new ArrayList() ;
		
		parseList.add("#user_id#") ;                       parseList.add(userToChangeId) ;
		parseList.add("#uses#") ;                          parseList.add(""+rrUserData.getUses()) ;
		parseList.add("#max_uses#") ;                      parseList.add(""+rrUserData.getMaxUses()) ;
		parseList.add("#max_uses_warning_threshold#") ;    parseList.add(""+rrUserData.getMaxUsesWarningThreshold()) ;
		parseList.add("#expiry_date#") ;                   parseList.add(expiryDateString) ;
		parseList.add("#expiry_date_warning_threshold#") ; parseList.add(""+rrUserData.getExpiryDateWarningThreshold()) ;
		//parseList.add("#expiry_date_warning_sent#") ; 	   parseList.add(""+rrUserData.getExpiryDateWarningSent()) ;
		
		
		res.setContentType("text/html") ;
		Writer out = res.getWriter() ;
		
		//Useradmin is not allowed to change his own readrunner values
		if (isUseradmin && (null == userToChange) ||  // useradmin is going to add a new user
		   (isUseradmin && user.getUserId() != userToChange.getUserId() ) || // or is going to change a user 
		   isSuperadmin){   // or Superadmin
			out.write(imcref.parseDoc(parseList, HTML_RESPONSE_ADMIN, user.getLangPrefix())) ;
		}else{
			out.write(imcref.parseDoc(parseList, HTML_RESPONSE_USER, user.getLangPrefix())) ;
		}
	}
	
}
