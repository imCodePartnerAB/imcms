package com.imcode.imcms.servlet.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class TestTag extends SimpleTagSupport {
		
	private int times;

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	@Override
	public void doTag() throws JspException, IOException {
		int i = 0;
		while (i++ < times) {
			getJspContext().setAttribute("index", i);
			getJspBody().invoke(null);
		}
	}

}
