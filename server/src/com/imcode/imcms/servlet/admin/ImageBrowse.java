package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.collections.Transformer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Browse images in image-directory.
 */
public class ImageBrowse extends HttpServlet {

    private final static Logger log = Logger.getLogger( ImageBrowse.class.getName() );
    public static final String REQUEST_ATTRIBUTE__IMAGE_BROWSE_PAGE = "imagebrowsepage";

    private static final String JSP__IMAGE_BROWSE = "ImageBrowse.jsp";
    public static final String REQUEST_PARAMETER__OK_BUTTON = "ImageBrowse.button.ok";
    public static final String REQUEST_PARAMETER__PREVIEW_BUTTON = "ImageBrowse.button.preview";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "ImageBrowse.button.cancel";
    public static final String REQUEST_PARAMETER__CHANGE_DIRECTORY_BUTTON = "ImageBrowse.button.change";
    public static final String REQUEST_PARAMETER__IMAGE_URL = "imglist";
    public static final String REQUEST_PARAMETER__IMAGE_DIRECTORY = "dirlist";
    public static final String REQUEST_PARAMETER__LABEL = "label";
    public static final String REQUEST_PARAMETER__UPLOAD_BUTTON = "upload";
    public static final String REQUEST_PARAMETER__FILE = "file";

    public void doPost( HttpServletRequest req, HttpServletResponse response ) throws IOException, ServletException {
        MultipartHttpServletRequest request = new MultipartHttpServletRequest( req );
        ImageBrowser imageBrowser = (ImageBrowser)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER );
        String imageUrl = request.getParameter( REQUEST_PARAMETER__IMAGE_URL );

        if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            imageBrowser.cancel( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__OK_BUTTON )
                    && ( null != imageUrl || imageBrowser.isNullSelectable() ) ) {
            imageBrowser.selectImageUrl( imageUrl, request, response );
        } else {
            browse( imageUrl, request, response );
        }
    }

    public static void browse( String imageUrl, HttpServletRequest request,
                                       HttpServletResponse response ) throws ServletException, IOException {
        final File imagesRoot = ApplicationServer.getIMCServiceInterface().getConfig().getImagePath();
        boolean changeDirectoryButtonWasPressed = null
                                                  != request.getParameter( REQUEST_PARAMETER__CHANGE_DIRECTORY_BUTTON );
        File selectedImage = null;
        if ( null != imageUrl && !changeDirectoryButtonWasPressed ) {
            File image = new File( imagesRoot, imageUrl );
            if ( FileUtility.directoryIsAncestorOfOrEqualTo( imagesRoot, image.getParentFile() ) ) {
                selectedImage = image;
            }
        }

        String imageDirectoryString = request.getParameter( REQUEST_PARAMETER__IMAGE_DIRECTORY );
        File selectedDirectory = null != selectedImage ? selectedImage.getParentFile() : imagesRoot;
        if ( null != imageDirectoryString ) {
            File imageDirectory = new File( imagesRoot.getParentFile(), imageDirectoryString );
            if ( FileUtility.directoryIsAncestorOfOrEqualTo( imagesRoot, imageDirectory ) ) {
                selectedDirectory = imageDirectory;
            }
        }

        if ( null != request.getParameter( REQUEST_PARAMETER__UPLOAD_BUTTON ) ) {
            FileItem fileItem = ( (MultipartHttpServletRequest)request ).getParameterFileItem( REQUEST_PARAMETER__FILE );
            if ( null != fileItem ) {
                File destinationFile = new File( selectedDirectory, fileItem.getName() );
                if ( !FileUtility.directoryIsAncestorOfOrEqualTo( imagesRoot, destinationFile.getParentFile() ) ) {
                    log.info( "User " + Utility.getLoggedOnUser( request ) + " was denied uploading to file "
                              + destinationFile );
                } else if ( !destinationFile.exists() ) {
                    try {
                        fileItem.write( destinationFile );
                        selectedImage = destinationFile ;
                    } catch ( Exception e ) {
                        throw new UnhandledException( "Failed to write file "+destinationFile+". Possible permissions problem?", e );
                    }
                }
            }
        }
        browse( selectedDirectory, selectedImage, request, response );
    }

    public static void browse( File currentDirectory, File currentImage, HttpServletRequest request,
                               HttpServletResponse response ) throws ServletException, IOException {
        String label = StringUtils.defaultString( request.getParameter( "label" ) );
        UserDomainObject user = Utility.getLoggedOnUser( request );

        Page page = new Page( currentDirectory, currentImage );
        page.setLabel( label );

        request.setAttribute( REQUEST_ATTRIBUTE__IMAGE_BROWSE_PAGE, page );
        String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP__IMAGE_BROWSE;
        request.getRequestDispatcher( forwardPath ).forward( request, response );
    }

    public static String getImageUri( HttpServletRequest req ) {
        boolean pressedCancelButton = ( null != req.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) );
        String imageUrl = null;
        if ( !pressedCancelButton ) {
            imageUrl = req.getParameter( REQUEST_PARAMETER__IMAGE_URL );
            if ( null != imageUrl ) {
                imageUrl = "../images/" + imageUrl;
            }
        }
        return imageUrl;
    }

    public static class Page {

        private String label;
        private String directoriesOptionList;
        private String imagesOptionList;
        private String imageUrl;

        public Page( File currentDirectory, File currentImage ) {
            final File imagesRoot = ApplicationServer.getIMCServiceInterface().getConfig().getImagePath();
            if (null != currentImage) {
                imageUrl = FileUtility.relativeFileToString( FileUtility.relativizeFile( imagesRoot, currentImage ) ) ;
            }
            Collection imageDirectories = Utility.collectImageDirectories();

            File[] images = currentDirectory.listFiles( new ImageExtensionFileFilter() );
            Arrays.sort(images) ;
            List imageList = Arrays.asList( images );

            File currentDirectoryRelativeToImageRootParent = FileUtility.relativizeFile( imagesRoot.getParentFile(), currentDirectory );
            directoriesOptionList = Html.createOptionList( imageDirectories, currentDirectoryRelativeToImageRootParent, new Transformer() {
                public Object transform( Object input ) {
                    File file = (File)input;
                    return new String[]{file.getPath(), FileUtility.relativeFileToString( file )};
                }
            } );
            imagesOptionList = Html.createOptionList( imageList, currentImage, new Transformer() {
                public Object transform( Object input ) {
                    File file = (File)input;
                    return new String[]{
                        FileUtility.relativizeFile( imagesRoot, file ).getPath(), file.getName() + "\t["
                                                                                  + file.length()
                                                                                  + "]"
                    };
                }
            } );

        }

        public String getLabel() {
            return label;
        }

        private void setLabel( String label ) {
            this.label = label;
        }

        public String getDirectoriesOptionList() {
            return directoriesOptionList;
        }

        public String getImagesOptionList() {
            return imagesOptionList;
        }

        public String getImageUrl() {
            return imageUrl;
        }

    }
}
