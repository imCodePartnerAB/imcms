package com.imcode.imcms.servlet.admin;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentMapper;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.ImageFileMetaData;
import imcode.util.Utility;
import com.imcode.imcms.flow.EditDocumentInformationPageFlow;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileInputStream;
import java.util.Vector;

import org.apache.log4j.Logger;

public class SaveImage extends HttpServlet implements imcode.server.IMCConstants {
    private Logger log = Logger.getLogger( SaveImage.class );

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        String okParameter = req.getParameter( "ok" );
        String showImageParam = req.getParameter( "show_img" );

        // get old image_height
        String oldH = req.getParameter( "oldH" );

        // get new image_height
        String image_height = req.getParameter( "image_height" );

        // get old image_width
        String oldW = req.getParameter( "oldW" );

        // get new image_width
        String image_width = req.getParameter( "image_width" );

        // get image_border
        String image_border = req.getParameter( "image_border" );

        // get vertical_space
        String v_space = req.getParameter( "v_space" );

        // get horizonal_space
        String h_space = req.getParameter( "h_space" );

        boolean keepAspectRatio = (req.getParameter( "keepAspectRatio" ) != null);

        String origWidth = req.getParameter( "origW" ); // width
        String origHeight = req.getParameter( "origH" ); // height

        ImageDomainObject image = new ImageDomainObject();
        try {
            image.setHeight( Integer.parseInt( image_height ) );
        } catch ( NumberFormatException ex ) {
            image_height = "0";
            image.setHeight( 0 );
        }

        try {
            image.setBorder( Integer.parseInt( image_border ) );
        } catch ( NumberFormatException ex ) {
            image_border = "0";
            image.setBorder( 0 );
        }

        try {
            image.setWidth( Integer.parseInt( image_width ) );
        } catch ( NumberFormatException ex ) {
            image_width = "0";
            image.setWidth( 0 );
        }

        if ( keepAspectRatio && (okParameter != null || showImageParam != null) ) {
            int oHeight = 0;
            try {
                oHeight = Integer.parseInt( origHeight ); // image height
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse origHeight" );
            }
            int oWidth = 0;
            try {
                oWidth = Integer.parseInt( origWidth ); // image width
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse origHeight" );
            }

            int iHeight = 0;
            try {
                iHeight = Integer.parseInt( image_height ); // form width
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse image_height" );
            }
            int iWidth = 0;
            try {
                iWidth = Integer.parseInt( image_width ); // form height
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse image_width" );
            }

            int oldHeight = 0;
            try {
                oldHeight = (oldH.length() > 0) ? Integer.parseInt( oldH ) : oHeight;
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse oldHeight" );
            }
            int oldWidth = 0;
            try {
                oldWidth = (oldW.length() > 0) ? Integer.parseInt( oldW ) : oWidth;
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse oldWidth" );
            }

            double asp_rat = ((double) oWidth / (double) oHeight);

            int heightDiff = Math.abs( iHeight - oldHeight );
            int widthDiff = Math.abs( iWidth - oldWidth );

            // Dominant value:
            // 1. greatest diff, 2. greatest int, 3. width

            if ( widthDiff > heightDiff ) {
                iHeight = (int) (iWidth / asp_rat);
            } else if ( heightDiff > widthDiff ) {
                iWidth = (int) (iHeight * asp_rat);
            } else if ( heightDiff == widthDiff ) {
                if ( iHeight > iWidth ) {
                    iWidth = (int) (iHeight * asp_rat);
                } else {
                    iHeight = (int) (iWidth / asp_rat);
                }
            } else {
                iHeight = (int) (iWidth * asp_rat);
            }

            image.setHeight( iHeight );
            image.setWidth( iWidth );
            image_width = "" + iWidth;
            image_height = "" + iHeight;
        }

        try {
            image.setVerticalSpace( Integer.parseInt( v_space ) );
        } catch ( NumberFormatException ex ) {
            v_space = "0";
            image.setVerticalSpace( 0 );
        }

        try {
            image.setVerticalSpace( Integer.parseInt( v_space ) );
        } catch ( NumberFormatException ex ) {
            v_space = "0";
            image.setVerticalSpace( 0 );
        }

        try {
            image.setHorizontalSpace( Integer.parseInt( h_space ) );
        } catch ( NumberFormatException ex ) {
            h_space = "0";
            image.setHorizontalSpace( 0 );
        }

        // get imageref
        String image_ref = req.getParameter( "imageref" );
        image.setUrl( image_ref );
        if ( "".equals( image_ref ) ) {  // remove width and height if user removes image ref
            image.setWidth( 0 );
            image.setHeight( 0 );
        }

        // get image_name
        String image_name = req.getParameter( "image_name" );
        if ( null != image_name ) {
            image_name = image_name.trim();
            image.setName( image_name );
        }

        // get image_align
        String image_align = req.getParameter( "image_align" );
        image.setAlign( image_align );

        // get alt_text
        String alt_text = imcode.server.HTMLConv.toHTML( req.getParameter( "alt_text" ) );
        image.setAlternateText( alt_text );

        // get low_scr
        String low_scr = req.getParameter( "low_scr" );
        image.setLowResolutionUrl( low_scr );

        // get target
        String target = EditDocumentInformationPageFlow.getTargetFromRequest( req );
        image.setTarget( target );

        // get image_ref_link
        String imageref_link = req.getParameter( "imageref_link" );
        image.setLinkUrl( imageref_link );

        // Check if user has write rights
        UserDomainObject user = Utility.getLoggedOnUser( req );
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        Utility.setDefaultHtmlContentType( res );
        Writer out = res.getWriter();

