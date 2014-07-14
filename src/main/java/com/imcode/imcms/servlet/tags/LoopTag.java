package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class LoopTag extends SimpleTagSupport {

    /**
     * Loop no in a TextDocument.
     */
    private int no;

    /**
     * Label - common imcms attribute.
     */
    private String label;

    private Properties attributes = new Properties();

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        ParserParameters parserParameters = ParserParameters.fromRequest(request);
        TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
        Loop loop = document.getLoop(no);
        UserDomainObject user = Utility.getLoggedOnUser(request);

        boolean editMode = parserParameters.isContentLoopMode();
        StringWriter writer = new StringWriter();

        if (loop == null) {
            loop = Loop.singleEntry();

            document.setLoop(no, loop);
        }

        for (Map.Entry<Integer, Boolean> entry : loop.getEntries().entrySet()) {
            int entryNo = entry.getKey();
            boolean enabled = entry.getValue();

            pageContext.setAttribute("loopEntryRef", LoopEntryRef.of(no, entryNo));

            if (editMode) {
                getJspBody().invoke(writer);
            } else if (enabled) {
                getJspBody().invoke(null);
            }
        }

        if (editMode) {
            try {
                String content = writer.toString();

                request.setAttribute("loopNo", no);
                request.setAttribute("loop", loop);
                request.setAttribute("content", content);
                request.setAttribute("document", document);
                request.setAttribute("flags", parserParameters.getFlags());

                try {

                    content = Utility.getContents("/imcms/" + user.getLanguageIso639_2()
                            + "/jsp/docadmin/text/edit_loop.jsp", request, response);
                } catch (Exception e) {
                    throw new JspException(e);
                }

                pageContext.getOut().write(content);
            } catch (Exception e) {
                throw new JspException(e);
            }
        }
    }

//    @Override
//    public int doAfterBody() throws JspException {
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
//    }



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

    // todo: return as page cts param
    public LoopEntryRef getLoopEntryRef() {
        return null;//LoopEntryRef.of(no, loopIterator.getCurrentEntryNo());
    }
}