package com.imcode.imcms.servlet.admin;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Html;
import imcode.util.HttpSessionUtils;
import imcode.util.ImageExtensionFilenameFilter;
import imcode.util.ToStringPairTransformer;
import imcode.util.Utility;
import imcode.util.io.FileUtility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;

import com.imcode.imcms.util.l10n.LocalizedMessage;
import com.imcode.util.HumanReadable;
import com.imcode.util.MultipartHttpServletRequest;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;
import org.apache.commons.io.FileUtils;

/**
 * Browse images in image-directory.
 */
public class ImageBrowse extends HttpServlet {

    private final static Logger LOG = Logger.getLogger( ImageBrowse.class.getName() );

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

    private static final LocalizedMessage ERROR_MESSAGE__FILE_EXISTS = new LocalizedMessage( "error/servlet/images/image_file_exists" );

    public void doPost( HttpServletRequest req, HttpServletResponse response ) throws IOException, ServletException {
        MultipartHttpServletRequest request = new MultipartHttpServletRequest( req );
        ImageBrowser imageBrowser = (ImageBrowser)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, ImageBrowser.REQUEST_ATTRIBUTE_OR_PARAMETER__IMAGE_BROWSER );
        String imageUrl = request.getParameter( REQUEST_PARAMETER__IMAGE_URL );

