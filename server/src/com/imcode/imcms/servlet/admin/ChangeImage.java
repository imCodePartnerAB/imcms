package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import com.imcode.imcms.servlet.ImageArchive;
import com.imcode.imcms.servlet.DocumentFinder;
import imcode.server.ApplicationServer;
import imcode.server.IMCConstants;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentMapper;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ImageData;
import imcode.util.ImageParser;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.StringTokenizer;

public class ChangeImage extends HttpServlet {

    final static String REQUEST_PARAMETER__IMAGE_URL = "imageref";
    final static String REQUEST_PARAMETER__IMAGE_FILE_DOCUMENT_ID = "imageFileDocumentId";
    public final static String REQUEST_PARAMETER__GOING_TO_OR_COMING_FROM_IMAGE_ARCHIVE = "GotoImageArchive";

    public static final String REQUEST_PARAMETER__OK_BUTTON = "ok";
    public static final String REQUEST_PARAMETER__PREVIEW_BUTTON = "show_img";
    private static final String REQUEST_PARAMETER__IMAGE_HEIGHT = "image_height";
    private static final String REQUEST_PARAMETER__IMAGE_WIDTH = "image_width";
    private static final String REQUEST_PARAMETER__IMAGE_BORDER = "image_border";
    private static final String REQUEST_PARAMETER__VERTICAL_SPACE = "v_space";
    private static final String REQUEST_PARAMETER__HORIZONTAL_SPACE = "h_space";
    private static final String REQUEST_PARAMETER__IMAGE_NAME = "image_name";
    private static final String REQUEST_PARAMETER__IMAGE_INDEX = "img_no";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "cancel";
    public static final String REQUEST_PARAMETER__DELETE_BUTTON = "delete";
    private static final String REQUEST_PARAMETER__IMAGE_ALIGN = "image_align";
    private static final String REQUEST_PARAMETER__IMAGE_ALT = "alt_text";
    private static final String REQUEST_PARAMETER__IMAGE_LOWSRC = "low_scr";
    public static final String REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER = "goToImageBrowser";

    public void doPost( final HttpServletRequest request, final HttpServletResponse response ) throws ServletException, IOException {

        final ImageDomainObject image = getImageFromRequest( request );
        UserDomainObject user = Utility.getLoggedOnUser( request );
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        final TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.getDocument( Integer.parseInt( request.getParameter( "meta_id" ) ) );
        final int imageIndex = Integer.parseInt( request.getParameter( REQUEST_PARAMETER__IMAGE_INDEX ) );

        if ( !userHasImagePermissionsOnDocument( user, document ) ) {	// Checking to see if user may edit this
            goBack( document, response );
            return;
        }

        if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            goBack( document, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__DELETE_BUTTON ) ) {
            image.setUrl( "" );
            goToImageEditPage( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__PREVIEW_BUTTON ) ) {
            goToImageEditPage( document, imageIndex, image, request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER ) ) {
            request.setAttribute( ImageBrowse.PARAMETER__CALLER, "ChangeImage" );
            ImageBrowse.getPage( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__GOING_TO_OR_COMING_FROM_IMAGE_ARCHIVE ) ) {
            DocumentFinder documentFinder = new DocumentFinder() ;
            documentFinder.setDocumentsSelectable( true );
            Query imageFileDocumentQuery = new TermQuery( new Term( DocumentIndex.FIELD__DOC_TYPE_ID, ""+DocumentDomainObject.DOCTYPE_FILE));
            documentFinder.setRestrictingQuery(imageFileDocumentQuery) ;
            documentFinder.setSelectDocumentCommand(new DocumentFinder.SelectDocumentCommand() {
                public void selectDocument( DocumentDomainObject documentFound, HttpServletRequest request,
                                              HttpServletResponse response ) throws IOException, ServletException {
                    FileDocumentDomainObject imageFileDocument = (FileDocumentDomainObject)documentFound ;
                    if ( null != imageFileDocument ) {
                        image.setUrl( "../servlet/GetDoc?meta_id=" + imageFileDocument.getId() );
                        image.setWidth( 0 );
                        image.setHeight( 0 );
                    }
                    goToImageEditPage( document, imageIndex, image, request, response );
                }
            } );
            documentFinder.forward( request, response );
        } else {
            document.setImage( imageIndex, image );
            documentMapper.saveDocument( document, user );
            imcref.updateMainLog( "ImageRef " + imageIndex + " =" + image.getUrl() +
                                  " in  " + "[" + document.getId() + "] modified by user: [" +
                                  user.getFullName() + "]" );
            goBack( document, response );
        }
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
        String label = getLabelParam( request );

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
        // Check if ChangeImage is invoked by ImageBrowse, hence containing
        // an image filename as option value.
        String paramCancel = req.getParameter( ImageBrowse.PARAMETER_BUTTON__CANCEL );
        String imageListParam = "";
        if ( null == paramCancel ) {
            imageListParam = req.getParameter( "imglist" );
        }

