import java.io.*;
import java.util.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;

/**
 *  A class that allows Web users to upload local files to a web server's file system.
 */
public class ImageUpload extends HttpServlet {   
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	/*
	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	res.setContentType("text/html") ;
	PrintWriter out = res.getWriter() ;
	   String htmlStr = Utility.loadAdminTemplate( imcserver, "new_img.html", "se") ;                        	
		out.println(htmlStr) ;
	return ;
	}
	*/
	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		File file_path 				= new File(Utility.getDomainPref( "image_path", host ));
		String image_url			= Utility.getDomainPref( "image_url",host ) ;
		String image_path			= Utility.getDomainPref( "image_path",host ) ;

		imcode.server.User user ; 

		// Check if user logged on
		if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		} 

		res.setContentType("text/html") ;

		int length = req.getContentLength();

		ServletInputStream in = req.getInputStream() ;
		PrintWriter out = res.getWriter() ;

		HttpSession session = req.getSession(true) ;

		session.removeValue("ImageBrowse.optionlist") ;

		byte buffer[] = new byte[ length ] ;
		int bytes_read = 0;
		while ( bytes_read < length ) {
			bytes_read += in.read(buffer,bytes_read,length-bytes_read) ;
		}


		String contentType = req.getContentType() ;
		MultipartFormdataParser mp = new MultipartFormdataParser(new String(buffer,"8859_1"),contentType) ;
		String file = mp.getParameter("file") ;
		String filename = mp.getFilename("file") ;
		
		//submitted with Browse Images button, no ImageUpload (M Wallin)
		if (mp.getParameter("browse_images") != null) 
		{ // Browse Image Library
			Utility.redirect(req,res,"ImageBrowse");
		}
		
		if ( mp.getParameter("ok") == null ) {
			doGet(req,res) ;
			return ;
		}
		int meta_id = Integer.parseInt(mp.getParameter("meta_id")) ;
		int img_no = Integer.parseInt(mp.getParameter("img_no")) ;
		
		// extraParameter, presets imagepath... set by ImageBrowse
		
				
		File fn = new File(filename) ;
		fn = new File (file_path,fn.getName()) ;

		if (file.length() > 0) {
			if ( fn.exists() ) {
				Vector vec = new Vector() ;
				vec.add("#back#") ;
				vec.add("ChangeImage?meta_id="+meta_id+"&img_no="+img_no) ;
				vec.add("#meta_id#") ;
				vec.add(String.valueOf(meta_id)) ;
				vec.add("#img_no#") ;
				vec.add(String.valueOf(img_no)) ;
				String htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "file_exists.html", "se") ;
				out.println(htmlStr) ;
				return ;
			}
			FileOutputStream fos = new FileOutputStream(fn) ;
			fos.write(file.getBytes("8859_1")) ;
			fos.close() ;
		}
		//String htmlStr = IMCServiceRMI.interpretAdminTemplate(imcserver,meta_id,user,"change_img.html",img_no,0,0,0) ;
		//out.println(htmlStr) ;
		String image_ref = fn.getName() ;
		ImageFileMetaData imagefile = new ImageFileMetaData(new File(image_path,image_ref)) ;
		int width = imagefile.getWidth() ;
		int height = imagefile.getHeight() ;
		Vector vec = new Vector() ;
		vec.add("#keep_aspect#") ;
		vec.add("checked") ;
		vec.add("#imgUrl#") ;
		vec.add(image_url) ;
		vec.add("#imgName#") ;
		vec.add("") ;
		vec.add("#imgRef#") ;
		vec.add(image_ref) ;
		vec.add("#imgWidth#") ;
		vec.add(""+width) ;
		vec.add("#imgHeight#") ;
		vec.add(""+height) ;
		vec.add("#origW#") ;
		vec.add(""+width) ;
		vec.add("#origH#") ;
		vec.add(""+height) ;
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
		vec.add("#getMetaId#") ;
		vec.add(String.valueOf(meta_id)) ;
		vec.add("#img_no#") ;
		vec.add(String.valueOf(img_no)) ;
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		String htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"change_img.html", lang_prefix) ;
		out.print(htmlStr) ;
		
		return ;
	}
}
