package com.imcode.imcms.servlet.tags;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ContentTag extends BodyTagSupport {
	
	public final static int STEP = 100000;
	
	/*
	class GroupData {
		private int iterationIndex = 0;
		
		private int iterationCount = 0;
		
		private List<Integer> orderedIndexes;
		
		private Map<Integer, List<SimpleImcmsTag>> itemsMap = new LinkedHashMap<Integer, List<SimpleImcmsTag>>();
				
		public void addGroupItem(SimpleImcmsTag tag) {
	    	List<SimpleImcmsTag> items = itemsMap.get(iterationIndex);
	    	
	    	if (items == null) {
	    		items = new LinkedList<SimpleImcmsTag>();
	    		itemsMap.put(iterationIndex, items);
	    	}
	    	
	    	items.add(tag);			
		}
		
		public int getIterationIndex() {
			return orderedIndexes.get(iterationIndex);
		}
		
		public int incIterationIndex() {
			return ++iterationIndex;
		}

		public int getIterationCount() {
			return iterationCount;
		}

		public void setOrderedIndexes(List<Integer> orderedIndexes) {
			this.orderedIndexes = orderedIndexes;
			this.iterationCount = orderedIndexes.size();
		}		
	}
	*/
	
    private int no;  
    
    private String indexVar;
    
    private int baseIndex;
        
    private Properties attributes = new Properties();
    
    private String label;  
    
    public void addGroupItem(SimpleImcmsTag tag) {
    	/*
		GroupData groupData = (GroupData)pageContext.getAttribute("groupData");
		
		groupData.addGroupItem(tag);
		*/
    }
    
    
    public int doAfterBody() throws JspException {
    	Iterator<Integer> indexIterator = (Iterator<Integer>)pageContext.getAttribute("indexIterator");
    	
    	//GroupData groupData = (GroupData)pageContext.getAttribute("groupData");
    	//int index = groupData.incIterationIndex(); 
    	// getLoopIndex
    	
    	if (!indexIterator.hasNext()) {
    		pageContext.removeAttribute(indexVar);
    		
    		return super.doAfterBody();
    	}
    	
    	int index = baseIndex + (indexIterator.next() * STEP);
    		
    	pageContext.setAttribute(indexVar, index);
    		
    	return EVAL_BODY_AGAIN;
    	
    	//pageContext.setAttribute(indexVar, baseIndex + index + STEP);
    	
    	/*
    	return index < groupData.getIterationCount()
    	  ? EVAL_BODY_AGAIN
    	  : super.doAfterBody();
    	*/
    }
    
    

	public int doStartTag() throws JspException {
    	// Prototype try-catch block
    	try {
			String scriptsRoot = pageContext.getServletContext().getRealPath("WEB-INF") + "/groovy";	
			String[] roots = new String[] { scriptsRoot };
			GroovyScriptEngine gse = new GroovyScriptEngine(roots);
			Binding binding = new Binding();
			binding.setVariable("contentTag", this);			
			gse.run("ContentTag.groovy", binding);
			
			List<Integer> orderedIndexes = (List<Integer>)binding.getVariable("ordredLoopIndexes");
			
			Iterator<Integer> indexIterator = orderedIndexes.iterator(); 
			
			if (!indexIterator.hasNext()) {
				return SKIP_BODY;
			} 
			
			pageContext.setAttribute("indexIterator", indexIterator);
						
	    	int index = baseIndex + (indexIterator.next() * STEP);
    		
	    	pageContext.setAttribute(indexVar, index);
	    	
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
