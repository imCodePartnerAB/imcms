package com.imcode.imcms.servlet.tags;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;

import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

/**
 * Content loop tag prototype.
 * 
 * Admin GUI is not yet defined for this tag.
 */
public class ContentLoopTag extends BodyTagSupport {
	
	public final static int STEP = 100000;
		
	/**
	 * Content loop index in TextDocument.
	 */
    private int no;  
    
    /**
     * Content loop base index.
     */
    private int baseIndex;    
    
    /**
     * Content loop index variable name - bind to pageContext.
     */
    private String indexVar;
    
    /**
     * Contents iterator in loop.
     */
    Iterator<Content> contentsIterator;
            
    private Properties attributes = new Properties();
    
    /**
     * Label - common imcms attribute.
     */
    private String label;  
    
	public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        ParserParameters parserParameters = ParserParameters.fromRequest(request);
        TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
		
        ContentLoop loop = document.getContentLoop(no);
        
        if (loop == null) {
        	return SKIP_BODY;
        }
        
        contentsIterator = loop.getContents().iterator();
       
        return handleNextContent() ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}
	
	private boolean handleNextContent() {
        if (!contentsIterator.hasNext()) {
        	pageContext.removeAttribute(indexVar);
        	return false;
        }		
		
        Content content = contentsIterator.next();
        
		int sequenceIndex = content.getSequenceIndex();
    	int index = baseIndex + (sequenceIndex * STEP);
    	
    	pageContext.setAttribute(indexVar, index);
    	// ??? pageContext.getRequest().setAttribute("contentId", content.getId());
    	
    	return true;
	}
    
    
    public int doAfterBody() throws JspException {
    	return handleNextContent() ? EVAL_BODY_AGAIN : SKIP_BODY;
    }
    
	public int doEndTag() throws JspException {
		/*
		BodyContent bodyContent = getBodyContent();		
		String content = bodyContent == null ? "" : getBodyContent().getString();
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		UserDomainObject user = Utility.getLoggedOnUser(request);
		
		ParserParameters parserParameters = ParserParameters.fromRequest(request);
		
		try {
			pageContext.getOut().write(content);
		} catch (Exception e) {
			throw new JspException(e);
		} finally {
			//???pageContext.getRequest().removeAttribute("contentId");			
		}
		*/
		
		//???pageContext.getRequest().removeAttribute("contentId");	
		
		return EVAL_PAGE;
	}
	
	
    public void setNo(int no) {
        this.no = no ;
    }

    public int getNo() {
        return no ;
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode) ;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPre(String pre) {
        attributes.setProperty("pre", pre) ;
    }

    public void setPost(String post) {
        attributes.setProperty("post", post) ;
    }

	public int getBaseIndex() {
		return baseIndex;
	}


	public void setBaseIndex(int baseIndex) {
		this.baseIndex = baseIndex;
	}


	public String getIndexVar() {
		return indexVar;
	}


	public void setIndexVar(String indexVar) {
		this.indexVar = indexVar;
	}	
}
