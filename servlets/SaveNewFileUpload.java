import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;

public class SaveNewFileUpload extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url        	= Utility.getDomainPref( "servlet_url",host ) ;
		File file_path 				= new File(Utility.getDomainPref( "file_path", host ));

		imcode.server.User user ; 

		// Check if user logged on
		if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		} 
		
		res.setContentType("text/html") ;
		res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
		res.setHeader("Pragma","no-cache;") ;
		
		int length = req.getContentLength();

		ServletInputStream in = req.getInputStream() ;
		ServletOutputStream out = res.getOutputStream() ;

		byte buffer[] = new byte[ length ] ;	
		int bytes_read = 0;
		while ( bytes_read < length ) {
			bytes_read += in.read(buffer,bytes_read,length-bytes_read) ;
		}
		
		String contentType = req.getContentType() ;
		MultipartFormdataParser mp = new MultipartFormdataParser(new String(buffer,"8859_1"),contentType) ;
		String meta_id = mp.getParameter("new_meta_id") ;
		String parent = mp.getParameter("meta_id") ;		
		String mime = mp.getParameter("mime") ;
		String other = mp.getParameter("other") ;
		if ( mp.getParameter("ok")!=null ) {
			String file = mp.getParameter("file") ;
			String filename = mp.getFilename("file") ;
			if (mime == null || "".equals(mime) ) { 
			    mime = "application/octet-stream" ;
			    int dot = filename.lastIndexOf(".") ;
			    if ( dot != -1 ) {
				String ext = filename.substring(dot+1).toLowerCase() ;
				String mimetemp = Utility.getMimeTypeFromExtension(ext) ;
				if ( mimetemp != null ) {
				    mime = mimetemp ;
				}
			    }
			} else if ( mime.equals("other") ) {
			    mime = other ;
			}
			if ( file.length()>0 ) {
				File fn = new File(filename) ;
				filename = fn.getName() ;
				fn = new File(file_path, meta_id+"_se") ;
				String sqlStr = "insert into fileupload_docs (meta_id,filename,mime) values ("+meta_id+",'"+filename+"','"+mime+"')" ;
				IMCServiceRMI.sqlUpdateQuery( imcserver, sqlStr ) ;
				FileOutputStream fos = new FileOutputStream(fn) ;
				fos.write(file.getBytes("8859_1")) ;
				fos.close() ;
				IMCServiceRMI.activateChild(imcserver,Integer.parseInt(meta_id),user) ;
			}
			byte[] tempbytes = AdminDoc.adminDoc(Integer.parseInt(meta_id),Integer.parseInt(meta_id),host,user,req,res) ;
			if ( tempbytes != null ) {
			    out.write(tempbytes) ;
			}
		} else {
		    byte[] tempbytes = AdminDoc.adminDoc(Integer.parseInt(parent),Integer.parseInt(parent),host,user,req,res) ;
		    if ( tempbytes != null ) {
			out.write(tempbytes) ;
		    }		
		}
//		String htmlStr = IMCServiceRMI.interpretTemplate(imcserver,Integer.parseInt(parent),user) ;
		return ;

	}

}
