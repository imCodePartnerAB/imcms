package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.dao.ImageDao;
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
import imcode.util.Utility;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.ShouldNotBeThrownException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ChangeImage extends HttpServlet {

    public static final String REQUEST_PARAMETER__IMAGE_INDEX = "img";
    public static final String REQUEST_PARAMETER__LABEL = "label";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final ImcmsServices imcref = Imcms.getServices();
        final DocumentMapper documentMapper = imcref.getDocumentMapper();
        final TextDocumentDomainObject document = (TextDocumentDomainObject) documentMapper.getDocument(Integer.parseInt(request.getParameter("meta_id")));
        final int imageIndex = Integer.parseInt(request.getParameter(REQUEST_PARAMETER__IMAGE_INDEX));        
        final UserDomainObject user = Utility.getLoggedOnUser(request);
        
        // I18n changes
        // This image object is now used for holding generic properties
        // such as size, border, etc
        final ImageDomainObject image = document.getImage(imageIndex);

        // Check if user has image rights
        if ( !ImageEditPage.userHasImagePermissionsOnDocument(user, document) ) {
            Utility.redirectToStartDocument(request, response);
            return;
        }

        DispatchCommand returnCommand = new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
                response.sendRedirect("AdminDoc?meta_id=" + document.getId() + "&flags="
                                      + ImcmsConstants.DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_IMAGES);

            }
        };
        
        Handler<Map<I18nLanguage, ImageDomainObject>> imageCommand = new Handler<Map<I18nLanguage, ImageDomainObject>>() {
            public void handle(Map<I18nLanguage, ImageDomainObject> imageMap) {
                ImcmsServices services = Imcms.getServices();
                
                // TODO: I18n images assignment to document.                
                document.setI18nImageMap(imageIndex, imageMap);
                
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
            

/*            public void handle2(ImageDomainObject image) {
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

            }*/
            

        };
        
        ImageDao imageDao = (ImageDao)Imcms.getServices().getSpringBean("imageDao");
        
        Map<I18nLanguage, ImageDomainObject> i18nImageMap = imageDao.getI18nImageMap(document.getId(), imageIndex);
        
        LocalizedMessage heading = new LocalizedMessageFormat("image/edit_image_on_page", imageIndex, document.getId());
        ImageEditPage imageEditPage = new ImageEditPage(document, image, heading, StringUtils.defaultString(request.getParameter(REQUEST_PARAMETER__LABEL)), getServletContext(), imageCommand, returnCommand, true);
        
        imageEditPage.setImageDao(imageDao);
        imageEditPage.setImages(i18nImageMap);
        imageEditPage.forward(request, response);
    }
}
