import javax.servlet.http.* ;
import javax.servlet.* ;

import java.io.* ;
import java.util.* ;
import java.text.* ;

import imcode.server.* ;
import imcode.util.* ;

import imcode.readrunner.* ;

public class AdminUserReadrunner extends HttpServlet {

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	User user = null ;

	if( null == (user = Check.userLoggedOn(req,res,imcref.getStartUrl())) ) {
	    // User is not logged on
	    return ;
	}

	if (!imcref.checkAdminRights(user)) {
	    // User is not superadmin
	    return ;
	}

	User theEditedUser = imcref.getUserById(Integer.parseInt(req.getParameter("user_id"))) ;
	displayPage(imcref,user,theEditedUser,res) ;

    }

    public void doPost(HttpServletRequest req,HttpServletResponse res)  throws ServletException, IOException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	User user = null ;

	if( null == (user = Check.userLoggedOn(req,res,imcref.getStartUrl())) ) {
	    // User is not logged on
	    return ;
	}

	if (!imcref.checkAdminRights(user)) {
	    // User is not superadmin
	    return ;
	}

	if (null != req.getParameter("cancel")) {
	    res.sendRedirect("AdminUser") ;
	    return ;
	}

	User theEditedUser = imcref.getUserById(Integer.parseInt(req.getParameter("user_id"))) ;
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
	    /* rrUserData.setMaxUsesWarningThreshold    ( Integer.parseInt(req.getParameter("max_uses_warning_threshold")) ) ; */
	    rrUserData.setExpiryDate                 ( expiryDate ) ;
	    /* rrUserData.setExpiryDateWarningThreshold ( Integer.parseInt(req.getParameter("expiry_date_warning_threshold")) ) ; */
	    imcref.setReadrunnerUserData(theEditedUser,rrUserData) ;
	} catch (NumberFormatException nfe) {
	    throw nfe ;
	}
	res.sendRedirect("AdminUser") ;
    }


    private void displayPage(IMCServiceInterface imcref, User user, User theEditedUser, HttpServletResponse res) throws IOException {

	ReadrunnerUserData rrUserData = imcref.getReadrunnerUserData(theEditedUser) ;
	if (null == rrUserData) {
	    rrUserData = new ReadrunnerUserData() ;
	}

	String expiryDateString =
	    null != rrUserData.getExpiryDate()
	    ? new SimpleDateFormat("yyyy-MM-dd").format(rrUserData.getExpiryDate())
	    : "" ;

	ArrayList parseList = new ArrayList() ;
	parseList.add("#user_id#") ;                       parseList.add(""+theEditedUser.getUserId()) ;
	parseList.add("#uses#") ;                          parseList.add(""+rrUserData.getUses()) ;
	parseList.add("#max_uses#") ;                      parseList.add(""+rrUserData.getMaxUses()) ;
	/* parseList.add("#max_uses_warning_threshold#") ;    parseList.add(""+rrUserData.getMaxUsesWarningThreshold()) ; */
	parseList.add("#expiry_date#") ;                   parseList.add(expiryDateString) ;
	/* parseList.add("#expiry_date_warning_threshold#") ; parseList.add(""+rrUserData.getExpiryDateWarningThreshold()) ; */

	res.setContentType("text/html") ;
	Writer out = res.getWriter() ;
	out.write(imcref.parseDoc(parseList, "readrunner/adminreadrunneruser.html", user.getLangPrefix())) ;

    }

}
