package imcode.util.log;

import org.apache.log4j.*;
import java.io.*;

import imcode.server.WebAppGlobalConstants;

public class WebAppFileAppender  extends FileAppender
{	
	private static String absoluteWebAppPath = WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath();

	public WebAppFileAppender() 
	{
		super();
	}
	
	public void setFile(String fileName, boolean append ) throws IOException 
	{
		super.setFile( absoluteWebAppPath + fileName, append );
	}
}