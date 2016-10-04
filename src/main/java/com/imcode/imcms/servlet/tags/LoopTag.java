package com.imcode.imcms.servlet.tags;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.servlet.tags.Editor.LoopEditor;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class LoopTag extends BodyTagSupport implements IEditableTag {


    /**
     * Loop number in a TextDocument.
     */
    private volatile int no;

    private volatile Loop loop;

    private volatile Iterator loopIterator;

    private volatile Map.Entry<Integer, Boolean> currentEntry;

    private volatile Properties attributes = new Properties();

    /**
     * Label - common imcms attribute.
     */
    private volatile String label;

    private volatile boolean editMode;

    private volatile HttpServletRequest request;

    private volatile TextDocumentDomainObject document;

    private volatile ParserParameters parserParameters;

    private volatile LoopEditor editor;

    @Override
    public int doStartTag() throws JspException {

        request = (HttpServletRequest) pageContext.getRequest();
        parserParameters = ParserParameters.fromRequest(request);
        document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
        editMode = TagParser.isEditable(attributes, parserParameters.isContentLoopMode());

        loop = document.getLoop(no);

        if (loop == null) {
            loop = Loop.singleEntry();

            document.setLoop(no, loop);
        }
        loopIterator = loop.getEntries().entrySet().iterator();

        if (loopIterator.hasNext()) {
            currentEntry = (Map.Entry<Integer, Boolean>) loopIterator.next();
            return EVAL_BODY_BUFFERED;
        } else {
            currentEntry = null;
            return SKIP_BODY;
        }
    }

    @Override
    public int doAfterBody() throws JspException {
        if (loopIterator.hasNext()) {
            currentEntry = (Map.Entry<Integer, Boolean>) loopIterator.next();
            return EVAL_BODY_AGAIN;
        } else {
            currentEntry = null;
            return SKIP_BODY;
        }
    }

    @Override
    public int doEndTag() throws JspException {
        try {

            String bodyContentString = null != getBodyContent() ? getBodyContent().getString() : "";
            if (TagParser.isEditable(attributes, parserParameters.isMenuMode())) {
                editor = createEditor().setNo(no);
                bodyContentString = editor.wrap(bodyContentString);
            }
            bodyContentString = TagParser.addPreAndPost(attributes, bodyContentString);

            if (editMode) {
                try {
                    request.setAttribute("loopNo", no);
                    request.setAttribute("loop", loop);
                    request.setAttribute("content", bodyContentString);
                    request.setAttribute("document", document);
                    request.setAttribute("label", label);
                    request.setAttribute("flags", parserParameters.getFlags());
                } catch (Exception e) {
                    throw new JspException(e);
                }
            }
            pageContext.getOut().write(bodyContentString);
        } catch (IOException | RuntimeException e) {
            throw new JspException(e);
        }
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
        return LoopEntryRef.of(no, currentEntry.getKey());
    }

    // @Override
    public LoopEditor createEditor() {
        return new LoopEditor();
    }
}



