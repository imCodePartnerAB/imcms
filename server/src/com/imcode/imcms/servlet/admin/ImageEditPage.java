package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.flow.*;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.NoPermissionInternalException;
import com.imcode.imcms.servlet.DocumentFinder;
import com.imcode.imcms.servlet.SearchDocumentsPage;
import com.imcode.imcms.servlet.superadmin.AdminManager;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.util.HumanReadable;
import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.index.DefaultQueryParser;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.QueryParser;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import imcode.util.ImcmsImageUtils;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;
import imcode.util.io.InputStreamSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ImageEditPage extends OkCancelPage {

    public final static String REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH_BUTTON = "goToImageSearch";
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON = "goToImageBrowser";
    public static final String REQUEST_PARAMETER__GO_TO_ADD_RESTRICTED_IMAGE_BUTTON = "goToAddRestrictedImage";
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
    static final LocalizedMessage ERROR_MESSAGE__ONLY_ALLOWED_TO_UPLOAD_IMAGES = new LocalizedMessage("error/servlet/images/only_allowed_to_upload_images");
    private final static String[] IMAGE_MIME_TYPES = new String[] { "image/jpeg", "image/png", "image/gif" };

    private TextDocumentDomainObject document;
    private ImageDomainObject image;
    private String label;
    private final ServletContext servletContext;
    private final Handler<ImageDomainObject> imageCommand;
    private final LocalizedMessage heading;

    public ImageEditPage(TextDocumentDomainObject document, ImageDomainObject image,
                         LocalizedMessage heading, String label, ServletContext servletContext,
                         Handler<ImageDomainObject> imageCommand,
                         DispatchCommand returnCommand
    ) {
        super(returnCommand, returnCommand);
        this.document = document;
        this.image = image;
        this.label = label;
        this.servletContext = servletContext;
        this.imageCommand = imageCommand;
        this.heading = heading ;
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

    private static ImageDomainObject getImageFromRequest(HttpServletRequest req) {
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
        String imageUrl = req.getParameter(REQUEST_PARAMETER__IMAGE_URL);
        if ( null != imageUrl && imageUrl.startsWith(req.getContextPath()) ) {
            imageUrl = imageUrl.substring(req.getContextPath().length());
        }
        ImageSource imageSource = ImcmsImageUtils.createImageSourceFromString(imageUrl);

        image.setSource(imageSource);
        image.setName(StringUtils.trim(req.getParameter(REQUEST_PARAMETER__IMAGE_NAME)));
        image.setAlign(req.getParameter(REQUEST_PARAMETER__IMAGE_ALIGN));
        image.setAlternateText(req.getParameter(REQUEST_PARAMETER__IMAGE_ALT));
        image.setLowResolutionUrl(req.getParameter(REQUEST_PARAMETER__IMAGE_LOWSRC));
        image.setTarget(EditDocumentInformationPageFlow.getTargetFromRequest(req));
        image.setLinkUrl(req.getParameter(REQUEST_PARAMETER__LINK_URL));
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
        } else if ( null != request.getParameter(REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH_BUTTON) ) {
            goToImageSearch(documentMapper, request, response);
        } else if ( null != request.getParameter(REQUEST_PARAMETER__GO_TO_ADD_RESTRICTED_IMAGE_BUTTON) ) {
            goToImageAdder(documentMapper, user, request, response);
        }
    }

    private void goToImageAdder(final DocumentMapper documentMapper,
                                UserDomainObject user,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException, ServletException {
        try {
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
                        image.setSourceAndClearSize(new FileDocumentImageSource(documentMapper.getDocumentReference(fileDocument)));
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
                image.setSourceAndClearSize(new ImagesPathRelativePathImageSource(imageUrl));
                forward(request, response);
            }
        });
        imageBrowser.forward(request, response);
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
        documentFinder.setSelectDocumentCommand(new DocumentFinder.SelectDocumentCommand() {
            public void selectDocument(DocumentDomainObject documentFound
            ) throws IOException, ServletException {
                FileDocumentDomainObject imageFileDocument = (FileDocumentDomainObject) documentFound;
                if ( null != imageFileDocument ) {
                    image.setSourceAndClearSize(new FileDocumentImageSource(documentMapper.getDocumentReference(imageFileDocument)));
                }

                forward(request, response);
            }
        });
        documentFinder.setRestrictingQuery(createImageFileDocumentsQuery());
        documentFinder.addExtraSearchResultColumn(new AdminManager.DatesSummarySearchResultColumn());
        documentFinder.addExtraSearchResultColumn(new ImageThumbnailSearchResultColumn());
        documentFinder.forward(request, response);
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
