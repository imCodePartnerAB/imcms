package com.imcode.imcms.servlet.admin;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.server.*;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Edit imageref  - upload image to server.
 */
public class ChangeImage extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        if ( req.getParameter( "preview" ) == null ) {
            doGet( req, res );
            return;
        }

        res.setContentType( "text/html" );
        ImageBrowse.getPage( req, res );
        return;
    }

    /**
     * doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        res.setContentType( "text/html" );
        PrintWriter out = res.getWriter();

        String metaIdStr = req.getParameter( "meta_id" );
        int meta_id = Integer.parseInt( metaIdStr );

        String label = req.getParameter( "label" );
        if ( label == null ) {
            label = "";
        }

        UserDomainObject user = Utility.getLoggedOnUser( req );
        // Check if user has write rights
        if ( !imcref.checkDocAdminRights( meta_id, user ) ) {
            String start_url = imcref.getStartUrl();
            log( "User " + user.getId() + " was denied access to meta_id " + meta_id + " and was sent to " + start_url );
            res.sendRedirect( start_url );
            return;
        }


        //*lets get some path we need later on
        String image_url = imcref.getImageUrl();
        File image_path = Utility.getDomainPrefPath( "image_path" );


        //*lets get the dirlist, and add the rootdir to it
        List imageFolders = GetImages.getImageFolders( image_path, true );
        imageFolders.add( 0, image_path );

        //*the StringBuffers to save the image directories list html-code in
        StringBuffer folderOptions = createImageFolderOptionList(imageFolders,image_path);

        HttpSession session = req.getSession( true );
        session.setAttribute( "imageFolderOptionList", folderOptions.toString() );

        String imgNoStr = ( req.getParameter( "img_no" ) != null ) ? req.getParameter( "img_no" ) : req.getParameter( "img" );
        int img_no = Integer.parseInt( imgNoStr );

        DocumentMapper documentMapper = imcref.getDocumentMapper() ;
        TextDocumentDomainObject.Image image = documentMapper.getDocumentImage( meta_id, img_no );

        // Check if ChangeImage is invoked by ImageBrowse, hence containing
        // an image filename as option value.
        String paramCancel = req.getParameter( ImageBrowse.PARAMETER_BUTTON__CANCEL );
        String imageListParam = "";
        if( null == paramCancel ) {
            imageListParam = req.getParameter( "imglist" );
        }
        String browsedImageUrl = ( imageListParam == null ) ? "" : URLDecoder.decode( imageListParam );

        String imageUrl = ( "".equals( browsedImageUrl ) && null != image ? image.getUrl() : browsedImageUrl ); // selected OPTION or ""

        //****************************************************************
        ImageFileMetaData imageFileMetaData = new ImageFileMetaData( new File( image_path, imageUrl ) );
        int widthFromFile = imageFileMetaData.getWidth();
        int heightFromFile = imageFileMetaData.getHeight();
        //****************************************************************

        Vector vec = new Vector();
        if ( null != image ) {
            int current_width = 0;
            try {
                current_width = "".equals( browsedImageUrl ) ? image.getWidth() : widthFromFile;
            } catch ( NumberFormatException ex ) {

            }
            int current_height = 0;
            try {
                current_height = "".equals( browsedImageUrl ) ? image.getHeight() : heightFromFile;
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
            vec.add( HTMLConv.toHTMLSpecial(image.getName()));
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
            vec.add( ""+image.getBorder() );
            vec.add( "#imgVerticalSpace#" );
            vec.add( ""+image.getVerticalSpace() );
            vec.add( "#imgHorizontalSpace#" );
            vec.add( ""+image.getHorizontalSpace() );
            if ( "_top".equals( image.getTarget() ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#top_checked#" );
            } else if ( "_self".equals( image.getTarget() ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#self_checked#" );
            } else if ( "_blank".equals( image.getTarget() ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#blank_checked#" );
            } else if ( "_parent".equals( image.getTarget() ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#blank_checked#" );
            } else {
                vec.add( "#target_name#" );
                vec.add( image.getTarget() );
                vec.add( "#other_checked#" );
            }
            vec.add( "selected" );

            if ( "baseline".equals( image.getAlign() ) ) {
                vec.add( "#baseline_selected#" );
            } else if ( "top".equals( image.getAlign() ) ) {
                vec.add( "#top_selected#" );
            } else if ( "middle".equals( image.getAlign() ) ) {
                vec.add( "#middle_selected#" );
            } else if ( "bottom".equals( image.getAlign() ) ) {
                vec.add( "#bottom_selected#" );
            } else if ( "texttop".equals( image.getAlign() ) ) {
                vec.add( "#texttop_selected#" );
            } else if ( "absmiddle".equals( image.getAlign() ) ) {
                vec.add( "#absmiddle_selected#" );
            } else if ( "absbottom".equals( image.getAlign() ) ) {
                vec.add( "#absbottom_selected#" );
            } else if ( "left".equals( image.getAlign() ) ) {
                vec.add( "#left_selected#" );
            } else if ( "right".equals( image.getAlign() ) ) {
                vec.add( "#right_selected#" );
            } else {
                vec.add( "#none_selected#" );
            }
            vec.add( "selected" );

            vec.add( "#imgAltText#" );
            vec.add( image.getAlternateText());
            vec.add( "#imgLowScr#" );
            vec.add( image.getLowResolutionUrl() );
            vec.add( "#imgRefLink#" );
            vec.add( image.getLinkUrl() );
        } else {

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

        }
        vec.add( "#imgUrl#" );
        vec.add( image_url );
        vec.add( "#getMetaId#" );
        vec.add( String.valueOf( meta_id ) );
        vec.add( "#img_no#" );
        vec.add( String.valueOf( img_no ) );
        vec.add( "#folders#" );
        vec.add( folderOptions.toString() );
        vec.add( "#label#" );
        vec.add( label );

        String htmlStr = imcref.getAdminTemplate( "change_img.html", user, vec );
        out.print( htmlStr );

    }


    public StringBuffer createImageFolderOptionList( List imageFolders, File image_path) throws IOException {

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
