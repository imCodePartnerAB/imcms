import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;
import imcode.server.* ;

public class TemplateAdmin extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		User user ;
		if ( (user = Check.userLoggedOn( req, res, start_url ))==null ) {
			return;
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

		PrintWriter out = res.getWriter() ;
			
		res.setContentType("text/html") ;
		
		String temp[] ;
		temp = IMCServiceRMI.sqlProcedure(imcserver, "getLanguages") ;
		String temps = "" ;
		for (int i = 0; i < temp.length; i+=2) {
			temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
		}

		Vector vec = new Vector() ;

		vec.add("#languages#") ;
		vec.add(temps);
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		String htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_admin.html",lang_prefix) ;
		out.println( htmlStr ) ;
		
	}

	
	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		// Check if user logged on
		User user ;
		
		if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
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
//		res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
//		res.setHeader("Pragma","no-cache;") ;
		PrintWriter out = res.getWriter() ;
		
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		String lang = req.getParameter("language") ;
		String htmlStr = null ;
		if ( req.getParameter("cancel") != null ) {
			Utility.redirect(req,res,"AdminManager") ;
			return ;
		} else if ( req.getParameter("add_template") != null ) {
			Vector vec = new Vector() ;
			String temp[] ;
			temp = IMCServiceRMI.sqlProcedure(imcserver, "getTemplategroups") ;
			String temps = "" ;
			for (int i = 0; i < temp.length; i+=2) {
				temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
			}
			vec.add("#templategroups#") ;
			vec.add(temps);
			vec.add("#language#") ;
			vec.add(lang) ;
			htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_upload.html",lang_prefix) ;
		} else if ( req.getParameter("add_demotemplate") != null ) {
			String list[] ;
			list = IMCServiceRMI.getDemoTemplateList(imcserver) ;
			String temp[] ;
			temp = IMCServiceRMI.sqlQuery(imcserver, "select template_id, simple_name from templates where lang_prefix = '"+lang+"' order by simple_name") ;
			Vector vec = new Vector() ;
			vec.add("#language#") ;
			vec.add(lang) ;
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
		} else if ( req.getParameter("delete_template") != null ) {
			String temp[][] = IMCServiceRMI.sqlQueryMulti(imcserver,"select simple_name,count(meta_id),t.template_id  from templates t left join text_docs td on td.template_id = t.template_id where lang_prefix = '"+lang+"' group by t.template_id,simple_name order by simple_name") ;
			//String temp[] ;
			htmlStr = "" ;
			Vector vec ;
			for ( int i = 0 ; i<temp.length ; i++ ) {
				vec = new Vector() ;
				vec.add("#template_name#") ;
				vec.add(temp[i][0]) ;
				vec.add("#docs#") ;
				vec.add(temp[i][1]) ;
				vec.add("#template_id#") ;
				vec.add(temp[i][2]) ;
				htmlStr += IMCServiceRMI.parseDoc(imcserver,vec,"template_list_row.html",lang_prefix) ;
			}
			//temp = IMCServiceRMI.sqlQuery(imcserver, "select template_id, simple_name from templates where lang_prefix = '"+lang+"' order by simple_name") ;
			vec = new Vector() ;
			vec.add("#language#") ;
			vec.add(lang) ;
			if ( temp.length > 0 ) {
//				String temps = "" ;
//				for (int i = 0; i < temp.length; i+=2) {
//					temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
//				}
				vec.add("#templates#") ;
				vec.add(htmlStr);
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_delete.html",lang_prefix) ;
			} else {
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_no_langtemplates.html",lang_prefix) ;
			}
		} else if ( req.getParameter("rename_template") != null ) {
			String temp[] ;
			temp = IMCServiceRMI.sqlQuery(imcserver, "select template_id, simple_name from templates where lang_prefix = '"+lang+"' order by simple_name") ;
			Vector vec = new Vector() ;
			vec.add("#language#") ;
			vec.add(lang) ;
			if ( temp.length > 0 ) {
				String temps = "" ;
				for (int i = 0; i < temp.length; i+=2) {
					temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
				}
				vec.add("#templates#") ;
				vec.add(temps);
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_rename.html",lang_prefix) ;
			} else {
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_no_langtemplates.html",lang_prefix) ;
			}
		} else if ( req.getParameter("get_template") != null ) {
			String temp[] ;
			temp = IMCServiceRMI.sqlQuery(imcserver, "select template_id, simple_name from templates where lang_prefix = '"+lang+"' order by simple_name") ;
			Vector vec = new Vector() ;
			vec.add("#language#") ;
			vec.add(lang) ;
			if ( temp.length > 0 ) {
				String temps = "" ;
				for (int i = 0; i < temp.length; i+=2) {
					temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
				}
				vec.add("#templates#") ;
				vec.add(temps);
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_get.html",lang_prefix) ;
			} else {
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_no_langtemplates.html",lang_prefix) ;
			}
		} else if ( req.getParameter("add_group") != null ) {
			htmlStr = IMCServiceRMI.parseDoc(imcserver, null, "templategroup_add.html",lang_prefix) ;
		} else if ( req.getParameter("delete_group") != null ) {
			String temp[] ;
			temp = IMCServiceRMI.sqlProcedure(imcserver, "getTemplategroups") ;
			String temps = "" ;
			for (int i = 0; i < temp.length; i+=2) {
				temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
			}
			Vector vec = new Vector() ;
			vec.add("#templategroups#") ;
			vec.add(temps);
			htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "templategroup_delete.html",lang_prefix) ;
		} else if ( req.getParameter("rename_group") != null ) {
			String temp[] ;
			temp = IMCServiceRMI.sqlProcedure(imcserver, "getTemplategroups") ;
			String temps = "" ;
			for (int i = 0; i < temp.length; i+=2) {
				temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
			}
			Vector vec = new Vector() ;
			vec.add("#templategroups#") ;
			vec.add(temps);
			htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "templategroup_rename.html",lang_prefix) ;
		} else if ( req.getParameter("assign_group") != null ) {
			String temp[] ;
			temp = IMCServiceRMI.sqlQuery(imcserver,"select template_id from templates where lang_prefix = '"+lang+"'") ;
			Vector vec = new Vector() ;
			vec.add("#language#") ;
			vec.add(lang) ;
			if ( temp.length > 0 ) {
				temp = IMCServiceRMI.sqlProcedure(imcserver, "getTemplategroups") ;
				String temps = "" ;
				for (int i = 0; i < temp.length; i+=2) {
					temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
				}
				vec.add("#templategroups#") ;
				vec.add(temps);
				vec.add("#assigned#") ;
				vec.add("");
				vec.add("#unassigned#") ;
				vec.add("");
				vec.add("#group#") ;
				vec.add("");
				vec.add("#group_id#") ;
				vec.add("");
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_assign.html",lang_prefix) ;
			} else {
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_no_langtemplates.html",lang_prefix) ;
			}
		} else if ( req.getParameter("show_templates") != null ) {
			String temp[][] = IMCServiceRMI.sqlQueryMulti(imcserver,"select simple_name,count(meta_id),t.template_id  from templates t left join text_docs td on td.template_id = t.template_id where lang_prefix = '"+lang+"' group by t.template_id,simple_name order by simple_name") ;
			htmlStr = "" ; //IMCServiceRMI.parseDoc(imcserver,null,"template_list_head.html",lang_prefix) ;
			for ( int i = 0 ; i<temp.length ; i++ ) {
				Vector vec = new Vector() ;
				vec.add("#template_name#") ;
				vec.add(temp[i][0]) ;
				vec.add("#docs#") ;
				vec.add(temp[i][1]) ;
				vec.add("#template_id#") ;
				vec.add(temp[i][2]) ;
				htmlStr += IMCServiceRMI.parseDoc(imcserver,vec,"template_list_row.html",lang_prefix) ;
			}
//			htmlStr += IMCServiceRMI.parseDoc(imcserver,null,"template_list_tail.html",lang_prefix) ;
			Vector vec = new Vector() ;
			vec.add("#template_list#") ;
			vec.add(htmlStr) ;
			vec.add("#language#") ;
			vec.add(lang) ;
			htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"template_list.html",lang_prefix) ;
		}
		out.print(htmlStr) ;
	}

	public void log (String str) {
		super.log(str);
		System.out.println("TemplateAdmin: " + str);
	}
	
}
