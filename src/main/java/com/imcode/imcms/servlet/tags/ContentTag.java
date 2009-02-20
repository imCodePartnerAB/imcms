package com.imcode.imcms.servlet.tags;

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
	 * If user is in 'group' mode, then add admin's controls.
	 */
	/*
	public int doEndTag() throws JspException {
		String content = getBodyContent().getString();
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		UserDomainObject user = Utility.getLoggedOnUser(request);
		
		ParserParameters parserParameters = ParserParameters.fromRequest(request);		

		try {
			if (parserParameters.isGroupMode()) {
		        request.setAttribute("content", content);
				
		        content = Utility.getContents("/imcms/"+user.getLanguageIso639_2()+"/jsp/docadmin/text/edit_content_loop.jsp",
		                                   request, response) ;
			}
			
			pageContext.getOut().write(content);
		} catch (Exception e) {
			throw new JspException(e);
		}
		
		return super.doEndTag();
	}
	*/
}
