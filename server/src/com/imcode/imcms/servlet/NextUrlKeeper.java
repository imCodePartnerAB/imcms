package com.imcode.imcms.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Serhii from Ubrainians for Imcode
 * on 11.04.16.
 */
public class NextUrlKeeper extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextUrl = request.getParameter(VerifyUser.REQUEST_PARAMETER__NEXT_URL);
		if (nextUrl == null) {
			Redirector.setNextUrl(null);
		} else {
			String[] urlArr = nextUrl.split("/");
			if (urlArr[urlArr.length - 1].equalsIgnoreCase("login")) { // if we have no any next url but just login page
				Redirector.setNextUrl(null);
			} else {
				Redirector.setNextUrl(nextUrl);
			}
		}
	}
}
