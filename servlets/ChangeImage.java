import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
//import javax.swing.*; // (Använder ImageIcon)
/**
  Edit imageref  - upload image to server.
  */
public class ChangeImage extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	/**
	init()
	*/
	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	String host 			= req.getHeader("Host") ;
	String imcserver 		= Utility.getDomainPref("adminserver",host) ;
	String start_url        	= Utility.getDomainPref( "start_url",host ) ;
	String image_url                = Utility.getDomainPref( "image_url",host ) ;
	
	if (req.getParameter("preview")==null) {
	    doGet(req,res) ;
	    return ;
	}
	
	HttpSession session = req.getSession(true);
	imcode.server.User user = (imcode.server.User)session.getValue("logon.isDone");

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
        res.getOutputStream().print(ImageBrowse.getPage(req,res)) ;
	return ;
    }

	/**
	  doGet()
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 			= req.getHeader("Host") ;
		String imcserver 		= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String image_url                = Utility.getDomainPref( "image_url",host ) ;
		String image_path               = Utility.getDomainPref( "image_path",host ) ;
		imcode.server.User user ; 
		String htmlStr = "" ;     
		int meta_id ;
		int img_no ;

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		String tmp = (req.getParameter("img_no") != null)?req.getParameter("img_no"):req.getParameter("img") ;
		//log (tmp);
		img_no = Integer.parseInt(tmp) ;


		tmp = req.getParameter("meta_id") ;
		//log (tmp);
		meta_id = Integer.parseInt(tmp) ;


		// Check if ChangeImage is invoked by ImageBrowse, hence containing
		// an image filename as option value (M. Wallin)
		String img_preset = (req.getParameter("imglist") == null)?"":java.net.URLDecoder.decode(req.getParameter("imglist"));

		/*
		  Enumeration logga = req.getParameterNames();
		  while(logga.hasMoreElements())
		  log("PARAMETER: " + logga.nextElement());
		*/
		
		// Get the session
		HttpSession session = req.getSession(true);

		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		user = (imcode.server.User)done ;
		//log ("a") ;


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
		//log ("b") ;
		// Check if user has write rights
		if ( !IMCServiceRMI.checkDocAdminRights(imcserver, meta_id, user) ) {
			log("User "+user.getInt("user_id")+" was denied access to meta_id "+meta_id+" and was sent to "+start_url) ;			
			String scheme = req.getScheme() ;
			String serverName = req.getServerName() ;
			int p = req.getServerPort() ;
			String port = ( p == 80 ) ? "" : ":" + p ;
			res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
			return ;
		}
		//log ("c") ;
		
		
		
		//*lets get some path we need later on
		File file_path = new File(image_path);
		String canon_path = file_path.getCanonicalPath();//ex: C:\Tomcat3\webapps\imcms\images
		String root_dir_parent = file_path.getParent();//ex: c:\Tomcat3\webapps\imcms
		String root_dir_name = canon_path.substring(root_dir_parent.length());
		if (root_dir_name.startsWith(File.separator))
		{
			root_dir_name = root_dir_name.substring(File.separator.length());
			//ex: root_dir_name = images
		}		
		//*lets get the dirlist, and add the rootdir to it
		List imageFolders = GetImages.getImageFolders(file_path, true) ;
		imageFolders.add(0,file_path);
				
		//ok we have the list, now lets setup the option list
		StringBuffer folderOptions = new StringBuffer();
		for(int i=0;i<imageFolders.size();i++)
		{
			File fileObj = (File) imageFolders.get(i);						
			//ok lets set up the folder name to show and the one to put as value
			String optionName = fileObj.getCanonicalPath();
			//lets remove the start of the path so we end up at the rootdir. 
			if (optionName.startsWith(canon_path))
			{
				optionName = optionName.substring(root_dir_parent.length()) ;
				if (optionName.startsWith(File.separator))
				{
					optionName = optionName.substring(File.separator.length()) ;
				}
			}else if(optionName.startsWith(File.separator))
			{
				optionName = optionName.substring(File.separator.length()) ;
			}			
			//the path to put in the option value
			String optionPath = optionName;
			if (optionPath.startsWith(root_dir_name))
			{
				optionPath = optionPath.substring(root_dir_name.length());
			}
			System.out.println("optionPath: "+optionPath);
			//ok now we have to replace all parent folders with a '-' char
			StringTokenizer token = new StringTokenizer(optionName,"\\",false);
			StringBuffer buff = new StringBuffer("");
			if (token.countTokens() > 2)
			{
				//lets only allowe one dir down from imageroot
				break;
			}
			while ( token.countTokens() > 1 )
			{
				String temp = token.nextToken();
				buff.append("&nbsp;&nbsp;-");				
			}			
			if (token.countTokens() > 0)
			{
				optionName = buff.toString()+token.nextToken();
			}			
			File urlFile = new File(optionName) ;
			String fileName = urlFile.getName() ;
			File parentDir = urlFile.getParentFile() ;			
			if (parentDir != null)
			{
				optionName = parentDir.getPath()+"/" ;
			}
			else
			{
				optionName = "" ;
			}			
			//filepathfix ex: images\nisse\kalle.gif to images/nisse/kalle.gif
			optionName = optionName.replace(File.separatorChar,'/')+fileName ;			
			StringTokenizer tokenizer = new StringTokenizer(optionName, "/", true);
			StringBuffer filePathSb = new StringBuffer();
			StringBuffer displayFolderName = new StringBuffer();
			//the URLEncoder.encode() method replaces '/' whith "%2F" and the can't be red by the browser
			//that's the reason for the while-loop. 
			while ( tokenizer.countTokens() > 0 )
			{
				String temp = tokenizer.nextToken();
				if (temp.length() > 1)
				{	
					filePathSb.append(java.net.URLEncoder.encode(temp));
				}else
				{
					filePathSb.append(temp);
				}
			}	
			optionName = optionName.replace('-','\\');//Gud strul					
			String parsedFilePath = filePathSb.toString() ;
			folderOptions.append("<option value=\"" + optionPath + "\">" + optionName + "</option>\r\n");	
			session.setAttribute("imageFolderOptionList",folderOptions.toString());
		}//end for loop
		
				
		
		String sqlStr = "select image_name,imgurl,width,height,border,v_space,h_space,target,target_name,align,alt_text,low_scr,linkurl from images where meta_id = "+meta_id+" and name = "+img_no ;
		String[] sql = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
		//log ("d") ;
		Vector vec = new Vector () ;

		String imageName = ("".equals(img_preset)&&sql.length>0?sql[1]:img_preset); // selected OPTION or ""

		//****************************************************************
		ImageFileMetaData image = new ImageFileMetaData(new File(image_path,imageName)) ;
		int width = image.getWidth() ;
		int height = image.getHeight() ;
		//****************************************************************
		
		imageName = imageName ;
		
		if ( sql.length > 0 ) {
		    int current_width = 0 ;
		    try {
			current_width = Integer.parseInt(img_preset.equals("")?sql[2]:"" + width) ;
		    } catch ( NumberFormatException ex ) {
			
		    }
		    int current_height = 0 ;
		    try {
			current_height = Integer.parseInt(img_preset.equals("")?sql[3]:"" + height) ;
		    } catch ( NumberFormatException ex ) {
			
		    }
		    int aspect = 0 ;
		    if (current_width * current_height != 0) {
			aspect = 100 * current_width / current_height ;
		    }

		    String keepAspect = "checked" ;
		    
		    if (width * height != 0 && aspect != (100 * width / height)) {
			keepAspect = "" ;
		    }

		    //log("sql.lenght > 0");
			vec.add("#imgName#") ;
			vec.add(sql[0]) ;
			vec.add("#imgRef#") ;
			vec.add(imageName);
			vec.add("#imgWidth#") ;
			vec.add(current_width!=0?""+current_width:""+width);
			vec.add("#origW#"); // original imageWidth
			vec.add("" + width);
			vec.add("#imgHeight#") ;
			vec.add(current_height!=0?""+current_height:""+height);
			vec.add("#origH#");
			vec.add("" + height); // original imageHeight

			vec.add("#keep_aspect#") ;
			vec.add(keepAspect) ;
			
			vec.add("#imgBorder#") ;
			vec.add(sql[4]) ;
			vec.add("#imgVerticalSpace#") ;
			vec.add(sql[5]) ;
			vec.add("#imgHorizontalSpace#") ;
			vec.add(sql[6]) ;
			if ( "_top".equals(sql[7]) ) {
				vec.add("#target_name#") ;
				vec.add("") ;
				vec.add("#top_checked#") ;
			} else if ( "_self".equals(sql[7]) ) {
				vec.add("#target_name#") ;
				vec.add("") ;
				vec.add("#self_checked#") ;
			} else if ( "_blank".equals(sql[7]) ) {
				vec.add("#target_name#") ;
				vec.add("") ;
				vec.add("#blank_checked#") ;
			} else if ( "_parent".equals(sql[7]) ) {
				vec.add("#target_name#") ;
				vec.add("") ;
				vec.add("#blank_checked#") ;
			} else {
				vec.add("#target_name#") ;
				vec.add(sql[8]) ;
				vec.add("#other_checked#") ;
			}
			vec.add("selected") ;	

			if ( "baseline".equals(sql[9]) ) {
				vec.add("#baseline_selected#") ;
			} else if ( "top".equals(sql[9]) ) {
				vec.add("#top_selected#") ;
			} else if ( "middle".equals(sql[9]) ) {
				vec.add("#middle_selected#") ;
			} else if ( "bottom".equals(sql[9]) ) {
				vec.add("#bottom_selected#") ;
			} else if ( "texttop".equals(sql[9]) ) {
				vec.add("#texttop_selected#") ;
			} else if ( "absmiddle".equals(sql[9]) ) {
				vec.add("#absmiddle_selected#") ;
			} else if ( "absbottom".equals(sql[9]) ) {
				vec.add("#absbottom_selected#") ;
			} else if ( "left".equals(sql[9]) ) {
				vec.add("#left_selected#") ;
			} else if ( "right".equals(sql[9]) ) {
				vec.add("#right_selected#") ;
			} else {
				vec.add("#none_selected#") ;
			}
			vec.add("selected") ;	

			vec.add("#imgAltText#") ;
			vec.add(sql[10]) ;
			vec.add("#imgLowScr#") ;
			vec.add(sql[11]) ;
			vec.add("#imgRefLink#") ;
			vec.add(sql[12]) ;
		} else {
								
			vec.add("#imgName#") ;
			vec.add("") ;
			vec.add("#imgRef#") ;
			vec.add(imageName);
			vec.add("#imgWidth#") ;
			vec.add("" + width) ;
			vec.add("#imgHeight#") ;
			vec.add("" + height) ;
			
			vec.add("#origW#");
			vec.add("" + width);
			vec.add("#origH#");
			vec.add("" + height);
			
			vec.add("#imgBorder#") ;
			vec.add("0") ;
			vec.add("#imgVerticalSpace#") ;
			vec.add("0") ;
			vec.add("#imgHorizontalSpace#") ;
			vec.add("0") ;
			vec.add("#target_name#") ;
			vec.add("") ;
			vec.add("#self_checked#") ;
			vec.add("selected") ;	
			vec.add("#top_selected#") ;
			vec.add("selected") ;	
			vec.add("#imgAltText#") ;
			vec.add("") ;
			vec.add("#imgLowScr#") ;
			vec.add("") ;
			vec.add("#imgRefLink#") ;
			vec.add("") ;
			
		}
		vec.add("#imgUrl#") ;
		vec.add(image_url) ;
		vec.add("#getMetaId#") ;
		vec.add(String.valueOf(meta_id)) ;
		vec.add("#img_no#") ;
		vec.add(String.valueOf(img_no)) ;
		
		
		vec.add("#folders#");
		vec.add(folderOptions.toString());
		
		//log ("e") ;
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		//log ("f") ;		
		htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"change_img.html", lang_prefix) ;
		//log ("g") ;
		//htmlStr = IMCServiceRMI.interpretAdminTemplate(imcserver,meta_id,user,"change_img.html",img_no,0,0,0) ;                        	
		out.print(htmlStr) ;

	}
}
