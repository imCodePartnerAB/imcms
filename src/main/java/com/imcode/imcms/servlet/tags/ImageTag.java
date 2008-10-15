package com.imcode.imcms.servlet.tags;

import imcode.server.parser.TagParser;

import javax.servlet.jsp.JspException;

public class ImageTag extends SimpleImcmsTag {
	
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
        return tagParser.tagImage(attributes);
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode) ;
    }

    public void setStyle(String style) {
        attributes.setProperty("style", style) ;
    }

    public void setStyleClass(String styleClass) {
        attributes.setProperty("class", styleClass) ;
    }

    public void setUsemap(String usemap) {
        attributes.setProperty("usemap", usemap) ;
    }
    
    public void setStyleId(String id) {
        attributes.setProperty("id", id) ;
    }

    public void setDocument(String documentName) {
        attributes.setProperty("document", documentName) ;
    }
}
