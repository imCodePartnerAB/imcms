package com.imcode.imcms.servlet.tags;

import javax.servlet.jsp.JspException;

import imcode.server.parser.TagParser;

public class TextTag extends SimpleImcmsTag {
	
	/**
	 * Checks if tag inside of group tag...
	 */
	public int doStartTag() throws JspException {
		GroupTag groupTag = (GroupTag)findAncestorWithClass(this, GroupTag.class);
		
		if (groupTag != null) {
			groupTag.addGroupItem(this);
		}
		
		return super.doStartTag();
	}

    protected String getContent(TagParser tagParser) {
        return tagParser.tagText(attributes);
    }

    public void setRows(int rows) {
        attributes.setProperty("rows", ""+rows) ;
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode) ;
    }

    public void setFormats(String formats) {
        attributes.setProperty("formats", formats) ;
    }

    public void setDocument(String documentName) {
        attributes.setProperty("document", documentName) ;
    }

}
