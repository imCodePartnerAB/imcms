/*
 * MainInitServlet.java
 *
 * Created on den 11 september 2001, 08:47
 */
 
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File ;

import imcode.util.Prefs;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
/** 
 *
 * @author  Hasse Brattberg, hasse@erudio.se
 */
public class MainInitServlet extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
	private final static Category log = Category.getInstance(MainInitServlet.class.getName());

	public void init(ServletConfig config) throws ServletException
	{
		try 
		{
			// This is the first thin we allways must do.
			// Other parts of the programi depending that this is done.
			// Uggly, but can't find anoterh way for the moment/Hasse
			super.init(config);	
			File realPathToWebApp = new File(this.getServletContext().getRealPath("/")) ;
			imcode.server.WebAppGlobalConstants.init( realPathToWebApp );
			
			String file = getInitParameter("log4j-init-file");
			System.out.println( realPathToWebApp + " " + file );
			
			String initLogString = (new File(realPathToWebApp,file)).toString() ;
			//PropertyConfigurator.resetConfiguration();
			PropertyConfigurator.configure( initLogString );

			Category logger = Category.getInstance( MainInitServlet.class.getName() );
			logger.info("Logging started" );
			logPlattformInfo( this.getServletContext() );
			
			File fil = new File(this.getServletContext().getRealPath("/"), "WEB-INF/conf/");
			Prefs.setConfigPath( fil.getCanonicalPath());
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

		
		log.info( "Servlet Engine: " + application.getServerInfo() );
		
		log.info(  javaVersion + ": " + System.getProperty( javaVersion ) );
		log.info(  javaVendor + ": " + System.getProperty( javaVendor ) );
		log.info(  osName + ": " + System.getProperty( osName ) );
		log.info(  osArch + ": " + System.getProperty( osArch ) );
		log.info(  osVersion + ": " + System.getProperty( osVersion ) );

	}
}

