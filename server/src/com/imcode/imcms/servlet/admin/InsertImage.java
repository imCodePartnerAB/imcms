package com.imcode.imcms.servlet.admin;

import imcode.server.document.textdocument.ImageDomainObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.imcms.flow.DispatchCommand;

public class InsertImage extends HttpServlet {

    private static final String REQUEST_ATTRIBUTE__IMAGE = InsertImage.class+".image";
    private static final String IMAGE_RETURN_PATH = "/imcms/xinha/plugins/ImcmsIntegration/return_image.jsp";

    public void doGet(final HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        final ImageRetrievalCommand imageCommand = new ImageRetrievalCommand();
        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                request.setAttribute(REQUEST_ATTRIBUTE__IMAGE, imageCommand.getImage());
                request.getRequestDispatcher(IMAGE_RETURN_PATH).forward(request, response);
            }
        };
        ImageEditPage imageEditPage = new ImageEditPage(null, null, new LocalizedMessage(""), "", getServletContext(), imageCommand, returnCommand);
        imageEditPage.updateFromRequest(request);
        imageEditPage.forward(request, response);
    }

    public static ImageDomainObject getImage(HttpServletRequest request) {
        return (ImageDomainObject) request.getAttribute(REQUEST_ATTRIBUTE__IMAGE);
    }

    private static class ImageRetrievalCommand implements ImageEditPage.ImageCommand {

        private ImageDomainObject image;

        public void handleImage(ImageDomainObject image) {
            this.image = image;
        }

        public ImageDomainObject getImage() {
            return image;
        }

    }
}
