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
	String start_url        	= Utility.getDomainPref( "start_url",host ) ;

	// Get the session
	HttpSession session = req.getSession(true);

	// Does the session indicate this user already logged in?
	imcode.server.User user  = (imcode.server.User)session.getValue("logon.isDone");  // marker object

	if (user == null) {
	    // No logon.isDone means he hasn't logged in.
	    // Save the request URL as the true target and redirect to the login page.
	    String scheme = req.getScheme();
	    String serverName = req.getServerName();
	    int p = req.getServerPort();
	    String port = (p == 80) ? "" : ":" + p;
	    res.sendRedirect(scheme + "://" + serverName + port + start_url) ;              
	    return ;
	}

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	
	out.print(getPage(req,res)) ;
    }

    public static String getPage(HttpServletRequest req, HttpServletResponse res) throws IOException {
	String host 				= req.getHeader("Host") ;
	String imcserver 			= Utility.getDomainPref("adminserver",host) ;
	String servlet_url       	= Utility.getDomainPref( "servlet_url",host ) ;
	String image_url                = Utility.getDomainPref( "image_url", host ) ;
	File file_path = new File(Utility.getDomainPref( "image_path", host ));

	// Get the session
	HttpSession session = req.getSession(false);
	imcode.server.User user  = (imcode.server.User)session.getValue("logon.isDone");  // marker object

	String meta_id = req.getParameter("meta_id");
	String img_no = req.getParameter("img_no");
	String img_preset = req.getParameter("imglist") ;
	String img_tag = "" ;
	if (img_preset == null) {
	    img_preset = "" ;
	} else {
	    img_tag = "<img src='"+img_preset+"'>" ;
	}

	String canon_path = file_path.getAbsolutePath() ;
	List imgList = (List)session.getValue("ImageBrowse.optionlist") ;
	session.removeValue("ImageBrowse.optionlist") ;

	if (imgList == null) {
	    imgList = GetImages.getImageFiles(file_path, true, true);
	}
			
	StringBuffer options = new StringBuffer(imgList.size()*64) ;
	
	for(Iterator it=imgList.iterator();it.hasNext();) 
	    {
		File fileObj = (File) it.next();

		try {
		    String fileLen = "" + fileObj.length();
		} catch (SecurityException e) {
		    String fileLen = "Read ERROR";
		}
						
		String filePath = fileObj.getAbsolutePath() ;
		if (filePath.startsWith(canon_path)) {
		    filePath = filePath.substring(canon_path.length()) ;
		}
		if (filePath.startsWith(File.separator)) {
		    filePath = filePath.substring(File.separator.length()) ;
		}

		File urlFile = new File(image_url+filePath) ;
		filePath = urlFile.getParentFile().getPath() ;

		String fileName = java.net.URLEncoder.encode(urlFile.getName()) ;
		filePath = filePath.replace(File.separatorChar,'/')+"/"+fileName ;

		StringBuffer filePathSb = new StringBuffer(filePath) ;
		// Replace all '+' and ' ' with '%20'
		for (int i=0; i<filePathSb.length() ; ++i) {
		    if (filePathSb.charAt(i)=='+' || filePathSb.charAt(i)==' ') {
			filePathSb.replace(i,i+1,"%20") ;
			i += 2 ;
		    }
		}
		filePath = filePathSb.toString() ;

		options.append("<option value=\"" + filePath + "\"" + (filePath.equals(img_preset)?" selected":"") + ">[" + fileObj.length() + "] " + filePath + "</option>\r\n");
	    }
				
				
				// TEMPLATE ImageBrowse.html
	Vector vec = new Vector () ;
	vec.add("#meta_id#");
	vec.add(meta_id);
				
	vec.add("#img_preview#");
	vec.add(img_tag);
				
	vec.add("#img_no#");
	vec.add(img_no);
				
	vec.add("#options#");
	vec.add(options.toString());

	session.putValue("ImageBrowse.optionlist",imgList);
				
	String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
	return IMCServiceRMI.parseDoc(imcserver,vec,"ImageBrowse.html", lang_prefix) ;

    }
}
