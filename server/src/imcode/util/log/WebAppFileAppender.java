package imcode.util.log;

import org.apache.log4j.*;
import java.io.*;

import imcode.server.WebAppGlobalConstants;

public class WebAppFileAppender  extends FileAppender
{	
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	private static File absoluteWebAppPath = WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath() ;

	public WebAppFileAppender() 
	{
	    super();
	    super.setBufferedIO( true ) ;
	}

	public void setFile(String fileName, boolean append ) throws IOException 
	{
	    File logFile = new File(absoluteWebAppPath, fileName) ;
	    super.setFile( logFile.toString() ) ;
	    super.setAppend( append ) ;
	    super.activateOptions() ;
	}
}