        return ( imageListParam == null ) ? null : URLDecoder.decode( imageListParam );
    }

    private int getImageNumberParam( HttpServletRequest req ) {
        String imgNoStr = ( req.getParameter( "img_no" ) != null )
                          ? req.getParameter( "img_no" ) : req.getParameter( "img" );
        int img_no = Integer.parseInt( imgNoStr );
        return img_no;
    }

    private String getLabelParam( HttpServletRequest req ) {
        String label = req.getParameter( "label" );
        if ( label == null ) {
            label = "";
        }
        return label;
    }

    private StringBuffer createImageFolderOptionList( List imageFolders, File image_path ) throws IOException {

        // create the image folder option list

        //lets get some path we need later on
        String canon_imagePath = image_path.getCanonicalPath(); //ex: C:\Tomcat3\webapps\imcms\images
        String root_dir_parent = image_path.getParentFile().getCanonicalPath();  //ex: C:\Tomcat3\webapps\webapps\imcms
        String root_dir_name = canon_imagePath.substring( root_dir_parent.length() );
        if ( root_dir_name.startsWith( File.separator ) ) {
            root_dir_name = root_dir_name.substring( File.separator.length() );
            //ex: root_dir_name = images
        }

        StringBuffer folderOptions = new StringBuffer( imageFolders.size() * 64 );

        for ( int x = 0; x < imageFolders.size(); x++ ) {
            File fileObj = (File)imageFolders.get( x );

            //ok lets set up the folder name to show and the one to put as value
            String optionName = fileObj.getCanonicalPath();
            //lets remove the start of the path so we end up at the rootdir.
            if ( optionName.startsWith( canon_imagePath ) ) {
                optionName = optionName.substring( root_dir_parent.length() );
                if ( optionName.startsWith( File.separator ) ) {
                    optionName = optionName.substring( File.separator.length() );
                }
            } else if ( optionName.startsWith( File.separator ) ) {
                optionName = optionName.substring( File.separator.length() );
            }
            //the path to put in the option value
            String optionPath = optionName;
            if ( optionPath.startsWith( root_dir_name ) ) {
                optionPath = optionPath.substring( root_dir_name.length() );
            }
            //ok now we have to replace all parent folders with a '-' char
            StringTokenizer token = new StringTokenizer( optionName, "\\", false );
            StringBuffer buff = new StringBuffer( "" );
            while ( token.countTokens() > 1 ) {
                token.nextToken();
                buff.append( "&nbsp;&nbsp;-" );
            }
            if ( token.countTokens() > 0 ) {
                optionName = buff.toString() + token.nextToken();
            }
            File urlFile = new File( optionName );
            String fileName = urlFile.getName();
            File parentDir = urlFile.getParentFile();
            if ( parentDir != null ) {
                optionName = parentDir.getPath() + "/";
            } else {
                optionName = "";
            }
            //filepathfix ex: images\nisse\kalle.gif to images/nisse/kalle.gif
            optionName = optionName.replace( File.separatorChar, '/' ) + fileName;

            int i = optionName.lastIndexOf( '-' );
            String tempPathFirst = "";
            String tempPathLast = "";
            if ( i > 0 ) {
                tempPathFirst = ( optionName.substring( 0, i ) ).replace( '-', ' ' );
                tempPathLast = ( optionName.substring( i ) );
                optionName = tempPathFirst + tempPathLast;
            }
            optionName = optionName.replace( '-', '\\' );
            folderOptions.append( "<option value=\"" + optionPath + "\""
                                  + ">"
                                  + optionName
                                  + "</option>\r\n" );
        }//end setUp option dir list

        return folderOptions;
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

}
