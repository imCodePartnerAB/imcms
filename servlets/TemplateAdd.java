import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;
import imcode.server.* ;

public class TemplateAdd extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
    }

    public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	String host 				= req.getHeader("Host") ;
	String imcserver 			= Utility.getDomainPref("adminserver",host) ;
	String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		
	// Check if user logged on
	User user ;
	if ( (user = Check.userLoggedOn(req,res,start_url))==null ) {
	    return ;
	} 
	// Is user superadmin?

	String sqlStr  = "select role_id from users,user_roles_crossref\n" ;
	sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
	sqlStr += "and user_roles_crossref.role_id = 0\n" ;
	sqlStr += "and users.user_id = " + user.getInt("user_id") ;
		
	if ( IMCServiceRMI.sqlQuery(imcserver,sqlStr).length == 0 ) {
	    Utility.redirect(req,res,start_url) ;
	    return ;
	}
		
	String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
	ServletOutputStream out = res.getOutputStream() ;

	//**********************************************************************************************
	// Redirected here with bogus parameter, no-cache workaround
	// 
	if(req.getParameter("action") != null) {
	    byte[] htmlStr ;
	    if(req.getParameter("action").equals("noCacheImageView")) 
		{
		    String template = req.getParameter("template");
		
		    Object[] suffixAndStream = IMCServiceRMI.getDemoTemplate(imcserver,Integer.parseInt(template)) ;
		    byte[] temp = (byte[])suffixAndStream[1];
				
		    if ( temp == null || temp.length == 0 ) {
			htmlStr = IMCServiceRMI.parseDoc( imcserver, null, "no_demotemplate.html", lang_prefix ).getBytes("8859_1") ;
		    } else {
			htmlStr = temp ;
		    }
			
		    String suffix = (String)suffixAndStream[0];
		    String mimeType = Utility.getMimeTypeFromExtension(suffix);

		    res.setContentType(mimeType) ;
		    out.write(htmlStr) ;
		    return ;
		} else if (req.getParameter("action").equals("return")) {
		    res.setContentType("text/html");
			
		    Vector vec = new Vector() ;
		    vec.add("#buttonName#");
		    vec.add("return");
		    vec.add("#formAction#");
		    vec.add("TemplateAdmin");
		    vec.add("#formTarget#");
		    vec.add("_top");
		    //		    vec.add("#hiddenName#");
		    //		    vec.add("add_demotemplate");
		    //		    vec.add("#hiddenValue#");
		    //		    vec.add("Rastapaupoulous");
		    htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "back_button.html",lang_prefix).getBytes("8859_1") ;
		    out.write(htmlStr);
		    return;
		}
	}
	//***********************************************************************************************
		
    }





    public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	String host 				= req.getHeader("Host") ;
	String imcserver 			= Utility.getDomainPref("adminserver",host) ;
	String start_url        	= Utility.getDomainPref( "start_url",host ) ;
	int uploadsize				= Integer.parseInt(Utility.getDomainPref("max_uploadsize",host)) ;
		
	// Check if user logged on
	User user ;
	if ( (user = Check.userLoggedOn(req,res,start_url))==null ) {
	    return ;
	} 
	// Is user superadmin?

	String sqlStr  = "select role_id from users,user_roles_crossref\n" ;
	sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
	sqlStr += "and user_roles_crossref.role_id = 0\n" ;
	sqlStr += "and users.user_id = " + user.getInt("user_id") ;
		
	if ( IMCServiceRMI.sqlQuery(imcserver,sqlStr).length == 0 ) {
	    Utility.redirect(req,res,start_url) ;
	    return ;
	}

	//res.setContentType("text/html") ;
		
				
	int length = req.getContentLength();

	String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
	PrintWriter out = res.getWriter() ;

	if (length<1||length>uploadsize) {
	    Vector vec = new Vector() ;
	    vec.add("#uploadsize#") ;
	    vec.add(String.valueOf(uploadsize));
	    out.print(IMCServiceRMI.parseDoc(imcserver, vec, "template_upload_size.html",lang_prefix)) ;
	    return ;
	}

	ServletInputStream in = req.getInputStream() ;
	byte buffer[] = new byte[ length ] ;
	int bytes_read = 0;
	while ( bytes_read < length ) {
	    bytes_read += in.read(buffer,bytes_read,length-bytes_read) ;
	}
	String contentType = req.getContentType() ;

	// Min klass tar emot datan och plockar ut det som är intressant...
	MultipartFormdataParser mp = new MultipartFormdataParser(buffer,contentType) ;

	if ( mp.getParameter("cancel") != null ) {
	    Utility.redirect(req,res,"TemplateAdmin") ;
	}

	// Plocka ut språket, så vi vet vilket vi editerar...
	String lang = mp.getParameter("language") ;		

	boolean demo = mp.getParameter("demo") != null ;
	

	String template = null ;
	String simple_name = null ;
	if ( demo ) {
	    template = mp.getParameter("template") ;
	    if ( template == null || template.equals("") ) {
		Vector vec = new Vector() ;
		vec.add("#language#") ;
		vec.add(lang);
		String htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "templatedemo_upload_template_blank.html", lang_prefix) ;
		res.setContentType("text/html") ;
		out.print(htmlStr) ;
		return ;
		// ************************* DELETE DEMO
	    } else if ( mp.getParameter("delete_demo")!=null ) {
		IMCServiceRMI.deleteDemoTemplate(imcserver, Integer.parseInt(template)) ;
		String list[] ;
		list = IMCServiceRMI.getDemoTemplateList(imcserver) ;
		String temp[] ;
		temp = IMCServiceRMI.sqlQuery(imcserver, "select template_id, simple_name from templates where lang_prefix = '"+lang+"' order by simple_name") ;
		Vector vec = new Vector() ;
		vec.add("#language#") ;
		vec.add(lang) ;
		String htmlStr ;
		if ( temp.length > 0 ) {
		    String temps = "" ;
		    for (int i = 0; i < temp.length; i+=2) {
			int tmp = Integer.parseInt(temp[i]) ;
			for ( int j = 0 ; j < list.length ; j++ ) {
			    if ( Integer.parseInt(list[j]) == tmp ) {
				temp[i+1] = "*" + temp[i+1] ;
				break ;
			    }
			}
			temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
		    }
		    vec.add("#templates#") ;
		    vec.add(temps);
		    htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "templatedemo_upload.html",lang_prefix) ;
		} else {
		    htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_no_langtemplates.html",lang_prefix) ;
		}
		res.setContentType("text/html") ;
		out.print(htmlStr) ;
		return ;
		// ************************** VIEW DEMO
		// Updated IMCService + interface, IMCServiceRMI : Now returns Object[] filesuffix, byteStream
	    } else if ( mp.getParameter("view_demo") != null ) {
		Object[] suffixAndStream = IMCServiceRMI.getDemoTemplate(imcserver,Integer.parseInt(template)) ;
		String htmlStr ;
		res.setContentType("text/html");				
		if ( suffixAndStream == null) {
		    htmlStr = IMCServiceRMI.parseDoc( imcserver, null, "no_demotemplate.html", lang_prefix ) ;
		    out.print(htmlStr) ;
		    return ;
					
		} else {
		    byte[] temp = (byte[])suffixAndStream[1];
		    if (temp == null) {
			htmlStr = IMCServiceRMI.parseDoc( imcserver, null, "no_demotemplate.html", lang_prefix ) ;
			out.print(htmlStr) ;
			return ;
		    } else {
			htmlStr = new String(temp,"8859_1") ;
			//res.setContentType(mimeType) ;
			String redirect = ("TemplateAdd?action=noCacheImageView&template=" + template
					   + "&bogus=" + (int)(1000*Math.random()));
						
			// create frameset with topframe containing return-button
			// and the main-frame doing a redirect
			//FIXME: What The Fawk is this? Put it in a template, or suffer the consequences!
			out.print(
				  "<html><head><title></title></head>"
				  + "<frameset rows=\"80,*\" frameborder=\"NO\" border=\"0\" framespacing=\"0\">"
				  + "<frame name=\"topFrame\" scrolling=\"NO\" noresize src=\"TemplateAdd?action=return\">"
				  + "<frame name=\"mainFrame\" src=\"" + redirect + "\">"
				  + "</frameset>"
				  + "<noframes><body>" + redirect + "</body></noframes></html>");
							
			//res.sendRedirect(redirect);	
		    }
						
				 
		}
	
	    }
	} else {
	    simple_name = mp.getParameter("name") ;
	    if ( simple_name == null || simple_name.equals("") ) {
		Vector vec = new Vector() ;
		vec.add("#language#") ;
		vec.add(lang);
		String htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "template_upload_name_blank.html", lang_prefix) ;
		res.setContentType("text/html") ;
		out.print(htmlStr) ;
		return ;
	    }
	}

	String file = mp.getParameter("file") ;
	if ( file == null || file.length() == 0 ) {
	    Vector vec = new Vector() ;
	    vec.add("#language#") ;
	    vec.add(lang);
	    String htmlStr = null ;
	    if ( demo ) {
		htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "templatedemo_upload_file_blank.html", lang_prefix) ;                        				
	    } else {
		htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "template_upload_file_blank.html", lang_prefix) ;
				
	    }
	    res.setContentType("text/html") ;
	    out.print(htmlStr) ;
	    return ;
	}

	log ("Filesize: "+file.length()) ;
	String filename = mp.getFilename("file") ;
	log (filename) ;
	File fn = new File(filename) ;
	filename = fn.getName() ;
	boolean overwrite = (mp.getParameter("overwrite") != null) ;
	String htmlStr = null ;

	// ********************************** OK

	if ( demo ) {
	    try {
				// get the suffix
		log("*** TEMPLATE_ADD ***  FILENAME = " + filename + " | SUFFIX = " + filename.substring(filename.lastIndexOf('.') +1));
		String suffix = filename.substring(filename.lastIndexOf('.') +1);
				
		if(filename.lastIndexOf(".") == -1)
		    suffix="";
		Vector vec = new Vector() ;
		if(!suffix.equals("jpg")
		   && !suffix.equals("jpeg")
		   && !suffix.equals("png")
		   && !suffix.equals("gif")
		   && !suffix.equals("htm")
		   && !suffix.equals("html"))
		    {
			vec.add("#language#") ;
			vec.add(lang);
			htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "templatedemo_upload_done.html", lang_prefix) ;
		    }
		else {
		    IMCServiceRMI.saveDemoTemplate( imcserver, Integer.parseInt(template), file.getBytes("8859_1"), suffix) ;
					
		    vec.add("#language#") ;
		    vec.add(lang);
		    htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "templatedemo_upload_done.html", lang_prefix) ;                        	
		}
				
				
				

	    } catch ( RemoteException ex ) {
		Vector vec = new Vector() ;
		vec.add("#language#") ;
		vec.add(lang);
		htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "templatedemo_upload_error.html", lang_prefix) ;
	    }
	} else {
	    int result = IMCServiceRMI.saveTemplate(  imcserver, simple_name, filename, file.getBytes("8859_1"), overwrite, lang ) ;

	    if ( result == -2 ) {
		Vector vec = new Vector() ;
		vec.add("#language#") ;
		vec.add(lang);
		htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "template_upload_error.html", lang_prefix) ;                        	
	    } else if ( result == -1 ) {
		Vector vec = new Vector() ;
		vec.add("#language#") ;
		vec.add(lang);
		htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "template_upload_file_exists.html", lang_prefix) ;                        	
	    } else {
		String t_id = IMCServiceRMI.sqlQueryStr (imcserver,"select template_id from templates where simple_name = '"+ simple_name+"'" ) ;
		String[] temp = mp.getParameterValues("templategroup") ;
		sqlStr = "" ;
		if ( temp != null ) {
		    for ( int foo = 0 ; foo < temp.length ; foo++ ) {
			sqlStr += "delete from templates_cref where group_id = "+temp[foo]+" and template_id = "+t_id+"\n" ;
			sqlStr += "insert into templates_cref (group_id, template_id) values("+temp[foo]+","+t_id+")\n" ;
		    }
		    IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
		}

		Vector vec = new Vector() ;
		vec.add("#language#") ;
		vec.add(lang);
		htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "template_upload_done.html", lang_prefix) ;                        	
	    }
	}
	res.setContentType("text/html") ;
	out.print(htmlStr) ;
	return ;
    }

    public void log (String str) {
	super.log(str);
	System.out.println("TemplateAdd: " + str);
    }
	
}
