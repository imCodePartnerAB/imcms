package com.imcode.imcms.servlet;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.imcode.imcms.api.ContentManagementSystem;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Serhii from Ubrainians for Imcode
 * on 14.04.16.
 */
public class Redirector extends HttpServlet {

	private static String nextUrl;

	public static String getNextUrl() {
		return nextUrl;
	}

	public static void setNextUrl(String nextUrl) {
		Redirector.nextUrl = nextUrl;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> json = new StringMap<String>();

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		String next_meta = request.getParameter(VerifyUser.REQUEST_PARAMETER__NEXT_META);
		String next_url = request.getParameter(VerifyUser.REQUEST_PARAMETER__NEXT_URL);

		if (StringUtils.isNotEmpty(StringUtils.trimToNull(next_meta))) {
			nextUrl = next_meta;
		} else if (StringUtils.isNotEmpty(StringUtils.trimToNull(next_url))) {
			nextUrl = next_url;
		}

		if (nextUrl != null && !nextUrl.contains("/")) {
			nextUrl = "/" + nextUrl;
		}

		if (ContentManagementSystem.fromRequest(request).getCurrentUser().isDefaultUser()) {
			json = null;
		} else if (nextUrl == null) {
			json.put("redirect", "/"); // this will redirect user to start page
		} else {
			json.put("redirect", nextUrl);
		}

		new Gson().toJson(json, response.getWriter());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
