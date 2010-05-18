package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
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
import com.imcode.imcms.util.l10n.LocalizedMessage;

public class ImageEditPage extends OkCancelPage {

    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON = "goToImageBrowser";
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
    public static final String REQUEST_PARAMETER__I18N_CODE = "i18nCode";
    static final LocalizedMessage ERROR_MESSAGE__ONLY_ALLOWED_TO_UPLOAD_IMAGES = new LocalizedMessage("error/servlet/images/only_allowed_to_upload_images");
    
    public static final String REQUEST_PARAMETER__SHARE_IMAGE = "share_image";

    private TextDocumentDomainObject document;
    private String label;
    private final Handler<List<ImageDomainObject>> imageCommand;
    private final LocalizedMessage heading;
    private boolean linkable;
    private boolean shareImages;
    
    /**
     * Image DTO. Contains generic image properties such as size, border,
     * target and url.
     */
    private ImageDomainObject image;
        
    /** 
     * Edited images.
     */
    private List<ImageDomainObject> images = new LinkedList<ImageDomainObject>();

    public ImageEditPage(TextDocumentDomainObject document, ImageDomainObject image,
                         LocalizedMessage heading, String label, ServletContext servletContext,
                         Handler<List<ImageDomainObject>> imageCommand,
                         DispatchCommand returnCommand, boolean linkable) {
        super(returnCommand, returnCommand);
        this.document = document;
        this.image = image;
        this.label = label;
        this.imageCommand = imageCommand;
        this.heading = heading ;
        this.linkable = linkable ;

        for (ImageDomainObject i: images) {
            i.setNo(image.getNo());
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
        image = getImageFromRequest(request);
    }

    public String getLabel() {
        return label;
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
        
        image.setName(StringUtils.trim(req.getParameter(REQUEST_PARAMETER__IMAGE_NAME)));
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
                
        image.setAlternateText(req.getParameter(REQUEST_PARAMETER__IMAGE_ALT));
        
        image.setLowResolutionUrl(req.getParameter(REQUEST_PARAMETER__IMAGE_LOWSRC));
        
        shareImages = req.getParameter(REQUEST_PARAMETER__SHARE_IMAGE) != null;
        
        int index = 0;
        ImageDomainObject firstImage = images.get(0);
        
        for (ImageDomainObject i18nImage: images) {
    		String suffix = "_" + i18nImage.getLanguage().getCode();
    		String alternateText = req.getParameter(REQUEST_PARAMETER__IMAGE_ALT
    			+ suffix);
    		
        	if (shareImages && index++ > 0) {
        		imageSource = firstImage.getSource();
        	} else {        	
        		imageUrl = req.getParameter(REQUEST_PARAMETER__IMAGE_URL + suffix);
        		if ( null != imageUrl && imageUrl.startsWith(req.getContextPath()) ) {
        			imageUrl = imageUrl.substring(req.getContextPath().length());
        		}
        		
        		imageSource = ImcmsImageUtils.createImageSourceFromString(imageUrl);
        	}                        

            i18nImage.setNo(this.image.getNo());
            i18nImage.setImageUrl(imageUrl);
            i18nImage.setType(imageSource.getTypeId());
            i18nImage.setAlternateText(alternateText);
            i18nImage.setSource(imageSource);  
            
            i18nImage.setWidth(image.getWidth());
            i18nImage.setHeight(image.getHeight());
            i18nImage.setBorder(image.getBorder());
            i18nImage.setVerticalSpace(image.getVerticalSpace());
            i18nImage.setHorizontalSpace(image.getHorizontalSpace());
            i18nImage.setName(image.getName());
            i18nImage.setAlign(image.getAlign());
                        
            if (isLinkable()) {
                i18nImage.setTarget(image.getTarget());
                i18nImage.setLinkUrl(image.getLinkUrl());
            }
        }

        image.setNo(this.image.getNo());
        
        return image;
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
        }
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
                		i18nImage.setSource(new ImagesPathRelativePathImageSource(imageUrl));
                 	}
                	
                 	i18nImage.setHeight(height);
                 	i18nImage.setWidth(width);
                }
                
                forward(request, response);
            }
        });
        imageBrowser.forward(request, response);
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
        imageCommand.handle(images);
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

	public boolean isShareImages() {
		return shareImages;
	}

	public void setShareImages(boolean shareImages) {
		this.shareImages = shareImages;
	}

}