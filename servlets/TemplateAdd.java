import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;
//import imcode.external.diverse.* ;
import imcode.server.* ;

public class TemplateAdd extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
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

		res.setContentType("text/html") ;
		
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
			res.sendRedirect("TemplateAdmin") ;
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
				out.print(htmlStr) ;
				return ;
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
				out.print(htmlStr) ;
				return ;
			} else if ( mp.getParameter("view_demo") != null ) {
				byte[] temp = IMCServiceRMI.getDemoTemplate(imcserver,Integer.parseInt(template)) ;
				String htmlStr ;
				if ( temp == null ) {
					htmlStr = IMCServiceRMI.parseDoc( imcserver, null, "no_demotemplate.html", lang_prefix ) ;
				} else {
					htmlStr = new String(temp,"8859_1") ;
				}
				out.print(htmlStr) ;
				return ;
			}
		} else {
			simple_name = mp.getParameter("name") ;
			if ( simple_name == null || simple_name.equals("") ) {
				Vector vec = new Vector() ;
				vec.add("#language#") ;
				vec.add(lang);
				String htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "template_upload_name_blank.html", lang_prefix) ;                        	
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

		if ( demo ) {
			try {
				IMCServiceRMI.saveDemoTemplate( imcserver, Integer.parseInt(template), file.getBytes("8859_1")) ;				
				Vector vec = new Vector() ;
				vec.add("#language#") ;
				vec.add(lang);
				htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "templatedemo_upload_done.html", lang_prefix) ;                        	

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

		out.print(htmlStr) ;
		return ;
	}

	public void log (String str) {
		super.log(str);
		System.out.println("TemplateAdd: " + str);
	}
	
}