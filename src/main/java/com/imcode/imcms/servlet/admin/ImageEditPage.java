package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ImcmsImageUtils;
import imcode.util.Utility;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.admin.ImageCropPage.CropResult;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.document.textdocument.ImageArchiveImageSource;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.commons.lang.math.NumberUtils;

public class ImageEditPage extends OkCancelPage {

    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON = "goToImageBrowser";
    public static final String REQUEST_PARAMETER__GO_TO_CROP_IMAGE = "goToCropImage";
    public static final String REQUEST_PARAMETER__PREVIEW_BUTTON = "show_img";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__DELETE_BUTTON = "delete";
    public static final String REQUEST_PARAMETER__DOCUMENT_ID = "documentId";
    public final static String REQUEST_PARAMETER__IMAGE_URL = "imageref";
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
    public static final String REQUEST_PARAMETER__LINK_TARGET = EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET;
    public static final String REQUEST_PARAMETER__FORMAT = "format";
    public static final String REQUEST_PARAMETER__FORMAT_EXTENSION = "format_ext";
    public static final String REQUEST_PARAMETER__CROP_X1 = "crop_x1";
    public static final String REQUEST_PARAMETER__CROP_Y1 = "crop_y1";
    public static final String REQUEST_PARAMETER__CROP_X2 = "crop_x2";
    public static final String REQUEST_PARAMETER__CROP_Y2 = "crop_y2";
    public static final String REQUEST_PARAMETER__ROTATE_ANGLE = "rotate_angle";
    public static final String REQUEST_PARAMETER__I18N_CODE = "i18nCode";
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_ARCHIVE_BUTTON = "goToImageArchive";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE = "image_archive";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ID = "archive_img_id";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_NAME = "archive_img_nm";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_FILE_NAME = "archive_file_nm";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ALT_TEXT = "archive_img_alt_text";
    static final LocalizedMessage ERROR_MESSAGE__ONLY_ALLOWED_TO_UPLOAD_IMAGES = new LocalizedMessage("error/servlet/images/only_allowed_to_upload_images");
    static final LocalizedMessage ERROR_MESSAGE__FILE_NOT_IMAGE = new LocalizedMessage("error/servlet/images/file_not_image");
    public static final Format[] ALLOWED_FORMATS = new Format[] { Format.GIF, Format.JPEG, Format.PNG };
    
    public static final String REQUEST_PARAMETER__SHARE_IMAGE = "share_image";

    private TextDocumentDomainObject document;
    private String label;
    private final Handler<ImageEditResult> imageCommand;
    private final LocalizedMessage heading;
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
    private List<ImageDomainObject> images = new LinkedList<ImageDomainObject>();
    private List<ImageDomainObject> origImages = new LinkedList<ImageDomainObject>();

    public ImageEditPage(TextDocumentDomainObject document, ImageDomainObject image,
                         LocalizedMessage heading, String label, ServletContext servletContext,
                         Handler<ImageEditResult> imageCommand,
                         DispatchCommand returnCommand, boolean linkable,
                         int forcedWidth, int forcedHeight) {
        super(returnCommand, returnCommand);
        this.document = document;
        this.image = image;
        this.label = label;
        this.imageCommand = imageCommand;
        this.heading = heading ;
        this.linkable = linkable ;
        this.forcedWidth = forcedWidth;
        this.forcedHeight = forcedHeight;

        forceWidthHeight(image);

        if (image != null && image.getFormat() == null) {
            image.setFormat(Format.PNG);
        }
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
        String urlPath = null;
        if (fileName != null) {
            fileName = fileName.replaceAll("/|\\\\", "");
            source = new ImageArchiveImageSource(fileName);
            urlPath = source.getUrlPathRelativeToContextPath();
        }

        Long archiveImageId = null;
        if (archiveImageIdStr != null) {
            try {
                archiveImageId = Long.parseLong(archiveImageIdStr);
            } catch (NumberFormatException ex) {
            }
        }

        for (ImageDomainObject img : images) {
            boolean save = shareImages || img.getLanguage().getCode().equals(lang);
            if (!save) {
                continue;
            }

            if (fileName != null) {
                img.setImageUrl(urlPath);
                setNewSourceAndClearProps(img, source);
            }
            if (imageName != null) {
                img.setImageName(imageName);
            }
            if (archiveImageId != null) {
                img.setArchiveImageId(archiveImageId);
            }
            if(altText != null) {
                img.setAlternateText(altText);
            }
        }

        constrainImageFormat(lang);
        image = images.get(0);
    }

