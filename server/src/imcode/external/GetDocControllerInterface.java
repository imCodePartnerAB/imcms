package imcode.external ;

import java.util.List ;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;

public interface GetDocControllerInterface {
	final static String CVS_REV = "$Revision$" ;
	final static String CVS_DATE = "$Date$" ;

	
	String  createString(HttpServletRequest req) throws ServletException, IOException;



}