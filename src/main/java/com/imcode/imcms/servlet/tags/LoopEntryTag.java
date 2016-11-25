package com.imcode.imcms.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 23.11.16.
 */
public class LoopEntryTag extends TagSupport {

    public int doStartTag() throws JspException {
        LoopTag loopTag = (LoopTag) findAncestorWithClass(this, LoopTag.class);

        if (null == loopTag) {
            throw new JspTagException("loopentry must be enclosed in loop.");
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
}
