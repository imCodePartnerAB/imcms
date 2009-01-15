package com.imcode.imcms.servlet.tags;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

/**
 * Content tag prototype.
 */
public class ContentTag extends BodyTagSupport {
	
	public final static int STEP = 100000;
		
    private int no;  
    
    private String indexVar;
    
    private int baseIndex;
        
    private Properties attributes = new Properties();
    
    private String label;  
    
    public void addGroupItem(SimpleImcmsTag tag) {
    }
    
    
    public int doAfterBody() throws JspException {
    	Iterator<Content> contentsIterator = (Iterator<Content>)pageContext.getAttribute("contentsIterator");
    	
    	if (!contentsIterator.hasNext()) {
    		pageContext.removeAttribute(indexVar);
    		
    		return super.doAfterBody();
    	}
    	
    	Content content = contentsIterator.next();
    	
    	int sequenceIndex = content.getSequenceIndex();
    	int index = baseIndex + (sequenceIndex * STEP);
    		
    	pageContext.setAttribute(indexVar, index);
    	pageContext.getRequest().setAttribute("contentId", content.getId());
    		
    	return EVAL_BODY_AGAIN;
    }
    
    

	public int doStartTag() throws JspException {
    	// Prototype try-catch block
		// TODO: Replace with real code 
    	try {
			String scriptsRoot = pageContext.getServletContext().getRealPath("WEB-INF") + "/groovy";	
			String[] roots = new String[] { scriptsRoot };
			GroovyScriptEngine gse = new GroovyScriptEngine(roots);
			Binding binding = new Binding();
			binding.setVariable("contentTag", this);			
			gse.run("ContentTag.groovy", binding);
			
			ContentLoop loop = (ContentLoop)binding.getVariable("loop");
			Iterator<Content> contentsIterator = loop.getContents().iterator();
			
			if (!contentsIterator.hasNext()) {
				return SKIP_BODY;
			} 
			
			pageContext.setAttribute("contentsIterator", contentsIterator);
							    	
			Content content = contentsIterator.next();
			
			int sequenceIndex = content.getSequenceIndex();
	    	int index = baseIndex + (sequenceIndex * STEP);
	    		
	    	pageContext.setAttribute(indexVar, index);
	    	pageContext.getRequest().setAttribute("contentId", content.getId());
	    	
	    	return super.doStartTag();
        } catch (Exception e) {
        	throw new JspException(e);
        }            
	}
	
	public int doEndTag() throws JspException {
		BodyContent bodyContent = getBodyContent();		
		String content = bodyContent == null ? "" : getBodyContent().getString();
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		UserDomainObject user = Utility.getLoggedOnUser(request);
		
		ParserParameters parserParameters = ParserParameters.fromRequest(request);		

		try {
			if (parserParameters.isGroupMode()) {
		        request.setAttribute("content", content);
		        request.setAttribute("label", label);
		        request.setAttribute("groupNo", no);
		        content = Utility.getContents("/imcms/"+user.getLanguageIso639_2()+"/jsp/docadmin/text/edit_content.jsp",
		                                   request, response) ;
			}
			
			pageContext.getOut().write(content);
		} catch (Exception e) {
			throw new JspException(e);
		} finally {
			pageContext.getRequest().removeAttribute("contentId");			
		}
		
		return super.doEndTag();
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
