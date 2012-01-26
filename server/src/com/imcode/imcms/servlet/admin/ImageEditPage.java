package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.NoPermissionToCreateDocumentException;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.index.DefaultQueryParser;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.QueryParser;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageArchiveImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.ImcmsImageUtils;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.io.InputStreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.flow.CreateDocumentPageFlow;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.DocumentPageFlow;
import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import com.imcode.imcms.flow.EditFileDocumentPageFlow;
import com.imcode.imcms.flow.OkCancelPage;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.NoPermissionInternalException;
import com.imcode.imcms.servlet.DocumentFinder;
import com.imcode.imcms.servlet.SearchDocumentsPage;
import com.imcode.imcms.servlet.superadmin.AdminManager;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.util.HumanReadable;
import com.imcode.util.ImageSize;
import imcode.util.image.Resize;

public class ImageEditPage extends OkCancelPage {

    public final static String REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH_BUTTON = "goToImageSearch";
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON = "goToImageBrowser";
    public static final String REQUEST_PARAMETER__GO_TO_ADD_RESTRICTED_IMAGE_BUTTON = "goToAddRestrictedImage";
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
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_ARCHIVE_BUTTON = "goToImageArchive";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE = "image_archive";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ID = "archive_img_id";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_NAME = "archive_img_nm";
    public static final String REQUEST_PARAMETER__IMAGE_ARCHIVE_FILE_NAME = "archive_file_nm";
    public static final String REQUEST_PARAMETER__FORMAT = "format";
    public static final String REQUEST_PARAMETER__FORMAT_EXTENSION = "format_ext";
    public static final String REQUEST_PARAMETER__CROP_X1 = "crop_x1";
    public static final String REQUEST_PARAMETER__CROP_Y1 = "crop_y1";
    public static final String REQUEST_PARAMETER__CROP_X2 = "crop_x2";
    public static final String REQUEST_PARAMETER__CROP_Y2 = "crop_y2";
    public static final String REQUEST_PARAMETER__ROTATE_ANGLE = "rotate_angle";
    static final LocalizedMessage ERROR_MESSAGE__ONLY_ALLOWED_TO_UPLOAD_IMAGES = new LocalizedMessage("error/servlet/images/only_allowed_to_upload_images");
    static final LocalizedMessage ERROR_MESSAGE__FILE_NOT_IMAGE = new LocalizedMessage("error/servlet/images/file_not_image");
    private final static String[] IMAGE_MIME_TYPES = new String[] { "image/jpeg", "image/png", "image/gif" };
    public static final Format[] ALLOWED_FORMATS = new Format[] { Format.GIF, Format.JPEG, Format.PNG };

    private TextDocumentDomainObject document;
    private ImageDomainObject image;
    private Integer imageIndex;
    private String label;
    private final ServletContext servletContext;
    private final Handler<ImageDomainObject> imageCommand;
    private final LocalizedMessage heading;
    private boolean linkable;
    private int forcedWidth;
    private int forcedHeight;
    private int maxWidth;
    private int maxHeight;
    private String returnUrl;

    public ImageEditPage(TextDocumentDomainObject document, ImageDomainObject image, Integer imageIndex, 
                         LocalizedMessage heading, String label, ServletContext servletContext,
                         Handler<ImageDomainObject> imageCommand,
                         DispatchCommand returnCommand, boolean linkable, 
                         int forcedWidth, int forcedHeight, int maxWidth, int maxHeight, String returnUrl) {
        super(returnCommand, returnCommand);
        this.document = document;
        this.image = image;
        this.imageIndex = imageIndex;
        this.label = label;
        this.servletContext = servletContext;
        this.imageCommand = imageCommand;
        this.heading = heading ;
        this.linkable = linkable ;
        this.forcedWidth = forcedWidth;
        this.forcedHeight = forcedHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.returnUrl = returnUrl;
        
        if (forcedWidth > 0) {
            this.maxWidth = 0;
        }
        if (forcedHeight > 0) {
            this.maxHeight = 0;
        }
        
        forceWidthHeight();
        restrictMaximumDimensions();
    }
    
