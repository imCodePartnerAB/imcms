package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.imcms.util.l10n.LocalizedMessageFormat;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.ImcmsConstants;
import imcode.server.document.textdocument.*;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.user.UserDomainObject;
import imcode.util.ImcmsImageUtils;
import imcode.util.Utility;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.ShouldNotBeThrownException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChangeImage extends HttpServlet {

    public static final String REQUEST_PARAMETER__IMAGE_INDEX = "img";
    public static final String REQUEST_PARAMETER__LABEL = "label";
    public static final String REQUEST_PARAMETER__WIDTH = "width";
    public static final String REQUEST_PARAMETER__HEIGHT = "height";
    public static final String REQUEST_PARAMETER__MAX_WIDTH = "max-width";
    public static final String REQUEST_PARAMETER__MAX_HEIGHT = "max-height";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final ImcmsServices imcref = Imcms.getServices();
        final DocumentMapper documentMapper = imcref.getDocumentMapper();
        final TextDocumentDomainObject document = (TextDocumentDomainObject) documentMapper.getDocument(Integer.parseInt(request.getParameter("meta_id")));
        final int imageIndex = Integer.parseInt(request.getParameter(REQUEST_PARAMETER__IMAGE_INDEX));
        final ImageDomainObject image = document.getImage(imageIndex);
        final UserDomainObject user = Utility.getLoggedOnUser(request);
        int forcedWidth = NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__WIDTH));
        int forcedHeight = NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__HEIGHT));
        forcedWidth = Math.max(forcedWidth, 0);
        forcedHeight = Math.max(forcedHeight, 0);
        
        int maxWidth = NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__MAX_WIDTH));
        int maxHeight = NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__MAX_HEIGHT));
        maxWidth = Math.max(maxWidth, 0);
        maxHeight = Math.max(maxHeight, 0);

        // Check if user has image rights
        if ( !ImageEditPage.userHasImagePermissionsOnDocument(user, document) ) {
            Utility.redirectToStartDocument(request, response);
            return;
        }

        final String returnURL = request.getParameter(ImcmsConstants.REQUEST_PARAM__RETURN_URL);
        DispatchCommand returnCommand =
            new DispatchCommand() {
                public void dispatch(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
                    String redirectURL = returnURL == null
                            ? "AdminDoc?meta_id=" + document.getId() + "&flags="
                                + ImcmsConstants.DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_IMAGES
                            : returnURL;

                    response.sendRedirect(redirectURL);
                }
            };

        Handler<ImageDomainObject> imageCommand = new Handler<ImageDomainObject>() {
            public void handle(ImageDomainObject image) {
                image.generateFilename();
                ImcmsImageUtils.generateImage(image, false);

                ImcmsServices services = Imcms.getServices();
                document.setImage(imageIndex, image);
                try {
                    services.getDocumentMapper().saveDocument(document, user);
                } catch ( NoPermissionToEditDocumentException e ) {
                    throw new ShouldHaveCheckedPermissionsEarlierException(e);
                } catch ( NoPermissionToAddDocumentToMenuException e ) {
                    throw new ConcurrentDocumentModificationException(e);
                } catch ( DocumentSaveException e ) {
                    throw new ShouldNotBeThrownException(e);
                }
                services.updateMainLog("ImageRef " + imageIndex + " =" + image.getUrlPathRelativeToContextPath() +
                                       " in  " + "[" + document.getId() + "] modified by user: [" +
                                       user.getFullName() + "]");

            }

        };
        LocalizedMessage heading = new LocalizedMessageFormat("image/edit_image_on_page", String.valueOf(imageIndex), String.valueOf(document.getId()));
        ImageEditPage imageEditPage = new ImageEditPage(document, image, imageIndex, heading, 
        		StringUtils.defaultString(request.getParameter(REQUEST_PARAMETER__LABEL)), getServletContext(), imageCommand, 
        		returnCommand, true, forcedWidth, forcedHeight, maxWidth, maxHeight, returnURL);
        imageEditPage.forward(request, response);

    }

}
