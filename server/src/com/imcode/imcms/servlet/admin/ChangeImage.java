package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import com.imcode.imcms.servlet.DocumentFinder;
import com.imcode.imcms.servlet.WebComponent;
import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class ChangeImage extends HttpServlet {

    final static String REQUEST_PARAMETER__IMAGE_URL = "imageref";
    public final static String REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH = "goToImageSearch";
    public static final String REQUEST_PARAMETER__OK_BUTTON = "ok";
    public static final String REQUEST_PARAMETER__PREVIEW_BUTTON = "show_img";
    private static final String REQUEST_PARAMETER__IMAGE_HEIGHT = "image_height";
    private static final String REQUEST_PARAMETER__IMAGE_WIDTH = "image_width";
    private static final String REQUEST_PARAMETER__IMAGE_BORDER = "image_border";
    private static final String REQUEST_PARAMETER__VERTICAL_SPACE = "v_space";
    private static final String REQUEST_PARAMETER__HORIZONTAL_SPACE = "h_space";
    private static final String REQUEST_PARAMETER__IMAGE_NAME = "image_name";
    public static final String REQUEST_PARAMETER__IMAGE_INDEX = "img";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__DELETE_BUTTON = "delete";
    private static final String REQUEST_PARAMETER__IMAGE_ALIGN = "image_align";
    private static final String REQUEST_PARAMETER__IMAGE_ALT = "alt_text";
    private static final String REQUEST_PARAMETER__IMAGE_LOWSRC = "low_scr";
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER = "goToImageBrowser";
    public static final String REQUEST_PARAMETER__UPLOAD_BUTTON = "upload";
    public static final String REQUEST_PARAMETER__DIRECTORY = "directory";
    public static final String REQUEST_PARAMETER__FILE = "file";
    public static final String REQUEST_PARAMETER__DOCUMENT_ID = "documentId";
    public static final String REQUEST_PARAMETER__LABEL = "label";

    private final static Logger log = Logger.getLogger( ChangeImage.class.getName() );

    public void doPost( HttpServletRequest req, HttpServletResponse response ) throws ServletException, IOException {
        MultipartHttpServletRequest request = new MultipartHttpServletRequest( req );

        final ImageDomainObject image = getImageFromRequest( request );
        UserDomainObject user = Utility.getLoggedOnUser( request );
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        final TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( Integer.parseInt( request.getParameter( REQUEST_PARAMETER__DOCUMENT_ID ) ) );
        final int imageIndex = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__IMAGE_INDEX ) );

        if ( !userHasImagePermissionsOnDocument( user, document ) ) {
            goBack( document, response );
            return;
        }

        if ( null != request.getParameter( REQUEST_PARAMETER__UPLOAD_BUTTON ) ) {
            saveFileFromRequest( request, user );
            goToImageEditPage( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            goBack( document, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__DELETE_BUTTON ) ) {
            image.setUrl( "" );
            goToImageEditPage( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__PREVIEW_BUTTON ) ) {
            goToImageEditPage( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER ) ) {
            goToImageBrowser( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH ) ) {
            goToImageSearch( document, imageIndex, image, request, response );
        } else {
            document.setImage( imageIndex, image );
            documentMapper.saveDocument( document, user );
            imcref.updateMainLog( "ImageRef " + imageIndex + " =" + image.getUrl() +
                                  " in  " + "[" + document.getId() + "] modified by user: [" +
                                  user.getFullName() + "]" );
            goBack( document, response );
        }
    }

    private void goToImageBrowser( final TextDocumentDomainObject document, final int imageIndex,
                                   final ImageDomainObject image, final HttpServletRequest request,
                                   final HttpServletResponse response ) throws IOException, ServletException {
        ImageBrowser imageBrowser = new ImageBrowser();
        imageBrowser.setCancelCommand( new WebComponent.CancelCommand() {
            public void cancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        imageBrowser.setSelectImageUrlCommand( new ImageBrowser.SelectImageCommand() {
            public void selectImageUrl( String imageUrl, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                image.setUrl( imageUrl );
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        imageBrowser.forward( request, response );
    }

    private void saveFileFromRequest( MultipartHttpServletRequest request, UserDomainObject user ) {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        File imagePath = imcref.getConfig().getImagePath();

        String relativeDestinationDir = request.getParameter( REQUEST_PARAMETER__DIRECTORY );
        File destinationDir = new File( imagePath.getParentFile(), relativeDestinationDir );
        FileItem fileItem = request.getParameterFileItem( REQUEST_PARAMETER__FILE );
        File destinationFile = new File( destinationDir, fileItem.getName() );
        if ( !FileUtility.directoryIsAncestorOfOrEqualTo( imagePath, destinationFile ) ) {
            log.info( "User " + user + " was denied uploading to file " + destinationFile );
            return;
        }
        if ( !destinationFile.exists() ) {
            try {
                fileItem.write( destinationFile );
            } catch ( Exception e ) {
                throw new UnhandledException( "Failed to write file. Possible permissions problem?", e );
            }
        }
    }

    private void goToImageSearch( final TextDocumentDomainObject document, final int imageIndex,
                                  final ImageDomainObject image, final HttpServletRequest request,
                                  final HttpServletResponse response ) throws IOException, ServletException {
        DocumentFinder documentFinder = new DocumentFinder();
        documentFinder.setCancelCommand( new DocumentFinder.CancelCommand() {
            public void cancel( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        documentFinder.setSelectDocumentCommand( new DocumentFinder.SelectDocumentCommand() {
            public void selectDocument( DocumentDomainObject documentFound, HttpServletRequest request,
                                        HttpServletResponse response ) throws IOException, ServletException {
                FileDocumentDomainObject imageFileDocument = (FileDocumentDomainObject)documentFound;
                if ( null != imageFileDocument ) {
                    image.setUrl( "../servlet/GetDoc?meta_id=" + imageFileDocument.getId() );
                    image.setWidth( 0 );
                    image.setHeight( 0 );
                }
                goToImageEditPage( document, imageIndex, image, request, response );
            }
        } );
        Query imageFileDocumentQuery = createImageFileDocumentQuery();
        documentFinder.setRestrictingQuery( imageFileDocumentQuery );
        documentFinder.forward( request, response );
    }

    private Query createImageFileDocumentQuery() {
        BooleanQuery imageMimeTypeQuery = new BooleanQuery();
        imageMimeTypeQuery.add( new TermQuery( new Term( DocumentIndex.FIELD__MIME_TYPE, "image/jpeg" ) ), false, false );
        imageMimeTypeQuery.add( new TermQuery( new Term( DocumentIndex.FIELD__MIME_TYPE, "image/png" ) ), false, false );
        imageMimeTypeQuery.add( new TermQuery( new Term( DocumentIndex.FIELD__MIME_TYPE, "image/gif" ) ), false, false );

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
        image.setLinkUrl( req.getParameter( "imageref_link" ) );
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

        String browsedImageUrl = getChosenImageFromImageBrowse( request );
        if ( null != browsedImageUrl ) {
            image.setUrl( browsedImageUrl );
            image.setWidth( 0 );
            image.setHeight( 0 );
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
            Perl5Util perl5util = new Perl5Util();
            if ( perl5util.match( "/GetDoc\\?meta_id=(\\d+)/", imageUrl ) ) {
                int imageFileDocumentId = Integer.parseInt( perl5util.group( 1 ) );
                FileDocumentDomainObject imageFileDocument = (FileDocumentDomainObject)documentMapper.getDocument( imageFileDocumentId );
                imageData = getImageDataFromFileDocument( imageFileDocument );
            } else if ( imageFile.isFile() ) {
                imageData = new ImageParser().parseImageFile( imageFile );
            }
        }
        return imageData;
    }

    private ImageData getImageDataFromFileDocument( FileDocumentDomainObject imageFileDocument ) {
        ImageData imageData;
        try {
            InputStream imageFileDocumentInputStream = imageFileDocument.getInputStreamSource().getInputStream();
            imageData = new ImageParser().parseImageStream( imageFileDocumentInputStream, imageFileDocument.getFilename() );
        } catch ( IOException ioe ) {
            imageData = new ImageData( 0, 0 );
        }
        return imageData;
    }

    private String getChosenImageFromImageBrowse( HttpServletRequest req ) {
        return req.getParameter( "imglist" );
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
        private Collection imageDirectories;

        public ImageEditPage( TextDocumentDomainObject document, int imageIndex, ImageDomainObject image,
                              ImageData imageFileData, String label ) {
            this.document = document;
            this.image = image;
            this.imageIndex = imageIndex;
            this.imageFileData = imageFileData;
            this.label = label;

            this.imageDirectories = Utility.collectImageDirectories();
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

        public Collection getImageDirectories() {
            return imageDirectories;
        }

    }

}
