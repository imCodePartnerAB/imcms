package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.*;
import com.imcode.imcms.servlet.DocumentFinder;
import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.QueryParser;
import imcode.server.document.index.DefaultQueryParser;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ImageData;
import imcode.util.ImageParser;
import imcode.util.LocalizedMessage;
import imcode.util.Utility;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.queryParser.ParseException;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Arrays;

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
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
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
            goToImageSearch( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_ADD_RESTRICTED_IMAGE_BUTTON ) ) {
            goToImageAdder( documentMapper, document, user, image, imageIndex, request, response );
        } else {
            document.setImage( imageIndex, image );
            documentMapper.saveDocument( document, user );
            imcref.updateMainLog( "ImageRef " + imageIndex + " =" + image.getUrl() +
                                  " in  " + "[" + document.getId() + "] modified by user: [" +
                                  user.getFullName() + "]" );
            goBack( document, response );
        }
    }

    private void goToImageAdder( final DocumentMapper documentMapper, final TextDocumentDomainObject document,
                                 UserDomainObject user, final ImageDomainObject image, final int imageIndex,
                                 HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)documentMapper.createDocumentOfTypeFromParent( DocumentDomainObject.DOCTYPE_FILE, document, user );
        DocumentPageFlow.SaveDocumentCommand saveNewImageFileDocument = new CreateDocumentPageFlow.SaveDocumentCommand() {
            public void saveDocument( DocumentDomainObject document, UserDomainObject user ) {
                FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
                if ( ArrayUtils.contains( IMAGE_MIME_TYPES, fileDocument.getMimeType() ) ) {
                    fileDocument.setHeadline( fileDocument.getFilename() );
                    fileDocument.setStatus( DocumentDomainObject.STATUS_PUBLICATION_APPROVED );
                    fileDocument.setCreatedAsImage( true );
                    documentMapper.saveNewDocument( document, user );
                    image.setUrlAndClearSize( "../servlet/GetDoc?meta_id=" + document.getId() );
                }
            }
        };
        DispatchCommand returnToImageEditPageCommand = new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        };
        EditFileDocumentPageFlow.MimeTypeRestriction mimeTypeRestriction = new EditFileDocumentPageFlow.MimeTypeRestriction( IMAGE_MIME_TYPES, ERROR_MESSAGE___ONLY_ALLOWED_TO_UPLOAD_IMAGES );
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
                image.setUrlAndClearSize( imageUrl );
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        imageBrowser.forward( request, response );
    }

    private void goToImageSearch( final TextDocumentDomainObject document, final int imageIndex,
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
                    image.setUrlAndClearSize( "../servlet/GetDoc?meta_id=" + imageFileDocument.getId() );
                }
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        documentFinder.setRestrictingQuery( QUERY__IMAGE_FILE_DOCUMENTS );
        documentFinder.addExtraSearchResultColumn(new ImageThumbnailSearchResultColumn()) ;
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
        image.setUrl( req.getParameter( REQUEST_PARAMETER__IMAGE_URL ) );
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
                               + IMCConstants.DISPATCH_FLAG__EDIT_TEXT_DOCUMENT_IMAGES );
    }

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
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
        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentMapper.getUsersMostPrivilegedPermissionSetOnDocument( user, document );
        boolean imagePermission = textDocumentPermissionSet.getEditImages();
        return imagePermission;
    }

    private void goToImageEditPage( TextDocumentDomainObject document, int imageIndex, ImageDomainObject image,
                                    HttpServletRequest request,
                                    HttpServletResponse response ) throws IOException, ServletException {

        ImageData imageData = getImageData( image );
        String label = StringUtils.defaultString( request.getParameter( REQUEST_PARAMETER__LABEL ) );

        new ImageEditPage( document, imageIndex, image, imageData, label ).forward( request, response );
    }

    private ImageData getImageData( ImageDomainObject image ) throws IOException {
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        DocumentMapper documentMapper = service.getDocumentMapper();
        File image_path = service.getConfig().getImagePath();
        ImageData imageData = new ImageData( 0, 0 );
        String imageUrl = image.getUrl();
        if ( StringUtils.isNotBlank( imageUrl ) ) {
            File imageFile = new File( image_path, image.getUrl() );
            Integer imageFileDocumentId = getDocumentIdFromImageUrl( imageUrl );
            if ( null != imageFileDocumentId ) {
                FileDocumentDomainObject imageFileDocument = (FileDocumentDomainObject)documentMapper.getDocument( imageFileDocumentId.intValue() );
                imageData = getImageDataFromFileDocument( imageFileDocument );
            } else if ( imageFile.isFile() ) {
                imageData = new ImageParser().parseImageFile( imageFile );
            }
        }
        return imageData;
    }

    public static Integer getDocumentIdFromImageUrl( String imageUrl ) {
        Integer documentId = null;
        Perl5Util perl5util = new Perl5Util();
        if ( perl5util.match( "/GetDoc\\?meta_id=(\\d+)/", imageUrl ) ) {
            documentId = Integer.valueOf( perl5util.group( 1 ) );
        }
        return documentId;
    }

    private ImageData getImageDataFromFileDocument( FileDocumentDomainObject imageFileDocument ) {
        ImageData imageData;
        try {
            InputStream imageFileDocumentInputStream = imageFileDocument.getInputStreamSource().getInputStream();
            imageData = new ImageParser().parseImageStream( imageFileDocumentInputStream, imageFileDocument.getFilename() );
        } catch ( IllegalArgumentException iae ) {
            imageData = new ImageData( 0, 0 );
        } catch ( IOException ioe ) {
            imageData = new ImageData( 0, 0 );
        }
        return imageData;
    }

    private int getImageNumberParam( HttpServletRequest req ) {
        return Integer.parseInt( req.getParameter( REQUEST_PARAMETER__IMAGE_INDEX ) );
    }

    public static class ImageEditPage {

        public static final String REQUEST_ATTRIBUTE__PAGE = "page";

        private TextDocumentDomainObject document;
        private int imageIndex;
        private ImageDomainObject image;
        private ImageData imageFileData;
        private String label;

        public ImageEditPage( TextDocumentDomainObject document, int imageIndex, ImageDomainObject image,
                              ImageData imageFileData, String label ) {
            this.document = document;
            this.image = image;
            this.imageIndex = imageIndex;
            this.imageFileData = imageFileData;
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

        public ImageData getImageFileData() {
            return imageFileData;
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
            BooleanQuery booleanQuery = new BooleanQuery() ;
            booleanQuery.add(wildcardsQuery, false, false) ;
            try {
                booleanQuery.add(new DefaultQueryParser().parse( queryString ), false, false) ;
            } catch ( ParseException e ) {
            }
            return booleanQuery;
        }
    }

    private static class ImageThumbnailSearchResultColumn implements DocumentFinder.SearchResultColumn {

        public String render( DocumentDomainObject document, HttpServletRequest request ) {
            UserDomainObject user = Utility.getLoggedOnUser( request );
            List values = Arrays.asList(new String[] { "imageUrl", "GetDoc?meta_id="+document.getId() } );
            return ApplicationServer.getIMCServiceInterface().getAdminTemplate( "images/thumbnail.frag", user, values ) ;
        }

        public LocalizedMessage getName() {
            return new LocalizedMessage( "server/src/com/imcode/imcms/servlet/admin/ChangeImage/search/image_thumbnail_label" ) ;
        }
    }
}