        String m_id = req.getParameter( "meta_id" );
        int meta_id = Integer.parseInt( m_id );

        if ( !imcref.checkDocAdminRights( meta_id, user, 131072 ) ) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;
        }
        user.put( "flags", new Integer( 131072 ) );

        //the folderlist
        HttpSession session = req.getSession( true );
        String dirList = (String) session.getAttribute( "imageFolderOptionList" );
        if ( dirList == null ) dirList = "";

        String i_no = req.getParameter( "img_no" );
        int img_no = Integer.parseInt( i_no );

        if ( req.getParameter( "cancel" ) != null ) {
            String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
            if ( output != null ) {
                out.write( output );
            }
            return;

        } else if ( req.getParameter( "show_img" ) != null ) {
            //****************************************************************

            File image_path = Utility.getDomainPrefPath( "image_path" );
            ImageFileMetaData imagefile = new ImageFileMetaData( new FileInputStream(new File( image_path, image_ref )), image_ref );

            int width = imagefile.getWidth();
            int height = imagefile.getHeight();
            //****************************************************************
            Vector vec = new Vector();
            vec.add( "#imgUrl#" );
            vec.add( imcref.getImageUrl() );
            vec.add( "#imgName#" );
            vec.add( image_name );
            vec.add( "#imgRef#" );
            vec.add( image_ref );
            vec.add( "#imgWidth#" );
            vec.add( "0".equals( image_width ) ? "" + width : image_width );
            vec.add( "#imgHeight#" );
            vec.add( "0".equals( image_height ) ? "" + height : image_height );

            vec.add( "#origW#" );
            vec.add( "" + width );
            vec.add( "#origH#" );
            vec.add( "" + height );

            vec.add( "#imgBorder#" );
            vec.add( image_border );
            vec.add( "#imgVerticalSpace#" );
            vec.add( v_space );
            vec.add( "#imgHorizontalSpace#" );
            vec.add( h_space );
            if ( "_top".equals( target ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#top_checked#" );
            } else if ( "_self".equals( target ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#self_checked#" );
            } else if ( "_blank".equals( target ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#blank_checked#" );
            } else if ( "_parent".equals( target ) ) {
                vec.add( "#target_name#" );
                vec.add( "" );
                vec.add( "#blank_checked#" );
            } else {
                vec.add( "#target_name#" );
                vec.add( target );
                vec.add( "#other_checked#" );
            }
            vec.add( "selected" );

            if ( "baseline".equals( image_align ) ) {
                vec.add( "#baseline_selected#" );
            } else if ( "top".equals( image_align ) ) {
                vec.add( "#top_selected#" );
            } else if ( "middle".equals( image_align ) ) {
                vec.add( "#middle_selected#" );
            } else if ( "bottom".equals( image_align ) ) {
                vec.add( "#bottom_selected#" );
            } else if ( "texttop".equals( image_align ) ) {
                vec.add( "#texttop_selected#" );
            } else if ( "absmiddle".equals( image_align ) ) {
                vec.add( "#absmiddle_selected#" );
            } else if ( "absbottom".equals( image_align ) ) {
                vec.add( "#absbottom_selected#" );
            } else if ( "left".equals( image_align ) ) {
                vec.add( "#left_selected#" );
            } else if ( "right".equals( image_align ) ) {
                vec.add( "#right_selected#" );
            } else {
                vec.add( "#none_selected#" );
            }
            vec.add( "selected" );

            vec.add( "#imgAltText#" );
            vec.add( alt_text );
            vec.add( "#imgLowScr#" );
            vec.add( low_scr );
            vec.add( "#imgRefLink#" );
            vec.add( imageref_link );
            vec.add( "#getMetaId#" );
            vec.add( m_id );
            vec.add( "#img_no#" );
            vec.add( i_no );
            vec.add( "#label#" );

            // get img label
            String label = req.getParameter( "label" );
            if ( label == null ) {
                label = "";
            }

            vec.add( label );

            vec.add( "#folders#" );
            vec.add( dirList );
            String htmlStr = imcref.getAdminTemplate( "change_img.html", user, vec );
            out.write( htmlStr );
            return;
        } else if ( req.getParameter( "delete" ) != null ) {
            Vector vec = new Vector();
            vec.add( "#imgUrl#" );
            vec.add( "" );
            vec.add( "#imgName#" );
            vec.add( "" );
            vec.add( "#imgRef#" );
            vec.add( "" );
            vec.add( "#imgWidth#" );
            vec.add( "0" );
            vec.add( "#imgHeight#" );
            vec.add( "0" );
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
            vec.add( "#getMetaId#" );
            vec.add( String.valueOf( meta_id ) );
            vec.add( "#img_no#" );
            vec.add( String.valueOf( img_no ) );

            vec.add( "#folders#" );
            vec.add( dirList );

            String htmlStr = imcref.getAdminTemplate( "change_img.html", user, vec );
            out.write( htmlStr );
            return;
        } else {
            String imageDocumentFileId = req.getParameter( "imageDocumentFileId" );
            if ( null != imageDocumentFileId ) {
                log.debug( "todo, save imageFileDocument and link it to the document. Image file id = " + imageDocumentFileId );
            } else {
                imcref.saveImage( meta_id, user, img_no, image );

                DocumentMapper documentMapper = imcref.getDocumentMapper();
                documentMapper.touchDocument( documentMapper.getDocument( meta_id ) );

                String tempstring = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
                if ( tempstring != null ) {
                    out.write( tempstring );
                }
            }
            return;
        }
    }
}
