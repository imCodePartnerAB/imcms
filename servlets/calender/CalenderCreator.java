import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
import imcode.server.* ;

/**
   Servlet that creates a new Calender.
   Extends Calender making it a servlet.
   @author Robert Engzell
*/
public class CalenderCreator extends Calender
{
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /**
       Creates a new Calender and activates it in the imCMS system.
       @param req servlet request object
       @param res servlet response object
       @throws ServletException if the request could not be handled
       @throws IOException if detected when handling the request
    */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
    {
	// Get info and check login
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	Properties prop = getSessionParams(req, res);
	imcode.server.user.UserDomainObject user = super.checkLogin(req, res, imcref.getStartUrl());

	if (prop.equals(null) || user.equals(null))
	    {
		res.sendRedirect("Getdoc?meta_id=1001");
		return;
	    }

	try
	    {
		CalenderDbManager dbManager = new CalenderDbManager(req);
		dbManager.newCalender(prop, user);
	    }
	catch(IOException excep)
	    {
		System.out.println(excep.getMessage());
	    }
	res.sendRedirect("CalenderManager?action=VIEW");
	return;

    }
}
