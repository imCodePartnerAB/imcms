package com.imcode.imcms.addon.imagearchive.tag;

import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Locale;

/*
Prints out iframe with /web/archive as it's src attribute.
Intercepts locale set in imcms and sets for use in image archive.
Appends text found in body to image archive <head> tag(the main purpose for this is change of image archive looks),
for example '<link href="${contextPath}/css/tag_image_archive.css.jsp" rel="stylesheet" type="text/css"/>'
Note: Although there are full window previews of images and modal dialogs, they will work when used inside iframe too(will spread onto parent window).
 */
public class ImageArchiveTag extends BodyTagSupport {
    public static final String CSS_OVERRIDES_FROM_IMAGE_ARCHIVE_TAG = "CSS_OVERRIDES_FROM_IMAGE_ARCHIVE_TAG";
    /*
    Used to redirect to the last page visited in image archive after switching languages with external(imcms) language switch(it reloads page)
     */
    public static final String IMAGE_ARCHIVE_LAST_VISITED_PAGE_URL = "imagearchive.visited.page.with.top";

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            UserDomainObject user = Utility.getLoggedOnUser((HttpServletRequest) pageContext.getRequest());
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            ParserParameters parserParameters = ParserParameters.fromRequest(request);
            TextDocumentDomainObject document = (TextDocumentDomainObject) parserParameters.getDocumentRequest().getDocument();
            String archiveUri = getContextPath() + "/web/archive";
            /* set by top.jsp, which is used only on major pages(aka not previews and overlays) to redirect to the last
            * visited page inside iframe when switching languages using imcms lang switch(aka meta-id?imcms.document.language=lang)
            * */
            String redirectTo = (String)request.getSession().getAttribute(IMAGE_ARCHIVE_LAST_VISITED_PAGE_URL);
            boolean toTheSearchPage = request.getParameter("toArchiveSearchPage") != null;
            if(redirectTo != null && !toTheSearchPage) {
                archiveUri = redirectTo;
            }

            String currentLocale = Imcms.getUser().getDocGetterCallback().getParams().language().getCode();
            request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale(currentLocale));
            if (user.canAccess(document)) {
                String iframe = "<iframe src='" + archiveUri +"' ";
                if (getStyleClass() != null && !"".equals(getStyleClass())) {
                    iframe += "class='" + styleClass + "' ";
                }
                iframe += "seamless frameBorder='0' scrolling='no' id='imageArchive'></iframe>";
                out.print(iframe);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new JspException(e);
        }

        return EVAL_BODY_BUFFERED;
    }

    /* Sets the overriding css attribute in session that's appended by javascript in header.jsp when image archive is
     * inside iframe */
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

    /* Class name(s) set for the iframe produces */
    public String getStyleClass(){
        return this.styleClass;
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
}
