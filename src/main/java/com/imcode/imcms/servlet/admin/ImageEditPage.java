package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.domain.dto.ImageData.CropRegion;
import com.imcode.imcms.domain.dto.ImageData.RotateDirection;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.mapping.container.TextDocImagesContainer;
import com.imcode.imcms.servlet.admin.ImageCropPage.CropResult;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import imcode.util.ImcmsImageUtils;
import imcode.util.Utility;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

public class ImageEditPage extends OkCancelPage {

    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON = "goToImageBrowser";
    public static final String REQUEST_PARAMETER__GO_TO_CROP_IMAGE = "goToCropImage";
    public static final String REQUEST_PARAMETER__PREVIEW_BUTTON = "show_img";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__DELETE_BUTTON = "delete";
    public static final String REQUEST_PARAMETER__IMAGE_URL = "imageref";
    public static final String REQUEST_PARAMETER__OK_BUTTON = "ok";
    public static final String REQUEST_PARAMETER__IMAGE_HEIGHT = "image_height";
    public static final String REQUEST_PARAMETER__IMAGE_WIDTH = "image_width";
    public static final String REQUEST_PARAMETER__IMAGE_BORDER = "image_border";
    public static final String REQUEST_PARAMETER__VERTICAL_SPACE = "v_space";
    public static final String REQUEST_PARAMETER__HORIZONTAL_SPACE = "h_space";
    public static final String REQUEST_PARAMETER__IMAGE_NAME = "image_name";
    public static final String REQUEST_PARAMETER__IMAGE_ALIGN = "image_align";
    public static final String REQUEST_PARAMETER__IMAGE_ALT = "alt_text";
    public static final String REQUEST_PARAMETER__IMAGE_LOWSRC = "low_scr";
    public static final String REQUEST_PARAMETER__LINK_URL = "imageref_link";
    public static final String REQUEST_PARAMETER__FORMAT = "format";
    public static final String REQUEST_PARAMETER__CROP_X1 = "crop_x1";
    public static final String REQUEST_PARAMETER__CROP_Y1 = "crop_y1";
    public static final String REQUEST_PARAMETER__CROP_X2 = "crop_x2";
    public static final String REQUEST_PARAMETER__CROP_Y2 = "crop_y2";
    public static final String REQUEST_PARAMETER__ROTATE_ANGLE = "rotate_angle";
    public static final String REQUEST_PARAMETER__I18N_CODE = "i18nCode";
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_ARCHIVE_BUTTON = "goToImageArchive";
    public static final String REQUEST_PARAMETER__SHARE_IMAGE = "share_image";
    static final Format[] ALLOWED_FORMATS = new Format[]{Format.GIF, Format.JPEG, Format.PNG};
    static final LocalizedMessage ERROR_MESSAGE__ONLY_ALLOWED_TO_UPLOAD_IMAGES = new LocalizedMessage("error/servlet/images/only_allowed_to_upload_images");
    static final LocalizedMessage ERROR_MESSAGE__FILE_NOT_IMAGE = new LocalizedMessage("error/servlet/images/file_not_image");
    private static final String REQUEST_PARAMETER__TARGET = "target";
    public static final String REQUEST_PARAMETER__LINK_TARGET = REQUEST_PARAMETER__TARGET;
    private static final String REQUEST_PARAMETER__FORMAT_EXTENSION = "format_ext";
    private static final String REQUEST_PARAMETER__IMAGE_ARCHIVE = "image_archive";
    private static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ID = "archive_img_id";
    private static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_NAME = "archive_img_nm";
    private static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_FILE_NAME = "archive_file_nm";
    private static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ALT_TEXT = "archive_img_alt_text";
    private static final long serialVersionUID = 4058898601460492131L;
    private final Handler<ImageEditResult> imageCommand;
    private final LocalizedMessage heading;
    private TextDocumentDomainObject document;
    private String label;
    private boolean linkable;
    private boolean shareImages;
    private int forcedWidth;
    private int forcedHeight;

    /**
     * Image DTO. Contains generic image properties such as size, border,
     * target and url.
     */
    private ImageDomainObject image;

    /**
     * Edited images.
     */
    private TextDocImagesContainer imagesContainer;
    private TextDocImagesContainer origImagesContainer;

