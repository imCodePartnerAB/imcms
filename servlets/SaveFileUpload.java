import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;

public class SaveFileUpload extends HttpServlet {

	static protected Hashtable mimetypes ;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// Read an apache style mime-types file (mime	ext1 ext2 ext3)
		// and invert it into a hashtable with the extensions as keys.
		mimetypes = new Hashtable() ;
		try {
			Properties mt = Prefs.getProperties(new File(Prefs.get("mime.types","servlet.cfg"))) ;
			Enumeration enum = mt.propertyNames() ;
			while ( enum.hasMoreElements() ) {
				String mime = (String)enum.nextElement() ;
				StringTokenizer file_exts = new StringTokenizer(mt.getProperty(mime)," \t") ;
				while ( file_exts.hasMoreTokens() ) {
					mimetypes.put(file_exts.nextToken(), mime.toLowerCase()) ;
				}
			}
		} catch ( IOException ex ) {
			log("Unable to load mime-types-file: "+ex.getMessage()) ;
		}

	}

	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= imcode.util.Utility.getDomainPref("adminserver",host) ;
		String start_url        	= imcode.util.Utility.getDomainPref( "start_url",host ) ;
		File file_path 				= new File(imcode.util.Utility.getDomainPref( "file_path", host ));
		int uploadsize 				= Integer.parseInt(Utility.getDomainPref( "max_uploadsize",host )) ;

		imcode.server.User user ;

		// Check if user logged on
		if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		}

		res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
		res.setHeader("Pragma","no-cache;") ;

		int length = req.getContentLength();

		ServletInputStream in = req.getInputStream() ;
		ServletOutputStream out = res.getOutputStream() ;

		if (length<1||length>uploadsize) {
			return ;
		}

		byte buffer[] = new byte[ length ] ;
		int bytes_read = 0;
		while ( bytes_read < length ) {
			bytes_read += in.read(buffer,bytes_read,length-bytes_read) ;
		}

		String contentType = req.getContentType() ;
		MultipartFormdataParser mp = new MultipartFormdataParser(new String(buffer,"8859_1"),contentType) ;


		String meta_id = mp.getParameter("meta_id") ;
		int meta_id_int = Integer.parseInt(meta_id) ;

		res.setContentType("text/html") ;
		// Check if user has write rights
		if ( !IMCServiceRMI.checkDocAdminRights(imcserver,meta_id_int,user,65536 ) ) {	// Checking to see if user may edit this
			byte[] tempbytes ;
			tempbytes = AdminDoc.adminDoc(meta_id_int,meta_id_int,host,user,req,res) ;
			if ( tempbytes != null ) {
				out.write(tempbytes) ;
			}
			return ;
		}

		String parent = mp.getParameter("parent_meta_id") ;

		/*if ( mp.getParameter("metadata")!=null) {
			//String htmlStr = IMCServiceRMI.interpretAdminTemplate ( imcserver, Integer.parseInt(meta_id),user,"change_meta.html",8,Integer.parseInt(parent),0,0 ) ;
			String htmlStr = imcode.util.MetaDataParser.parseMetaData(meta_id, parent,user,host) ;
			out.print(htmlStr) ;
			return ;
		} else if ( mp.getParameter("download")!=null ) {
			BufferedInputStream fr ;
			try {
				fr = new BufferedInputStream( new FileInputStream( new File( file_path, String.valueOf( meta_id )+"_se" ) ) ) ;
			} catch ( IOException ex ) {
				String lang_prefix = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
				String htmlStr = IMCServiceRMI.parseDoc( imcserver, null,"no_page.html",lang_prefix ) ;
				out.write( htmlStr.getBytes("8859_1") );
				return ;
			}
			String sqlStr = "select filename from fileupload_docs where meta_id = " + meta_id ;
			String filename =	IMCServiceRMI.sqlQueryStr( imcserver,sqlStr ) ;
			int len = fr.available( ) ;
			res.setContentLength( len ) ;
			res.setContentType( "application/octetstream; name=\""+filename+"\"" ) ;
			res.setHeader( "Content-Disposition","attachment; filename=\""+filename+"\";" ) ;
			bytes_read = 0 ;
			int total_read = 0 ;
			buffer = new byte[len] ;
//			BufferedOutputStream os = new BufferedOutputStream (out) ;
			while( total_read < len ) {
				bytes_read = fr.read( buffer,total_read,len-total_read ) ;
				//os.write(buffer,total_read,bytes_read) ;
				total_read += bytes_read ;
			}
			out.write(buffer) ;

			fr.close( ) ;
//			os.close( ) ;
			return ;
		} else*/ if ( mp.getParameter("ok")!=null ) {
			String file = mp.getParameter("file") ;
			String filename = mp.getFilename("file") ;
			String mime = mp.getParameter("mime") ;
			String other = mp.getParameter("other") ;
			int dot = filename.lastIndexOf(".") ;
			if (mime == null ||"".equals(mime) ) {
			    if ( dot != -1 ) {
				String ext = filename.substring(dot+1).toLowerCase() ;
				String mimetemp = (String)mimetypes.get(ext) ;
				if ( mimetemp != null ) {
				    mime = mimetemp ;
				} else {
				    mime = "application/octet-stream" ; 
				}
			    }
			} else {
			    mime = other ;
			}
			File fn = null ;
			if (file.length() > 0) {
				fn = new File(filename) ;
				log ("Users filename: "+filename) ;
				filename = fn.getName() ;
				log ("Servers filename: "+filename) ;
				fn = new File(file_path, meta_id+"_se") ;
				log (fn.toString()) ;
			}

			String sqlStr ;
			if ( file.length()>0 ) {
				sqlStr = "update fileupload_docs set filename = '"+filename+"', mime = '"+mime+"' where meta_id = " + meta_id ;
			} else {
				sqlStr = "update fileupload_docs set mime = '"+mime+"' where meta_id = " + meta_id ;
			}
			try {
				IMCServiceRMI.sqlUpdateQuery( imcserver, sqlStr ) ;
			} catch ( RemoteException ex ) {
				log ("Det sket sig med databasen...") ;
			}
			if (file.length() > 0) {
				FileOutputStream fos = new FileOutputStream(fn) ;
				fos.write(file.getBytes("8859_1")) ;
				fos.close() ;
			}
		}

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
		Date dt = IMCServiceRMI.getCurrentDate(imcserver) ;
		String sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
		IMCServiceRMI.sqlUpdateQuery(imcserver,sqlStr) ;


//		String htmlStr = IMCServiceRMI.interpretTemplate(imcserver,Integer.parseInt(parent),user) ;
		byte[] tempbytes = AdminDoc.adminDoc(Integer.parseInt(meta_id),Integer.parseInt(meta_id),host,user,req,res) ;
		if ( tempbytes != null ) {
			out.write(tempbytes) ;
		}
		return ;

	}

}
