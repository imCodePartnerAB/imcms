import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;
import imcode.server.* ;

import org.apache.log4j.Category;

public class TemplateChange extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	private static Category log = Category.getInstance(TemplateChange.class.getName());
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url       	= Utility.getDomainPref( "servlet_url",host ) ;

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
	
		ServletOutputStream out = res.getOutputStream() ;
		String htmlStr = null ;
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		String lang = req.getParameter("language") ;
		if ( req.getParameter("cancel") != null ) {
			Utility.redirect(req,res,"TemplateAdmin") ;
			return ;
		} else if ( req.getParameter("template_get")!=null ) {
			int template_id = Integer.parseInt(req.getParameter("template")) ;
			String filename = IMCServiceRMI.sqlQueryStr(imcserver, "select template_name from templates where template_id = "+template_id) ;
			if ( filename == null ) {
				filename = "" ;
			}
			byte[] file = IMCServiceRMI.getTemplate(imcserver, template_id) ;
			res.setContentType("application/octet-stream; name=\""+filename+"\"") ;
			res.setContentLength(file.length) ;
			res.setHeader( "Content-Disposition","attachment; filename=\""+filename+"\";" ) ;
			out.write(file) ;
			out.flush() ;
			return ;
		} else if (req.getParameter("template_delete_cancel")!=null) {
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
//					String temps = "" ;
//					for (int i = 0; i < temp.length; i+=2) {
//						temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
//					}
				vec.add("#templates#") ;
				vec.add(htmlStr);
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_delete.html",lang_prefix) ;
			} else {
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_no_langtemplates.html",lang_prefix) ;
			}
		} else if ( req.getParameter("template_delete")!=null ) {
			String new_temp_id = req.getParameter("new_template") ;
			int template_id = Integer.parseInt(req.getParameter("template")) ;
			if ( new_temp_id != null ) {
				IMCServiceRMI.sqlUpdateQuery(imcserver,"update text_docs set template_id = "+new_temp_id+" where template_id = "+template_id) ;	
			}
			IMCServiceRMI.deleteTemplate(imcserver, template_id) ;
//			Vector vec = new Vector() ;
//			String temp[] = IMCServiceRMI.sqlQuery(imcserver, "select template_id, simple_name from templates where lang_prefix = '"+lang+"' order by simple_name") ;
//			String temps = "" ;
//			for (int i = 0; i < temp.length; i+=2) {
//				temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
//			}
/**/
			String temp[][] = IMCServiceRMI.sqlQueryMulti(imcserver,"select simple_name,count(meta_id),t.template_id  from templates t left join text_docs td on td.template_id = t.template_id where lang_prefix = '"+lang+"' group by t.template_id,simple_name order by simple_name") ;
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
			vec = new Vector() ;
			vec.add("#language#") ;
			vec.add(lang) ;
			if ( temp.length > 0 ) {
				vec.add("#templates#") ;
				vec.add(htmlStr);
			}
/**/			
			htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_delete.html",lang_prefix) ;
		} else if ( req.getParameter("assign")!=null ) {
			String grp_id = req.getParameter("group_id") ;
			String temp_id[] = req.getParameterValues("unassigned") ;			
			if ( temp_id == null ) {
				htmlStr = parseAssignTemplates(grp_id,lang,lang_prefix,host) ;
				out.print(htmlStr) ;
				return ;
			}
			for ( int i = 0 ; i < temp_id.length ; i++ ) {
				sqlStr = "insert into templates_cref (group_id,template_id) values("+grp_id+","+temp_id[i]+")" ;
				IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
			}
			htmlStr = parseAssignTemplates(grp_id,lang,lang_prefix,host) ;
		} else if ( req.getParameter("deassign")!=null ) {
			String grp_id = req.getParameter("group_id") ;
			String temp_id[] = req.getParameterValues("assigned") ;
			if ( temp_id == null ) {
				htmlStr = parseAssignTemplates(grp_id,lang,lang_prefix,host) ;
				out.print(htmlStr) ;
				return ;
			}
			for ( int i = 0 ; i < temp_id.length ; i++ ) {
				sqlStr = "delete from templates_cref where group_id = "+grp_id+" and template_id = "+temp_id[i] ;
				IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
			}
			htmlStr = parseAssignTemplates(grp_id,lang,lang_prefix,host) ;
		} else if ( req.getParameter("show_assigned")!=null ) {
			String grp_id = req.getParameter("templategroup") ;
			htmlStr = parseAssignTemplates(grp_id,lang,lang_prefix,host) ;
		} else if ( req.getParameter("template_rename")!=null ) { 
			int template_id = Integer.parseInt(req.getParameter("template")) ;
			String name = req.getParameter("name") ;
			if ( name == null || "".equals(name) ) {
				Vector vec = new Vector () ;
				vec.add("#language#") ;
				vec.add(lang) ;
				htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "template_rename_name_blank.html", lang_prefix) ;				
			} else {
				sqlStr = "update templates set simple_name = '"+name+"' where template_id = "+template_id ;
				IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
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
			}
		} else if ( req.getParameter("template_delete_check")!=null ) {
			int template_id = Integer.parseInt(req.getParameter("template")) ;
			sqlStr = "select top 50 meta_id from text_docs where template_id = "+template_id ;
			String temp[] = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
			if ( temp.length > 0 ) {
				Vector vec = new Vector() ;
				vec.add("#language#") ;
				vec.add(lang) ;
				String tempstr = "";
				for ( int i = 0 ; i < temp.length ; i++ ) {
					tempstr += "<option>"+ temp[i]+ "</option>" ;
				}
				vec.add("#template#") ;
				vec.add(String.valueOf(template_id));
				vec.add("#docs#") ;
				vec.add(tempstr) ;
				temp = IMCServiceRMI.sqlQuery(imcserver, "select t.template_id,t.simple_name from templates t where lang_prefix = '"+lang+"' and template_id != "+template_id+" order by simple_name") ;
				tempstr = "" ;
				for (int i = 0; i < temp.length; i+=2) {
					tempstr += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
				}
				vec.add("#templates#") ;
				vec.add( tempstr );
				
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_delete_warning.html",lang_prefix) ;
			} else {
				IMCServiceRMI.deleteTemplate(imcserver, template_id) ;
//				String tempa[] = IMCServiceRMI.sqlQuery(imcserver, "select template_id, simple_name from templates where lang_prefix = '"+lang+"' order by simple_name") ;
//				String temps = "" ;
//				for (int i = 0; i < tempa.length; i+=2) {
//					temps += "<option value=\""+tempa[i]+"\">"+tempa[i+1]+"</option>" ;
//				}
//				vec.add("#templates#") ;
//				vec.add(temps) ;			
/**/
				String foo[][] = IMCServiceRMI.sqlQueryMulti(imcserver,"select simple_name,count(meta_id),t.template_id  from templates t left join text_docs td on td.template_id = t.template_id where lang_prefix = '"+lang+"' group by t.template_id,simple_name order by simple_name") ;
				htmlStr = "" ;
				Vector vec ;
				for ( int i = 0 ; i<foo.length ; i++ ) {	
					vec = new Vector() ;
					vec.add("#template_name#") ;
					vec.add(foo[i][0]) ;
					vec.add("#docs#") ;
					vec.add(foo[i][1]) ;
					vec.add("#template_id#") ;
					vec.add(foo[i][2]) ;
					htmlStr += IMCServiceRMI.parseDoc(imcserver,vec,"template_list_row.html",lang_prefix) ;
				}
				vec = new Vector() ;
				vec.add("#language#") ;
				vec.add(lang) ;
				if ( foo.length > 0 ) {
					vec.add("#templates#") ;
					vec.add(htmlStr);
				}
/**/			
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "template_delete.html",lang_prefix) ;
			}
		} else if ( req.getParameter("group_delete_check")!=null ) {
			int grp_id = Integer.parseInt(req.getParameter("templategroup")) ;
			sqlStr = "select simple_name from templates t,templates_cref c where c.template_id = t.template_id and group_id = "+grp_id+" order by simple_name" ;
			String temp[] = IMCServiceRMI.sqlQuery(imcserver,sqlStr) ;
			if ( temp.length > 0 ) {
				String temps = temp[0] ;
				for (int i = 1; i < temp.length; i++) {
					temps += ", "+temp[i] ;
				}
				Vector vec = new Vector() ;
				vec.add("#templates#") ;
				vec.add(temps) ;
				vec.add("#templategroup#") ;
				vec.add(String.valueOf(grp_id)) ;
				htmlStr = IMCServiceRMI.parseDoc( imcserver, vec, "templategroup_delete_warning.html", lang_prefix) ;
			} else {
				IMCServiceRMI.deleteTemplateGroup(imcserver,grp_id) ;
				temp = IMCServiceRMI.sqlProcedure(imcserver, "getTemplategroups") ;
				String temps = "" ;
				for (int i = 0; i < temp.length; i+=2) {
					temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
				}
				Vector vec = new Vector() ;
				vec.add("#templategroups#") ;
				vec.add(temps);
				htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "templategroup_delete.html",lang_prefix) ;
			}
		} else if ( req.getParameter("group_delete")!=null ) {
			int grp_id = Integer.parseInt(req.getParameter("templategroup")) ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,"delete from templates_cref where group_id = "+grp_id) ;
			IMCServiceRMI.deleteTemplateGroup(imcserver,grp_id) ;
			String temp[] = IMCServiceRMI.sqlProcedure(imcserver, "getTemplategroups") ;
			String temps = "" ;
			for (int i = 0; i < temp.length; i+=2) {
				temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
			}
			Vector vec = new Vector() ;
			vec.add("#templategroups#") ;
			vec.add(temps);
			htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "templategroup_delete.html",lang_prefix) ;
		} else if ( req.getParameter("group_delete_cancel")!=null ) {
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
		} else if ( req.getParameter("group_add")!=null ) {
			String name = req.getParameter("name") ;
			if ( name == null || name.equals("") ) {
				htmlStr = IMCServiceRMI.parseDoc( imcserver, null, "templategroup_add_name_blank.html", lang_prefix) ;
			} else {
				sqlStr = "select group_id from templategroups where group_name = '"+name+"'" ;
				if ( IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) != null ) {
					htmlStr = IMCServiceRMI.parseDoc(imcserver, null, "templategroup_add_exists.html",lang_prefix) ;
				} else {
					sqlStr = "declare @new_id int\nselect @new_id = max(group_id)+1 from templategroups\ninsert into templategroups values(@new_id,'"+name+"')" ;
					IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;						
					htmlStr = IMCServiceRMI.parseDoc(imcserver, null, "templategroup_add.html",lang_prefix) ;
				}
			}
		} else if ( req.getParameter("group_rename")!=null ) {
			int grp_id = Integer.parseInt(req.getParameter("templategroup")) ;
			String name = req.getParameter("name") ;
			if ( name == null || name.equals("") ) {
				htmlStr = IMCServiceRMI.parseDoc( imcserver, null, "templategroup_rename_name_blank.html", lang_prefix) ;                        	
			} else {
				IMCServiceRMI.changeTemplateGroupName(imcserver,grp_id,name) ;				
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
			}
		} else if ( req.getParameter("list_templates_docs")!=null ) {
			String template_id = req.getParameter("template") ;
			String temp[][] = IMCServiceRMI.sqlQueryMulti(imcserver,"select simple_name,count(meta_id),t.template_id  from templates t left join text_docs td on td.template_id = t.template_id where lang_prefix = '"+lang+"' group by t.template_id,simple_name order by simple_name") ;
			htmlStr = "" ;
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
			Vector vec2 = new Vector() ;
			vec2.add("#template_list#") ;
			vec2.add(htmlStr) ;
			if ( template_id != null ) {
				temp = IMCServiceRMI.sqlQueryMulti(imcserver,"select td.meta_id, meta_headline from text_docs td join meta m on td.meta_id = m.meta_id where template_id = "+template_id+" order by td.meta_id") ;
				String htmlStr2 = "" ;
				for ( int i = 0 ; i<temp.length ; i++ ) {
					Vector vec = new Vector() ;
					vec.add("#meta_id#") ;
					vec.add(temp[i][0]) ;
					vec.add("#meta_headline#") ;
					String[] pd = {
						"&",	"&amp;",
						"<",	"&lt;",
						">",	"&gt;",
						"\"",	"&quot;"
					} ;
					if ( temp[i][1].length() > 60) {
						temp[i][1] = temp[i][1].substring(0,57)+"..." ;
					}
					temp[i][1] = Parser.parseDoc(temp[i][1],pd) ;
					vec.add(temp[i][1]) ;
					htmlStr2 += IMCServiceRMI.parseDoc(imcserver,vec,"templates_docs_row.html",lang_prefix) ;
				}
				vec2.add("#templates_docs#") ;
				vec2.add(htmlStr2) ;
			}
			vec2.add("#language#") ;
			vec2.add(lang) ;
			htmlStr = IMCServiceRMI.parseDoc(imcserver,vec2,"template_list.html",lang_prefix) ;
		} else if ( req.getParameter("show_doc") != null ) {
			String meta_id = req.getParameter("templates_doc") ;
			if ( meta_id != null ) {
				Utility.redirect(req,res,"AdminDoc?meta_id="+meta_id) ;
				return ;
			}
			String temp[][] = IMCServiceRMI.sqlQueryMulti(imcserver,"select simple_name,count(meta_id),t.template_id  from templates t left join text_docs td on td.template_id = t.template_id where lang_prefix = '"+lang+"' group by t.template_id,simple_name order by simple_name") ;
			htmlStr = "" ;
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
			Vector vec = new Vector() ;
			vec.add("#template_list#") ;
			vec.add(htmlStr) ;
			vec.add("#language#") ;
			vec.add(lang) ;
			htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"template_list.html",lang_prefix) ;
		}
			
		out.print(htmlStr) ;
	}

	private String parseAssignTemplates(String grp_id, String language, String lang_prefix, String host) throws IOException {
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		log ("Imcserver: "+imcserver) ;
		String temp[] ;
		temp = IMCServiceRMI.sqlProcedure(imcserver, "getTemplategroups") ;
		String temps = "" ;
		for (int i = 0; i < temp.length; i+=2) {
			if ( grp_id.equals(temp[i]) ){
				temps += "<option selected value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
			} else {
				temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
			}
		}
		Vector vec = new Vector() ;
		vec.add("#templategroups#") ;
		vec.add(temps);
		temps = "" ;
		temp = IMCServiceRMI.sqlQuery(imcserver, "select t.template_id,t.simple_name from templates_cref c join templates t on t.template_id = c.template_id where group_id = "+grp_id+" and lang_prefix = '"+language+"' order by t.simple_name") ;
		String list[] ;
		list = IMCServiceRMI.getDemoTemplateList(imcserver) ;
		for (int i = 0; i < temp.length; i+=2) {
			int tmp = Integer.parseInt(temp[i]) ;
			for ( int j = 0 ; j < list.length ; j++ ) {
				try {
					if ( Integer.parseInt(list[j]) == tmp ) {
						temp[i+1] = "*" + temp[i+1] ;
						break ;
					}
				} catch ( NumberFormatException ex ) {
					log.debug( "Exception occured" + ex );	   					
				}
			}
			temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
		}
		if ( grp_id != null ) {

			vec.add("#assigned#") ;
			vec.add(temps);
			temp = IMCServiceRMI.sqlQuery(imcserver, "select t.template_id,t.simple_name from templates t where lang_prefix = '"+language+"' and t.template_id not in (select template_id from templates_cref where group_id = "+grp_id+") order by t.simple_name") ;
			temps = "" ;
			for (int i = 0; i < temp.length; i+=2) {
				int tmp = Integer.parseInt(temp[i]) ;
				for ( int j = 0 ; j < list.length ; j++ ) {
					try {
						if ( Integer.parseInt(list[j]) == tmp ) {
							temp[i+1] = "*" + temp[i+1] ;
							break ;
						}					
					} catch ( NumberFormatException ex ) {
						
					}
				}
				temps += "<option value=\""+temp[i]+"\">"+temp[i+1]+"</option>" ;
			}
			vec.add("#unassigned#") ;
			vec.add(temps);
			temps = IMCServiceRMI.sqlQueryStr(imcserver, "select group_name from templategroups where group_id = "+grp_id) ;
			if ( temps == null ) {
				temps = "" ;
			}
			vec.add("#group#") ;
			vec.add(temps);
			vec.add("#group_id#") ;
			vec.add(String.valueOf(grp_id));
		}
		vec.add("#language#") ;
		vec.add(language) ;	
		return IMCServiceRMI.parseDoc(imcserver, vec, "template_assign.html",lang_prefix) ;
	}

	public void log (String str) {
		super.log(str);
		System.out.println("TemplateChange: " + str);
	}
	
}