    ImageEditPage(TextDocumentDomainObject document, ImageDomainObject image, LocalizedMessage heading, String label,
                  Handler<ImageEditResult> imageCommand, DispatchCommand returnCommand, boolean linkable,
                  int forcedWidth, int forcedHeight) {
        super(returnCommand, returnCommand);
        this.document = document;
        this.image = image;
        this.label = label;
        this.imageCommand = imageCommand;
        this.heading = heading;
        this.linkable = linkable;
        this.forcedWidth = forcedWidth;
        this.forcedHeight = forcedHeight;

        forceWidthHeight(image);

        if (image != null && image.getFormat() == null) {
            image.setFormat(Format.PNG);
        }
    }

    private static void clearArchivePropertiesIfNullSource(ImageDomainObject image) {
        if (image.getSource() instanceof NullImageSource) {
            image.setArchiveImageId(null);
            image.setName("");
        }
    }

    static boolean userHasImagePermissionsOnDocument(UserDomainObject user, TextDocumentDomainObject document) {
        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(document);
        return textDocumentPermissionSet.getEditImages();
    }

    public static ImageEditPage getFromRequest(HttpServletRequest request) {
        return fromRequest(request);
    }

    private static String getTargetFromRequest(HttpServletRequest request, String parameterName) {
        String[] possibleTargets = request.getParameterValues(parameterName);
        String target = null;
        if (null != possibleTargets) {
            for (String possibleTarget : possibleTargets) {
                target = possibleTarget;
                boolean targetIsPredefinedTarget
                        = "_self".equalsIgnoreCase(target)
                        || "_blank".equalsIgnoreCase(target)
                        || "_parent".equalsIgnoreCase(target)
                        || "_top".equalsIgnoreCase(target);
                if (targetIsPredefinedTarget) {
                    break;
                }
            }
        }
        return target;
    }

    private void forceWidthHeight(ImageDomainObject img) {
        if (img != null) {
            if (forcedWidth > 0) {
                img.setWidth(forcedWidth);
            }
            if (forcedHeight > 0) {
                img.setHeight(forcedHeight);
            }
        }
    }

    public ImageDomainObject getImage() {
        return image;
    }

    public String getPath(HttpServletRequest request) {
        UserDomainObject user = Utility.getLoggedOnUser(request);
        return "/imcms/" + user.getLanguageIso639_2() + "/jsp/change_img.jsp";
    }

