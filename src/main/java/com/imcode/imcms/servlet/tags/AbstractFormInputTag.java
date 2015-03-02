package com.imcode.imcms.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Created by Shadowgun on 19.02.2015.
 */
public abstract class AbstractFormInputTag extends SimpleTagSupport implements IAttributedTag {
    private final String type;
    private final String name;
    private String attributes = "";

    protected AbstractFormInputTag(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().getOut().print(String.format("<input type='%s' name='%s' %s />", type, name, attributes));
        super.doTag();
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
