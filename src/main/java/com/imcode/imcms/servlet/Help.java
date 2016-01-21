package com.imcode.imcms.servlet;

import imcode.util.PropertyManager;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Help extends HttpServlet {

	private final static Logger log = Logger.getLogger(GetDoc.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doGet(req, res);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String helpDocName = req.getParameter("name");
		String lang = req.getParameter("lang");
		String propsFilePath = "/WEB-INF/help/helpdoc_" + lang + ".properties";

		try {
			int helpDoc = Integer.parseInt(PropertyManager.getPropertyFrom(propsFilePath, helpDocName));
			res.sendRedirect(String.valueOf(helpDoc));
		} catch (NumberFormatException e) {
			log.error("Help link error, help doc name: " + helpDocName + ",  no corresponding meta_id found.");
			res.sendError(HttpStatus.SC_NOT_FOUND);
		} catch (NullPointerException e) {
			log.error("Help link error, help doc name: " + helpDocName + ", 'lang' parameter is wrong.");
			res.sendError(HttpStatus.SC_NOT_FOUND);
		}
	}
}
