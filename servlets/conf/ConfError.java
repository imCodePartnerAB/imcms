import java.io.* ;
import java.awt.* ;
import java.util.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.Utility;

public class ConfError extends Conference {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private static final String ERROR_FILE = "Conf_Error.htm";
	String myErrorHeader ;
	String myErrorMessage ;


/**
	Constructor which is used to read the error strings in the translation file. This
	one should not be used to generate errormessages
*/
	public ConfError() throws ServletException, IOException {
		myErrorHeader  = "" ;
		myErrorMessage = "" ;
	}

	public ConfError(HttpServletRequest req, HttpServletResponse res, String header, int errorCode)
                               throws ServletException, IOException {

		myErrorHeader = header ;
		VariableManager vm = new VariableManager() ;

	// Lets get the errormessage from the error file
		String myErrorMessage = this.getErrorMessage(req, errorCode) ;

		vm.addProperty("ERROR_HEADER", header) ;
		vm.addProperty("ERROR_MESSAGE", myErrorMessage) ;
		//String fileName = "Conf_Error.htm" ;

	// Lets send a html string to the browser
		//super.sendHtml(req, res, vm, fileName) ;
		sendErrorHtml(req, res, vm, ERROR_FILE ) ;
		return ;

}

public ConfError(HttpServletRequest req, HttpServletResponse res, String header, String msg, int errorCode)
                               throws ServletException, IOException {

		VariableManager vm = new VariableManager() ;

	// Lets get the errormessage from the error file
		String aMessage = this.getErrorMessage(req, errorCode) ;
		aMessage += " " + msg ;

		vm.addProperty("ERROR_HEADER", header) ;
		vm.addProperty("ERROR_MESSAGE", aMessage) ;
		//String fileName = "Conf_Error.htm" ;

	// Lets send a html string to the browser
		//super.sendHtml(req, res, vm, fileName) ;
		sendErrorHtml(req, res, vm, ERROR_FILE) ;
		return ;

}

/**
	ConfError, takes a message instead of an int
*/
public ConfError(HttpServletRequest req, HttpServletResponse res, String header, String msg)
                               throws ServletException, IOException {

		VariableManager vm = new VariableManager() ;

		vm.addProperty("ERROR_HEADER", header) ;
		vm.addProperty("ERROR_MESSAGE", msg) ;
		//String fileName = "Conf_Error.htm" ;

	// Lets send a html string to the browser
		//super.sendHtml(req, res, vm, fileName) ;
		sendErrorHtml(req, res, vm, ERROR_FILE) ;
		return ;

}



/**
	Returns the error header och message for this object
*/

public String getErrorString() {
	return myErrorHeader + " " + myErrorMessage ;
}



/**
	Returns the errormessageheader for this object
*/

public String getErrorHeader() {
	return myErrorHeader ;
}

/**
	Returns the errormessage for this object
*/

public String getErrorMsg() {
	return myErrorMessage ;
}

/**
	Retrieves the errormessage corresponding to the errorcode. Reads the
	information from a file in the template folder called errmsg.ini
*/

public String getErrorMessage(HttpServletRequest req, int errCode) {
	try {
	// Lets get the path to the template library
		File folder = super.getExternalTemplateRootFolder(req) ;
		log("ExternalFolder was: " + folder) ;

	// Lets get the error code

		SettingsAccessor setObj = new SettingsAccessor(new File(folder, "errmsg.ini")) ;
		setObj.setDelimiter("=") ;
		setObj.loadSettings() ;
		myErrorMessage = setObj.getSetting("" + errCode) ;
		if (myErrorMessage == null ) {
			myErrorMessage = "Missing Errorcode " + errCode;
		}

	} catch(Exception e) {
			log("An error occured while reading the errmsg.ini file")	;
	}
		return myErrorMessage ;
}

public void log(String msg) {
//	 super.log(msg) ;
	 System.out.println("ConfError: " + msg) ;
}

/*
	For special messages, if we want to pass a special htmlfile
*/
	public ConfError(HttpServletRequest req, HttpServletResponse res, String header, int errorCode, String fileName)
     throws ServletException, IOException {

		myErrorHeader = header ;
		VariableManager vm = new VariableManager() ;

	// Lets get the errormessage from the error file
		myErrorMessage = this.getErrorMessage(req, errorCode) ;
		vm.addProperty("ERROR_CODE", "" + errorCode) ;
		vm.addProperty("ERROR_HEADER", header) ;
		vm.addProperty("ERROR_MESSAGE", myErrorMessage) ;

	// Lets send a html string to the browser
		//super.sendHtml(req, res, vm, fileName) ;
		sendErrorHtml(req, res, vm, fileName) ;
		return ;

	}

	protected void sendErrorHtml( HttpServletRequest req, HttpServletResponse res,
		VariableManager vm, String htmlFile ) throws ServletException, IOException {

		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;

		// Lets get the TemplateFolder  and the foldername used for this certain metaid
		File templateLib = this.getExternalTemplateFolder( req ) ;

		// Lets add 3 server hostadresses
		String servletPath = MetaInfo.getServletPath(req) ;

		// Lets get the path to the imagefolder.
		String imagePath = this.getExternalImageFolder(req) ;

		vm.addProperty("IMAGE_URL", imagePath);
		vm.addProperty("SERVLET_URL", servletPath);

		HtmlGenerator htmlObj = new HtmlGenerator( templateLib, htmlFile ) ;
		String html = htmlObj.createHtmlString(vm,req) ;
		log(html);
		log( htmlFile );

		htmlObj.sendToBrowser( req, res, html ) ;
	}

} // End of class


