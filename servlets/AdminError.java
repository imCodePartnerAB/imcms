import java.io.* ;
import java.awt.* ;
import java.util.* ;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.* ;
import imcode.server.* ;
import imcode.util.* ;

public class AdminError extends Administrator {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;


    public AdminError(HttpServletRequest req, HttpServletResponse res, String header,String msg)
	throws ServletException, IOException {

	Vector tags = new Vector() ;
	Vector data = new Vector() ;
	tags.add("ERROR_HEADER") ;
	tags.add("ERROR_MESSAGE") ;
	data.add(header) ;
	data.add(msg) ;

	String fileName = "AdminError.htm" ;

	// Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) {
	    String aHeader = "Error in AdminCounter." ;
	    String aMsg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(aHeader + aMsg) ;
	    AdminError err = new AdminError(req,res,aHeader,aMsg) ;
	    return ;
	}


	// Lets get the path to the admin templates folder
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	File templateLib = super.getAdminTemplateFolder(imcref, user) ;

	HtmlGenerator htmlObj = new HtmlGenerator(templateLib, fileName) ;
	String html = htmlObj.createHtmlString(tags,data,req) ;
	res.setContentType("text/html") ;
	htmlObj.sendToBrowser(req,res,html) ;
	return ;
    }

    public void log( String str) {
	System.err.println("AdminError: " + str) ;
    }

} // End of class
