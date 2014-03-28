package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class LoopTag extends BodyTagSupport {

    private static class LoopIterator {

        private final Iterator<Map.Entry<Integer, Boolean>> entryIterator;
        private Map.Entry<Integer, Boolean> currentEntry;

        LoopIterator(Loop loop) {
            entryIterator = loop.getEntries().entrySet().iterator();
            getNextEntryNo();
        }

        Integer getCurrentEntryNo() {
            return currentEntry == null ? null : currentEntry.getKey();
        }

        Integer getNextEntryNo() {
            currentEntry = entryIterator.hasNext() ? entryIterator.next() : null;

            return getCurrentEntryNo();
        }
    }

    /**
     * Loop number in a TextDocument.
     */
    private int no;

    private Loop loop;

    private LoopIterator loopIterator;

    private Properties attributes = new Properties();

    /**
     * Label - common imcms attribute.
     */
    private String label;

    private boolean editMode;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private TextDocumentDomainObject document;

    private ParserParameters parserParameters;

    private StringBuilder result;

    /**
     * @return
     * @throws JspException
     */
    @Override
    public int doStartTag() throws JspException {
        result = new StringBuilder();
        request = (HttpServletRequest) pageContext.getRequest();
        response = (HttpServletResponse) pageContext.getResponse();
        parserParameters = ParserParameters.fromRequest(request);
        document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
        editMode = parserParameters.isContentLoopMode();

        loop = document.getLoop(no);

        if (loop == null) {
            loop = Loop.singleton();

            document.setLoop(no, loop);
        }

        loopIterator = new LoopIterator(loop);

        return loopIterator.getCurrentEntryNo() == null
                ? SKIP_BODY
                : EVAL_BODY_INCLUDE;
    }

    @Override
    public int doAfterBody() throws JspException {
//        if (editMode) {
//            BodyContent bodyContent = getBodyContent();
//            String viewFragment = bodyContent.getString();
//
//            request.setAttribute("document", document);
//            request.setAttribute("contentLoop", loop);
//            //request.setAttribute("content", currentLoopEntry);
//            request.setAttribute("flags", parserParameters.getFlags());
//            request.setAttribute("viewFragment", viewFragment);
//            request.setAttribute("contentsCount", contentsCount);
//            request.setAttribute("isFirstContent", firstContent);
//            request.setAttribute("isLastContent", lastContent);
//
//            try {
//                viewFragment = Utility.getContents(
//                        "/WEB-INF/admin/textdoc/contentloop/tag/content.jsp",
//                        request, response);
//
//                result.append(viewFragment);
//                bodyContent.clearBody();
//            } catch (Exception e) {
//                throw new JspException(e);
//            }
//        }

        return loopIterator.getNextEntryNo() != null ? EVAL_BODY_AGAIN : SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
//        if (editMode) {
//            try {
//                String viewFragment = result.toString();
//
//                request.setAttribute("contentLoop", loop);
//                request.setAttribute("viewFragment", viewFragment);
//                request.setAttribute("document", document);
//                request.setAttribute("contentLoop", loop);
//                request.setAttribute("flags", parserParameters.getFlags());
//
//                try {
//                    viewFragment = Utility.getContents(
//                            "/WEB-INF/admin/textdoc/contentloop/tag/loop.jsp",
//                            request, response);
//                } catch (Exception e) {
//                    throw new JspException(e);
//                }
//
//                pageContext.getOut().write(viewFragment);
//            } catch (Exception e) {
//                throw new JspException(e);
//            }
//        }

        return EVAL_PAGE;
    }


    public void setNo(int no) {
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public void setMode(String mode) {
        attributes.setProperty("mode", mode);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPre(String pre) {
        attributes.setProperty("pre", pre);
    }

    public void setPost(String post) {
        attributes.setProperty("post", post);
    }

    public Loop getLoop() {
        return loop;
    }

    public void setLoop(Loop loop) {
        this.loop = loop;
    }

    public LoopEntryRef getLoopEntryRef() {
        return LoopEntryRef.of(no, loopIterator.getCurrentEntryNo());
    }
}