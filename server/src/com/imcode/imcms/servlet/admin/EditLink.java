package com.imcode.imcms.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.imcode.imcms.flow.DispatchCommand;

public class EditLink extends HttpServlet {

    private static final String REQUEST_ATTRIBUTE__DOCUMENT_ID = EditLink.class.getName()+".documentId";
    public enum Parameter {
        RETURN,
        HREF,
        TITLE,
        TARGET,
        USE_TARGET,
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        final String returnPath = getStringParameter(request, Parameter.RETURN) ;

        Link link = new LinkEditPage.SimpleLink(getStringParameter(request, Parameter.HREF),
                                                getStringParameter(request, Parameter.TITLE),
                                                getStringParameter(request, Parameter.TARGET)) ;
        final LinkRetrievalCommand linkRetrievalCommand = new LinkRetrievalCommand();
        LinkEditPage linkEditPage = new LinkEditPage(new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                request.setAttribute(REQUEST_ATTRIBUTE__DOCUMENT_ID, linkRetrievalCommand.getLink());
                request.getRequestDispatcher(returnPath).forward(request, response);
            }
        }, linkRetrievalCommand);
        linkEditPage.setLink(link);
        linkEditPage.forward(request, response);
    }

    private String getStringParameter(HttpServletRequest request, Parameter parameter) {
        return StringUtils.defaultString(request.getParameter(parameter.toString()));
    }

    public static String linkTo(HttpServletRequest request, String returnPath) {
        return request.getContextPath()+"/servlet/EditLink?"+Parameter.RETURN+"="+returnPath ;
    }

    public static Link getLink(HttpServletRequest request) {
        return (Link) request.getAttribute(REQUEST_ATTRIBUTE__DOCUMENT_ID);
    }

    public interface Link {

        String getHref() ;

        String getTitle() ;

        String getTarget() ;

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
