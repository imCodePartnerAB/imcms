package com.imcode.imcms.servlet.tags;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.io.IOException;
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
	}
	
    private int no;
    
    /**
     * Group item count
     */
    private int itemCount = 1;
        
    private Properties attributes = new Properties();
    
    private String label;  
    
    public void addGroupItem(TextTag tag) {
		GroupData groupData = (GroupData)pageContext.getAttribute("groupData");
		
		groupData.addGroupItem(tag);
    }
    
    
    public int doAfterBody() throws JspException {
    	GroupData groupData = (GroupData)pageContext.getAttribute("groupData");
    	
    	return groupData.incItemIndex() < itemCount 
    	  ? EVAL_BODY_AGAIN
    	  : super.doAfterBody();
    }
    
    

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		ParserParameters parserParameters = ParserParameters.fromRequest(request);
		
		if (parserParameters.isGroupMode()) {
			String cmd = request.getParameter("cmd");
			
			if (cmd != null) {
				if (cmd.equals("addFirst")) {
					
				} else if (cmd.equals("addLast")) {
					
				}
			}
		}
		
		GroupData groupData = new GroupData();		
		pageContext.setAttribute("groupData", groupData);
		
          HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		
        try {
		    //request.getSession().getServletContext().getRequestDispatcher("/WEB-INF/groovy/GroupTag.groovy").include(request, response);
        	if (true) {}
        } catch (Exception e) {
        	throw new JspException(e);
        }
		
		return EVAL_BODY_BUFFERED;
	}
	
	public int doEndTag() throws JspException {
		String content = getBodyContent().getString();
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		UserDomainObject user = Utility.getLoggedOnUser(request);
		
		ParserParameters parserParameters = ParserParameters.fromRequest(request);		
				
		//if (editing groups -> save group to db)
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
	
	/*
    public int doEndTag() throws JspException {
        try {
            String bodyContentString = null != getBodyContent() ? getBodyContent().getString() : "";
            bodyContent = null ;
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
                        
        	// Prototype try-catch block
        	try {
    			String scriptsRoot = pageContext.getServletContext().getRealPath("WEB-INF") + "/groovy";	
    			String[] roots = new String[] { scriptsRoot };
    			GroovyScriptEngine gse = new GroovyScriptEngine(roots);
    			Binding binding = new Binding();
    			binding.setVariable("groupTag", this);
    			gse.run("GroupTag.groovy", binding);
    			
    			bodyContentString = (String)binding.getVariable("content");
            } catch (Exception e) {
            	throw new JspException(e);
            }            
            
            
            //bodyContentString = MenuParser.addMenuAdmin(no,
            //                                            parserParameters.isMenuMode(),
            //                                            bodyContentString, menu, request,response,label);
            //

            bodyContentString = TagParser.addPreAndPost(attributes, bodyContentString);
            
            pageContext.getOut().write(bodyContentString);
        } catch ( IOException e ) {
            throw new JspException(e);
//        } catch ( ServletException e ) {
//            throw new JspException(e);
        } catch ( RuntimeException e ) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }	
    */
	
	protected String getContent(TagParser tagParser) {
		return "I love you";
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


	public int getItemCount() {
		return itemCount;
	}


	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}	
}
