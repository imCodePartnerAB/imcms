import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;

public class SaveFileUpload extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
    }

    public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	String host				= req.getHeader("Host") ;
	String imcserver			= imcode.util.Utility.getDomainPref("adminserver",host) ;
	String start_url	= imcode.util.Utility.getDomainPref( "start_url",host ) ;
	File file_path				= imcode.util.Utility.getDomainPrefPath( "file_path", host );

	imcode.server.User user ;

	// Check if user logged on
	if ( (user=Check.userLoggedOn(req,res,start_url))==null ) {
	    return ;
	}

	res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
	res.setHeader("Pragma","no-cache;") ;

	int length = req.getContentLength();

	ServletInputStream in = req.getInputStream() ;
	Writer out = res.getWriter() ;

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
	    String output = AdminDoc.adminDoc(meta_id_int,meta_id_int,host,user,req,res) ;
	    if ( output != null ) {
		out.write(output) ;
	    }
	    return ;
	}

	String parent = mp.getParameter("parent_meta_id") ;

	if ( mp.getParameter("ok")!=null ) {
	    String file = mp.getParameter("file") ;
	    String filename = mp.getFilename("file") ;
	    String oldname = mp.getParameter("oldname") ;
	    String mime = mp.getParameter("mime") ; // The users choice in the listbox (""==autodetect,"other"==other)
	    String other = mp.getParameter("other") ;
	    if (null == filename || "".equals(filename)) {
		// We weren't given a new filename to play with.
		// We probably weren't given a file either, but let's not assume that.
		// We use the previous name instead.
		filename = oldname ;
	    }
	    if (filename == null) {
		// We don't have a filename... use the empty string.
		filename = "" ;
	    }

	    int dot = filename.indexOf('.') ;
	    if (mime == null ||"".equals(mime) ) { // Autodetect?
		if ( dot != -1 ) { // Were we given a filename containing an extension?
		    // So... get the extension!
		    String ext = filename.substring(dot+1).toLowerCase() ;
		    // The extension may correspond to a mime-type.
		    String mimetemp = (String)Utility.getMimeTypeFromExtension(ext) ;
		    if ( mimetemp != null ) { // Did we find a mime-type?
			mime = mimetemp ; // Yep! Use it.
		    } else { // No. We have no mime-type. Use standard binary stream mime-type as default.
			mime = "application/octet-stream" ;
		    }
		} else { // Must not forget that there may not be a dot.
		    mime = "application/octet-stream" ;
		}
	    } else if (mime.equals("other")) { // The user selected "other" in the listbox
		mime = other ; // FIXME: What if other is the empty string? Well, of course, it _is_ the user's choice...
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
		log ("Failed to update db with new fileupload") ;
	    }

	    // Write the file to disk.
	    // FIXME: File needs to be sent to the appserver.
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
	String output = AdminDoc.adminDoc(Integer.parseInt(meta_id),Integer.parseInt(meta_id),host,user,req,res) ;
	if ( output != null ) {
	    out.write(output) ;
	}
	return ;

    }

}
