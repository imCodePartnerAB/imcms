import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;

public class MetaAdmin extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}
	
	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= imcode.util.Utility.getDomainPref("adminserver",host) ;
		String start_url        	= imcode.util.Utility.getDomainPref( "start_url",host ) ;

		res.setContentType("text/html") ;

		imcode.server.User user ;
		// Check if user logged on
		if( (user=Check.userLoggedOn( req,res,start_url ))==null ) {
			return ;
		} 
		String[] pd = {
			"&",	"&amp;",
			"<",	"&lt;",
			">",	"&gt;",
			"\"",	"&quot;",
		} ;
		
		ServletOutputStream out = res.getOutputStream() ;
		int user_id = user.getInt("user_id") ;
//		Hashtable hash = IMCServiceRMI.sqlQueryHash( imcserver, "select meta_id,meta_headline,doc_type from meta order by meta_id" ) ;
		boolean list = false ;
		int interval ;
		try {
			interval = Integer.parseInt(req.getParameter("interval")) ;
			list = true ;
		} catch ( NumberFormatException ex ) {
			interval = 1000 ;
		}
		int start ;
		int min = Integer.parseInt(IMCServiceRMI.sqlQueryStr(imcserver, "select min(meta_id) from meta")) ;
		int max = Integer.parseInt(IMCServiceRMI.sqlQueryStr(imcserver, "select max(meta_id) from meta")) ;
		try {
			start = Integer.parseInt(req.getParameter("start")) ;
			list = true ;
		} catch ( NumberFormatException ex ) {
			start = min ;
			//start = (Integer.parseInt(meta_id[0])-(Integer.parseInt(meta_id[0])%interval)) ;
		}
		int end = start+interval ;
		if ( !list ) {
			start = 0 ;
			end = 0 ;
		}

		Vector vec = new Vector () ;
		vec.add("#start#") ;
		vec.add(String.valueOf(start)) ;
		vec.add("#end#") ;
		vec.add(String.valueOf(end)) ;

		vec.add("#intervals#") ;
		int max_min = max - min ;
		String tmp = "" ;
		for ( int i = 10 ; i < (max_min*10) ; i*=10 ) {
			tmp += "<option value=\""+i+"\" "+ ( i == interval ? "selected" : "" )+">"+i+"</option>" ;
		}
		vec.add(tmp) ;
		vec.add("#starts#") ;
		tmp = "" ;
		for ( int i = min ; i <= max ; i+=interval ) {
			tmp += "<option value=\""+i+"\" "+( i == start ? "selected" : "" )+">"+i+"</option>" ;
		}
		vec.add(tmp) ;
		String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		out.println(IMCServiceRMI.parseDoc(imcserver, vec, "MetaAdminControl.html", lang_prefix)) ;
		if ( !list ) {
			return ;
		}
		Hashtable hash = IMCServiceRMI.sqlProcedureHash( imcserver, "getDocs "+user_id+","+start+","+end ) ;
		String[] meta_id = (String[])hash.get("meta_id") ;
		String[] pc = (String[])hash.get("parentcount") ;
		String[] hl = (String[])hash.get("meta_headline") ;
		String[] types = (String[])hash.get("doc_type") ;

		out.println("<hr>") ;
		
		for ( int i = 0 ; i < meta_id.length ; i++ ) {
		out.println("<table border=\"0\" cellspacing=\"0\"><tr bgcolor=\"#bababa\"><td width=\"500\" nowrap><ul>") ;
			if ( Integer.parseInt(meta_id[i]) < start ) {
				continue ;
			}
			if ( Integer.parseInt(meta_id[i]) >= end ) {
				break ;
			}
			out.println("<li>") ;
			if ( hl[i].length() > 80 ) {
				hl[i]=hl[i].substring(0,77)+"..." ;
			}
			String type ;
			if ( "5".equals(types[i]) ) {
				type = "URL-dok" ;
			} else if ( "6".equals(types[i]) ) {
				type = "Browserkontroll" ;
			} else if ( "7".equals(types[i]) ) {
				type = "HTML-dok" ;
			} else if ( "8".equals(types[i]) ) {
				type = "Fil" ;
			} else if ( "101".equals(types[i]) ) {
				type = "Diagram" ;
			} else if ( "102".equals(types[i]) ) {
				type = "Konferens" ;
			} else {
				type = "Text/Meny-dok" ;
			}
			out.println("<A name=\""+meta_id[i]+"\" href=\"AdminDoc?meta_id="+meta_id[i]+"\"><FONT COLOR=\"#FF0000\">"+meta_id[i]+"</FONT></A>&nbsp;<A name=\""+meta_id[i]+"\" href=\"GetDoc?meta_id="+meta_id[i]+"\">"+type+",&nbsp;"+pc[i]+"&nbsp;parents&nbsp;:&nbsp;"+Parser.parseDoc(hl[i],pd)+"</A>") ;
			if ( types[i].equals("2") ) {
				//Hashtable h2 = IMCServiceRMI.sqlQueryHash( imcserver, "select to_meta_id, meta_headline from childs c join meta m on c.to_meta_id = m.meta_id where c.meta_id = "+meta_id[i]+" order by to_meta_id") ;
				Hashtable h2 = IMCServiceRMI.sqlProcedureHash( imcserver, "getMenuDocChilds "+meta_id[i]+", "+user_id) ;
				String[] childs = (String[])h2.get("to_meta_id") ;
				String[] hl2 = (String[])h2.get("meta_headline") ;
				if ( childs != null && childs.length !=0 ) {
					out.println("<ul>") ;
					for ( int j = 0 ; j < childs.length ; j++ ) {
						String address = "MetaAdmin?start="+childs[j]+"&interval="+interval+"#"+childs[j] ;
						//int m_id = Integer.parseInt(childs[j]) ;
						//String address = "MetaAdmin?start="+(m_id - (m_id % interval))+"&interval="+interval+"#"+childs[j] ;
						if ( hl2[j].length() > 80 ) {
							hl2[j]=hl2[j].substring(0,77)+"..." ;
						}
						out.println("<li><A href=\""+address+"\">"+childs[j] +":&nbsp;"+Parser.parseDoc(hl2[j],pd)+"</A></li>") ;
					}
					out.println("</ul>") ;
				}
			} else if ( types[i].equals("6") ) {
				Hashtable h2 = IMCServiceRMI.sqlProcedureHash( imcserver, "getBrowserDocChilds "+meta_id[i]+", "+user_id ) ;
				String[] childs = (String[])h2.get("to_meta_id") ;
				String[] hl2 = (String[])h2.get("meta_headline") ;
				if ( childs != null && childs.length !=0 ) {
					out.println("<ul>") ;
					for ( int j = 0 ; j < childs.length ; j++ ) {
						String address = "MetaAdmin?start="+childs[j]+"&interval="+interval+"#"+childs[j] ;
						//int m_id = Integer.parseInt(childs[j]) ;
						//String address = "MetaAdmin?start="+(m_id - (m_id % interval))+"&interval="+interval+"#" ;
						if ( hl2[j].length() > 80 ) {
							hl2[j]=hl2[j].substring(0,77)+"..." ;
						}
						out.println("<li><A href=\""+address+childs[j]+"\">"+childs[j] +":&nbsp;"+Parser.parseDoc(hl2[j],pd)+"</A></li>") ;
					}
					out.println("</ul>") ;
				}
			}
			out.println("<br></li></ul></td></tr></table>") ;
			out.flush() ;
		}
		out.println("</body></html>") ;
	}
}
