import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import imcode.util.* ;

public class ReDirecter extends HttpServlet {
		
	private final static int METAID_OFFSET_FROM_REDIRECT_STRING=3;
	private final static String REDIRECT_STRING="RD";

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
		String host = req.getHeader("host");
		String servlet_url	= Utility.getDomainPref( "servlet_url",host );
        //String[] sURI=req.getRequestURI().split("/");
		String reqa=req.getRequestURI();
		String[] sURI=split(reqa,'/',true);

		for (int i=1; i<sURI.length; i++ ){
			if (sURI[i].equals(REDIRECT_STRING) ){
				if (sURI.length<=i+METAID_OFFSET_FROM_REDIRECT_STRING)
				{
					res.sendRedirect(servlet_url + "StartDoc");
					break;
				}else{
					res.sendRedirect(servlet_url + "GetDoc?meta_id=" + sURI[i+METAID_OFFSET_FROM_REDIRECT_STRING]);
					break;
				}
			}
		}
    }

	private static String[] split (String input, char splitChar, boolean includeSeparators) {
		StringTokenizer tokenizer = new StringTokenizer(input,""+splitChar,includeSeparators) ;
		String[] output = new String[tokenizer.countTokens()] ;
		
		for (int i = 0; i < output.length; ++i) {
			output[i] = tokenizer.nextToken() ;
		}
		return output ;
    }
}