import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.*;
import java.rmi.registry.*;

import imcode.util.* ;
import imcode.external.diverse.*;

//former class GenericUpload

public class ConfAndBillbTemplateUpload extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doPost ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String maxUooLoadStr		= Utility.getDomainPref( "max_uploadsize",host );
		String imcServer = Utility.getDomainPref("userserver",host) ;
		// Check if user logged on
		if ( (Check.userLoggedOn(req,res,start_url))==null ) {
			return ;
		} 
		
		HttpSession session = req.getSession(true);
		imcode.server.User user = (imcode.server.User)session.getValue("logon.isDone");
		if (user == null)
		{
			log("the user was null so RETURN");
			return;
		}
					
		res.setContentType("text/html") ;
		res.setHeader("Cache-Control","no-cache; must-revalidate;") ;
		res.setHeader("Pragma","no-cache;") ;
		
		int length = req.getContentLength();

		ServletInputStream in = req.getInputStream() ;
		byte buffer[] = new byte[ length ] ;
		int bytes_read = 0;
		while ( bytes_read < length ) {
			bytes_read += in.read(buffer,bytes_read,length-bytes_read) ;
		}
		
		String contentType = req.getContentType() ;
		MultipartFormdataParser mp = new MultipartFormdataParser(buffer,contentType) ;
		String file = mp.getParameter("file") ;
		//log ("Filesize: "+file.length()) ;
		String filename = mp.getFilename("file");
//vet inte om det behövs men borde vi inte oxå kolla att det verkligen är en bild eller image fil


		//ok lets get some info
		String metaId = mp.getParameter("metaId");
		File externalPath;
		
		//lets get the templatefolder to save the file in
		String uploadType = mp.getParameter("uploadType");//IMAGE or TEMPLATE
		String folderName = mp.getParameter("folderName");
		if ( uploadType.equalsIgnoreCase("TEMPLATE"))
		{
			externalPath = new File(MetaInfo.getExternalTemplateFolder(imcServer, metaId) , folderName );
		}else if(uploadType.equalsIgnoreCase("IMAGE"))
		{
			RmiConf rmi = new RmiConf(user);
			externalPath = new File(rmi.getExternalImageHomeFolder(host, imcServer, metaId) ,folderName ) ;
		}else
		{
			log("not a template or image so lets RETURN");
			return;
		}
		
		if (externalPath == null)
		{
			log("externalPath was null so return");
			return;
		}
	

		File fp = new File (filename) ;
		filename = fp.getName() ;
		
		fp = new File(externalPath.toString()) ;
		if (!fp.exists() &&  !fp.isDirectory()) {
			log("we dont allowes new directories so lets RETURN");
			return;
			//fp.mkdirs() ;
		}
		
		
		fp = new File(fp,filename) ;
		log("fp: "+fp);
		FileOutputStream fw = new FileOutputStream(fp) ;
		fw.write(file.getBytes("8859_1")) ;
		fw.close() ;
		res.sendRedirect(mp.getParameter("target")) ;
		return ;

	}

	public void log (String str) {
		super.log(str);
		System.out.println("ConfAndBillbTemplateUpload: " + str);
	}
	
}