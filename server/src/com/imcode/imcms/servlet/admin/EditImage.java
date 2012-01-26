package com.imcode.imcms.servlet.admin;

import imcode.server.document.textdocument.ImageDomainObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import com.imcode.imcms.flow.DispatchCommand;
import org.apache.commons.lang.math.NumberUtils;

public class EditImage extends HttpServlet {

    private static final String REQUEST_ATTRIBUTE__IMAGE = EditImage.class+".image";
    private static final String REQUEST_ATTRIBUTE__META_ID = EditImage.class + ".metaId";
    public static final String REQUEST_PARAMETER__RETURN = "return";
    public static final String REQUEST_PARAMETER__META_ID = "meta_id";

    public void doGet(final HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        final int metaId = NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__META_ID));
        final String returnPath = request.getParameter(REQUEST_PARAMETER__RETURN);
        final ImageRetrievalCommand imageCommand = new ImageRetrievalCommand();
        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                ImageDomainObject image = imageCommand.getImage();

                request.setAttribute(REQUEST_ATTRIBUTE__IMAGE, image);
                if (metaId > 0) {
                    request.setAttribute(REQUEST_ATTRIBUTE__META_ID, metaId);
                }
                
                request.getRequestDispatcher(returnPath).forward(request, response);
            }
        };
        ImageEditPage imageEditPage = new ImageEditPage(null, null, null, null, "", getServletContext(), imageCommand, returnCommand, false, 0, 0, 0, 0, null);
        imageEditPage.updateFromRequest(request);
        imageEditPage.forward(request, response);
    }

    public static String linkTo(HttpServletRequest request, String returnPath) {
        return request.getContextPath()+"/servlet/EditImage?"+REQUEST_PARAMETER__RETURN+"="+returnPath ;
    }
    
    public static ImageDomainObject getImage(HttpServletRequest request) {
        return (ImageDomainObject) request.getAttribute(REQUEST_ATTRIBUTE__IMAGE);
    }
    
    public static Integer getMetaId(HttpServletRequest request) {
        return (Integer) request.getAttribute(REQUEST_ATTRIBUTE__META_ID);
    }

    private static class ImageRetrievalCommand implements Handler<ImageDomainObject> {

        private ImageDomainObject image;

        public ImageDomainObject getImage() {
            return image;
        }

        public void handle(ImageDomainObject image) {
            this.image = image;
        }
    }
}
