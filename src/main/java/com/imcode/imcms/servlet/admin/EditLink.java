package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.DispatchCommand;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditLink extends HttpServlet {

    public final static String[] OTHER_PARAMETERS = {
            "id", "name", "charset", "coords", "hreflang", "rel", "rev", "shape", "accesskey", "dir", "lang", "tabindex", "xml:lang",
            "onclick", "ondblclick", "onblur", "onfocus", "onmouseover", "onmouseout", "onmouseup", "onmousedown", "onmousemove", "onkeyup", "onkeydown", "onkeypress"
    };
    private static final String REQUEST_ATTRIBUTE__DOCUMENT_ID = EditLink.class.getName() + ".documentId";

    public static String linkTo(HttpServletRequest request, String returnPath) {
        return request.getContextPath() + "/servlet/EditLink?" + Parameter.RETURN.toString() + "=" + returnPath;
    }

    public static Link getLink(HttpServletRequest request) {
        return (Link) request.getAttribute(REQUEST_ATTRIBUTE__DOCUMENT_ID);
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        final String returnPath = getStringParameter(request, Parameter.RETURN);

        Link link = new LinkEditPage.SimpleLink(getIntParameter(request, Parameter.TYPE, 1),
                getStringParameter(request, Parameter.HREF),
                getStringParameter(request, Parameter.TITLE),
                getStringParameter(request, Parameter.TARGET),
                getStringParameter(request, Parameter.CLASS),
                getStringParameter(request, Parameter.STYLE),
                getStringParameter(request, Parameter.OTHER));

        final LinkRetrievalCommand linkRetrievalCommand = new LinkRetrievalCommand();
        LinkEditPage linkEditPage = new LinkEditPage((DispatchCommand) (request1, response1) -> {
            request1.setAttribute(REQUEST_ATTRIBUTE__DOCUMENT_ID, linkRetrievalCommand.getLink());
            request1.getRequestDispatcher(returnPath).forward(request1, response1);
        }, linkRetrievalCommand);
        linkEditPage.setLink(link);
        linkEditPage.forward(request, response);
    }

    private String getStringParameter(HttpServletRequest request, Parameter parameter) {
        return StringUtils.defaultString(request.getParameter(parameter.toString()));
    }

    private int getIntParameter(HttpServletRequest request, Parameter parameter, int defVal) {
        try {
            if (null != request.getParameter(parameter.toString())) {
                return Integer.parseInt(request.getParameter(parameter.toString()));
            }
        } catch (Exception e) {
        }
        return defVal;
    }

    public enum Parameter {
        RETURN,
        TYPE,
        HREF,
        TITLE,
        TARGET,
        USE_TARGET,
        CLASS,
        STYLE,
        OTHER,
    }

    public interface Link {

        int getType();

        String getHref();

        String getTitle();

        String getTarget();

        String getCssClass();

        String getCssStyle();

        String getOtherParams();

    }

    private static class LinkRetrievalCommand implements Handler<Link> {

        private Link link;

        public Link getLink() {
            return link;
        }

        public void handle(Link link) {
            this.link = link;
        }
    }
}