    protected void updateFromRequest(HttpServletRequest request) {
        if (request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE) != null) {
            getImageFromImageArchive(request);
        } else {
            image = getImageFromRequest(request);
        }
    }

    public String getLabel() {
        return label;
    }

    private void getImageFromImageArchive(HttpServletRequest request) {
        String imageName = StringUtils.trimToNull(request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_NAME));
        imageName = StringUtils.substring(imageName, 0, ImageDomainObject.IMAGE_NAME_LENGTH);
        String fileName = StringUtils.trimToNull(request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE_FILE_NAME));
        String archiveImageIdStr = StringUtils.trimToNull(request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ID));
        String lang = StringUtils.trimToNull(request.getParameter(REQUEST_PARAMETER__I18N_CODE));
        String altText = StringUtils.trimToNull(request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ALT_TEXT));

        ImageSource source = null;
        if (fileName != null) {
            fileName = fileName.replaceAll("/|\\\\", "");
            source = new ImageArchiveImageSource(fileName);
        }

        Long archiveImageId = null;
        if (archiveImageIdStr != null) {
            try {
                archiveImageId = Long.parseLong(archiveImageIdStr);
            } catch (NumberFormatException ex) {
            }
        }

        for (Map.Entry<DocumentLanguage, ImageDomainObject> entry : imagesContainer.getImages().entrySet()) {
            ImageDomainObject img = entry.getValue();
            boolean save = shareImages || entry.getKey().getCode().equals(lang);
            if (!save) {
                continue;
            }

            if (fileName != null) {
                setNewSourceAndClearProps(img, source);
            }
            if (imageName != null) {
                img.setName(imageName);
            }
            if (archiveImageId != null) {
                img.setArchiveImageId(archiveImageId);
            }
            if (altText != null) {
                img.setAlternateText(altText);
            }
        }

        constrainImageFormat(lang);
        // fixme: something wrong here!
        image = imagesContainer.getImages().get(0);
    }

    private ImageDomainObject getImageFromRequest(HttpServletRequest req) {
        ImageDomainObject image = new ImageDomainObject();
        try {
            image.setWidth(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__IMAGE_WIDTH)));
        } catch (NumberFormatException ignored) {
        }
        try {
            image.setHeight(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__IMAGE_HEIGHT)));
        } catch (NumberFormatException ignored) {
        }
        try {
            image.setBorder(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__IMAGE_BORDER)));
        } catch (NumberFormatException ignored) {
        }
        try {
            image.setVerticalSpace(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__VERTICAL_SPACE)));
        } catch (NumberFormatException ignored) {
        }
        try {
            image.setHorizontalSpace(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__HORIZONTAL_SPACE)));
        } catch (NumberFormatException ignored) {
        }

        String imageName = StringUtils.trimToEmpty(req.getParameter(REQUEST_PARAMETER__IMAGE_NAME));
        image.setName(StringUtils.substring(imageName, 0, ImageDomainObject.IMAGE_NAME_LENGTH));
        image.setAlign(req.getParameter(REQUEST_PARAMETER__IMAGE_ALIGN));

        if (isLinkable()) {
            image.setTarget(getTargetFromRequest(req, REQUEST_PARAMETER__TARGET));
            image.setLinkUrl(req.getParameter(REQUEST_PARAMETER__LINK_URL));
        }

        String imageUrl = req.getParameter(REQUEST_PARAMETER__IMAGE_URL);
        if (null != imageUrl && imageUrl.startsWith(req.getContextPath())) {
            imageUrl = imageUrl.substring(req.getContextPath().length());
        }

        ImageSource imageSource = ImcmsImageUtils.createImageSourceFromString(imageUrl);

        image.setSource(imageSource);

        Format format;
        if (req.getParameter(REQUEST_PARAMETER__FORMAT_EXTENSION) != null) {
            format = Format.findFormatByExtension(req.getParameter(REQUEST_PARAMETER__FORMAT_EXTENSION));
        } else {
            format = Format.findFormat((short) NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__FORMAT), 0));
        }

        image.setFormat(format);

        image.setAlternateText(req.getParameter(REQUEST_PARAMETER__IMAGE_ALT));

        image.setLowResolutionUrl(req.getParameter(REQUEST_PARAMETER__IMAGE_LOWSRC));

        shareImages = req.getParameter(REQUEST_PARAMETER__SHARE_IMAGE) != null;

        clearArchivePropertiesIfNullSource(image);

        // fixme: something wrong here!
        ImageDomainObject firstImage = imagesContainer.getImages().get(0);

        for (Map.Entry<DocumentLanguage, ImageDomainObject> entry : imagesContainer.getImages().entrySet()) {
            ImageDomainObject img = entry.getValue();
            boolean first = img == firstImage;

            String suffix = "_" + entry.getKey().getCode();
            String alternateText = req.getParameter(REQUEST_PARAMETER__IMAGE_ALT
                    + suffix);

            CropRegion cropRegion;
            RotateDirection rotateDirection;

            if (shareImages && !first) {
                imageSource = firstImage.getSource();
                cropRegion = firstImage.getCropRegion();
                rotateDirection = firstImage.getRotateDirection();

            } else {
                imageUrl = req.getParameter(REQUEST_PARAMETER__IMAGE_URL + suffix);
                if (null != imageUrl && imageUrl.startsWith(req.getContextPath())) {
                    imageUrl = imageUrl.substring(req.getContextPath().length());
                }

                imageSource = ImcmsImageUtils.createImageSourceFromString(imageUrl);

                cropRegion = new CropRegion();
                cropRegion.setCropX1(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__CROP_X1 + suffix), -1));
                cropRegion.setCropY1(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__CROP_Y1 + suffix), -1));
                cropRegion.setCropX2(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__CROP_X2 + suffix), -1));
                cropRegion.setCropY2(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__CROP_Y2 + suffix), -1));
                cropRegion.updateValid();

                int rotateAngle = NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__ROTATE_ANGLE + suffix));
                rotateDirection = RotateDirection.getByAngleDefaultIfNull(rotateAngle);
            }

            img.setAlternateText(alternateText);
            img.setSource(imageSource);
            img.setFormat(format);
            img.setCropRegion(cropRegion);
            img.setRotateDirection(rotateDirection);

            clearArchivePropertiesIfNullSource(img);

            img.setWidth(image.getWidth());
            img.setHeight(image.getHeight());
            img.setBorder(image.getBorder());
            img.setVerticalSpace(image.getVerticalSpace());
            img.setHorizontalSpace(image.getHorizontalSpace());
            img.setName(image.getName());
            img.setAlign(image.getAlign());

            if (isLinkable()) {
                img.setTarget(image.getTarget());
                img.setLinkUrl(image.getLinkUrl());
            }
        }

        return image;
    }

    protected void dispatchOther(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {

        if (null != request.getParameter(REQUEST_PARAMETER__DELETE_BUTTON)) {
            NullImageSource source = new NullImageSource();

            for (ImageDomainObject image : imagesContainer.getImages().values()) {
                image.setSourceAndClearSize(source);
                image.setAlternateText(null);
            }

            // fixme: something wrong here!
            image = imagesContainer.getImages().get(0);

            forward(request, response);
        } else if (null != request.getParameter(REQUEST_PARAMETER__PREVIEW_BUTTON)) {

            forward(request, response);
        } else if (null != request.getParameter(REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON)) {
            goToImageBrowser(request, response);
        } else if (request.getParameter(REQUEST_PARAMETER__GO_TO_IMAGE_ARCHIVE_BUTTON) != null) {
            goToImageArchive(request, response);
        } else if (request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE) != null) {
            forward(request, response);
        } else if (null != request.getParameter(REQUEST_PARAMETER__GO_TO_CROP_IMAGE)) {
            goToCropImage(request, response);
        }
    }

    private void goToCropImage(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {

        String lang = request.getParameter(REQUEST_PARAMETER__I18N_CODE);
        image = getImageByLangCode(lang);

        DispatchCommand returnCommand = (DispatchCommand) this::forward;

        Handler<CropResult> cropHandler = (Handler<CropResult>) result -> {
            image.setCropRegion(result.getCropRegion());
            image.setRotateDirection(result.getRotateDirection());

            if (shareImages) {
                for (ImageDomainObject img : imagesContainer.getImages().values()) {
                    img.setCropRegion(result.getCropRegion());
                    img.setRotateDirection(result.getRotateDirection());
                }
            }
        };

        ImageCropPage cropPage = new ImageCropPage(returnCommand, cropHandler, image, forcedWidth, forcedHeight);
        cropPage.forward(request, response);
    }

    private void goToImageArchive(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append(request.getContextPath());
        builder.append("/servlet/PageDispatcher?page=");
        builder.append(URLEncoder.encode(getSessionAttributeName(), "UTF-8"));
        builder.append("&");
        builder.append(REQUEST_PARAMETER__IMAGE_ARCHIVE);
        builder.append("=yes&");
        builder.append(REQUEST_PARAMETER__I18N_CODE);
        builder.append("=");

        String code = request.getParameter(REQUEST_PARAMETER__I18N_CODE);
        if (code == null) {
            throw new RuntimeException("Language code is not set.");
        }
        builder.append(code);

        String imageArchiveUrl = String.format("%s/web/archive?returnTo=%s", request.getContextPath(), URLEncoder.encode(builder.toString(), "UTF-8"));

        response.sendRedirect(imageArchiveUrl);
    }

    private void goToImageBrowser(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException, ServletException {

        final String i18nCode = request.getParameter(REQUEST_PARAMETER__I18N_CODE);

        if (i18nCode == null) {
            throw new RuntimeException("Language code is not set.");
        }

        // imageBrowser.setCode ???

        ImageBrowser imageBrowser = new ImageBrowser();
        imageBrowser.setCancelCommand((DispatchCommand) this::forward);
        imageBrowser.setSelectImageUrlCommand((ImageBrowser.SelectImageUrlCommand) (imageUrl, request1, response1) -> {

            // Image size on view for every chosen image must remain
            // the same.
            int width = image.getWidth();
            int height = image.getHeight();

            for (Map.Entry<DocumentLanguage, ImageDomainObject> entry : imagesContainer.getImages().entrySet()) {
                ImageDomainObject img = entry.getValue();
                if (shareImages || entry.getKey().getCode().equals(i18nCode)) {
                    setNewSourceAndClearProps(img, new ImagesPathRelativePathImageSource(imageUrl));
                }

                img.setHeight(height);
                img.setWidth(width);
                forceWidthHeight(img);
            }

            constrainImageFormat(i18nCode);
            // fixme: something wrong here!
            image = imagesContainer.getImages().get(0);

            forward(request1, response1);
        });
        imageBrowser.forward(request, response);
    }

    private void setNewSourceAndClearProps(ImageDomainObject img, ImageSource imageSource) {
        img.setSource(imageSource);
        img.setCropRegion(new CropRegion());
        img.setRotateDirection(RotateDirection.NORTH);
    }

    private ImageDomainObject getImageByLangCode(String langCode) {
        for (Map.Entry<DocumentLanguage, ImageDomainObject> entry : imagesContainer.getImages().entrySet()) {
            if (entry.getKey().getCode().equals(langCode)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private void constrainImageFormat(String langCode) {
        if (!shareImages) {
            return;
        }

        ImageDomainObject img = null;
        for (Map.Entry<DocumentLanguage, ImageDomainObject> entry : imagesContainer.getImages().entrySet()) {
            if (entry.getKey().getCode().equals(langCode)) {
                img = entry.getValue();
                break;
            }
        }

        if (img == null) {
            return;
        }

        Format imageFormat = null;
        ImageInfo info = img.getImageInfo();
        if (info != null) {
            imageFormat = info.getFormat();
        }

        boolean allowedFormat = false;
        for (Format format : ALLOWED_FORMATS) {
            if (format == imageFormat) {
                allowedFormat = true;
                break;
            }
        }
        imageFormat = (allowedFormat ? imageFormat : Format.PNG);

        for (ImageDomainObject image : imagesContainer.getImages().values()) {
            image.setFormat(imageFormat);
        }
    }

    public LocalizedMessage getHeading() {
        return heading;
    }

    public boolean isLinkable() {
        return linkable;
    }

    protected void dispatchOk(HttpServletRequest request,
                              HttpServletResponse response) throws IOException, ServletException {
        ImageEditResult editResult = new ImageEditResult(shareImages, origImagesContainer, imagesContainer);
        imageCommand.handle(editResult);
        super.dispatchOk(request, response);
    }

    public TextDocImagesContainer getImagesContainer() {
        return imagesContainer;
    }

    /**
     * Sets images and resets shareImages flag.
     */
    void setImagesContainer(TextDocImagesContainer imagesContainer) {
        Map<DocumentLanguage, ImageDomainObject> origImages = new HashMap<>();

        for (Map.Entry<DocumentLanguage, ImageDomainObject> entry : imagesContainer.getImages().entrySet()) {
            ImageDomainObject img = entry.getValue();
            forceWidthHeight(img);

            origImages.put(entry.getKey(), img);
        }

        boolean mayShareImages = origImages.size() > 1;

        if (mayShareImages) {
            Iterator<ImageDomainObject> iterator = imagesContainer.getImages().values().iterator();
            ImageDomainObject image = iterator.next();
            ImageSource source = image.getSource();

            while (iterator.hasNext()) {
                image = iterator.next();
                ImageSource otherSource = image.getSource();

                if (!(source.getTypeId() == otherSource.getTypeId()
                        && source.getUrlPathRelativeToContextPath().equals(
                        otherSource.getUrlPathRelativeToContextPath())))
                {
                    mayShareImages = false;
                    break;
                }
            }
        }

        this.shareImages = mayShareImages;
        this.imagesContainer = imagesContainer;
        this.origImagesContainer = new TextDocImagesContainer(imagesContainer.getVersionRef(), imagesContainer.getLoopEntryRef(), imagesContainer.getImageNo(), origImages);
    }

    public String getLangCodes() {
        String[] codes = new String[imagesContainer.getImages().size()];

        for (ListIterator<DocumentLanguage> i = new LinkedList<>(imagesContainer.getImages().keySet()).listIterator(); i.hasNext(); )
        {
            codes[i.nextIndex()] = i.next().getCode();
        }

        return StringUtils.join(codes, ',');
    }

    public boolean isShareImages() {
        return shareImages;
    }

    public int getForcedHeight() {
        return forcedHeight;
    }

    public int getForcedWidth() {
        return forcedWidth;
    }

    public TextDocumentDomainObject getDocument() {
        return document;
    }

    public Format[] getAllowedFormats() {
        return ALLOWED_FORMATS;
    }

}