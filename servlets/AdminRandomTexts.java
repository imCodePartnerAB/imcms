import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;

import imcode.server.* ;
import imcode.server.document.DocumentDomainObject;
import imcode.util.* ;
import imcode.util.fortune.* ;

import java.text.SimpleDateFormat;


public class AdminRandomTexts extends Administrator implements imcode.server.IMCConstants {

    private final static String HTML_TEMPLATE		= "admin_random_texts.html";
    private final static String HTML_TEMPLATE_ADMIN = "admin_random_texts_file.html";
    private final static String OPTION_LINE		= "option_line.frag";

    private final static long ONE_DAY = 86400000 ;

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException	{

	res.setContentType("text/html");
	Writer out = res.getWriter();


	// Lets get the server this request was aimed for
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

	// Lets validate the session
	if (super.checkSession(req,res) == false)	return ;

	// Lets get an user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	if(user == null){
	    String header = "Error in AdminRandomTexts." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	// Lets verify that the user who tries to admin a fortune is an admin
	if (imcref.checkAdminRights(user) == false){
	    String header = "Error in AdminRandomTexts." ;
	    String msg = "The user is not an administrator."+ "<BR>" ;

	    // Lets get the path to the admin templates folder
	    File templateLib = getAdminTemplateFolder(imcref, user) ;

	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	//get fortunefiles
	File fortune_path = Utility.getDomainPrefPath("FortunePath" );
	File files[] = fortune_path.listFiles();

	StringBuffer options = new StringBuffer() ;

	for(int i=0;i<files.length;i++)	{
	    //remove suffixes and create optionstring
	    String filename=files[i].getName();
	    int index = filename.lastIndexOf(".");
	    if (index != -1) {
		filename=filename.substring(0,index);
	    }
	    if ( ( !filename.endsWith(".current") ) && ( !filename.endsWith(".stat") ) && ( !filename.endsWith(".poll") ) )	{
		options.append( "<option value=\""  + filename + "\">" + filename + "</option>" ) ;
	    }
	}

	//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
	Vector values = new Vector();
	values.add("#options#");
	values.add(options.toString());

	out.write(imcref.parseExternalDoc(values, HTML_TEMPLATE , user.getLangPrefix(), DocumentDomainObject.DOCTYPE_FORTUNES+""));

    } // End doGet

    /**
       doPost
    */
    public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException{
	// Lets get the parameters and validate them, we dont have any own
	// parameters so were just validate the metadata
	res.setContentType("text/html");
	Writer out = res.getWriter();

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

	imcode.server.user.UserDomainObject user ;

	// Check if the user logged on
	if ((user = Utility.getLoggedOnUserOrRedirect(req,res,"StartDoc" )) == null ){
	    return ;
	}

	HttpSession session = req.getSession();

	String whichFile = req.getParameter("AdminFile") ;

	if (req.getParameter("back")!=null)	{
	    res.sendRedirect("AdminManager") ;
	    return;
	}

	if (whichFile == null || "".equals(whichFile)){
	    res.sendRedirect("AdminRandomTexts") ;
	    return;
	}

	session.setAttribute("file",whichFile);

	if (req.getParameter("edit")!=null)	{
	    String options = imcref.parseExternalDoc(null, OPTION_LINE , user.getLangPrefix(), DocumentDomainObject.DOCTYPE_FORTUNES+"");

	    StringBuffer buff = new StringBuffer();
	    List lines = imcref.getQuoteList(whichFile+".txt");
	    Iterator iter = lines.iterator();
	    int counter = 0;
	    SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");
	    while (iter.hasNext()) {
		Quote quote = (Quote) iter.next();
		DateRange dates = quote.getDateRange();
		buff.append("<option value=\""  + counter++ + "\">"+dateForm.format(dates.getStartDate()) +" "+dateForm.format(new Date(dates.getEndDate().getTime()-ONE_DAY))+" "+ quote.getText() + "</option>");
	    }


	    String date1 = "";
	    String date2 = "";
	    String text  = "";

	    //Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
	    Vector values = new Vector();
	    values.add("#date1#");
	    values.add(date1);
	    values.add("#date2#");
	    values.add(date2);
	    values.add("#text#");
	    values.add(text);
	    values.add("#file#");
	    values.add(whichFile);
	    values.add("#options#");
	    values.add(buff.toString());

	    out.write(imcref.parseExternalDoc(values, HTML_TEMPLATE_ADMIN , user.getLangPrefix(), DocumentDomainObject.DOCTYPE_FORTUNES+""));
	    session.setAttribute("lines",lines);
	    return;
	}
    }

} // End of class
