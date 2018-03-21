package com.imcode.imcms.servlet.tags;

import imcode.server.Imcms;
import imcode.server.parser.ParserParameters;
import imcode.server.parser.TagParser;
import imcode.server.parser.TextDocumentParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Properties;

public abstract class SimpleImcmsTag extends TagSupport {

    protected Properties attributes = new Properties();

    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
            TagParser tagParser = new TagParser(new TextDocumentParser(Imcms.getServices()), parserParameters);
            String content = getContent(tagParser);
            String contentWithPreAndPost = TagParser.addPreAndPost(attributes, content);
            pageContext.getOut().print(contentWithPreAndPost);
        } catch (IOException e) {
            throw new JspException(e);
        } catch (RuntimeException e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    protected abstract String getContent(TagParser tagParser);

    public void setNo(int no) {
        attributes.setProperty("no", "" + no);
    }

    public void setLabel(String label) {
        attributes.setProperty("label", label);
    }

    public void setPre(String pre) {
        attributes.setProperty("pre", pre);
    }

    public void setPost(String post) {
        attributes.setProperty("post", post);
    }
}