        if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            imageBrowser.cancel( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__OK_BUTTON )
                    && null != imageUrl ) {
            verifyImage(imageUrl, imageBrowser, request, response);
        } else {
            browse( imageUrl, false, request, response );
        }
    }

    public static void verifyImage(String imageUrl, ImageBrowser imageBrowser,
    		HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	File imagesRoot = Imcms.getServices().getConfig().getImagePath();
    	File selectedImage = null;
    	File image = new File( imagesRoot, imageUrl );
        if ( FileUtility.directoryIsAncestorOfOrEqualTo( imagesRoot, image.getParentFile() ) ) {
            selectedImage = image;
        }

        if (selectedImage != null) {
        	ImageInfo info = ImageOp.getImageInfo(image);
        	if (info == null) {
        		browse(imageUrl, true, request, response);
        	} else {
        		imageBrowser.selectImageUrl( imageUrl, request, response );
        	}
         }
     }

    public static void browse( String imageUrl, boolean fileNotImageError, HttpServletRequest request,
                               HttpServletResponse response ) throws ServletException, IOException {
        File imagesRoot = Imcms.getServices().getConfig().getImagePath();
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

        ImageBrowserPage page = new ImageBrowserPage( selectedDirectory, selectedImage );
        if ( null != request.getParameter( REQUEST_PARAMETER__UPLOAD_BUTTON ) ) {
            upload( request, selectedDirectory, page );
        }

        if (fileNotImageError) {
        	page.setErrorMessage(ImageEditPage.ERROR_MESSAGE__FILE_NOT_IMAGE);
        }

        page.setLabel( StringUtils.defaultString( request.getParameter( REQUEST_PARAMETER__LABEL ) ) );
        page.forward(request,response) ;
    }

    private static void upload( HttpServletRequest request, File selectedDirectory, ImageBrowserPage page ) throws IOException {
        File imagesRoot = Imcms.getServices().getConfig().getImagePath();
        FileItem fileItem = ( (MultipartHttpServletRequest)request ).getParameterFileItem( REQUEST_PARAMETER__FILE );
        if ( null != fileItem ) {
            File destinationFile = new File( selectedDirectory, fileItem.getName() );
            boolean underImagesRoot = FileUtility.directoryIsAncestorOfOrEqualTo( imagesRoot, destinationFile.getParentFile() );
            boolean hasImageExtension = new ImageExtensionFilenameFilter().accept( destinationFile, destinationFile.getName() );
            if (!hasImageExtension) {
                page.setErrorMessage(ImageEditPage.ERROR_MESSAGE__ONLY_ALLOWED_TO_UPLOAD_IMAGES) ;
            } else if ( destinationFile.exists() ) {
                page.setErrorMessage(ERROR_MESSAGE__FILE_EXISTS) ;
            } else if ( underImagesRoot ) {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("upload_img", null);
                	fileItem.write(tempFile);
                	ImageInfo info = ImageOp.getImageInfo(tempFile);

                	boolean validImage = false;

                    if (info != null && info.getFormat() != null) {
                	    Format imageFormat = info.getFormat();

                	    for (Format allowedFormat : ImageEditPage.ALLOWED_FORMATS) {
                	        if (imageFormat == allowedFormat) {
                	            validImage = true;
                	            break;
                	        }
                	    }
                	}

                	if (validImage) {
                	    FileUtils.copyFile(tempFile, destinationFile);
                        page.setCurrentImage( destinationFile ) ;
                	} else {
                		page.setErrorMessage(ImageEditPage.ERROR_MESSAGE__ONLY_ALLOWED_TO_UPLOAD_IMAGES) ;
                	}
                } catch ( Exception e ) {
                    throw new UnhandledException( "Failed to write file " + destinationFile
                                                  + ". Possible permissions problem?", e );
                } finally {
                    if (tempFile != null) {
                        tempFile.delete();
                    }
                }
            } else {
                LOG.info( "User " + Utility.getLoggedOnUser( request ) + " was denied uploading to file "
                          + destinationFile );
            }
        }
    }

    public static class ImageBrowserPage {

        private static final String REQUEST_ATTRIBUTE__IMAGE_BROWSE_PAGE = "imagebrowsepage";

        private String label;
        private String imageUrl;
        private LocalizedMessage errorMessage;
        private File currentDirectory;
        private File currentImage;

        public ImageBrowserPage( File currentDirectory, File currentImage ) {
            this.currentDirectory = currentDirectory ;
            this.currentImage = currentImage;
        }

        public String getLabel() {
            return label;
        }

        private void setLabel( String label ) {
            this.label = label;
        }

        public String getDirectoriesOptionList() throws IOException {
            final File imagesRoot = Imcms.getServices().getConfig().getImagePath();
            Collection imageDirectories = Utility.collectImageDirectories();

            File currentDirectoryRelativeToImageRootParent = FileUtility.relativizeFile( imagesRoot.getParentFile(), currentDirectory );
            return Html.createOptionList(imageDirectories, currentDirectoryRelativeToImageRootParent, new ToStringPairTransformer() {
                public String[] transformToStringPair(Object input) {
                    File file = (File) input;
                    return new String[] { FileUtility.relativeFileToString(file), FileUtility.relativeFileToString(file) };
                }
            });
        }

        public String getImagesOptionList() throws IOException {
            final File imagesRoot = Imcms.getServices().getConfig().getImagePath();
            if ( null != currentImage ) {
                imageUrl = FileUtility.relativeFileToString( FileUtility.relativizeFile( imagesRoot, currentImage ) );
            }

            File[] images = currentDirectory.listFiles( new ImageExtensionFilenameFilter() );
            Arrays.sort( images );
            List imageList = Arrays.asList( images );

            return Html.createOptionList(imageList, currentImage, new ToStringPairTransformer() {
                public String[] transformToStringPair(Object input) {
                    File file = (File) input;
                    String formattedFileSize = HumanReadable.getHumanReadableByteSize(file.length());
                    try {
                        return new String[] {
                                FileUtility.relativeFileToString(FileUtility.relativizeFile(imagesRoot, file)),
                                file.getName() + "\t[" + formattedFileSize + "]"
                        };
                    } catch ( IOException e ) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
            request.setAttribute( REQUEST_ATTRIBUTE__IMAGE_BROWSE_PAGE, this );
            UserDomainObject user = Utility.getLoggedOnUser( request );
            String forwardPath = "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP__IMAGE_BROWSE;
            request.getRequestDispatcher( forwardPath ).forward( request, response );
        }

        public static ImageBrowserPage fromRequest( HttpServletRequest request ) {
            return (ImageBrowserPage)request.getAttribute( REQUEST_ATTRIBUTE__IMAGE_BROWSE_PAGE ) ;
        }

        public void setErrorMessage( LocalizedMessage errorMessage ) {
            this.errorMessage = errorMessage;
        }

        public LocalizedMessage getErrorMessage() {
            return errorMessage;
        }

        public void setCurrentImage( File currentImage ) {
            this.currentImage = currentImage;
        }
    }
}
