import java.io.* ;
import javax.servlet.http.*;

import imcode.external.diverse.* ;
import imcode.server.* ;

public class AdminError2 extends Administrator {

    String myErrorHeader ;
    String myErrorMessage ;

    /**
       Constructor which is used to read the error strings in the translation file. This
       one should not be used to generate errormessages
    */
    public AdminError2() {
        myErrorMessage = "" ;
    }

    public AdminError2(HttpServletRequest req, HttpServletResponse res, String header, int errorCode)
	throws IOException {

        VariableManager vm = new VariableManager() ;

	// Lets get the errormessage from the error file
	String myErrorMessage = this.getErrorMessage(req, res, errorCode) ;

	vm.addProperty("ERROR_HEADER", header) ;
	vm.addProperty("ERROR_MESSAGE", myErrorMessage) ;
	vm.addProperty("ERROR_CODE", ""+ errorCode) ;
	String fileName = "Admin_Error2.htm" ;

	// Lets send a html string to the browser
	super.sendHtml(req, res, vm, fileName) ;
	return ;

    }

    public AdminError2(HttpServletRequest req, HttpServletResponse res, String header, String msg, int errorCode)
	throws IOException {

	VariableManager vm = new VariableManager() ;

	// Lets get the errormessage from the error file
	String aMessage = this.getErrorMessage(req, res, errorCode) ;
	aMessage += " " + msg ;

	vm.addProperty("ERROR_HEADER", header) ;
	vm.addProperty("ERROR_MESSAGE", aMessage) ;
	vm.addProperty("ERROR_CODE", ""+ errorCode) ;
	String fileName = "Admin_Error2.htm" ;

	// Lets send a html string to the browser
	super.sendHtml(req, res, vm, fileName) ;
	return ;

    }

    /**
       ConfError, takes a message instead of an int
    */
    public AdminError2(HttpServletRequest req, HttpServletResponse res, String header, String msg)
	throws IOException {

	VariableManager vm = new VariableManager() ;

	vm.addProperty("ERROR_HEADER", header) ;
	vm.addProperty("ERROR_MESSAGE", msg) ;
	vm.addProperty("ERROR_CODE", " ") ;
	String fileName = "Admin_Error2.htm" ;

	// Lets send a html string to the browser
	super.sendHtml(req, res, vm, fileName) ;
	return ;

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

    private String getErrorMessage(HttpServletRequest req, HttpServletResponse res,int errCode) {
	try {
	    // Lets get the path to the admin templates folder
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

	    imcode.server.user.UserDomainObject user = getUserObj(req,res) ;
	    File folder = this.getAdminTemplateFolder(imcref, user) ;
	    //log("ExternalFolder was: " + folder) ;

	    // Lets get the error code

	    SettingsAccessor setObj = new SettingsAccessor( new File(folder, "ADMINERRMSG.INI")) ;
	    setObj.setDelimiter("=") ;
	    setObj.loadSettings() ;
	    myErrorMessage = setObj.getSetting("" + errCode) ;
	    if (myErrorMessage == null ) myErrorMessage = "Missing Errorcode" ;

	} catch(Exception e) {
	    log("An error occured while reading the ADMINERRMSG.ini file")	;
	}
	return myErrorMessage ;

    }

    public void log(String msg) {
	//	 super.log(msg) ;
	System.out.println("AdminError2: " + msg) ;
    }

    /*
      For special messages, if we want to pass a special htmlfile
    */
    public AdminError2(HttpServletRequest req, HttpServletResponse res, String header, int errorCode, String fileName)
	throws IOException {

        VariableManager vm = new VariableManager() ;

	// Lets get the errormessage from the error file
	myErrorMessage = this.getErrorMessage(req, res, errorCode) ;
	vm.addProperty("ERROR_CODE", "" + errorCode) ;
	vm.addProperty("ERROR_HEADER", header) ;
	vm.addProperty("ERROR_MESSAGE", myErrorMessage) ;

	// Lets send a html string to the browser
	super.sendHtml(req, res, vm, fileName) ;
	return ;

    }

} // End of class
