/*
 * MainInitServlet.java
 *
 * Created on den 11 september 2001, 08:47
 */
 
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.Prefs;
import imcode.util.log.Log;
import imcode.util.log.LogLevels;


/** 
 *
 * @author  Hasse Brattberg, hasse@erudio.se
 */
public class MainInitServlet extends HttpServlet {

	public void init(ServletConfig config) throws ServletException
	{
		try 
		{
			super.init(config);
			// This is the first thin we allways must do.
			// Other parts of the programi depending that this is done.
			// Uggly, but can't find anoterh way for the moment/Hasse
			String realPathToWebApp = this.getServletContext().getRealPath("/");
			imcode.server.WebAppGlobalConstants.init( realPathToWebApp );
			
			String file = getInitParameter("log4j-init-file");
			System.out.println( realPathToWebApp + " " + file );
			Log.initLog( realPathToWebApp + file );
			logPlattformInfo( this.getServletContext() );
					
			Prefs.setConfigPath( this.getServletContext().getRealPath("/") + "/WEB-INF/conf/");
		}
		catch( Exception e ) 
		{
			System.err.println( e.getMessage() );
		}
	}

	private void logPlattformInfo( ServletContext application )
	{
		final String javaVersion = "java.version";
		final String javaVendor = "java.vendor";
		final String javaClassPath = "java.class.path";
		final String osName = "os.name";
		final String osArch = "os.arch";
		final String osVersion = "os.version";

		Log log = Log.getLog( this.getClass().getName() );
		log.log( LogLevels.INFO, "Servlet Engine: " + application.getServerInfo() );
		
		log.log( LogLevels.INFO, javaVersion + ": " + System.getProperty( javaVersion ) );
		log.log( LogLevels.INFO, javaVendor + ": " + System.getProperty( javaVendor ) );
		log.log( LogLevels.INFO,  osName + ": " + System.getProperty( osName ) );
		log.log( LogLevels.INFO,  osArch + ": " + System.getProperty( osArch ) );
		log.log( LogLevels.INFO, osVersion + ": " + System.getProperty( osVersion ) );

	}
}

