import java.util.*;
import javax.servlet.http.*;

import imcode.external.*;
import imcode.server.*;
import imcode.util.*;
import imcode.util.fortune.*;
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
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

		String fileName				= req.getParameter("qFile");
		if(fileName == null) {
		    fileName = (String) req.getAttribute("qFile");
		}

		String qLine				= req.getParameter("qLine");

		if(qLine == null) {
		    qLine = (String) req.getAttribute("qLine");
		}

		List quoteList = imcref.getQuoteList(fileName) ;

		int qInt;
		try{
			qInt = Integer.parseInt(qLine);
		}catch(NumberFormatException nfe){
			return null;
		}

		if (quoteList.size() > qInt && qInt >= 0) {
		    return HTMLConv.toHTMLSpecial(((Quote)quoteList.get(qInt)).getText()) ;
		}

		return null;
	}
}
