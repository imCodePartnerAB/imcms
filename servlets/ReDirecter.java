import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import imcode.util.* ;

public class ReDirecter extends HttpServlet {

    private final static int METAID_OFFSET=1;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

	String[] pathElements = split(req.getPathInfo(),'/',false);

	if (pathElements.length > METAID_OFFSET) {
	    res.sendRedirect("GetDoc?meta_id=" + pathElements[METAID_OFFSET]);
	} else {
	    res.sendRedirect("StartDoc");
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