    private ImageDomainObject getImageFromRequest(HttpServletRequest req) {
        ImageDomainObject image = new ImageDomainObject();
        try {
            image.setWidth(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__IMAGE_WIDTH)));
        } catch ( NumberFormatException ignored ) {
        }
        try {
            image.setHeight(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__IMAGE_HEIGHT)));
        } catch ( NumberFormatException ignored ) {
        }
        try {
            image.setBorder(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__IMAGE_BORDER)));
        } catch ( NumberFormatException ignored ) {
        }
        try {
            image.setVerticalSpace(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__VERTICAL_SPACE)));
        } catch ( NumberFormatException ignored ) {
        }
        try {
            image.setHorizontalSpace(Integer.parseInt(req.getParameter(REQUEST_PARAMETER__HORIZONTAL_SPACE)));
        } catch ( NumberFormatException ignored ) {
        }

        String imageName = StringUtils.trimToEmpty(req.getParameter(REQUEST_PARAMETER__IMAGE_NAME));
        image.setImageName(StringUtils.substring(imageName, 0, ImageDomainObject.IMAGE_NAME_LENGTH));
        image.setAlign(req.getParameter(REQUEST_PARAMETER__IMAGE_ALIGN));
        
        if (isLinkable()) {
            image.setTarget(EditDocumentInformationPageFlow.getTargetFromRequest(req, EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET));
            image.setLinkUrl(req.getParameter(REQUEST_PARAMETER__LINK_URL));
        }
        
        String imageUrl = req.getParameter(REQUEST_PARAMETER__IMAGE_URL);
        if ( null != imageUrl && imageUrl.startsWith(req.getContextPath()) ) {
            imageUrl = imageUrl.substring(req.getContextPath().length());
        }
        
        ImageSource imageSource = ImcmsImageUtils.createImageSourceFromString(imageUrl);

        image.setSource(imageSource);

        //ImageInfo imageInfo = image.getImageInfo();
        //image.setFormat((imageInfo != null ? imageInfo.getFormat() : null));

        Format format = null;
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
        
        ImageDomainObject firstImage = images.get(0);
        
        for (int i = 0, len = images.size(); i < len; ++i) {
            boolean first = (i == 0);
            ImageDomainObject i18nImage = images.get(i);
            
            
    		String suffix = "_" + i18nImage.getLanguage().getCode();
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
        		if ( null != imageUrl && imageUrl.startsWith(req.getContextPath()) ) {
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

            i18nImage.setNo(this.image.getNo());
            i18nImage.setImageUrl(imageUrl);
            i18nImage.setType(imageSource.getTypeId());
            i18nImage.setAlternateText(alternateText);
            i18nImage.setSource(imageSource);
            i18nImage.setFormat(format);
            i18nImage.setCropRegion(cropRegion);
            i18nImage.setRotateDirection(rotateDirection);

            clearArchivePropertiesIfNullSource(i18nImage);

            i18nImage.setWidth(image.getWidth());
            i18nImage.setHeight(image.getHeight());
            i18nImage.setBorder(image.getBorder());
            i18nImage.setVerticalSpace(image.getVerticalSpace());
            i18nImage.setHorizontalSpace(image.getHorizontalSpace());
            i18nImage.setImageName(image.getImageName());
            i18nImage.setAlign(image.getAlign());
                        
            if (isLinkable()) {
                i18nImage.setTarget(image.getTarget());
                i18nImage.setLinkUrl(image.getLinkUrl());
            }
        }

        image.setNo(this.image.getNo());
        
        return image;
    }

    private static void clearArchivePropertiesIfNullSource(ImageDomainObject image) {
        if (image.getSource() instanceof NullImageSource) {
            image.setArchiveImageId(null);
            image.setImageName("");
        }
    }

    protected void dispatchOther(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser(request);
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();

        if ( null != request.getParameter(REQUEST_PARAMETER__DELETE_BUTTON) ) {
            NullImageSource source = new NullImageSource();
            
            for (ImageDomainObject image: images) {
            	image.setSourceAndClearSize(source);
            	image.setAlternateText(null);
            }
            
            image = images.get(0);

            forward(request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__PREVIEW_BUTTON) ) {

            forward(request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON) ) {
            goToImageBrowser(request, response);
        } else if (request.getParameter(REQUEST_PARAMETER__GO_TO_IMAGE_ARCHIVE_BUTTON) != null) {
            goToImageArchive(request, response);
        } else if (request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE) != null) {
            forward(request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__GO_TO_CROP_IMAGE) ) {
        	goToCropImage(request, response);
        }
    }

    private void goToCropImage(final HttpServletRequest request, final HttpServletResponse response) 
    		throws IOException, ServletException {

        String lang = request.getParameter(REQUEST_PARAMETER__I18N_CODE);
        image = getImageByLangCode(lang);

    	DispatchCommand returnCommand = new DispatchCommand() {
			public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
				forward(request, response);		
			}
		};
		
		Handler<CropResult> cropHandler = new Handler<CropResult>() {
			public void handle(CropResult result) {
				image.setCropRegion(result.getCropRegion());
                image.setRotateDirection(result.getRotateDirection());

                if (shareImages) {
                    for (ImageDomainObject img : images) {
                        img.setCropRegion(result.getCropRegion());
                        img.setRotateDirection(result.getRotateDirection());
                    }
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
        imageBrowser.setCancelCommand(new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                forward(request, response);
            }
        });
        imageBrowser.setSelectImageUrlCommand(new ImageBrowser.SelectImageUrlCommand() {
            public void selectImageUrl(String imageUrl, HttpServletRequest request,
                                       HttpServletResponse response) throws IOException, ServletException {
            	
            	// Image size on view for every chosen image must remain
            	// the same.
            	int width = image.getWidth();
            	int height = image.getHeight();
                
                // TODO i18n: refactor
                for (ImageDomainObject i18nImage: images) {
                	if (shareImages || i18nImage.getLanguage().getCode().equals(i18nCode)) {
                        setNewSourceAndClearProps(i18nImage, new ImagesPathRelativePathImageSource(imageUrl));
                 	}
                	
                 	i18nImage.setHeight(height);
                 	i18nImage.setWidth(width);
                    forceWidthHeight(i18nImage);
                }

                constrainImageFormat(i18nCode);
                image = images.get(0);
                
                forward(request, response);
            }
        });
        imageBrowser.forward(request, response);
    }

    public void setNewSourceAndClearProps(ImageDomainObject img, ImageSource imageSource) {
        img.setSource(imageSource);
        img.setCropRegion(new CropRegion());
        img.setRotateDirection(RotateDirection.NORTH);
    }

    public ImageDomainObject getImageByLangCode(String langCode) {
        for (ImageDomainObject img : images) {
            if (img.getLanguage().getCode().equals(langCode)) {
                return img;
            }
        }
        
        return null;
    }

    private void constrainImageFormat(String langCode) {
        if (!shareImages) {
            return;
        }

        ImageDomainObject img = null;
        for (ImageDomainObject i18nImage : images) {
            if (i18nImage.getLanguage().getCode().equals(langCode)) {
                img = i18nImage;
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

        for (ImageDomainObject i18nImage : images) {
            i18nImage.setFormat(imageFormat);
        }
    }

    static boolean userHasImagePermissionsOnDocument(UserDomainObject user, TextDocumentDomainObject document) {
        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(document);
        return textDocumentPermissionSet.getEditImages();
    }

    public static ImageEditPage getFromRequest(HttpServletRequest request) {
        return fromRequest(request);
    }

    public LocalizedMessage getHeading() {
        return heading;
    }

    public boolean canAddImageFiles(UserDomainObject user) {
        return user.canCreateDocumentOfTypeIdFromParent( DocumentTypeDomainObject.FILE_ID, document );
    }

    public boolean isLinkable() {
        return linkable;
    }

    protected void dispatchOk(HttpServletRequest request,
                              HttpServletResponse response) throws IOException, ServletException {
        ImageEditResult editResult = new ImageEditResult(shareImages, origImages, images);
        imageCommand.handle(editResult);
        super.dispatchOk(request, response);
    }

	public List<ImageDomainObject> getImages() {
		return images;
	}

	/** 
	 * Sets images and resets shareImages flag. 
	 */
	public void setImages(List<ImageDomainObject> images) {
		this.images = images;
        origImages = new ArrayList<ImageDomainObject>(images.size());

        for (ImageDomainObject img : images) {
            forceWidthHeight(img);

            origImages.add(img.clone());
        }
		
		boolean mayShareImages = images != null && images.size() > 1;
		
		if (mayShareImages) {
			Iterator<ImageDomainObject> iterator = images.iterator();			
			ImageDomainObject image = iterator.next();			
			ImageSource source = image.getSource();
			
			while (iterator.hasNext()) {
				image = iterator.next();
				ImageSource otherSource = image.getSource();
				
				if (!(source.getTypeId() == otherSource.getTypeId() 
						&& source.getUrlPathRelativeToContextPath().equals(
								otherSource.getUrlPathRelativeToContextPath()))) {
					mayShareImages = false;
					break;
				}
			}
		}
		
		this.shareImages = mayShareImages;
	}
	
	/**
	 * Returns if all images shares same image source. 
	 */
	public boolean getImagesSharesSameSource() {
		boolean same = images.size() > 0;
		String path = null; 
		
		for (ImageDomainObject image: images) {
			ImageSource source = image.getSource();
			String newPath = source.getUrlPathRelativeToContextPath();
			
			if (image.getSource() instanceof NullImageSource) {
				same = false;
				break;
			}
			
			if (path != null && !path.equals(newPath)) {
				same = false;
				break;
			}
			
			path = newPath;
		}
		
		return same;
	}

    public String getLangCodes() {
        String[] codes = new String[images.size()];
        
        for (int i = 0, len = images.size(); i < len; ++i) {
            codes[i] = images.get(i).getLanguage().getCode();
        }
        
        return StringUtils.join(codes, ',');
    }

	public boolean isShareImages() {
		return shareImages;
	}

	public void setShareImages(boolean shareImages) {
		this.shareImages = shareImages;
	}

    public int getForcedHeight() {
        return forcedHeight;
    }

    public void setForcedHeight(int forcedHeight) {
        this.forcedHeight = forcedHeight;
    }

    public int getForcedWidth() {
        return forcedWidth;
    }

    public void setForcedWidth(int forcedWidth) {
        this.forcedWidth = forcedWidth;
    }

    public TextDocumentDomainObject getDocument() {
        return document;
    }

    public Format[] getAllowedFormats() {
        return ALLOWED_FORMATS;
    }

}