    private void forceWidthHeight() {
    	if (image != null) {
        	if (forcedWidth > 0) {
        		image.setWidth(forcedWidth);
        	}
        	if (forcedHeight > 0) {
        		image.setHeight(forcedHeight);
        	}
        }
    }
    
    private void restrictMaximumDimensions() {
        if (image == null) {
            return;
        }
        
        if (maxWidth > 0) {
            int imgWidth = image.getWidth();
            
            if (imgWidth <= 0 || imgWidth > maxWidth) {
                imgWidth = maxWidth;
            }
            
            image.setWidth(imgWidth);
        }
        if (maxHeight > 0) {
            int imgHeight = image.getHeight();
            
            if (imgHeight <= 0 || imgHeight > maxHeight) {
                imgHeight = maxHeight;
            }
            
            image.setHeight(imgHeight);
        }
        
        image.setResize(getResize());
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
    	imageName = StringUtils.substring(imageName, 0, 40);
    	String fileName = StringUtils.trimToNull(request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE_FILE_NAME));
    	String archiveImageIdStr = StringUtils.trimToNull(request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ID));
    	
    	Long archiveImageId = null;
    	if (archiveImageIdStr != null) {
    		try {
    			archiveImageId = Long.parseLong(archiveImageIdStr, 10);
    		} catch (NumberFormatException ex) {
    		}
    	}
    	
    	if (fileName != null) {
    		fileName = fileName.replaceAll("/|\\\\", "");
    		setNewSourceAndClearSize(new ImageArchiveImageSource(fileName));
    		
    		if (imageName != null) {
    			image.setName(imageName);
    		}
    		if (archiveImageId != null) {
    			image.setArchiveImageId(archiveImageId);
    		}
    	}
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
        try {
        	image.setArchiveImageId(Long.parseLong(req.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ID)));
        } catch (NumberFormatException ex) {
        }
        
        int rotateAngle = NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__ROTATE_ANGLE));
        image.setRotateDirection(RotateDirection.getByAngleDefaultIfNull(rotateAngle));
        
        image.setResize(getResize());
        
        CropRegion region = new CropRegion();
        region.setCropX1(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__CROP_X1), -1));
        region.setCropY1(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__CROP_Y1), -1));
        region.setCropX2(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__CROP_X2), -1));
        region.setCropY2(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__CROP_Y2), -1));
        region.updateValid();
        image.setCropRegion(region);

        
        String imageUrl = req.getParameter(REQUEST_PARAMETER__IMAGE_URL);
        if ( null != imageUrl && imageUrl.startsWith(req.getContextPath()) ) {
            imageUrl = imageUrl.substring(req.getContextPath().length());
        }
        ImageSource imageSource = ImcmsImageUtils.createImageSourceFromString(imageUrl);

        image.setSource(imageSource);
        
        ImageInfo imageInfo = image.getImageInfo();
        image.setFormat((imageInfo != null ? imageInfo.getFormat() : null));
        
        Format format = null;
        if (req.getParameter(REQUEST_PARAMETER__FORMAT_EXTENSION) != null) {
            format = Format.findFormatByExtension(req.getParameter(REQUEST_PARAMETER__FORMAT_EXTENSION));
        } else {
            format = Format.findFormat(NumberUtils.toInt(req.getParameter(REQUEST_PARAMETER__FORMAT), 0));
        }
        
        if (format != null) {
        	image.setFormat(format);
        }
        
        image.setName(StringUtils.trim(req.getParameter(REQUEST_PARAMETER__IMAGE_NAME)));
        image.setAlign(req.getParameter(REQUEST_PARAMETER__IMAGE_ALIGN));
        image.setAlternateText(req.getParameter(REQUEST_PARAMETER__IMAGE_ALT));
        image.setLowResolutionUrl(req.getParameter(REQUEST_PARAMETER__IMAGE_LOWSRC));
        if (isLinkable()) {
            image.setTarget(EditDocumentInformationPageFlow.getTargetFromRequest(req, EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET));
            image.setLinkUrl(req.getParameter(REQUEST_PARAMETER__LINK_URL));
        }
        return image;
    }

    protected void dispatchOther(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
        UserDomainObject user = Utility.getLoggedOnUser(request);
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();

        if ( null != request.getParameter(REQUEST_PARAMETER__DELETE_BUTTON) ) {
            image = new ImageDomainObject();

            forward(request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__PREVIEW_BUTTON) ) {

            forward(request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON) ) {
            goToImageBrowser(request, response);
        } else if (request.getParameter(REQUEST_PARAMETER__GO_TO_IMAGE_ARCHIVE_BUTTON) != null) {
        	goToImageArchive(request, response);
        } else if (request.getParameter(REQUEST_PARAMETER__IMAGE_ARCHIVE) != null) {
        	forward(request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH_BUTTON) ) {
            goToImageSearch(documentMapper, request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__GO_TO_ADD_RESTRICTED_IMAGE_BUTTON) ) {
            goToImageAdder(documentMapper, user, request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__GO_TO_CROP_IMAGE) ) {
        	goToCropImage(request, response);
        }
    }
    
    private void goToImageArchive(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	int port = request.getServerPort();
    	
    	StringBuilder builder = new StringBuilder();
    	builder.append("http://");
    	builder.append(request.getServerName());
    	
    	if (port != 80) {
    		builder.append(":");
    		builder.append(port);
    	}
    	
    	builder.append(request.getContextPath());
    	builder.append("/servlet/PageDispatcher?page=");
    	builder.append(Utility.encodeUrl(getSessionAttributeName()));
    	builder.append("&");
    	builder.append(REQUEST_PARAMETER__IMAGE_ARCHIVE);
    	builder.append("=yes&");
    	
    	String imageArchiveUrl = String.format("http://%s?returnTo=%s", Imcms.getServices().getConfig().getImageArchiveUrl(), 
    			Utility.encodeUrl(builder.toString()));
    	
    	response.sendRedirect(imageArchiveUrl);
    }

    private void goToImageAdder(final DocumentMapper documentMapper,
                                UserDomainObject user,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException, ServletException {
        try {
            if (!user.canCreateDocumentOfTypeIdFromParent(DocumentTypeDomainObject.FILE_ID, document)) {
                throw new NoPermissionToCreateDocumentException("User can't create documents from document " + document.getId());
            }
            FileDocumentDomainObject fileDocument = (FileDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.FILE_ID, document, user);
            final EditFileDocumentPageFlow.ArrayMimeTypeRestriction mimeTypeRestriction = new EditFileDocumentPageFlow.ArrayMimeTypeRestriction(IMAGE_MIME_TYPES, ERROR_MESSAGE__ONLY_ALLOWED_TO_UPLOAD_IMAGES);
            DocumentPageFlow.SaveDocumentCommand saveNewImageFileDocument = new CreateDocumentPageFlow.SaveDocumentCommand() {
                public void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionInternalException, NoPermissionToAddDocumentToMenuException, DocumentSaveException {
                    FileDocumentDomainObject fileDocument = (FileDocumentDomainObject) document;
                    Map files = fileDocument.getFiles();
                    for ( FileDocumentDomainObject.FileDocumentFile file : (Iterable<FileDocumentDomainObject.FileDocumentFile>) files.values() ) {
                        file.setCreatedAsImage(true);
                    }
                    FileDocumentDomainObject.FileDocumentFile file = (FileDocumentDomainObject.FileDocumentFile) files.values().iterator().next();
                    if ( null != file ) {
                        fileDocument.setHeadline(file.getFilename());
                        fileDocument.setPublicationStatus(Document.PublicationStatus.APPROVED);
                        documentMapper.saveNewDocument(document, user, false);
                        setNewSourceAndClearSize(new FileDocumentImageSource(documentMapper.getDocumentReference(fileDocument)));
                    }
                }
            };
            DispatchCommand returnToImageEditPageCommand = new DispatchCommand() {
                public void dispatch(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException, ServletException {

                    forward(request, response);
                }
            };
            DocumentPageFlow pageFlow = new EditFileDocumentPageFlow(fileDocument, servletContext, returnToImageEditPageCommand, saveNewImageFileDocument, mimeTypeRestriction);
            pageFlow.dispatch(request, response);
        } catch ( NoPermissionToCreateDocumentException e ) {
            throw new ShouldHaveCheckedPermissionsEarlierException(e);
        }
    }

    private void goToImageBrowser(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException, ServletException {
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
            	setNewSourceAndClearSize(new ImagesPathRelativePathImageSource(imageUrl));
                forward(request, response);
            }
        });
        imageBrowser.forward(request, response);
    }
    
    private void goToCropImage(final HttpServletRequest request, final HttpServletResponse response) 
    		throws IOException, ServletException {
    	DispatchCommand returnCommand = new DispatchCommand() {
			public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
				forward(request, response);		
			}
		};
		
		Handler<CropRegion> cropHandler = new Handler<CropRegion>() {
			public void handle(CropRegion cropRegion) {
				image.setCropRegion(cropRegion);
				forceWidthHeight();
                restrictMaximumDimensions();
			}
		};
		
		ImageCropPage cropPage = new ImageCropPage(returnCommand, cropHandler, image, forcedWidth, forcedHeight);
		cropPage.forward(request, response);
    }

    private void goToImageSearch(final DocumentMapper documentMapper,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response) throws IOException, ServletException {
        DocumentFinder documentFinder = new DocumentFinder(new SearchDocumentsPage());
        documentFinder.setQueryParser(new HeadlineWildcardQueryParser());
        documentFinder.setCancelCommand(new DispatchCommand() {
            public void dispatch(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, ServletException {
                forward(request, response);
            }
        });
        documentFinder.setSelectDocumentCommand(new Handler<Integer>() {
            public void handle(Integer documentIdFound){
                if ( null != documentIdFound ) {
                	setNewSourceAndClearSize(new FileDocumentImageSource(documentMapper.getDocumentReference(documentIdFound)));
                }
            }
        });
        documentFinder.setRestrictingQuery(createImageFileDocumentsQuery());
        documentFinder.addExtraSearchResultColumn(new AdminManager.DatesSummarySearchResultColumn());
        documentFinder.addExtraSearchResultColumn(new ImageThumbnailSearchResultColumn());
        documentFinder.forward(request, response);
    }

    private void setNewSourceAndClearSize(ImageSource imageSource) {
    	image.setSourceAndClearSize(imageSource);
        image.setFormat(image.getImageInfo().getFormat());
        image.setCropRegion(new CropRegion());
        forceWidthHeight();
        restrictMaximumDimensions();
        
        Format imageFormat = image.getImageInfo().getFormat();
        boolean allowed = false;
        for (Format allowedFormat : ALLOWED_FORMATS) {
        	if (imageFormat == allowedFormat) {
        		allowed = true;
        	}
        }
        image.setFormat((allowed ? imageFormat : Format.PNG));
    }
    
    static boolean userHasImagePermissionsOnDocument(UserDomainObject user, TextDocumentDomainObject document) {
        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject) user.getPermissionSetFor(document);
        return textDocumentPermissionSet.getEditImages();
    }

    private Query createImageFileDocumentsQuery() {
        BooleanQuery imageMimeTypeQuery = new BooleanQuery();

        for ( String imageMimeType : IMAGE_MIME_TYPES ) {
            imageMimeTypeQuery.add(new TermQuery(new Term(DocumentIndex.FIELD__MIME_TYPE, imageMimeType)), false, false);
        }

        TermQuery fileDocumentQuery = new TermQuery(new Term(DocumentIndex.FIELD__DOC_TYPE_ID, ""
                                                                                               + DocumentTypeDomainObject
                .FILE_ID));

        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(fileDocumentQuery, true, false);
        booleanQuery.add(imageMimeTypeQuery, true, false);
        return booleanQuery;
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
    
    public static boolean allowImageArchive(UserDomainObject user) {
        return allowAccess(user, Imcms.getServices().getConfig().getImageArchiveAllowedRoleIdList());
    }
    
    public static boolean allowChooseFile(UserDomainObject user) {
        return allowAccess(user, Imcms.getServices().getConfig().getChooseFileAllowedRoleIdsList());
    }
    
    private static boolean allowAccess(UserDomainObject user, List<RoleId> roleIds) {
        if (roleIds.isEmpty()) {
            return true;
        }
        
        for (RoleId roleId : roleIds) {
            if (user.hasRoleId(roleId)) {
                return true;
            }
        }
        
        return false;
    }

    public boolean isLinkable() {
        return linkable;
    }

    public int getForcedWidth() {
		return forcedWidth;
	}

	public void setForcedWidth(int forcedWidth) {
		this.forcedWidth = forcedWidth;
	}

	public int getForcedHeight() {
		return forcedHeight;
	}

	public void setForcedHeight(int forcedHeight) {
		this.forcedHeight = forcedHeight;
	}

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }
    
    private Resize getResize() {
        if (maxWidth > 0 || maxHeight > 0) {
            return Resize.GREATER_THAN;
        }
        
        return null;
    }

	public TextDocumentDomainObject getDocument() {
		return document;
	}

	public void setImage(ImageDomainObject image) {
		this.image = image;
	}

	public Integer getImageIndex() {
		return imageIndex;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	private static class HeadlineWildcardQueryParser implements QueryParser {
        public Query parse(String queryString) {
            String[] queryStrings = StringUtils.split(queryString);
            BooleanQuery wildcardsQuery = new BooleanQuery();
            for ( String queryTerm : queryStrings ) {
                wildcardsQuery.add(new WildcardQuery(new Term(DocumentIndex.FIELD__META_HEADLINE, "*" + queryTerm
                                                                                                  + "*")), true, false);
            }
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(wildcardsQuery, false, false);
            try {
                booleanQuery.add(new DefaultQueryParser().parse(queryString), false, false);
            } catch ( ParseException e ) {
            }
            return booleanQuery;
        }
    }

    private static class ImageThumbnailSearchResultColumn implements DocumentFinder.SearchResultColumn {
        public String render(DocumentDomainObject document, HttpServletRequest request, HttpServletResponse response) {
            UserDomainObject user = Utility.getLoggedOnUser(request);
            FileDocumentDomainObject imageFileDocument = (FileDocumentDomainObject) document;
            ImageSize imageSize = new ImageSize(0, 0);
            long fileSize;
            InputStreamSource inputStreamSource = imageFileDocument.getDefaultFile().getInputStreamSource();
            try {
                InputStream inputStream = inputStreamSource.getInputStream();
                fileSize = inputStreamSource.getSize();
                try {
                    imageSize = ImageSize.fromInputStream(inputStream);
                } catch ( IOException ignored ) {
                }
            } catch ( IOException ioe ) {
                throw new UnhandledException(ioe);
            }

            List values = Arrays.asList(new Object[] {
                "imageUrl", "GetDoc?meta_id=" + document.getId(),
                "imageSize", imageSize,
                "fileSize", HumanReadable.getHumanReadableByteSize(fileSize).replaceAll(" ", "&nbsp;"),
            });
            return Imcms.getServices().getAdminTemplate("images/thumbnail.frag", user, values);
        }

        public LocalizedMessage getName() {
            return new LocalizedMessage("server/src/com/imcode/imcms/servlet/admin/ChangeImage/search/image_thumbnail_label");
        }
    }

    protected void dispatchOk(HttpServletRequest request,
                              HttpServletResponse response) throws IOException, ServletException {
        imageCommand.handle(image);
        super.dispatchOk(request, response);
    }

}
