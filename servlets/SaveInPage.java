import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
/**
  Save data from editwindow.
  */
public class SaveInPage extends HttpServlet {

	/**
	init()
	*/
	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}


	/**
	doPost()
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		imcode.server.User user ;
		String htmlStr = "" ;
		String submit_name = "" ;
		String search_string = "" ;
		String text = "" ;
		String values[] ;
		int txt_no = 0 ;

		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();

		// get meta_id
		int meta_id = Integer.parseInt(req.getParameter("meta_id")) ;
//		int parent_meta_id = Integer.parseInt(req.getParameter("parent_meta_id")) ;

		// get form data
		imcode.server.Table doc = new imcode.server.Table() ;
		
		String template  = req.getParameter("template") ;
		String groupId  = req.getParameter("group");
	
		//the template group admin is a ugly mess but lets try to do the best of it
		//we save the group_id but if the group gets deleted else where it doesn't get changed
		//in the text_docs table, but the system vill not crash it only shows an empty group string.
		if(groupId == null) groupId= "-1"; //if there isn'n anyone lets set it to -1

		if ( template != null ) {
			doc.addField("template",template) ;
			//    String menu_template  = req.getParameter("menu_template") ;
			doc.addField("menu_template",template) ;
			//    String text_template  = req.getParameter("text_template") ;
			doc.addField("text_template",template) ;
			doc.addField("group_id",groupId);
		}
		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		}
		// Check if user has write rights
		if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,imcode.server.IMCConstants.PERM_DT_TEXT_CHANGE_TEMPLATE ) ) {	// Checking to see if user may edit this
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

		/*if (req.getParameter("metadata")!=null) {
			//htmlStr = IMCServiceRMI.interpretAdminTemplate(imcserver,meta_id,user,"change_meta.html",1,meta_id,0,0) ;
			htmlStr = imcode.util.MetaDataParser.parseMetaData(String.valueOf(meta_id), String.valueOf(meta_id),user,host) ;
		} else */
		if (req.getParameter("update")!=null) {
			user.put("flags",new Integer(0)) ;

			if ( template == null ) {
				Vector vec = new Vector() ;
				vec.add("#meta_id#") ;
				vec.add(String.valueOf(meta_id)) ;
				htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"inPage_admin_no_template.html",lang_prefix) ;
				out.print(htmlStr) ;
				return ;
			}
/*
			// old number of texts
			int old_tmpl_txt_count = IMCServiceRMI.getNoOfTxt(imcserver,meta_id,user) ;
*/
			// save textdoc
			IMCServiceRMI.saveTextDoc(imcserver,meta_id,user,doc) ;

			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
			Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;

			String sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
			IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr);

/*
			// add more text if needed
			int new_tmpl_txt_count = IMCServiceRMI.getNoOfTxt(imcserver,meta_id,user) ;

			if ( new_tmpl_txt_count > old_tmpl_txt_count)
				IMCServiceRMI.insertNewTexts(imcserver,meta_id,user,new_tmpl_txt_count - old_tmpl_txt_count) ;
*/
			// return page
//			htmlStr = IMCServiceRMI.interpretTemplate(imcserver,meta_id,user) ;
			byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;

		} else if (req.getParameter("preview")!=null) {
			// Call Magnus GetTemplateExample-procedure here
			if ( template == null ) {
				Vector vec = new Vector() ;
				vec.add("#meta_id#") ;
				vec.add(String.valueOf(meta_id)) ;
				htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"inPage_admin_no_template.html",lang_prefix) ;
				out.print(htmlStr) ;
				return ;
			}
			Object[] temp = null ;
			try {
				temp = IMCServiceRMI.getDemoTemplate(imcserver,Integer.parseInt(template)) ;
			} catch ( NumberFormatException ex ) {
			}
			if ( temp == null ) {
				htmlStr = IMCServiceRMI.parseDoc( imcserver, null, "no_demotemplate.html", lang_prefix ) ;
			} else {
				htmlStr = new String((byte[])temp[1],"8859_1") ;
			}
		} else if ( req.getParameter("change_group")!=null ) {

			user.put("flags",new Integer(imcode.server.IMCConstants.PERM_DT_TEXT_CHANGE_TEMPLATE)) ;

			String group = req.getParameter("group") ;
			if ( group != null ) {
				user.setTemplateGroup(Integer.parseInt(req.getParameter("group"))) ;
			}
//			htmlStr = IMCServiceRMI.interpretTemplate(imcserver,meta_id,user) ;
			byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;

		}
		out.print(htmlStr) ;
	}
}
