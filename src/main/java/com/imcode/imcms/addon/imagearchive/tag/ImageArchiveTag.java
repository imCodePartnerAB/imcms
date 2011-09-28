package com.imcode.imcms.addon.imagearchive.tag;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;


public class ImageArchiveTag extends TagSupport {
    public static final String CSS_INCLUDE_OVERRIDES_FILE_NAME = "CSS_IMPORT_OVERRIDES_FROM_IMAGE_ARCHIVE_TAG";

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            HttpSession session = request.getSession();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
            TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();

            if(getCssFileName() != null) {
                session.setAttribute(CSS_INCLUDE_OVERRIDES_FILE_NAME, getCssFileName());
            }

            if (user.canEdit(document)) {
                String iframe = "<iframe src='" + getContextPath() + "/web/archive' ";
                if (getStyleClass() != null && !"".equals(getStyleClass())) {
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

        return SKIP_BODY;
    }

    public String getStyleClass() {
        return styleClass;
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

    public String getCssFileName() {
        return cssFileName;
    }

    public void setCssFileName(String cssFileName) {
        this.cssFileName = cssFileName;
    }

    /* css file name attribute, URI */
    protected String cssFileName;
    /* class name for the iframe */
    protected String styleClass;
    private final String defaultStyle = "width='100%' height='800px' style='border:none;' ";
}
