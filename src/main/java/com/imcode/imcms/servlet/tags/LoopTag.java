package com.imcode.imcms.servlet.tags;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

/**
 * Working prototype - REFACTOR & OPTIMIZE
 */
public class LoopTag extends SimpleTagSupport {

    private Properties attributes = new Properties();
    private String template;
    private String label;
    
	public final static int STEP = 100000;
	
	/**
	 * Content loop index in TextDocument.
	 */
    private int loopIndex;  
    
    /**
     * Content loop base index.
     */
    private int baseIndex;    
    
    /**
     * Content loop index variable name - bind to pageContext.
     */
    private String indexVar;    
        
	@Override
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();  		
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        ParserParameters parserParameters = ParserParameters.fromRequest(request);
        TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
        UserDomainObject user = Utility.getLoggedOnUser(request);        
        ContentLoop loop = document.getContentLoop(loopIndex);
        
        if (loop == null) {        	
        	loop = new ContentLoop();
        	loop.setBaseIndex(baseIndex);
        	loop.setMetaId(document.getMeta().getId());
        	loop.setIndex(loopIndex);
        	loop.setMetaVersion(document.getVersion().getNumber());
        	Content content = new Content();
        	content.setOrderIndex(0);
        	content.setSequenceIndex(0);
        	loop.getContents().add(content);
        	document.setContentLoop(loopIndex, loop);
        }
        
        if (parserParameters.isContentLoopMode()) {
        	doTagInAdminMode(parserParameters, document, pageContext, request, response, user, loop);
        } else {
        	doTagInUserMode(pageContext, loop);
        }
    }
	
	private void doTagInAdminMode(ParserParameters parserParameters, TextDocumentDomainObject document, PageContext pageContext, HttpServletRequest request, HttpServletResponse response, UserDomainObject user, ContentLoop loop) 
	throws JspException, IOException {
        StringWriter loopViewWriter = new StringWriter();
        List<Content> contents = loop.getContents();
        int contentsCount = contents.size();
        int lastContentIndex = contentsCount - 1;

    	for (int i = 0; i <= lastContentIndex; i++) {
    		Content content = contents.get(i);
    		int sequenceIndex = content.getSequenceIndex();
        	int index = baseIndex + (sequenceIndex * STEP);
        	
        	pageContext.setAttribute(indexVar, index);

        	StringWriter contentViewWriter = new StringWriter();
    		getJspBody().invoke(contentViewWriter);
    		
    		String contentViewFragment = contentViewWriter.getBuffer().toString();
    		request.setAttribute("document", document);
    		request.setAttribute("contentLoop", loop);
    		request.setAttribute("content", content);
    		request.setAttribute("flags", parserParameters.getFlags());
    		request.setAttribute("viewFragment", contentViewFragment);
    		request.setAttribute("contentsCount", contentsCount);
    		request.setAttribute("isFirstContent", i == 0);
    		request.setAttribute("isLastContent", i == lastContentIndex);
    		
    		try {
    			contentViewFragment = Utility.getContents(
    		        		"/WEB-INF/admin/textdoc/contentloop/tag/content.jsp",
    		                request, response);
    		} catch (ServletException e) {
    			throw new JspException(e);
    		}
    		
    		loopViewWriter.append(contentViewFragment);
    	}
    	
    	pageContext.removeAttribute(indexVar);
    	
    	String loopViewFragment = loopViewWriter.getBuffer().toString();
    	request.setAttribute("contentLoop", loop);
    	request.setAttribute("viewFragment", loopViewFragment);    	
		request.setAttribute("document", document);
		request.setAttribute("contentLoop", loop);
		request.setAttribute("flags", parserParameters.getFlags());
		
		try {
			loopViewFragment = Utility.getContents(
	        		"/WEB-INF/admin/textdoc/contentloop/tag/loop.jsp", 
	        		request, response) ;
		} catch (ServletException e) {
			throw new JspException(e);
		}    	
    	
    	pageContext.getOut().write(loopViewFragment);		
	}
	
	private void doTagInUserMode(PageContext pageContext, ContentLoop loop) throws JspException, IOException {
    	for (Content content: loop.getContents()) {
    		int sequenceIndex = content.getSequenceIndex();
        	int index = baseIndex + (sequenceIndex * STEP);
        	
        	pageContext.setAttribute(indexVar, index);

    		getJspBody().invoke(null);
    	}
    	
    	pageContext.removeAttribute(indexVar);		
	}	

    public void setNo(int no) {
        this.loopIndex = no ;
    }

    public int getNo() {
        return loopIndex ;
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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
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

	public String getLabel() {
		return label;
	}	
}