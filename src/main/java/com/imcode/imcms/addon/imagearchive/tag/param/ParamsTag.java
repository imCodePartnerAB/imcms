package com.imcode.imcms.addon.imagearchive.tag.param;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ParamsTag extends BodyTagSupport implements Parameterizable {
    private static final long serialVersionUID = -4907208040708451602L;
    
    private List<Object> params;
    private String var;

    @Override
    public int doStartTag() throws JspException {
        params = new ArrayList<Object>();
        
        return super.doStartTag();
    }
    
    @Override
    public int doAfterBody() throws JspException {
        Object[] paramArr = params.toArray(new Object[params.size()]);
        
        pageContext.setAttribute(var, paramArr, PageContext.REQUEST_SCOPE);
        
        return SKIP_BODY;
    }

    @Override
    public void release() {
        params = null;
    }
    
    public void addParam(Object value) {
        params.add(value);
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }
}
