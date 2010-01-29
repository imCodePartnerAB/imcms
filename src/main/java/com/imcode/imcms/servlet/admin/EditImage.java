package com.imcode.imcms.servlet.admin;

import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.Imcms;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.flow.DispatchCommand;

/**
 * Used to edit/insert image in (Xina) editor. 
 */
public class EditImage extends HttpServlet {

    private static final String REQUEST_ATTRIBUTE__IMAGE = EditImage.class+".image";
    public static final String REQUEST_PARAMETER__RETURN = "return";

    public void doGet(final HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        final String returnPath = request.getParameter(REQUEST_PARAMETER__RETURN);
        final ImageRetrievalCommand imageCommand = new ImageRetrievalCommand();
        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                request.setAttribute(REQUEST_ATTRIBUTE__IMAGE, imageCommand.getImage());
                request.getRequestDispatcher(returnPath).forward(request, response);
            }
        };
        
        // Create edited image for current language.
        ImageDomainObject image = new ImageDomainObject();
        image.setLanguage(Imcms.getDocumentRequest().getLanguage());
        
        ImageEditPage imageEditPage = new ImageEditPage(null, null, null, "", getServletContext(), imageCommand, returnCommand, false);
        
        // Page should contain at least one image to edit.
        imageEditPage.getImages().add(image);
        
        imageEditPage.updateFromRequest(request);
        imageEditPage.forward(request, response);
    }

    public static String linkTo(HttpServletRequest request, String returnPath) {
        return request.getContextPath()+"/servlet/EditImage?"+REQUEST_PARAMETER__RETURN+"="+returnPath ;
    }
    
    public static ImageDomainObject getImage(HttpServletRequest request) {
        return (ImageDomainObject) request.getAttribute(REQUEST_ATTRIBUTE__IMAGE);
    }

    /**
     * This Command to retrieve image to (Xina) editor.  
     */
    private static class ImageRetrievalCommand implements Handler<List<ImageDomainObject>> {

        private List<ImageDomainObject> images;

        public ImageDomainObject getImage() {
            return images.get(0);
        }

        public void handle(List<ImageDomainObject> images) {
            this.images = images;
        }
    }
}
