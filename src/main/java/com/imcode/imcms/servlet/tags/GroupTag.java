package com.imcode.imcms.servlet.tags;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class GroupTag extends BodyTagSupport {
	
	class GroupData {
		private int itemIndex = 0;
		
		private int itemsCount = 0;
		
		private Map<Integer, List<SimpleImcmsTag>> itemsMap = new LinkedHashMap<Integer, List<SimpleImcmsTag>>();
		
		public void addGroupItem(TextTag tag) {
	    	List<SimpleImcmsTag> items = itemsMap.get(itemIndex);
	    	
	    	if (items == null) {
	    		items = new LinkedList<SimpleImcmsTag>();
	    		itemsMap.put(itemIndex, items);
	    	}
	    	
	    	items.add(tag);			
		}
		
		public int getItemIndex() {
			return itemIndex;
		}
		
		public int incItemIndex() {
			return itemIndex++;
		}

		public int getItemsCount() {
			return itemsCount;
		}

		public void setItemsCount(int itemCount) {
			this.itemsCount = itemCount;
		}
		
	}
	
    private int no;    
        
    private Properties attributes = new Properties();
    
    private String label;  
    
    public void addGroupItem(TextTag tag) {
		GroupData groupData = (GroupData)pageContext.getAttribute("groupData");
		
		groupData.addGroupItem(tag);
    }
    
    
    public int doAfterBody() throws JspException {
    	GroupData groupData = (GroupData)pageContext.getAttribute("groupData");
    	
    	return groupData.incItemIndex() < groupData.getItemsCount()
    	  ? EVAL_BODY_AGAIN
    	  : super.doAfterBody();
    }
    
    

	public int doStartTag() throws JspException {
    	// Prototype try-catch block
    	try {
			String scriptsRoot = pageContext.getServletContext().getRealPath("WEB-INF") + "/groovy";	
			String[] roots = new String[] { scriptsRoot };
			GroovyScriptEngine gse = new GroovyScriptEngine(roots);
			Binding binding = new Binding();
			binding.setVariable("groupTag", this);
			gse.run("GroupTag.groovy", binding);
			int itemsCount = (Integer)binding.getVariable("itemsCount");
			
			GroupData groupData = new GroupData();		
			pageContext.setAttribute("groupData", groupData);

			
			groupData.setItemsCount(itemsCount);
        } catch (Exception e) {
        	throw new JspException(e);
        }            
										
		return super.doStartTag();
	}
	
	public int doEndTag() throws JspException {
		String content = getBodyContent().getString();
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		UserDomainObject user = Utility.getLoggedOnUser(request);
		
		ParserParameters parserParameters = ParserParameters.fromRequest(request);		

		try {
			if (parserParameters.isGroupMode()) {
		        request.setAttribute("content", content);
		        request.setAttribute("label", label);
		        request.setAttribute("groupNo", no);
		        content = Utility.getContents("/imcms/"+user.getLanguageIso639_2()+"/jsp/docadmin/text/edit_group.jsp",
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
}
