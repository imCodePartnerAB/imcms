package com.imcode.imcms.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Content loop tag prototype.
 * 
 * Admin GUI is not yet defined for this tag.
 */
public class ContentTag extends BodyTagSupport {
	
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}
		
	/**
	 * Add admin buttons if user works in 'content loop' mode.
	 */
	public int doEndTag() throws JspException {
		return super.doEndTag();
	}	
}