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

	/**
	init()
	*/
	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}

	/**
	  doGet()
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

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
		String img_preset = (req.getParameter("imglist") == null)?"":req.getParameter("imglist");

		// Preview image from ImageBrowse
		if(req.getParameter("preview") != null)  {
				HttpSession session = req.getSession(true);
				Object done = session.getValue("logon.isDone");
				user = (imcode.server.User)done ;
				String optionList = (String)(session.getValue("optionlist"));

//				if (optionList == null)
//					res.sendRedirect("/servlet/ImageBrowse");
				
					Vector vec = new Vector () ;

					vec.add("#meta_id#");
					vec.add("" +meta_id);
					
					vec.add("#img_preview#");
					vec.add("<img src='" + img_preset + "'>");
					
					vec.add("#img_no#");
					vec.add("" + img_no);
					
					vec.add("#options#");
					vec.add(optionList);
					
					String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
					htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"ImageBrowse.html", lang_prefix) ;
					//log("HTMLSTR = " + htmlStr);
					out.print(htmlStr) ;
				

				
		} else {


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
	
		
		String sqlStr = "select image_name,imgurl,width,height,border,v_space,h_space,target,target_name,align,alt_text,low_scr,linkurl from images where meta_id = "+meta_id+" and name = "+img_no ;
		String[] sql = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
		//log ("d") ;
		Vector vec = new Vector () ;
		log("SQL LENGTH= " + sql.length);
		String imageName = (img_preset.equals("")&&sql.length>0?sql[1]:img_preset); // selected OPTION or ""
		if(imageName.lastIndexOf("/") != -1)
			imageName = imageName.substring(imageName.lastIndexOf("/") +1);
		String imagePath = Utility.getDomainPref( "image_path",host ) + imageName;
		//****************************************************************
			//String imagePath = image_url + imageName;
		//ImageIcon icon = new ImageIcon(imagePath);
		int width = 0 ; //icon.getIconWidth();
		int height = 0 ; //icon.getIconHeight();
		//****************************************************************
		
		
		if ( sql.length > 0 ) {
			log("sql.lenght > 0");
			vec.add("#imgName#") ;
			vec.add(sql[0]) ;
			vec.add("#imgRef#") ;
			vec.add(Utility.getDomainPref( "image_url",host ) + imageName);
			vec.add("#imgWidth#") ;
			vec.add(img_preset.equals("")?sql[2]:"" + width);
			vec.add("#origW#"); // original imageWidth
			vec.add("" + width);
			vec.add("#imgHeight#") ;
			vec.add(img_preset.equals("")?sql[3]:"" + height);
			vec.add("#origH#");
			vec.add("" + height); // original imageHeight
			
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
			vec.add(Utility.getDomainPref( "image_url",host ) + imageName);
			vec.add("#imgWidth#") ;
			vec.add("" + width) ;
			vec.add("#imgHeight#") ;
			vec.add("" + height) ;
			
			vec.add("#origW#"); // 0
			vec.add("0");
			vec.add("#origH#"); //0
			vec.add("0");
			
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

		
		
		vec.add("#getMetaId#") ;
		vec.add(String.valueOf(meta_id)) ;
		vec.add("#img_no#") ;
		vec.add(String.valueOf(img_no)) ;
		//log ("e") ;
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		//log ("f") ;		
		htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"change_img.html", lang_prefix) ;
		//log ("g") ;
		//htmlStr = IMCServiceRMI.interpretAdminTemplate(imcserver,meta_id,user,"change_img.html",img_no,0,0,0) ;                        	
		out.print(htmlStr) ;
		}


	}
}
