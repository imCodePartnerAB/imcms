package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.HTMLConv;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentMapper;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.GetImages;
import imcode.util.ImageFileMetaData;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.imcode.imcms.servlet.ImageArchiveFacade;
import org.apache.log4j.Logger;

/**
 * Edit imageref  - upload image to server.
 */
public class ChangeImage extends HttpServlet {
    private Logger log = Logger.getLogger(  ChangeImage.class );

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        if ( req.getParameter( "preview" ) == null ) {
            doGet( req, res );
            return;
        }

        Utility.setDefaultHtmlContentType( res );
        ImageBrowse.getPage( req, res );
        return;
    }

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        String metaIdStr = req.getParameter( "meta_id" );
        int meta_id = Integer.parseInt( metaIdStr );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        // Check if user has write rights
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        if ( !imcref.checkDocAdminRights( meta_id, user ) ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        int widthFromFile = 0;
        int heightFromFile = 0;

        boolean fileImageDocumentChoosen = false;
        String fileDocumentIdStr = "dummy text";
        String gotoImageArchive = req.getParameter( "GotoImageArchive");
        if( null != gotoImageArchive ) {
            ImageArchiveFacade imageArhiveFacade = ImageArchiveFacade.getInstance( req );
            if ( !imageArhiveFacade.isImageSelected() ) {
                imageArhiveFacade.setForwardReturnUrl( "ChangeImage?meta_id=" + req.getParameter("meta_id") + "&img=" + req.getParameter( "img" ) + "&label=" + req.getParameter( "label" ) );
                imageArhiveFacade.forward( req, res );
            } else {
                FileDocumentDomainObject imageFileDocument = imageArhiveFacade.getSelectedImage();
                if( null != imageFileDocument ) {
                    fileImageDocumentChoosen = true;
                    fileDocumentIdStr = ""+imageFileDocument.getId();
                    ImageFileMetaData imageFileMetaData = new ImageFileMetaData( imageFileDocument.getInputStreamSource().getInputStream(), imageFileDocument.getFilename() );
                    widthFromFile = imageFileMetaData.getWidth();
                    heightFromFile = imageFileMetaData.getHeight();
                }
            }
        }

        File image_path = Utility.getDomainPrefPath( "image_path" );
        List imageFolders = GetImages.getImageFolders( image_path, true );
        imageFolders.add( 0, image_path );

        HttpSession session = req.getSession( true );
        StringBuffer folderOptions = createImageFolderOptionList(imageFolders,image_path);
        session.setAttribute( "imageFolderOptionList", folderOptions.toString() );

        DocumentMapper documentMapper = imcref.getDocumentMapper() ;
        ImageDomainObject imageDomainObject = documentMapper.getDocumentImage( meta_id, getImageNumberParam( req ) );

        String browsedImageUrl = getChoosenImageFromImageBrowse( req );

        String imageUrl = ( "".equals( browsedImageUrl ) && null != imageDomainObject ? imageDomainObject.getUrl() : browsedImageUrl ); // selected OPTION or ""

        File imageFile = new File( image_path, imageUrl );
        if (imageFile.isFile()) {
            ImageFileMetaData imageFileMetaData = new ImageFileMetaData( new FileInputStream(imageFile), imageUrl );
            widthFromFile = imageFileMetaData.getWidth();
            heightFromFile = imageFileMetaData.getHeight();
        }

        Vector vec = new Vector();
        boolean useFileData = null == imageDomainObject;

        if ( useFileData ) {
                     vec.add( "#imgName#" );
            vec.add( "" );
            vec.add( "#imgRef#" );
            vec.add( imageUrl );
            vec.add( "#imgWidth#" );
            vec.add( "" + widthFromFile );
            vec.add( "#imgHeight#" );
            vec.add( "" + heightFromFile );

            vec.add( "#origW#" );
            vec.add( "" + widthFromFile );
            vec.add( "#origH#" );
            vec.add( "" + heightFromFile );

            vec.add( "#imgBorder#" );
            vec.add( "0" );
            vec.add( "#imgVerticalSpace#" );
            vec.add( "0" );
            vec.add( "#imgHorizontalSpace#" );
            vec.add( "0" );
            vec.add( "#target_name#" );
            vec.add( "" );
            vec.add( "#self_checked#" );
            vec.add( "selected" );
            vec.add( "#top_selected#" );
            vec.add( "selected" );
            vec.add( "#imgAltText#" );
            vec.add( "" );
            vec.add( "#imgLowScr#" );
            vec.add( "" );
            vec.add( "#imgRefLink#" );
            vec.add( "" );
        } else {
            int current_width = 0;
            try {
                current_width = "".equals( browsedImageUrl ) ? imageDomainObject.getWidth() : widthFromFile;
            } catch ( NumberFormatException ex ) {

            }
            int current_height = 0;
            try {
                current_height = "".equals( browsedImageUrl ) ? imageDomainObject.getHeight() : heightFromFile;
            } catch ( NumberFormatException ex ) {

            }
            int aspect = 0;
            if ( current_width * current_height != 0 ) {
                aspect = 100 * current_width / current_height;
            }

            String keepAspect = "checked";

            if ( widthFromFile * heightFromFile != 0 && aspect != ( 100 * widthFromFile / heightFromFile ) ) {
                keepAspect = "";
            }

            vec.add( "#imgName#" );
            vec.add( HTMLConv.toHTMLSpecial(imageDomainObject.getName()));
            vec.add( "#imgRef#" );
            vec.add( HTMLConv.toHTMLSpecial(imageUrl));
            vec.add( "#imgWidth#" );
            vec.add( current_width != 0 ? "" + current_width : "" + widthFromFile );
            vec.add( "#origW#" ); // original imageWidth
            vec.add( "" + widthFromFile );
            vec.add( "#imgHeight#" );
            vec.add( current_height != 0 ? "" + current_height : "" + heightFromFile );
            vec.add( "#origH#" );
            vec.add( "" + heightFromFile ); // original imageHeight

            vec.add( "#keep_aspect#" );
            vec.add( keepAspect );

            vec.add( "#imgBorder#" );
            vec.add( ""+imageDomainObject.getBorder() );
            vec.add( "#imgVerticalSpace#" );
            vec.add( ""+imageDomainObject.getVerticalSpace() );
            vec.add( "#imgHorizontalSpace#" );
            vec.add( ""+imageDomainObject.getHorizontalSpace() );
            if ( "_top".equals( imageDomainObject.getTarget() ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#top_checked#" );
            } else if ( "_self".equals( imageDomainObject.getTarget() ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#self_checked#" );
            } else if ( "_blank".equals( imageDomainObject.getTarget() ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#blank_checked#" );
            } else if ( "_parent".equals( imageDomainObject.getTarget() ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#blank_checked#" );
            } else {
                vec.add( "#target_name#" );
                vec.add( imageDomainObject.getTarget() );
                vec.add( "#other_checked#" );
            }
            vec.add( "selected" );

            if ( "baseline".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#baseline_selected#" );
            } else if ( "top".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#top_selected#" );
            } else if ( "middle".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#middle_selected#" );
            } else if ( "bottom".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#bottom_selected#" );
            } else if ( "texttop".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#texttop_selected#" );
            } else if ( "absmiddle".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#absmiddle_selected#" );
            } else if ( "absbottom".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#absbottom_selected#" );
            } else if ( "left".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#left_selected#" );
            } else if ( "right".equals( imageDomainObject.getAlign() ) ) {
                vec.add( "#right_selected#" );
            } else {
                vec.add( "#none_selected#" );
            }
            vec.add( "selected" );

            vec.add( "#imgAltText#" );
            vec.add( imageDomainObject.getAlternateText());
            vec.add( "#imgLowScr#" );
            vec.add( imageDomainObject.getLowResolutionUrl() );
            vec.add( "#imgRefLink#" );
            vec.add( imageDomainObject.getLinkUrl() );
        }
        vec.add( "#imgUrl#" );
        vec.add( imcref.getImageUrl() );
        vec.add( "#getMetaId#" );
        vec.add( String.valueOf( meta_id ) );
        vec.add( "#img_no#" );
        vec.add( String.valueOf( getImageNumberParam( req ) ) );
        vec.add( "#folders#" );
        vec.add( folderOptions.toString() );

        vec.add( "#label#" );
        vec.add( getLabelParam( req ) );
        vec.add( "#imageFileDocumentId#" );
        vec.add( fileDocumentIdStr );

        String htmlStr = imcref.getAdminTemplate( "change_img.html", user, vec );
        Utility.setDefaultHtmlContentType( res );
        PrintWriter out = res.getWriter();
        out.print( htmlStr );

    }

    private String getChoosenImageFromImageBrowse( HttpServletRequest req ) {
        // Check if ChangeImage is invoked by ImageBrowse, hence containing
        // an image filename as option value.
        String paramCancel = req.getParameter( ImageBrowse.PARAMETER_BUTTON__CANCEL );
        String imageListParam = "";
        if( null == paramCancel ) {
            imageListParam = req.getParameter( "imglist" );
        }

        String browsedImageUrl = ( imageListParam == null ) ? "" : URLDecoder.decode( imageListParam );
        return browsedImageUrl;
    }

    private int getImageNumberParam( HttpServletRequest req ) {
        String imgNoStr = ( req.getParameter( "img_no" ) != null ) ? req.getParameter( "img_no" ) : req.getParameter( "img" );
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


    private StringBuffer createImageFolderOptionList( List imageFolders, File image_path) throws IOException {

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

            int i = optionName.lastIndexOf('-');
            String tempPathFirst = "";
            String tempPathLast = "";
            if ( i > 0 ) {
                tempPathFirst = (optionName.substring(0,i)).replace( '-', ' ' );
                tempPathLast = (optionName.substring(i));
                optionName = tempPathFirst + tempPathLast;
            }
            optionName = optionName.replace( '-', '\\' );
            folderOptions.append(
                    "<option value=\"" + optionPath + "\""
                    + ">"
                    + optionName
                    + "</option>\r\n" );
        }//end setUp option dir list

        return folderOptions;
    }
}
