
import java.util.*;
import javax.servlet.http.*;

import imcode.external.*;
import imcode.server.*;
import imcode.util.*;
import javax.servlet.ServletException;
import java.io.IOException;

public class QuoteLineCollector implements imcode.external.GetDocControllerInterface{
	private final static String CVS_REV = "$Revision$"  ;
	private final static String CVS_DATE = "$Date$";

	public QuoteLineCollector(){}
	
	public String createString(HttpServletRequest req) throws ServletException, IOException
	{
		String retValue = null;
		//lets get the stuff we need to get the quote file
		String host					= req.getHeader("Host") ;	
		String imcserver			= Utility.getDomainPref("userserver",host) ;	
		
		String fileName				= req.getParameter("qFile");
		if(fileName == null) 
			fileName = (String) req.getAttribute("qFile");
		String qLine				= req.getParameter("qLine");
		if(qLine == null)
			qLine = (String) req.getAttribute("qLine");	
		
		String resFile = HTMLConv.toHTML(IMCServiceRMI.getFortune(imcserver,fileName));	
		
		//System.out.println(resFile);
		int qInt;
		try{
			qInt = Integer.parseInt(qLine);
		}catch(NumberFormatException nfe){
			return null;
		}
		
		//System.out.println(resFile);
		StringTokenizer token = new StringTokenizer(resFile, "#", false);
		int counter = 1; 
				
		while (token.hasMoreTokens()){
			String tmp = token.nextToken();		
			if (counter == ((qInt+1)*3)){
				return tmp;
			}
			counter++;		
		}
		
		//we didn't find anything so lets return null
		return null;
	}
}