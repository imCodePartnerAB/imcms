package com.imcode.imcms.servlet;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {
	private String encoding = "UTF-8";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		final String encodingParam = filterConfig.getInitParameter("encoding");
		if (encodingParam != null) {
			encoding = encodingParam;
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(encoding);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
}
