package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.DocumentMapper;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SaveImage extends HttpServlet {

    final static String REQUEST_PARAMETER__IMAGE_URL = "imageref" ;

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        String okParameter = req.getParameter( "ok" );
        String showImageParam = req.getParameter( "show_img" );

        String imageHeight = req.getParameter( "image_height" );
        String imageWidth = req.getParameter( "image_width" );
        String oldImageHeight = req.getParameter( "oldH" );
        String oldImageWidth = req.getParameter( "oldW" );
        String imageBorder = req.getParameter( "image_border" );
        String verticalSpace = req.getParameter( "v_space" );
        String horizontalSpace = req.getParameter( "h_space" );
        boolean keepAspectRatio = (req.getParameter( "keepAspectRatio" ) != null);
        String originalImageWidth = req.getParameter( "origW" );
        String originalImageHeight = req.getParameter( "origH" );
        ImageDomainObject image = new ImageDomainObject();
        try {
            image.setHeight( Integer.parseInt( imageHeight ) );
        } catch ( NumberFormatException ex ) {
            imageHeight = "0";
            image.setHeight( 0 );
        }

        try {
            image.setBorder( Integer.parseInt( imageBorder ) );
        } catch ( NumberFormatException ex ) {
            imageBorder = "0";
            image.setBorder( 0 );
        }

        try {
            image.setWidth( Integer.parseInt( imageWidth ) );
        } catch ( NumberFormatException ex ) {
            imageWidth = "0";
            image.setWidth( 0 );
        }

        if ( keepAspectRatio && (okParameter != null || showImageParam != null) ) {
            int oHeight = 0;
            try {
                oHeight = Integer.parseInt( originalImageHeight ); // image height
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse origHeight" );
            }
            int oWidth = 0;
            try {
                oWidth = Integer.parseInt( originalImageWidth ); // image width
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse origHeight" );
            }

            int iHeight = 0;
            try {
                iHeight = Integer.parseInt( imageHeight ); // form width
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse image_height" );
            }
            int iWidth = 0;
            try {
                iWidth = Integer.parseInt( imageWidth ); // form height
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse image_width" );
            }

            int oldHeight = 0;
            try {
                oldHeight = (oldImageHeight.length() > 0) ? Integer.parseInt( oldImageHeight ) : oHeight;
            } catch ( NumberFormatException ex ) {
                log( "Failed to parse oldHeight" );
            }
            int oldWidth = 0;
            try {
                oldWidth = (oldImageWidth.length() > 0) ? Integer.parseInt( oldImageWidth ) : oWidth;
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
            imageWidth = "" + iWidth;
            imageHeight = "" + iHeight;
        }

        try {
            image.setVerticalSpace( Integer.parseInt( verticalSpace ) );
        } catch ( NumberFormatException ex ) {
            verticalSpace = "0";
            image.setVerticalSpace( 0 );
        }

        try {
            image.setVerticalSpace( Integer.parseInt( verticalSpace ) );
        } catch ( NumberFormatException ex ) {
            verticalSpace = "0";
            image.setVerticalSpace( 0 );
        }

        try {
            image.setHorizontalSpace( Integer.parseInt( horizontalSpace ) );
        } catch ( NumberFormatException ex ) {
            horizontalSpace = "0";
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
            goBack( meta_id, user, req, res, out );
            return ;
        }
        user.put( "flags", new Integer( 131072 ) );

        //the folderlist
        HttpSession session = req.getSession( true );
        String dirList = (String) session.getAttribute( "imageFolderOptionList" );
        if ( dirList == null ) dirList = "";

        String i_no = req.getParameter( "img_no" );
        int img_no = Integer.parseInt( i_no );

        if ( req.getParameter( "cancel" ) != null ) {
            goBack( meta_id, user, req, res, out );
            return;

        } else if ( req.getParameter( "delete" ) != null ) {
            delete( meta_id, img_no, dirList, imcref, user, out );
            return;

        } else {
            save( req, image, imcref, meta_id, user, img_no, res, out );
            return;

        }
    }

    private void goBack( int meta_id, UserDomainObject user, HttpServletRequest req, HttpServletResponse res,
                         Writer out ) throws IOException, ServletException {
        String output = AdminDoc.adminDoc( meta_id, meta_id, user, req, res );
        if ( output != null ) {
            out.write( output );
        }
    }

    private void save( HttpServletRequest req, ImageDomainObject image, IMCServiceInterface imcref, int meta_id,
                       UserDomainObject user, int img_no, HttpServletResponse res, Writer out ) throws IOException, ServletException {
        String imageUrl = req.getParameter( REQUEST_PARAMETER__IMAGE_URL ) ;
        image.setUrl( imageUrl );

        imcref.saveImage( meta_id, user, img_no, image );
        ApplicationServer.getIMCServiceInterface().updateMainLog("ImageRef " + img_no + " =" + image.getUrl() +
                " in  " + "[" + meta_id + "] modified by user: [" +
                user.getFullName() + "]");


        DocumentMapper documentMapper = imcref.getDocumentMapper();
        documentMapper.touchDocument( documentMapper.getDocument( meta_id ) );

        goBack( meta_id, user, req, res, out );
    }

    private void delete( int meta_id, int img_no, String dirList, IMCServiceInterface imcref, UserDomainObject user,
                         Writer out ) throws IOException {
        List vec = new ArrayList();
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
    }

}
