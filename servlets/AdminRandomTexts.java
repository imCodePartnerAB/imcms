import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;
import imcode.util.fortune.* ;
import java.text.SimpleDateFormat;


public class AdminRandomTexts extends Administrator implements imcode.server.IMCConstants{
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private final static String HTML_TEMPLATE 		= "admin_random_texts.html";
	private final static String HTML_TEMPLATE_ADMIN = "admin_random_texts_file.html";
	private final static String OPTION_LINE 		= "option_line.frag";
	
	/**
	The GET method creates the html page when this side has been
	redirected from somewhere else.
	**/
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException	{

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		// Lets get the server this request was aimed for
		String host = req.getHeader("Host") ;	
		String imcServer = Utility.getDomainPref("adminserver",host) ;

		// Lets validate the session
		if (super.checkSession(req,res) == false)	return ;

		// Lets get an user object  
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null){
			String header = "Error in AdminRandomTexts." ;
			String msg = "Couldnt create an user object."+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;
		}

		// Lets verify that the user who tries to admin a fortune is an admin  	
		if (super.checkAdminRights(imcServer, user) == false){ 
			String header = "Error in AdminRandomTexts." ;
			String msg = "The user is not an administrator."+ "<BR>" ;
			this.log(header + msg) ;

			// Lets get the path to the admin templates folder
			String server 			= Utility.getDomainPref("adminserver",host) ;  
			File templateLib = getAdminTemplateFolder(server, user) ;
		
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;
		}

		//get fortunefiles
		File fortune_path = Utility.getDomainPrefPath("FortunePath",host);
		File files[] = fortune_path.listFiles();

		String options = IMCServiceRMI.parseExternalDoc(imcServer, null, OPTION_LINE , user.getLangPrefix(), DOCTYPE_FORTUNES+"");

		for(int i=0;i<files.length;i++)	{
			//remove suffixes and create optionstring
			String totalFile = files[i].toString();
			int index = totalFile.lastIndexOf("\\");
			String filename=totalFile.substring(index+1);
			index = filename.lastIndexOf(".");
			String name=filename.substring(0,index);
			if ( ( !name.endsWith("current") ) && ( !name.endsWith("statistics") ) && ( !name.endsWith("poll") ) )	{
				options = options + "<option value=\""  + name + "\" > " + name + "</option>";
			}			
		}

		//Add info for parsing to a Vector and parse it with a template to a htmlString that is printed
		Vector values = new Vector();
		values.add("#options#");
		values.add(options);
	
		out.print(IMCServiceRMI.parseExternalDoc(imcServer, values, HTML_TEMPLATE , user.getLangPrefix(), DOCTYPE_FORTUNES+""));

	} // End doGet

	/**
	doPost
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException{    
		// Lets get the parameters and validate them, we dont have any own
		// parameters so were just validate the metadata
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

			String host = req.getHeader("Host") ;	
		String imcServer = Utility.getDomainPref("adminserver",host) ;

		imcode.server.User user ;

		// Check if the user logged on
		if ((user = Check.userLoggedOn(req,res,"StartDoc" )) == null ){
			return ;
		}

		HttpSession session = req.getSession();

	//	Map lines 	= Collections.synchronizedMap(new TreeMap());
	//	Map results = Collections.synchronizedMap(new TreeMap());

		String whichFile = req.getParameter("AdminFile") ;

		if (req.getParameter("back")!=null)	{
			res.sendRedirect("AdminManager") ;
			return;
		}

		if (whichFile.equals("No_Choice")){
			res.sendRedirect("AdminRandomTexts") ;
			return;
		}	

		session.setAttribute("file",whichFile);

	

		if (req.getParameter("edit")!=null)	{
			String options = IMCServiceRMI.parseExternalDoc(imcServer, null, OPTION_LINE , user.getLangPrefix(), DOCTYPE_FORTUNES+"");
			
			StringBuffer buff = new StringBuffer();
			List lines = IMCServiceRMI.getQuoteList(imcServer, whichFile+".txt");
			Iterator iter = lines.iterator();
			int counter = 0;
			SimpleDateFormat dateForm = new SimpleDateFormat("yyMMdd");
			while (iter.hasNext()) {
				Quote quote = (Quote) iter.next();
				DateRange dates = quote.getDateRange();
				buff.append("<option value=\""  + counter++ + "\" > "+dateForm.format(dates.getStartDate()) +" "+dateForm.format(dates.getEndDate())+" "+ quote.getText() + "</option>");
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
			
			out.print(IMCServiceRMI.parseExternalDoc(imcServer, values, HTML_TEMPLATE_ADMIN , user.getLangPrefix(), DOCTYPE_FORTUNES+""));
			session.setAttribute("lines",lines);
			return;
		}
	} 
	
	/**
	Log function, will work for both servletexec and Apache
	**/
	public void log( String str){
		super.log(str) ;
		System.out.println("AdminRandomTexts: " + str ) ;	
	}

} // End of class
