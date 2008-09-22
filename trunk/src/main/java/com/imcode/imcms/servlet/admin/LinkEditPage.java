package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.DocumentFinder;

public class LinkEditPage extends OkCancelPage {

    private EditLink.Link link;
    private final Handler<EditLink.Link> linkRetrievalCommand;
    private boolean targetEditable = true;

    protected LinkEditPage(DispatchCommand returnCommand, Handler<EditLink.Link> linkRetrievalCommand) {
        super(returnCommand, returnCommand);
        this.linkRetrievalCommand = linkRetrievalCommand;
    }

    protected void dispatchOk(HttpServletRequest request,
                              HttpServletResponse response) throws IOException, ServletException {
        linkRetrievalCommand.handle(link);
        super.dispatchOk(request, response);
    }

    public EditLink.Link getLink() {
        return link;
    }

    public void setLink(EditLink.Link link) {
        this.link = link;
    }

    public boolean isTargetEditable() {
        return targetEditable;
    }

    public void setTargetEditable(boolean targetEditable) {
        this.targetEditable = targetEditable;
    }

    public enum Parameter {
        HREF,
        TITLE,
        TARGET,
        SEARCH
    }

    protected void dispatchOther(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
        if (null != request.getParameter(Parameter.SEARCH.toString())) {
            DocumentFinder documentFinder = new DocumentFinder();

            final DocumentIdRetrievalCommand documentRetrievalCommand = new DocumentIdRetrievalCommand();
            DispatchCommand returnCommand = new DispatchCommand() {
                public void dispatch(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException, ServletException {
                    Integer documentId = documentRetrievalCommand.getDocumentId();
                    if (null != documentId) {
												DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper() ;
	                      DocumentDomainObject doc = documentMapper.getDocument(documentId) ;
                        setLink(new SimpleLink(request.getContextPath()+"/"+doc.getName(), link.getTitle(), link.getTarget()));
                    }
                    forward(request, response);
                }
            };
            documentFinder.setCancelCommand(returnCommand);
            documentFinder.setSelectDocumentCommand(documentRetrievalCommand);
            documentFinder.forward(request, response);
        }
    }

    protected void updateFromRequest(HttpServletRequest request) {
        setLink(getLinkFromRequest(request)) ;
    }

    private EditLink.Link getLinkFromRequest(HttpServletRequest request) {
        final String href = StringUtils.defaultString(request.getParameter(Parameter.HREF.toString()));
        final String title = StringUtils.defaultString(request.getParameter(Parameter.TITLE.toString()));
        final String target = EditDocumentInformationPageFlow.getTargetFromRequest(request, Parameter.TARGET.toString());
        return new SimpleLink(href, title, isTargetEditable() ? target : link.getTarget());
    }

    public String getPath(HttpServletRequest request) {
        return "/WEB-INF/imcms/jsp/edit_link.jsp" ;
    }

    static class SimpleLink implements EditLink.Link {

        private final String href;
        private final String title;
        private final String target;

        public SimpleLink(String href, String title, String target) {
            this.href = href;
            this.title = title;
            this.target = target;
        }

        public String getHref() {
            return href;
        }

        public String getTitle() {
            return title;
        }

        public String getTarget() {
            return target;
        }
    }

    private static class DocumentIdRetrievalCommand implements Handler<Integer> {

        private Integer documentId;

        public void handle(Integer e) {
            this.documentId = e;
        }

        public Integer getDocumentId() {
            return documentId;
        }
    }

}
