import java.io.* ;
import java.awt.* ;
import java.util.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;

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
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) {
	    String aHeader = "Error in AdminCounter." ;
	    String aMsg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(aHeader + aMsg) ;
	    AdminError err = new AdminError(req,res,aHeader,aMsg) ;
	    return ;
	}

	//String meta_idStr = (req.getParameter("meta_id")==null) ? "1001" : (req.getParameter("meta_id")) ;
	//int meta_id = Integer.parseInt(meta_idStr) ;
		
	// Lets get the path to the admin templates folder
	String host 				= req.getHeader("Host") ;
	String server 			= Utility.getDomainPref("adminserver",host) ;  
	String templateLib = super.getAdminTemplateFolder(server, user) ;
    
	this.log("host: " + host) ;
	this.log("server: " + server) ;
	this.log("templateLib: " +  templateLib) ;
     
	//RmiLayer rmi = new RmiLayer(user) ;		
	//String templateLib = rmi.getInternalTemplateFolder(meta_id) ;
	//String templateLib = MetaInfo.getInternalTemplateFolder() ;
	//templateLib += "se/admin/" ;
	 	
	HtmlGenerator htmlObj = new HtmlGenerator(templateLib, fileName) ;
	String html = htmlObj.createHtmlString(tags,data,req) ;
	res.setContentType("text/html") ;
 	htmlObj.sendToBrowser(req,res,html) ;
	return ;
    }

    public void log( String str) {
	//super.log("AddminError: " + str) ;
	System.err.println("AdminError: " + str) ;     
    }

} // End of class
	
	
