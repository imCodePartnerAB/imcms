package com.imcode.imcms.addon.imagearchive.tag;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;


public class ImageArchiveTag extends BodyTagSupport {
    public static final String CSS_OVERRIDES_FROM_IMAGE_ARCHIVE_TAG = "CSS_OVERRIDES_FROM_IMAGE_ARCHIVE_TAG";

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
            TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();

            if (user.canEdit(document)) {
                String iframe = "<iframe src='" + getContextPath() + "/web/archive' ";
                if (styleClass != null && !"".equals(styleClass)) {
                    iframe += "class='" + styleClass + "' ";
                } else {
                    iframe += defaultStyle;
                }
                iframe += "seamless frameBorder='0' scrolling='no'></iframe>";
                out.print(iframe);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new JspException(e);
        }

        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = request.getSession();
        if(getBodyContent() != null) {
            String body = getBodyContent().getString().trim();
            body = body.replace("\"", "\\\"");
            body = body.replace("'", "\'");
            session.setAttribute(CSS_OVERRIDES_FROM_IMAGE_ARCHIVE_TAG, body);
        }

        return SKIP_BODY;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    protected String getContextPath() {
        if (pageContext.getRequest() instanceof HttpServletRequest) {
            return ((HttpServletRequest) pageContext.getRequest()).getContextPath();
        } else {
            return null;
        }
    }

    protected String styleClass;
    private final String defaultStyle = "width='100%' height='800px' style='border:none;' ";
}
