import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.*;
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
	String host			= req.getHeader("Host") ;
	String imcserver		= Utility.getDomainPref("adminserver",host) ;
	String start_url	= Utility.getDomainPref( "start_url",host ) ;
	String image_url                = Utility.getDomainPref( "image_url",host ) ;

	if (req.getParameter("preview")==null) {
	    doGet(req,res) ;
	    return ;
	}

	HttpSession session = req.getSession(true);
	imcode.server.User user = (imcode.server.User)session.getAttribute("logon.isDone");

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
	String host			= req.getHeader("Host") ;
	String imcserver		= Utility.getDomainPref("adminserver",host) ;
	String start_url	= Utility.getDomainPref( "start_url",host ) ;
	String image_url                = Utility.getDomainPref( "image_url",host ) ;
	File image_path               = Utility.getDomainPrefPath( "image_path",host ) ;
	imcode.server.User user ;
	String htmlStr = "" ;
	int meta_id ;
	int img_no ;

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String tmp = (req.getParameter("img_no") != null)?req.getParameter("img_no"):req.getParameter("img") ;
	img_no = Integer.parseInt(tmp) ;


	tmp = req.getParameter("meta_id") ;
	meta_id = Integer.parseInt(tmp) ;

	String label = req.getParameter("label") ;
	if (label == null) {
	    label = "" ;
	}

	// Check if ChangeImage is invoked by ImageBrowse, hence containing
	// an image filename as option value (M. Wallin)
	String img_preset = (req.getParameter("imglist") == null)?"":java.net.URLDecoder.decode(req.getParameter("imglist"));

	// Get the session
	HttpSession session = req.getSession(true);

	// Does the session indicate this user already logged in?
	Object done = session.getAttribute("logon.isDone");  // marker object
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




	//*lets get the root_dir_name that we need later on
	String root_dir_name = image_path.getName();

	//*lets get the dirlist, and add the rootdir to it
	List imageFolders = GetImages.getImageFolders(image_path, true) ;
	imageFolders.add(0,image_path);

	//ok we have the list, now lets setup the option list to send to browser
	StringBuffer folderOptions = new StringBuffer();
	Iterator iter = imageFolders.iterator();
	while (iter.hasNext()){

	    File fileObj = (File) iter.next();
	    String optionValue, optionName;
	    //OBS! we only allowe on directory down from imageRoot,
	    //so the current "directory" or the "parent" must be equal to root_dir_name
	    //lets start and see if the parent is root_dir
	    if (  root_dir_name.equals(fileObj.getParentFile().getName()) ){
		optionValue = HTMLConv.toHTML("\\"+fileObj.getName());
		optionName = HTMLConv.toHTML("&nbsp;&nbsp;\\"+fileObj.getName());
	    }else if( root_dir_name.equals(fileObj.getName()) ){
		optionValue = "";
		optionName = HTMLConv.toHTML(fileObj.getName());
	    }else{
		continue;
	    }

	    folderOptions.append("<option value=\"" + optionValue + "\">" + optionName + "</option>\r\n");
	}//end while loop
	session.setAttribute("imageFolderOptionList",folderOptions.toString());


	String sqlStr = "select image_name,imgurl,width,height,border,v_space,h_space,target,target_name,align,alt_text,low_scr,linkurl from images where meta_id = "+meta_id+" and name = "+img_no ;
	String[] sql = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;

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
	vec.add("#label#") ;
	vec.add(label) ;


	String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

	htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"change_img.html", lang_prefix) ;

	out.print(htmlStr) ;

    }
}
