import java.io.*;
import java.io.FilenameFilter;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;

/**
  Browse images in image-directory.
**/
public class ImageBrowse extends HttpServlet {

	
	
	/**
	init
	*/
	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}

	/**
	doGet
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url       	= Utility.getDomainPref( "servlet_url",host ) ;
		

		imcode.server.User user ; 
		String htmlStr = "" ;     

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();


		// Get the session
 		HttpSession session = req.getSession(true);

		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		user = (imcode.server.User)done ;

		if (done == null) {
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.
			String scheme = req.getScheme();
			String serverName = req.getServerName();
			int p = req.getServerPort();
			String port = (p == 80) ? "" : ":" + p;
			res.sendRedirect(scheme + "://" + serverName + port + start_url) ;              
			return ;
		}
		
		
			String meta_id = req.getParameter("meta_id");
			String img_no = req.getParameter("img_no");

			GetImages gi = new GetImages();
			String FILE_SEP = System.getProperty("file.separator");

			File file_path = new File(Utility.getDomainPref( "image_path", host ));
			List imgList = gi.getImageFiles("" + file_path, true, true);
			String filePath;
			File fileObj;
			
			
			
				String options = "";
								
				String whatToSelect = (req.getParameter("select")==null)?"":req.getParameter("select");
				String selected = (whatToSelect.equals(""))?"selected":"";
				
				System.out.println("whatToSelect & selected " + whatToSelect + " & " + selected);
				
				for(Iterator it=imgList.iterator();it.hasNext();) 
					{
						fileObj = (File) it.next();
						filePath = "" + fileObj;
						// make absolute path relative
						filePath = filePath.substring(filePath.lastIndexOf("\\images\\"));
						try 
						{
							 String fileLen = "" + fileObj.length();
						} catch (SecurityException e) 
						{
							String fileLen = "Read ERROR";
						}
						
					
						filePath = filePath.replace('\\','/');
						if(filePath.equals(whatToSelect))
							selected="selected";
						options += "<option name='select' value='" + filePath + "' " + selected + ">[" + fileObj.length() + "] " + filePath.substring(filePath.lastIndexOf(FILE_SEP)+1) + "</option>";
						if(selected.equals("selected"))
							selected = "";
					}
				
				
				// TEMPLATE ImageBrowse.html
				Vector vec = new Vector () ;
				vec.add("#meta_id#");
				vec.add(meta_id);
				
				vec.add("#img_preview#");
				vec.add("");
				
				vec.add("#img_no#");
				vec.add(img_no);
				
				vec.add("#options#");
				vec.add(options);
				session.putValue("optionlist",options);
				
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"ImageBrowse.html", lang_prefix) ;
				out.print(htmlStr) ;


	}

}
