package com.imcode.imcms.servlet.admin;

import java.io.*;
import java.util.*;
import java.net.URLDecoder;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.server.*;
import imcode.server.document.DocumentMapper;
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
            log( "User " + user.getUserId() + " was denied access to meta_id " + meta_id + " and was sent to " + start_url );
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

        String[] sql = DocumentMapper.getDocumentImageData(imcref, meta_id, img_no);

        // Check if ChangeImage is invoked by ImageBrowse, hence containing
        // an image filename as option value.
        String paramCancel = req.getParameter( ImageBrowse.PARAMETER_BUTTON__CANCEL );
        String imageListParam = "";
        if( null == paramCancel ) {
            imageListParam = req.getParameter( "imglist" );
        }
        String img_preset = ( imageListParam == null ) ? "" : URLDecoder.decode( imageListParam );

        String imageName = ( "".equals( img_preset ) && sql.length > 0 ? sql[1] : img_preset ); // selected OPTION or ""

        //****************************************************************
        ImageFileMetaData image = new ImageFileMetaData( new File( image_path, imageName ) );
        int width = image.getWidth();
        int height = image.getHeight();
        //****************************************************************

        Vector vec = new Vector();
        if ( sql.length > 0 ) {
            int current_width = 0;
            try {
                current_width = Integer.parseInt( img_preset.equals( "" ) ? sql[2] : "" + width );
            } catch ( NumberFormatException ex ) {

            }
            int current_height = 0;
            try {
                current_height = Integer.parseInt( img_preset.equals( "" ) ? sql[3] : "" + height );
            } catch ( NumberFormatException ex ) {

            }
            int aspect = 0;
            if ( current_width * current_height != 0 ) {
                aspect = 100 * current_width / current_height;
            }

            String keepAspect = "checked";

            if ( width * height != 0 && aspect != ( 100 * width / height ) ) {
                keepAspect = "";
            }

            vec.add( "#imgName#" );
            vec.add( HTMLConv.toHTMLSpecial(sql[0] ));
            vec.add( "#imgRef#" );
            vec.add( HTMLConv.toHTMLSpecial(imageName));
            vec.add( "#imgWidth#" );
            vec.add( current_width != 0 ? "" + current_width : "" + width );
            vec.add( "#origW#" ); // original imageWidth
            vec.add( "" + width );
            vec.add( "#imgHeight#" );
            vec.add( current_height != 0 ? "" + current_height : "" + height );
            vec.add( "#origH#" );
            vec.add( "" + height ); // original imageHeight

            vec.add( "#keep_aspect#" );
            vec.add( keepAspect );

            vec.add( "#imgBorder#" );
            vec.add( sql[4] );
            vec.add( "#imgVerticalSpace#" );
            vec.add( sql[5] );
            vec.add( "#imgHorizontalSpace#" );
            vec.add( sql[6] );
            if ( "_top".equals( sql[7] ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#top_checked#" );
            } else if ( "_self".equals( sql[7] ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#self_checked#" );
            } else if ( "_blank".equals( sql[7] ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#blank_checked#" );
            } else if ( "_parent".equals( sql[7] ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#blank_checked#" );
            } else {
                vec.add( "#target_name#" );
                vec.add( sql[8] );
                vec.add( "#other_checked#" );
            }
            vec.add( "selected" );

            if ( "baseline".equals( sql[9] ) ) {
                vec.add( "#baseline_selected#" );
            } else if ( "top".equals( sql[9] ) ) {
                vec.add( "#top_selected#" );
            } else if ( "middle".equals( sql[9] ) ) {
                vec.add( "#middle_selected#" );
            } else if ( "bottom".equals( sql[9] ) ) {
                vec.add( "#bottom_selected#" );
            } else if ( "texttop".equals( sql[9] ) ) {
                vec.add( "#texttop_selected#" );
            } else if ( "absmiddle".equals( sql[9] ) ) {
                vec.add( "#absmiddle_selected#" );
            } else if ( "absbottom".equals( sql[9] ) ) {
                vec.add( "#absbottom_selected#" );
            } else if ( "left".equals( sql[9] ) ) {
                vec.add( "#left_selected#" );
            } else if ( "right".equals( sql[9] ) ) {
                vec.add( "#right_selected#" );
            } else {
                vec.add( "#none_selected#" );
            }
            vec.add( "selected" );

            vec.add( "#imgAltText#" );
            vec.add( HTMLConv.toHTMLSpecial(sql[10]));
            vec.add( "#imgLowScr#" );
            vec.add( sql[11] );
            vec.add( "#imgRefLink#" );
            vec.add( HTMLConv.toHTMLSpecial( sql[12] ));
        } else {

            vec.add( "#imgName#" );
            vec.add( "" );
            vec.add( "#imgRef#" );
            vec.add( imageName );
            vec.add( "#imgWidth#" );
            vec.add( "" + width );
            vec.add( "#imgHeight#" );
            vec.add( "" + height );

            vec.add( "#origW#" );
            vec.add( "" + width );
            vec.add( "#origH#" );
            vec.add( "" + height );

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
