import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
/**
  Save a browserdocument.
	Shows a change_meta.html which calls SaveMeta
*/
public class SaveBrowserDoc extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	/**
	init()
	*/
	public void init( ServletConfig config ) throws ServletException {
		super.init( config ) ;
	}

	/**
	doPost()
	*/
	public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= imcode.util.Utility.getDomainPref("adminserver",host) ;
		String servlet_url        	= imcode.util.Utility.getDomainPref( "servlet_url",host ) ;
		String start_url        	= imcode.util.Utility.getDomainPref( "start_url",host ) ;

		imcode.server.User user ;
		String htmlStr = "" ;
		String submit_name = "" ;;
		String text = "" ;
		String values[] ;
		int meta_id ;
		int parent_meta_id ;
		int txt_no = 0 ;

		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream( );

		// get meta_id
		meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;

		// get parent_meta_id
		parent_meta_id = Integer.parseInt( req.getParameter( "parent_meta_id" ) ) ;
		log ("meta_id:"+meta_id) ;
		log ("parent_meta_id:"+parent_meta_id) ;
		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		}
		// Check if user has write rights
		if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id,user,65536 ) ) {	// Checking to see if user may edit this
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

		/*if( req.getParameter("cancel")!=null ) {
			String sqlStr = "delete from browser_docs where to_meta_id = 0 and meta_id = " + meta_id ;
			IMCServiceRMI.sqlUpdateQuery( imcserver, sqlStr ) ;
			// check if browser_doc
//			String br_id = (String)req.getSession(false).getValue("browser_id") ;
//			String sqlStr = "select top 1 to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+parent_meta_id+" and '"+br_id+"' like user_agent" ;
//			String tmp = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
//			log (sqlStr+ " = "+tmp) ;
//			if ( tmp != null && (!"".equals(tmp)) ) {
//				parent_meta_id = Integer.parseInt(tmp) ;
//			}
//FIXME?	htmlStr = IMCServiceRMI.interpretTemplate( imcserver,parent_meta_id,user ) ;
			byte[] tempbytes = AdminDoc.adminDoc(parent_meta_id,parent_meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		} else if( req.getParameter("metadata")!=null ) {
			log ("meta_id:"+meta_id) ;
			log ("parent_meta_id:"+parent_meta_id) ;
			htmlStr = imcode.util.MetaDataParser.parseMetaData(String.valueOf(meta_id), String.valueOf(parent_meta_id),user,host) ;
			//htmlStr = IMCServiceRMI.interpretAdminTemplate( imcserver,meta_id,user,"change_meta.html",6,parent_meta_id,0,0 ) ;
		} else */ if( req.getParameter("ok")!=null ) {
			Enumeration enum = req.getParameterNames() ;
			while ( enum.hasMoreElements() ) {
				String param = (String)enum.nextElement() ;
				if ( param.indexOf("bid")==0 ) {
					String bid = param.substring(3) ;
					String to = req.getParameter(param) ;
					String sqlStr = null ;
					try {
						int t = Integer.parseInt(to) ;
						sqlStr = "update browser_docs set to_meta_id = "+t+" where meta_id = "+meta_id+" and browser_id = "+bid ;
					} catch ( NumberFormatException ex ) {
						if ( !bid.equals("0") ) {
							sqlStr = "delete from browser_docs where meta_id = "+meta_id+" and browser_id = "+bid ;
						}
					}
					if ( sqlStr != null ) {
						IMCServiceRMI.sqlUpdateQuery( imcserver, sqlStr ) ;
					}
				}
			}

			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
			Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;
			String sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;

			//IMCServiceRMI.saveBrowserDoc( imcserver,meta_id,user,doc ) ;
/*			String br_id = (String)req.getSession(false).getValue("browser_id") ;
			String sqlStr = "select top 1 to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+parent_meta_id+" and '"+br_id+"' like user_agent" ;
			String tmp = IMCServiceRMI.sqlQueryStr(imcserver,sqlStr) ;
			log (sqlStr+ " = "+tmp) ;
			if ( tmp != null && (!"".equals(tmp)) ) {
				parent_meta_id = Integer.parseInt(tmp) ;
			}*/
//FIXME?	htmlStr = IMCServiceRMI.interpretTemplate( imcserver,parent_meta_id,user ) ;

			byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;

		} else if ( req.getParameter("add_browsers")!=null) {
			String[] browsers = req.getParameterValues("new_browsers") ;
			if ( browsers != null ) {
				for ( int i=0 ; i<browsers.length ; i++ ) {
					String sqlStr = "insert into browser_docs (meta_id,to_meta_id,browser_id) values ("+meta_id+",0,"+browsers[i]+")" ;
					IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;
				}
			}
			Vector vec = new Vector () ;
			String sqlStr = "select name,browsers.browser_id,to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+meta_id+" order by value desc,name asc" ;
			Hashtable hash = IMCServiceRMI.sqlQueryHash(imcserver,sqlStr) ;
			String[] b_id = (String[])hash.get("browser_id") ;
			String[] nm = (String[])hash.get("name") ;
			String[] to = (String[])hash.get("to_meta_id") ;
			String bs = "" ;
			if ( b_id != null ) {
				bs+="<table width=\"50%\" border=\"0\">" ;
				for ( int i = 0 ; i<b_id.length ; i++ ) {
					String[] temparr = {" ","&nbsp;"} ;
					bs += "<tr><td>"+Parser.parseDoc(nm[i],temparr)+":</td><td><input type=\"text\" size=\"10\" name=\"bid"+b_id[i]+"\" value=\""+(to[i].equals("0") ? "\">" : to[i]+"\"><a href=\"GetDoc?meta_id="+to[i]+"&parent_meta_id="+meta_id+"\">"+to[i]+"</a>")+"</td></tr>" ;
				}
				bs+="</table>" ;
			}
			vec.add("#browsers#") ;
			vec.add(bs) ;
			sqlStr = "select browser_id,name from browsers where browser_id not in (select browsers.browser_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = "+meta_id+" ) order by value desc,name asc" ;
			hash = IMCServiceRMI.sqlQueryHash(imcserver,sqlStr) ;
			b_id = (String[])hash.get("browser_id") ;
			nm = (String[])hash.get("name") ;
			String nb = "" ;
			if ( b_id!=null ) {
				for ( int i = 0 ; i<b_id.length ; i++ ) {
					nb += "<option value=\""+b_id[i]+"\">"+nm[i]+"</option>" ;
				}
			}
			vec.add("#new_browsers#") ;
			vec.add(nb) ;
			vec.add("#getMetaId#") ;
			vec.add(String.valueOf(meta_id)) ;
			vec.add("#getDocType#") ;
			vec.add("<INPUT TYPE=\"hidden\" NAME=\"doc_type\" VALUE=\"6\">") ;
//			vec.add("#DocMenuNo#") ;
//			vec.add("") ;
			vec.add("#getParentMetaId#") ;
			vec.add(String.valueOf(parent_meta_id)) ;
			vec.add("#servlet_url#") ;
			vec.add(servlet_url) ;
			vec.add("#adminMode#") ;
			vec.add(IMCServiceRMI.getMenuButtons(imcserver,meta_id,user)) ;
			String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;

			htmlStr = IMCServiceRMI.parseDoc(imcserver, vec, "change_browser_doc.html", lang_prefix) ;
		}
		out.print( htmlStr ) ;
	}
}

