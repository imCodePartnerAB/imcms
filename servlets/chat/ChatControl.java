













import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;
import imcode.util.IMCServiceRMI;



public class ChatControl extends ChatBase
{
	String HTML_TEMPLATE ;
	String A_HREF_HTML ;   // The code snippet where the aHref list with all discussions
	//	int DISCSHOWCOUNTER = 20 ;
	// will be placed.
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{


	} // DoPost

	/**
	doGet
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{

	} //DoGet


	/**
	Detects paths and filenames.
	*/

	public void init(ServletConfig config)
	throws ServletException
	{
		super.init(config);
		HTML_TEMPLATE = "Conf_Disc.htm" ;
		A_HREF_HTML = "Conf_Disc_List.htm" ;
	}

	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String str)
	{
		super.log(str) ;
		System.out.println("ConfDisc: " + str ) ;
	}






} // End of class











