import java.io.*;
import java.io.FilenameFilter;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.external.diverse.*;
import imcode.server.* ;

/**
   Browse images in image-directory.
**/
public class ImageBrowse extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    public final static String IMG_NEXT_LIST_TEMPLATE = "Admin_Img_List_Next.html";
    public final static String IMG_PREVIOUS_LIST_TEMPLATE = "Admin_Img_List_Previous.html";

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

	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	String start_url	= imcref.getStartUrl() ;

	// Get the session
	HttpSession session = req.getSession(true);

	// Does the session indicate this user already logged in?
	imcode.server.user.User user  = (imcode.server.user.User)session.getAttribute("logon.isDone");  // marker object

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

    public static String getPage(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException {
	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	String servlet_url	= Utility.getDomainPref( "servlet_url",host ) ;
	String image_url                = imcref.getImageUrl() ;
	File file_path                  = Utility.getDomainPrefPath( "image_path", host );

	// Get the session
	HttpSession session = req.getSession(false);
	imcode.server.user.User user  = (imcode.server.user.User)session.getAttribute("logon.isDone");  // marker object


	String meta_id = req.getParameter("meta_id");
	String img_no = req.getParameter("img_no");
	String img_preset = req.getParameter("imglist") ;//the choosen image to show
	String img_dir_preset = req.getParameter("dirlist");//the dir to chow
	String img_tag = "" ;

	if (img_dir_preset == null) {
	    //if img_dir_preset null then its first time, or a prew. of choosen image
	    img_dir_preset = req.getParameter("dirlist_preset") == null ? "":req.getParameter("dirlist_preset");
	}


	if (req.getParameter("PREVIOUS_IMG")!=null || req.getParameter("NEXT_IMG")!=null) {
	    session.removeAttribute("ImageBrowse.optionlist") ;
	    img_preset = null;
	}

	//**handles the case when we have a image to show
	if (img_preset == null) {
	    img_preset = "" ;
	} else {
	    img_tag = "<img src='"+image_url+img_preset+"' align=\"top\">" ;
	}

	//*lets get some path we need later on
	String canon_path = file_path.getCanonicalPath(); //ex: C:\Tomcat3\webapps\imcms\images
	String root_dir_parent = file_path.getParentFile().getCanonicalPath();  //ex: C:\Tomcat3\webapps\webapps\imcms
	String root_dir_name = file_path.getName() ;

	//*lets get all the folders in an ArrayList
	List folderList = GetImages.getImageFolders(file_path, true);
	//lets add the rootdir to the dir list
	folderList.add(0,file_path);


	//*lets get all the images in a folder and put them in an ArrayList
	File folderImgPath = new File(canon_path+img_dir_preset);
	List imgList = GetImages.getImageFilesInFolder(folderImgPath, true);


	//*the StringBuffers to save the lists html-code in
	StringBuffer imageOptions = new StringBuffer(imgList.size()*64) ;
	StringBuffer folderOptions = new StringBuffer(folderList.size()*64);


	//*hamdles the number of images to show and the buttons to admin it.
	String adminImgPath = user.getLangPrefix()+"/admin/";
	String previousButton = "&nbsp;";
	String nextButton = "&nbsp;";
	String startStr = req.getParameter("img_curr_max");
	int max = 1000;//the nr of img th show at the time
	int counter = 0; //the current startNr
	int img_numbers = imgList.size();//the total numbers of img
	if (startStr != null) {
	    counter = Integer.parseInt(startStr);
	}
	//lest see if a previous button whas punshed
	if (req.getParameter("PREVIOUS_IMG") != null) {
	    counter = counter - (max * 2);
	    if(counter<0) counter=0;
	}
	// Lets bee ready to create buttons
	VariableManager nextButtonVm = new VariableManager();
	nextButtonVm.addProperty( "IMAGE_URL", image_url+ adminImgPath);
	nextButtonVm.addProperty( "meta_id", meta_id);
	nextButtonVm.addProperty( "img_no", img_no);
	nextButtonVm.addProperty( "img_curr_max", Integer.toString(counter+max));
	nextButtonVm.addProperty( "SERVLET_URL", "" );

	VariableManager prevButtonVm = new VariableManager();
	prevButtonVm.addProperty( "IMAGE_URL", image_url+ adminImgPath);
	prevButtonVm.addProperty( "meta_id", meta_id);
	prevButtonVm.addProperty( "img_no", img_no);
	prevButtonVm.addProperty( "img_curr_max", Integer.toString(counter+max));
	prevButtonVm.addProperty( "SERVLET_URL", "" );
	//lets get the teplatePath to the buttons
	File templatePath = ImageBrowse.getAdminTemplateFolder (imcref, user);
	//now we have to find out what buttons to show
	boolean incButton = false;
	boolean decButton = false;
	if (counter > 0) {
	    HtmlGenerator previousButtonHtmlObj = new HtmlGenerator(templatePath,ImageBrowse.IMG_PREVIOUS_LIST_TEMPLATE );
	    previousButton = previousButtonHtmlObj.createHtmlString( prevButtonVm, req );
	}
	if (img_numbers > counter+max) {
	    HtmlGenerator nextButtonHtmlObj = new HtmlGenerator(templatePath,ImageBrowse.IMG_NEXT_LIST_TEMPLATE );
	    nextButton = nextButtonHtmlObj.createHtmlString( nextButtonVm, req );
	}

	//*lets create the image folder option list
	for (int x=0; x<folderList.size(); x++) {
	    File fileObj = (File) folderList.get(x);

	    //ok lets set up the folder name to show and the one to put as value
	    String optionName = fileObj.getCanonicalPath();
	    //lets remove the start of the path so we end up at the rootdir.
	    if (optionName.startsWith(canon_path)) {
		optionName = optionName.substring(root_dir_parent.length()) ;
		if (optionName.startsWith(File.separator)) {
		    optionName = optionName.substring(File.separator.length()) ;
		}
	    } else if(optionName.startsWith(File.separator)) {
		optionName = optionName.substring(File.separator.length()) ;
	    }
	    //the path to put in the option value
	    String optionPath = optionName;
	    if (optionPath.startsWith(root_dir_name)) {
		optionPath = optionPath.substring(root_dir_name.length());
	    }
	    //ok now we have to replace all parent folders with a '-' char
	    StringTokenizer token = new StringTokenizer(optionName,"\\",false);
	    StringBuffer buff = new StringBuffer("");
	    while ( token.countTokens() > 1 ) {
		String temp = token.nextToken();
		buff.append("&nbsp;&nbsp;-");
	    }
	    if (token.countTokens() > 0) {
		optionName = buff.toString()+token.nextToken();
	    }
	    File urlFile = new File(optionName) ;
	    String fileName = urlFile.getName() ;
	    File parentDir = urlFile.getParentFile() ;
	    if (parentDir != null) {
		optionName = parentDir.getPath()+"/" ;
	    } else {
		optionName = "" ;
	    }
	    //filepathfix ex: images\nisse\kalle.gif to images/nisse/kalle.gif
	    optionName = optionName.replace(File.separatorChar,'/')+fileName ;
	    StringTokenizer tokenizer = new StringTokenizer(optionName, "/", true);
	    StringBuffer filePathSb = new StringBuffer();
	    StringBuffer displayFolderName = new StringBuffer();
	    //the URLEncoder.encode() method replaces '/' whith "%2F" and the can't be red by the browser
	    //that's the reason for the while-loop.
	    while ( tokenizer.countTokens() > 0 ) {
		String temp = tokenizer.nextToken();
		if (temp.length() > 1) {
		    filePathSb.append(java.net.URLEncoder.encode(temp));
		} else {
		    filePathSb.append(temp);
		}
	    }
	    optionName = optionName.replace('-','\\');//Gud strul
	    String parsedFilePath = filePathSb.toString() ;
	    folderOptions.append("<option value=\"" + optionPath + "\"" + (optionPath.equals(img_dir_preset)?" selected":"") + ">" + optionName + "</option>\r\n");
	}//end setUp option dir list


	//*lets create the image file option list
	for(int i=counter; i< imgList.size() && i<counter+max;i++ ) {
	    File fileObj = (File) imgList.get(i);
	    try	{
		String fileLen = "" + fileObj.length();
	    } catch (SecurityException e) {
		String fileLen = "Read ERROR";
	    }

	    String filePath = fileObj.getCanonicalPath() ;
	    if (filePath.startsWith(canon_path)) {
		filePath = filePath.substring(canon_path.length()) ;
	    }
	    if (filePath.startsWith(File.separator)) {
		filePath = filePath.substring(File.separator.length()) ;
	    }

	    //lets copy the path before we gets rid of parent-dirs in the string to show
	    //not whery sexy but it whill do fore now
	    String imagePath = filePath;
	    StringTokenizer token = new StringTokenizer(imagePath,"\\",false);
	    StringBuffer buff = new StringBuffer("");
	    while ( token.countTokens() > 1 ) {
		String temp = token.nextToken();
		//do nothing just get rid of every token exept the image name
	    }
	    if (token.countTokens() > 0) {
		imagePath = buff.toString()+token.nextToken();
	    }

	    File urlFile = new File(filePath) ;
	    String fileName = urlFile.getName() ;
	    File parentDir = urlFile.getParentFile() ;

	    if (parentDir != null) {
		filePath = parentDir.getPath()+"/" ;
	    } else {
		filePath = "" ;
	    }


	    filePath = filePath.replace(File.separatorChar,'/')+fileName ;
	    StringTokenizer tokenizer = new StringTokenizer(filePath, "/", true);
	    StringBuffer filePathSb = new StringBuffer();
	    //the URLEncoder.encode() method replaces '/' whith "%2F" and the can't be red by the browser
	    //that's the reason for the while-loop.
	    while ( tokenizer.countTokens() > 0 ) {
		String temp = tokenizer.nextToken();
		if (temp.length() > 1) {
		    filePathSb.append(java.net.URLEncoder.encode(temp));
		} else {
		    filePathSb.append(temp);
		}
	    }

	    String parsedFilePath = filePathSb.toString() ;

	    imageOptions.append("<option value=\"" + parsedFilePath + "\"" + (parsedFilePath.equals(img_preset)?" selected":"") + ">" + imagePath + "\t[" + fileObj.length() + "]</option>\r\n");
	}
	counter += max; //image counter

	//** ok now we have to set up the response page
	// TEMPLATE ImageBrowse.html
	Vector vec = new Vector () ;
	vec.add("#folders#");
	vec.add(folderOptions.toString());

	vec.add("#meta_id#");
	vec.add(meta_id);

	vec.add("#img_preview#");
	vec.add(img_tag);

	vec.add("#img_no#");
	vec.add(img_no);

	vec.add("#dirlist_preset#");
	vec.add(img_dir_preset);

	vec.add("#imglist#");
	vec.add(img_preset);

	vec.add("#SERVLET_URL#");
	vec.add("");

	vec.add("#options#");
	vec.add(imageOptions.toString());

	vec.add("#nextButton#");
	vec.add(nextButton);

	vec.add("#previousButton#");
	vec.add(previousButton);

	vec.add("#img_startNr#");
	vec.add(Integer.toString(counter-max+1));

	if(counter > img_numbers)counter = img_numbers;
	vec.add("#img_stopNr#");
	vec.add(Integer.toString(counter));

	vec.add("#img_maxNr#");
	vec.add(Integer.toString(img_numbers));

	session.setAttribute("ImageBrowse.optionlist",imgList);

	String lang_prefix = user.getLangPrefix() ;
	return imcref.parseDoc(vec,"ImageBrowse.html", lang_prefix) ;
    }

    /**
       GetAdminTemplateFolder. Takes the userobject as argument to detect the language
       from the user and and returns the base path to the internal folder, hangs on the
       language prefix and an "/admin/" string afterwards...

       Example : D:\apache\htdocs\templates\se\admin\
    */
    public static File getAdminTemplateFolder (IMCServiceInterface imcref, imcode.server.user.User user) throws ServletException, IOException {

	// Since our templates are located into the admin folder, we'll have to hang on admin
	File templateLib = imcref.getInternalTemplateFolder(-1) ;
	// Lets get the users language id. Use the langid to get the lang prefix from db.
	String langPrefix = user.getLangPrefix() ;
	templateLib = new File(templateLib, langPrefix + "/admin") ;
	return templateLib ;
    }

}
