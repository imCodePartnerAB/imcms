package com.imcode.imcms.servlet.tags;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.util.Utility;

import java.util.ListIterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;

/**
 * Second version of content loop tag.
 */
public class ContentLoopTag2 extends BodyTagSupport {

    /** Creates content loop with a single content. */
    private static ContentLoop createLoop(Integer metaId, Integer documentVersion, Integer no) {
        ContentLoop loop = new ContentLoop();

        loop.setDocId(metaId);
        loop.setNo(no);
        loop.setDocVersionNo(documentVersion);

        return loop;
    }


	/** Loop number in a TextDocument. */
    private int no;

    /** Loop contentes. */
    private ListIterator<Content> contentsIterator;

    private ContentLoop loop;

    private Content currentContent;

    private int contentsCount;

    private Properties attributes = new Properties();

    /** Label - common imcms attribute. */
    private String label;

    private boolean editMode;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private TextDocumentDomainObject document;

    private ParserParameters parserParameters;

    private StringBuilder result;

    private boolean firstContent;

    private boolean lastContent;

    /**
     * @return
     * @throws JspException
     */
	public int doStartTag() throws JspException {
		result = new StringBuilder();
        request = (HttpServletRequest) pageContext.getRequest();
        response = (HttpServletResponse)pageContext.getResponse();
        parserParameters = ParserParameters.fromRequest(request);
        document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
        editMode = parserParameters.isContentLoopMode();

        loop = document.getContentLoop(no);
        
        if (loop == null) {
        	loop = createLoop(document.getMeta().getId(), document.getVersion().getNo(), no);

           	document.setContentLoop(no, loop);
        }

        currentContent = null;
        contentsCount = loop.getContents().size();
        contentsIterator = loop.getContents().listIterator();

        return !handleNextContent()
                ? SKIP_BODY
                : editMode
                    ? EVAL_BODY_BUFFERED
                    : EVAL_BODY_INCLUDE;
	}

	private boolean handleNextContent() {
        if (!contentsIterator.hasNext()) {
        	//pageContext.removeAttribute(indexVar);
        	return false;
        }

        firstContent = !contentsIterator.hasPrevious();
        currentContent = contentsIterator.next();
        lastContent = !contentsIterator.hasNext();

    	return true;
	}


    public int doAfterBody() throws JspException {
    	if (editMode) {
    		BodyContent bodyContent = getBodyContent();
    		String viewFragment = bodyContent.getString();

    		request.setAttribute("document", document);
    		request.setAttribute("contentLoop", loop);
    		request.setAttribute("content", currentContent);
    		request.setAttribute("flags", parserParameters.getFlags());
    		request.setAttribute("viewFragment", viewFragment);
    		request.setAttribute("contentsCount", contentsCount);
    		request.setAttribute("isFirstContent", firstContent);
    		request.setAttribute("isLastContent", lastContent);

    		try {
    			viewFragment = Utility.getContents(
    		        		"/WEB-INF/admin/textdoc/contentloop/tag/content.jsp",
    		                request, response);

    			result.append(viewFragment);
    			bodyContent.clearBody();
    		} catch (Exception e) {
    			throw new JspException(e);
    		}
    	}

    	return handleNextContent() ? EVAL_BODY_AGAIN : SKIP_BODY;
    }


	public int doEndTag() throws JspException {
		if (editMode) {
			try {
	    		String viewFragment = result.toString();

	        	request.setAttribute("contentLoop", loop);
	        	request.setAttribute("viewFragment", viewFragment);
	    		request.setAttribute("document", document);
	    		request.setAttribute("contentLoop", loop);
	    		request.setAttribute("flags", parserParameters.getFlags());

	    		try {
	    			viewFragment = Utility.getContents(
	    		        		"/WEB-INF/admin/textdoc/contentloop/tag/loop.jsp",
	    		                request, response);
	    		} catch (Exception e) {
	    			throw new JspException(e);
	    		}

				pageContext.getOut().write(viewFragment);
			} catch (Exception e) {
				throw new JspException(e);
			}
		}

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


    public ContentLoop getLoop() {
        return loop;
    }

    public void setLoop(ContentLoop loop) {
        this.loop = loop;
    }

    /**
     * @return current content.
     */
    public Content getCurrentContent() {
        return currentContent;
    }
}