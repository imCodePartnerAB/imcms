import java.io.* ;
import java.util.* ;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.* ;
import imcode.server.* ;

public class AdminError extends Administrator {

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
	imcode.server.user.UserDomainObject user = getUserObj(req,res) ;
	if(user == null) {
	    String aHeader = "Error in AdminCounter." ;
	    String aMsg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(aHeader + aMsg) ;
	    new AdminError(req,res,aHeader,aMsg) ;
	    return ;
	}


	// Lets get the path to the admin templates folder
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
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
