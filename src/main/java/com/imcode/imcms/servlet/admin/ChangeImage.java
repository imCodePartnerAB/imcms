package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.TextDocumentContentLoader;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.mapping.container.TextDocImagesContainer;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.ShouldNotBeThrownException;
import imcode.util.Utility;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.imcms.util.l10n.LocalizedMessageFormat;
import imcode.util.ImcmsImageUtils;
import org.apache.commons.lang3.math.NumberUtils;


/**
 * Change image withe the same 'no' for all available languages.
 */
public class ChangeImage extends HttpServlet {

    public static final String REQUEST_PARAMETER__IMAGE_INDEX = "img";
    public static final String REQUEST_PARAMETER__LABEL = "label";
    public static final String REQUEST_PARAMETER__WIDTH = "width";
    public static final String REQUEST_PARAMETER__HEIGHT = "height";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final ImcmsServices imcref = Imcms.getServices();
        final UserDomainObject user = Utility.getLoggedOnUser(request);
        final DocumentMapper documentMapper = imcref.getDocumentMapper();
        final Integer documentId = Integer.parseInt(request.getParameter("meta_id"));

        String loopEntryRefStr = request.getParameter("loop_entry_ref");
        LoopEntryRef loopEntryRef = LoopEntryRef.parse(loopEntryRefStr).orElse(null);

        final TextDocumentDomainObject document = documentMapper.getDocument(documentId);

        final int imageIndex = Integer.parseInt(request.getParameter(REQUEST_PARAMETER__IMAGE_INDEX));
        int forcedWidth = NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__WIDTH));
        int forcedHeight = NumberUtils.toInt(request.getParameter(REQUEST_PARAMETER__HEIGHT));
        forcedWidth = Math.max(forcedWidth, 0);
        forcedHeight = Math.max(forcedHeight, 0);

        /**
         * Image DTO. Holds generic properties such as size and border. 
         */
        final ImageDomainObject defaultImage = loopEntryRef == null
                ? document.getImage(imageIndex)
                : document.getImage(TextDocumentDomainObject.LoopItemRef.of(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo(), imageIndex));
        final ImageDomainObject image = defaultImage != null
                ? defaultImage
                : new ImageDomainObject();

        // Check if user has image rights
        if (!ImageEditPage.userHasImagePermissionsOnDocument(user, document)) {
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

        Handler<ImageEditResult> imageCommand = new Handler<ImageEditResult>() {
            public void handle(ImageEditResult editResult) {
                boolean shareImages = editResult.isShareImages();
                TextDocImagesContainer images = editResult.getEditedImages();

                ImcmsServices services = Imcms.getServices();
                String firstGeneratedFilename = null;

                for (ListIterator<ImageDomainObject> i = new LinkedList<>(images.getImages().values()).listIterator(); i.hasNext();) {
                    boolean first = i.nextIndex() == 0;
                    ImageDomainObject editImage = i.next();

                    if (first || !shareImages) {
                        editImage.generateFilename();
                        firstGeneratedFilename = editImage.getGeneratedFilename();

                        ImcmsImageUtils.generateImage(editImage, false);

                    } else if (shareImages) {
                        // share the same generated filename
                        editImage.setGeneratedFilename(firstGeneratedFilename);
                    }
                }

                try {
                    services.getDocumentMapper().saveTextDocImages(images, user);
                } catch (NoPermissionToEditDocumentException e) {
                    throw new ShouldHaveCheckedPermissionsEarlierException(e);
                } catch (NoPermissionToAddDocumentToMenuException e) {
                    throw new ConcurrentDocumentModificationException(e);
                    } catch (DocumentSaveException e) {
                        throw new ShouldNotBeThrownException(e);
                }
                services.updateMainLog("ImageRef " + imageIndex + " =" + image.getUrlPathRelativeToContextPath() +
                        " in  " + "[" + document.getId() + "] modified by user: [" +
                        user.getFullName() + "]");

            }

        };

        Map<DocumentLanguage, ImageDomainObject> images = imcref.getManagedBean(TextDocumentContentLoader.class).getImages(
                document.getVersionRef(), imageIndex, Optional.ofNullable(loopEntryRef)
        );

        for (DocumentLanguage language : imcref.getDocumentLanguages().getAll()) {
            if (!images.containsKey(language)) {
                images.put(language, new ImageDomainObject());
            }
        }

        LocalizedMessage heading = new LocalizedMessageFormat("image/edit_image_on_page", imageIndex, document.getId());
        ImageEditPage imageEditPage = new ImageEditPage(document, image, heading, StringUtils.defaultString(request.getParameter(REQUEST_PARAMETER__LABEL)), getServletContext(), imageCommand, returnCommand, true, forcedWidth, forcedHeight);

        TextDocImagesContainer container = TextDocImagesContainer.of(document.getVersionRef(), loopEntryRef, imageIndex, images);

        imageEditPage.setImagesContainer(container);
        imageEditPage.forward(request, response);
    }
}
