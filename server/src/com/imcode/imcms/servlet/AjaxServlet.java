package com.imcode.imcms.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Tommy Ullberg, imCode
 * Mail: tommy@imcode.com
 * Date: 2010-dec-14
 * Time: 09:52:17
 */
public class AjaxServlet extends HttpServlet {
	
	public static String getPath(String contextPath) {
		return contextPath + "/servlet/AjaxServlet" ;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response) ;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response) ;
	}
	
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = StringUtils.defaultString(request.getParameter("action")) ;
		if ("sendValidationToW3cAndReturnJson".equals(action)) {
			request.getRequestDispatcher("/WEB-INF/imcms/jsp/validate_w3c.jsp").forward(request, response) ;
		} else {
			request.getRequestDispatcher("/WEB-INF/imcms/jsp/ajax_data.jsp").forward(request, response) ;
		}
	}
	
}
