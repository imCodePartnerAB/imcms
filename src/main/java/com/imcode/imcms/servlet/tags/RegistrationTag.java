package com.imcode.imcms.servlet.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shadowgun on 19.02.2015.
 */
public class RegistrationTag extends TagSupport implements IAttributedTag {
    private volatile String attributes = "";
    private static final String ATTR_PATTERN = "(\\S+)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?";

    @Override
    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            pageContext.getOut().print("<form data-goal='register' method='POST' action='"
                    + request.getContextPath() +
                    "/servlet/RegisterUser'"
                    + attributes + " >");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        try {

            pageContext.getOut().print("</form>");
            pageContext.getOut().print(
                    "<script>" +
                            "if(jQuery && jQuery.validator){" +
                            "jQuery(\"form[data-goal=register]\").validate()" +
                            "}" +
                            "</script>"
            );
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    @Override
    public String getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}
