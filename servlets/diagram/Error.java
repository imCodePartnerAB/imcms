import java.io.* ;
import java.awt.* ;
import java.util.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
	
public class Error {

public Error(HttpServletRequest req, HttpServletResponse res,String fileName, String msg) 
                               throws ServletException, IOException {
	
	Vector tags = new Vector() ;
	Vector data = new Vector() ;
	tags.add("ERROR_MESSAGE") ;
	data.add(msg) ;
	
	// Lets get the TemplateFolder
	String templateLib = MetaInfo.getExternalTemplateFolder(req) ;
	
// Lets use the Html generator class to parse on the server
	HtmlGenerator htmlObj = new HtmlGenerator(templateLib + fileName) ;
	String html = htmlObj.createHtmlString(tags, data, req) ;
//	String html = this.createHtmlString(tags, data, fileName) ;
	this.sendToBrowser(req, res, html) ;
}

public void sendToBrowser(HttpServletRequest req, HttpServletResponse res, String str)
		throws ServletException, IOException {

		// Lets send settings to a browser
		PrintWriter out = res.getWriter() ;
		res.setContentType("Text/html") ;
		out.println(str) ;
	}

} // End of class