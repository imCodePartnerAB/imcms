package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

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

    public static final String REQUEST_ATTRIBUTE__IMAGE_BROWSE_PAGE = "imagebrowsepage";

    private static final String JSP__IMAGE_BROWSE = "ImageBrowse.jsp";
    public static final String REQUEST_PARAMETER__OK_BUTTON = "ImageBrowse.button.ok";
    public static final String REQUEST_PARAMETER__PREVIEW_BUTTON = "ImageBrowse.button.preview";
    public static final String REQUEST_PARAMETER__CANCEL_BUTTON = "ImageBrowse.button.cancel";
    public static final String REQUEST_PARAMETER__CHANGE_DIRECTORY_BUTTON = "ImageBrowse.button.change";
    public static final String REQUEST_PARAMETER__IMAGE_URL = "imglist";
    public static final String REQUEST_PARAMETER__IMAGE_DIRECTORY = "dirlist";
    public static final String REQUEST_PARAMETER__LABEL = "label";

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException {
        doGet( req, res );
    }

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException {
        forward(req, res);
    }

    public static void forward( HttpServletRequest request, HttpServletResponse response )
            throws IOException, ServletException {
        ImageBrowser imageBrowser = (ImageBrowser)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER );
        String imageUrl = request.getParameter( REQUEST_PARAMETER__IMAGE_URL );
        if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            imageBrowser.cancel( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__OK_BUTTON ) && (null != imageUrl || imageBrowser.isNullSelectable())) {
            imageBrowser.selectImageUrl( imageUrl, request, response );
        } else {
            view( imageUrl, request, response );
        }
    }

    private static void view( String imageUrl, HttpServletRequest request,
                              HttpServletResponse response ) throws IOException, ServletException {
        final File imagesRoot = ApplicationServer.getIMCServiceInterface().getConfig().getImagePath();

        String label = StringUtils.defaultString( request.getParameter( "label" ) );

        boolean changeDirectoryButtonWasPressed = null != request.getParameter( REQUEST_PARAMETER__CHANGE_DIRECTORY_BUTTON);

        File currentImage = null ;
        if ( null != imageUrl && !changeDirectoryButtonWasPressed) {
            File image = new File( imagesRoot, imageUrl );
            if ( FileUtility.directoryIsAncestorOfOrEqualTo(imagesRoot,image.getParentFile())) {
                currentImage = image ;
            }
        }

        String imageDirectoryString = request.getParameter( REQUEST_PARAMETER__IMAGE_DIRECTORY );
        File currentDirectory = null != currentImage ? currentImage.getParentFile() : imagesRoot;
        if ( null != imageDirectoryString ) {
            File imageDirectory = new File( imagesRoot.getParentFile(), imageDirectoryString );
            if ( FileUtility.directoryIsAncestorOfOrEqualTo( imagesRoot, imageDirectory ) ) {
                currentDirectory = imageDirectory;
            }
        }
        File currentDirectoryRelativeToImageRootParent = FileUtility.relativizeFile( imagesRoot.getParentFile(), currentDirectory) ;

        Collection imageDirectories = Utility.collectImageDirectories();

        List imgList = Arrays.asList( currentDirectory.listFiles( new ImageExtensionFileFilter() ) );

        UserDomainObject user = Utility.getLoggedOnUser( request );

        String imageOptionList = Html.createOptionList( imgList, currentImage, new Transformer() {
            public Object transform( Object input ) {
                File file = (File)input;
                return new String[]{
                    FileUtility.relativizeFile( imagesRoot, file ).getPath(), file.getName() + "\t[" + file.length() + "]"
                };
            }
        } );

        String directoriesOptionList = Html.createOptionList( imageDirectories, currentDirectoryRelativeToImageRootParent, new Transformer() {
            public Object transform( Object input ) {
                File file = (File)input;
                return new String[]{file.getPath(), FileUtility.relativeFileToString( file )};
            }
        } );

        Page page = new Page();
        page.setDirectoriesOptionList( directoriesOptionList );
        page.setLabel( label );
        page.setImagesOptionList( imageOptionList );
        page.setImageUrl( imageUrl );

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

        public String getLabel() {
            return label;
        }

        private void setLabel( String label ) {
            this.label = label;
        }

        public String getDirectoriesOptionList() {
            return directoriesOptionList;
        }

        private void setDirectoriesOptionList( String folders ) {
            this.directoriesOptionList = folders;
        }

        public String getImagesOptionList() {
            return imagesOptionList;
        }

        private void setImagesOptionList( String imagesOptionList ) {
            this.imagesOptionList = imagesOptionList;
        }

        public void setImageUrl( String imageUrl ) {
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

    }
}
