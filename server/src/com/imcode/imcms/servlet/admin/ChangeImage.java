package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import com.imcode.imcms.servlet.DocumentFinder;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.document.*;
import imcode.server.document.index.DefaultQueryParser;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.QueryParser;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ChangeImage extends HttpServlet {

    public final static String REQUEST_PARAMETER__IMAGE_URL = "imageref";
    public final static String REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH_BUTTON = "goToImageSearch";
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON = "goToImageBrowser";
    public static final String REQUEST_PARAMETER__GO_TO_ADD_RESTRICTED_IMAGE_BUTTON = "goToAddRestrictedImage";
    public static final String REQUEST_PARAMETER__GO_TO_EDIT_DOCUMENT = "toEdit";
    public static final String REQUEST_PARAMETER__DOCUMENT_ID_TO_EDIT = "documentIdToEdit";
    public static final String REQUEST_PARAMETER__OK_BUTTON = "ok";
    public static final String REQUEST_PARAMETER__PREVIEW_BUTTON = "show_img";
    public static final String REQUEST_PARAMETER__IMAGE_HEIGHT = "image_height";
    public static final String REQUEST_PARAMETER__IMAGE_WIDTH = "image_width";
    public static final String REQUEST_PARAMETER__IMAGE_BORDER = "image_border";
    public static final String REQUEST_PARAMETER__VERTICAL_SPACE = "v_space";
    public static final String REQUEST_PARAMETER__HORIZONTAL_SPACE = "h_space";
    public static final String REQUEST_PARAMETER__IMAGE_NAME = "image_name";
    public static final String REQUEST_PARAMETER__IMAGE_INDEX = "img";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__DELETE_BUTTON = "delete";
    public static final String REQUEST_PARAMETER__IMAGE_ALIGN = "image_align";
    public static final String REQUEST_PARAMETER__IMAGE_ALT = "alt_text";
    public static final String REQUEST_PARAMETER__IMAGE_LOWSRC = "low_scr";
    public static final String REQUEST_PARAMETER__DIRECTORY = "directory";
    public static final String REQUEST_PARAMETER__DOCUMENT_ID = "documentId";
    public static final String REQUEST_PARAMETER__LABEL = "label";
    public static final String REQUEST_PARAMETER__LINK_URL = "imageref_link";
    public static final String REQUEST_PARAMETER__LINK_TARGET = EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET;

    private final static String[] IMAGE_MIME_TYPES = new String[]{"image/jpeg", "image/png", "image/gif"};
    static final LocalizedMessage ERROR_MESSAGE___ONLY_ALLOWED_TO_UPLOAD_IMAGES = new LocalizedMessage( "error/servlet/images/only_allowed_to_upload_images" );
    private static final Query QUERY__IMAGE_FILE_DOCUMENTS = createImageFileDocumentsQuery();

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        ImageDomainObject image = getImageFromRequest( request );
        UserDomainObject user = Utility.getLoggedOnUser( request );
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        final TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( Integer.parseInt( request.getParameter( REQUEST_PARAMETER__DOCUMENT_ID ) ) );
        final int imageIndex = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__IMAGE_INDEX ) );

        if ( !userHasImagePermissionsOnDocument( user, document ) ) {
            goBack( document, response );
            return;
        }

        if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            goBack( document, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__DELETE_BUTTON ) ) {
            image = new ImageDomainObject();
            goToImageEditPage( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__PREVIEW_BUTTON ) ) {
            goToImageEditPage( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON ) ) {
            goToImageBrowser( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH_BUTTON ) ) {
            goToImageSearch( documentMapper, document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_ADD_RESTRICTED_IMAGE_BUTTON ) ) {
            goToImageAdder( documentMapper, document, user, image, imageIndex, request, response );
        } else {
            document.setImage( imageIndex, image );
            documentMapper.saveDocument( document, user );
            imcref.updateMainLog( "ImageRef " + imageIndex + " =" + image.getUrlPathRelativeToContextPath() +
                                  " in  " + "[" + document.getId() + "] modified by user: [" +
                                  user.getFullName() + "]" );
            goBack( document, response );
        }
    }

    private void goToImageAdder( final DocumentMapper documentMapper, final TextDocumentDomainObject document,
                                 UserDomainObject user, final ImageDomainObject image, final int imageIndex,
                                 HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)documentMapper.createDocumentOfTypeFromParent( DocumentDomainObject.DOCTYPE_FILE, document, user );
        final EditFileDocumentPageFlow.ArrayMimeTypeRestriction mimeTypeRestriction = new EditFileDocumentPageFlow.ArrayMimeTypeRestriction( IMAGE_MIME_TYPES, ERROR_MESSAGE___ONLY_ALLOWED_TO_UPLOAD_IMAGES );
        DocumentPageFlow.SaveDocumentCommand saveNewImageFileDocument = new CreateDocumentPageFlow.SaveDocumentCommand() {
            public void saveDocument( DocumentDomainObject document, UserDomainObject user ) {
                FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
                Map files = fileDocument.getFiles();
                for ( Iterator iterator = files.values().iterator(); iterator.hasNext(); ) {
                    FileDocumentDomainObject.FileDocumentFile file = (FileDocumentDomainObject.FileDocumentFile)iterator.next();
                    file.setCreatedAsImage( true );
                }
                FileDocumentDomainObject.FileDocumentFile file = (FileDocumentDomainObject.FileDocumentFile)files.values().iterator().next() ;
                if ( null != file ) {
                    fileDocument.setHeadline( file.getFilename() );
                    fileDocument.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
                    documentMapper.saveNewDocument( document, user );
                    image.setSourceAndClearSize( new ImageDomainObject.FileDocumentImageSource( documentMapper.getDocumentReference( fileDocument ) ) );
                }
            }
        };
        DispatchCommand returnToImageEditPageCommand = new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        };
        DocumentPageFlow pageFlow = new EditFileDocumentPageFlow( fileDocument, getServletContext(), returnToImageEditPageCommand, saveNewImageFileDocument, mimeTypeRestriction );
        pageFlow.dispatch( request, response );
    }

    private void goToImageBrowser( final TextDocumentDomainObject document, final int imageIndex,
                                   final ImageDomainObject image, final HttpServletRequest request,
                                   final HttpServletResponse response ) throws IOException, ServletException {
        ImageBrowser imageBrowser = new ImageBrowser();
        imageBrowser.setCancelCommand( new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        imageBrowser.setSelectImageUrlCommand( new ImageBrowser.SelectImageUrlCommand() {
            public void selectImageUrl( String imageUrl, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                image.setSourceAndClearSize( new ImageDomainObject.ImagesPathRelativePathImageSource( imageUrl ) );
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        imageBrowser.forward( request, response );
    }

    private void goToImageSearch( final DocumentMapper documentMapper, final TextDocumentDomainObject document, final int imageIndex,
                                  final ImageDomainObject image, final HttpServletRequest request,
                                  final HttpServletResponse response ) throws IOException, ServletException {
        DocumentFinder documentFinder = new DocumentFinder();
        documentFinder.setQueryParser( new HeadlineWildcardQueryParser() );
        documentFinder.setCancelCommand( new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        documentFinder.setSelectDocumentCommand( new DocumentFinder.SelectDocumentCommand() {
            public void selectDocument( DocumentDomainObject documentFound, HttpServletRequest request,
                                        HttpServletResponse response ) throws IOException, ServletException {
                FileDocumentDomainObject imageFileDocument = (FileDocumentDomainObject)documentFound;
                if ( null != imageFileDocument ) {
                    image.setSourceAndClearSize( new ImageDomainObject.FileDocumentImageSource( documentMapper.getDocumentReference( imageFileDocument ) ) );
                }
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        documentFinder.setRestrictingQuery( QUERY__IMAGE_FILE_DOCUMENTS );
        documentFinder.addExtraSearchResultColumn( new ImageThumbnailSearchResultColumn() );
        documentFinder.forward( request, response );
    }

    private static Query createImageFileDocumentsQuery() {
        BooleanQuery imageMimeTypeQuery = new BooleanQuery();
        for ( int i = 0; i < IMAGE_MIME_TYPES.length; i++ ) {
            String imageMimeType = IMAGE_MIME_TYPES[i];
            imageMimeTypeQuery.add( new TermQuery( new Term( DocumentIndex.FIELD__MIME_TYPE, imageMimeType ) ), false, false );
        }

        TermQuery fileDocumentQuery = new TermQuery( new Term( DocumentIndex.FIELD__DOC_TYPE_ID, ""
                                                                                                 + DocumentDomainObject.DOCTYPE_FILE ) );

        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add( fileDocumentQuery, true, false );
        booleanQuery.add( imageMimeTypeQuery, true, false );
        return booleanQuery;
    }

    private ImageDomainObject getImageFromRequest( HttpServletRequest req ) {
        ImageDomainObject image = new ImageDomainObject();
        try {
            image.setWidth( Integer.parseInt( req.getParameter( REQUEST_PARAMETER__IMAGE_WIDTH ) ) );
        } catch ( NumberFormatException ignored ) {
        }
        try {
            image.setHeight( Integer.parseInt( req.getParameter( REQUEST_PARAMETER__IMAGE_HEIGHT ) ) );
        } catch ( NumberFormatException ignored ) {
        }
        try {
            image.setBorder( Integer.parseInt( req.getParameter( REQUEST_PARAMETER__IMAGE_BORDER ) ) );
        } catch ( NumberFormatException ignored ) {
        }
        try {
            image.setVerticalSpace( Integer.parseInt( req.getParameter( REQUEST_PARAMETER__VERTICAL_SPACE ) ) );
        } catch ( NumberFormatException ignored ) {
        }
        try {
            image.setHorizontalSpace( Integer.parseInt( req.getParameter( REQUEST_PARAMETER__HORIZONTAL_SPACE ) ) );
        } catch ( NumberFormatException ignored ) {
        }
        String imageUrl = req.getParameter( REQUEST_PARAMETER__IMAGE_URL );
        ImageDomainObject.ImageSource imageSource = ImcmsImageUtils.createImageSourceFromString( imageUrl );

        image.setSource( imageSource );
        image.setName( req.getParameter( REQUEST_PARAMETER__IMAGE_NAME ).trim() );
        image.setAlign( req.getParameter( REQUEST_PARAMETER__IMAGE_ALIGN ) );
        image.setAlternateText( req.getParameter( REQUEST_PARAMETER__IMAGE_ALT ) );
        image.setLowResolutionUrl( req.getParameter( REQUEST_PARAMETER__IMAGE_LOWSRC ) );
        image.setTarget( EditDocumentInformationPageFlow.getTargetFromRequest( req ) );
        image.setLinkUrl( req.getParameter( REQUEST_PARAMETER__LINK_URL ) );
        return image;
    }

    private void goBack( TextDocumentDomainObject document, HttpServletResponse response ) throws IOException {
        response.sendRedirect( "AdminDoc?meta_id=" + document.getId() + "&flags="
                               + ImcmsConstants.DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_IMAGES );
    }

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( Integer.parseInt( request.getParameter( "meta_id" ) ) );
        ImageDomainObject image = document.getImage( getImageNumberParam( request ) );
        UserDomainObject user = Utility.getLoggedOnUser( request );

        // Check if user has image rights
        if ( !userHasImagePermissionsOnDocument( user, document ) ) {
            Utility.redirectToStartDocument( request, response );
            return;
        }

        goToImageEditPage( document, getImageNumberParam( request ), image, request, response );

    }

    private boolean userHasImagePermissionsOnDocument( UserDomainObject user, TextDocumentDomainObject document ) {
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );
        boolean imagePermission = textDocumentPermissionSet.getEditImages();
        return imagePermission;
    }

    private void goToImageEditPage( TextDocumentDomainObject document, int imageIndex, ImageDomainObject image,
                                    HttpServletRequest request,
                                    HttpServletResponse response ) throws IOException, ServletException {

        String label = StringUtils.defaultString( request.getParameter( REQUEST_PARAMETER__LABEL ) );

        new ImageEditPage( document, imageIndex, image, label ).forward( request, response );
    }

    private int getImageNumberParam( HttpServletRequest req ) {
        return Integer.parseInt( req.getParameter( REQUEST_PARAMETER__IMAGE_INDEX ) );
    }

    public static class ImageEditPage {

        public static final String REQUEST_ATTRIBUTE__PAGE = "page";

        private TextDocumentDomainObject document;
        private int imageIndex;
        private ImageDomainObject image;
        private String label;

        public ImageEditPage( TextDocumentDomainObject document, int imageIndex, ImageDomainObject image,
                              String label ) {
            this.document = document;
            this.image = image;
            this.imageIndex = imageIndex;
            this.label = label;
        }

        public TextDocumentDomainObject getDocument() {
            return document;
        }

        public ImageDomainObject getImage() {
            return image;
        }

        public int getImageIndex() {
            return imageIndex;
        }

        private void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__PAGE, this );
            UserDomainObject user = Utility.getLoggedOnUser( request );
            request.getRequestDispatcher( "/imcms/" + user.getLanguageIso639_2() + "/jsp/change_img.jsp" ).forward( request, response );
        }

        public String getLabel() {
            return label;
        }

    }

    private static class HeadlineWildcardQueryParser implements QueryParser {

        public Query parse( String queryString ) {
            String[] queryStrings = StringUtils.split( queryString );
            BooleanQuery wildcardsQuery = new BooleanQuery();
            for ( int i = 0; i < queryStrings.length; i++ ) {
                String queryTerm = queryStrings[i];
                wildcardsQuery.add( new WildcardQuery( new Term( DocumentIndex.FIELD__META_HEADLINE, "*" + queryTerm
                                                                                                     + "*" ) ), true, false );
            }
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add( wildcardsQuery, false, false );
            try {
                booleanQuery.add( new DefaultQueryParser().parse( queryString ), false, false );
            } catch ( ParseException e ) {
            }
            return booleanQuery;
        }
    }

    private static class ImageThumbnailSearchResultColumn implements DocumentFinder.SearchResultColumn {

        public String render( DocumentDomainObject document, HttpServletRequest request ) {
            UserDomainObject user = Utility.getLoggedOnUser( request );
            FileDocumentDomainObject imageFileDocument = (FileDocumentDomainObject)document;
            ImageSize imageSize = new ImageSize( 0, 0 );
            long fileSize ;
            InputStreamSource inputStreamSource = imageFileDocument.getDefaultFile().getInputStreamSource();
            try {
                InputStream inputStream = inputStreamSource.getInputStream();
                fileSize = inputStreamSource.getSize();
                try {
                    imageSize = Utility.getImageSize( inputStream );
                } catch ( IOException ignored ) {
                }
            } catch ( IOException ioe ) {
                throw new UnhandledException( ioe );
            }

            List values = Arrays.asList( new Object[]{
                "imageUrl", "GetDoc?meta_id=" + document.getId(),
                "imageSize", imageSize,
                "fileSize", Utility.getHumanReadableSize( fileSize, "&nbsp;" ),
            } );
            return Imcms.getServices().getAdminTemplate( "images/thumbnail.frag", user, values );
        }

        public LocalizedMessage getName() {
            return new LocalizedMessage( "server/src/com/imcode/imcms/servlet/admin/ChangeImage/search/image_thumbnail_label" );
        }
    }
}
