package com.imcode.imcms.addon.imagearchive.tag.param;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class ParamTag extends TagSupport {
    private static final long serialVersionUID = -6788682799568491969L;
    
    private Object value;
    

    @Override
    public int doEndTag() throws JspException {
        Parameterizable parent = (Parameterizable) getParent();
        
        if (parent != null) {
            parent.addParam(value);
        }
        
        return EVAL_PAGE;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
