import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;

public class GenericUpload extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		int uploadsize 				= Integer.parseInt(Utility.getDomainPref( "max_uploadsize",host )) ;

		// Check if user logged on
		if ( (Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		} 

		res.setContentType("text/html") ;
		res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
		res.setHeader("Pragma","no-cache;") ;
		
		int length = req.getContentLength();

		if (length<1||length>uploadsize) {
			doGet(req,res);
			return ;
		}
		ServletInputStream in = req.getInputStream() ;
		byte buffer[] = new byte[ length ] ;
		int bytes_read = 0;
		while ( bytes_read < length ) {
			bytes_read += in.read(buffer,bytes_read,length-bytes_read) ;
		}
		String contentType = req.getContentType() ;
		MultipartFormdataParser mp = new MultipartFormdataParser(buffer,contentType) ;
		String file = mp.getParameter("file") ;
		log ("Filesize: "+file.length()) ;
		String filename = mp.getFilename("file") ;
		File fp = new File (filename) ;
		filename = fp.getName() ;
		String path = mp.getParameter("path") ;
		fp = new File(path) ;
		if (!fp.exists() &&  !fp.isDirectory()) {
			fp.mkdirs() ;
		}
		String name = mp.getParameter("name") ;
		if (name!=null && name.length()>0) {
			filename = name ;
		}
		fp = new File(fp,filename) ;
		FileOutputStream fw = new FileOutputStream(fp) ;
		fw.write(file.getBytes("8859_1")) ;
		fw.close() ;
		res.sendRedirect(mp.getParameter("target")) ;
		return ;

	}

	public void log (String str) {
		super.log(str);
		System.out.println("GenericUpload: " + str);
	}
	
}