package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.mapping.DocRef;
import com.imcode.imcms.mapping.TextDocumentImageWrapper;
import imcode.server.document.textdocument.ImageDomainObject;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.flow.DispatchCommand;
import imcode.util.ImcmsImageUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;

/**
 * Used to edit/insert image in (Xina) editor. 
 */
public class EditImage extends HttpServlet {

    private static final String REQUEST_ATTRIBUTE__IMAGE = EditImage.class+".image";
    private static final String REQUEST_ATTRIBUTE__META_ID = EditImage.class + ".metaId";
    public static final String REQUEST_PARAMETER__RETURN = "return";
    public static final String REQUEST_PARAMETER__GENFILE = "gen_file";
    public static final String REQUEST_PARAMETER__META_ID = "meta_id";

    public void doGet(final HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        String returnPath = request.getParameter(REQUEST_PARAMETER__RETURN);
        ImageRetrievalCommand imageCommand = new ImageRetrievalCommand();

        int metaId = NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__META_ID));
        ImageDispatchCommand returnCommand = new ImageDispatchCommand(metaId);
        returnCommand.setReturnPath(returnPath);
        returnCommand.setImageCommand(imageCommand);

        // Create edited image for current language.
        ImageDomainObject image = new ImageDomainObject();

        //fixme - provide no, language and optionally loop entry
        ImageEditPage imageEditPage = new ImageEditPage(null, image, null, "", getServletContext(), imageCommand, returnCommand, false, 0, 0);

        // Page should contain at least one image to edit.
        List<TextDocumentImageWrapper> images = new ArrayList<>(1);
        images.add(TextDocumentImageWrapper.of(DocRef.of(metaId, 0, "en"), 0, image));
        imageEditPage.setImages(images);

        imageEditPage.updateFromRequest(request);

        ImageDomainObject editImage = imageEditPage.getImages().get(0).getImage();
        editImage.setGeneratedFilename(request.getParameter(REQUEST_PARAMETER__GENFILE));

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

    /**
     * This Command to retrieve image to (Xina) editor.  
     */
    private static class ImageRetrievalCommand implements Handler<ImageEditResult> {

        private List<TextDocumentImageWrapper> images;

        public ImageDomainObject getImage() {
            return (images != null ? images.get(0).getImage() : null);
        }

        public void handle(ImageEditResult editResult) {
            images = editResult.getEditedImages();
        }
    }

    private static class ImageDispatchCommand implements DispatchCommand {
        private String returnPath;
        private ImageRetrievalCommand imageCommand;
        private int metaId;

        public ImageDispatchCommand(int metaId) {
            this.metaId = metaId;
        }

        public void dispatch(HttpServletRequest request,
                             HttpServletResponse response) throws IOException, ServletException {
            ImageDomainObject editImage = imageCommand.getImage();

            if (editImage != null) {
                editImage.generateFilename();
                ImcmsImageUtils.generateImage(editImage, false);
            }

            request.setAttribute(REQUEST_ATTRIBUTE__IMAGE, editImage);
            if (metaId > 0) {
                request.setAttribute(REQUEST_ATTRIBUTE__META_ID, metaId);
            }
            request.getRequestDispatcher(returnPath).forward(request, response);
        }

        public void setReturnPath(String returnPath) {
            this.returnPath = returnPath;
        }

        public void setImageCommand(ImageRetrievalCommand imageCommand) {
            this.imageCommand = imageCommand;
        }
    }
}
