package imcode.util.log;

import org.apache.log4j.*;
import java.io.*;

import imcode.server.WebAppGlobalConstants;

public class WebAppFileAppender  extends FileAppender
{	
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	private static File absoluteWebAppPath = new File(WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath());

	public WebAppFileAppender() 
	{
		super();
	}
	
	public void setFile(String fileName, boolean append ) throws IOException 
	{
		super.setFile( (new File(absoluteWebAppPath, fileName)).toString(), append );
	}
}
