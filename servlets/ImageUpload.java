import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.server.*;

/**
 *  A class that allows Web users to upload local files to a web server's file system.
 */
public class ImageUpload extends HttpServlet {

    public void init( ServletConfig config ) throws ServletException {
        super.init( config );
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();
        File file_path = Utility.getDomainPrefPath( "image_path" );
        String image_url = imcref.getImageUrl();

        imcode.server.user.UserDomainObject user;

        // Check if user logged on
        if ( ( user = Check.userLoggedOn( req, res, start_url ) ) == null ) {
            return;
        }

        res.setContentType( "text/html" );

        int length = req.getContentLength();

        ServletInputStream in = req.getInputStream();
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession( true );

        session.removeAttribute( "ImageBrowse.optionlist" );

        byte buffer[] = new byte[length];
        int bytes_read = 0;
        while ( bytes_read < length ) {
            bytes_read += in.read( buffer, bytes_read, length - bytes_read );
        }


        String contentType = req.getContentType();
        MultipartFormdataParser mp = new MultipartFormdataParser( new String( buffer, "8859_1" ), contentType );
        String file = mp.getParameter( "file" );
        String filename = mp.getFilename( "file" );
        if ( file.equals( "" ) ) {
            res.sendRedirect( "ChangeImage?meta_id=" + mp.getParameter( "meta_id" ) + "&img_no=" + mp.getParameter( "img_no" ) );
            return;
        }
        String folder = mp.getParameter( "folder" );//ex: /se
        if ( folder == null ) folder = "";
        //submitted with Browse Images button, no ImageUpload (M Wallin)
        if ( mp.getParameter( "browse_images" ) != null ) { // Browse Image Library
            Utility.redirect( req, res, "ImageBrowse" );
        }

        if ( mp.getParameter( "ok" ) == null ) {
            doGet( req, res );
            return;
        }
        int meta_id = Integer.parseInt( mp.getParameter( "meta_id" ) );
        int img_no = Integer.parseInt( mp.getParameter( "img_no" ) );

        // extraParameter, presets imagepath... set by ImageBrowse

        filename = filename.substring( filename.lastIndexOf( '/' ) + 1 );
        filename = filename.substring( filename.lastIndexOf( '\\' ) + 1 );

        File fn = new File( new File( file_path, folder ), filename );

        if ( file.length() > 0 ) {
            if ( fn.exists() ) {
                Vector vec = new Vector();
                vec.add( "#back#" );
                vec.add( "ChangeImage?meta_id=" + meta_id + "&img_no=" + img_no );
                vec.add( "#meta_id#" );
                vec.add( String.valueOf( meta_id ) );
                vec.add( "#img_no#" );
                vec.add( String.valueOf( img_no ) );
                String htmlStr = imcref.parseDoc( vec, "file_exists.html", user.getLangPrefix() );
                out.println( htmlStr );
                return;
            }
            FileOutputStream fos = new FileOutputStream( fn );
            fos.write( file.getBytes( "8859_1" ) );
            fos.close();
        }
        String folderOptList = (String)session.getAttribute( "imageFolderOptionList" );
        StringBuffer buff = new StringBuffer( folderOptList );
        int countX = folderOptList.indexOf( folder );
        if ( countX > 0 )
            buff.insert( countX + folder.length() + 1, "selected" );

        if ( folderOptList == null ) folderOptList = "";

        //String htmlStr = imcref.interpretAdminTemplate(meta_id,user,"change_img.html",img_no,0,0,0) ;
        //out.println(htmlStr) ;
        String image_ref = fn.getCanonicalPath();
        image_ref = image_ref.substring( file_path.getCanonicalPath().length() + 1 );
        image_ref = image_ref.replace( '\\', '/' );
        ImageFileMetaData imagefile = new ImageFileMetaData( new File( file_path, image_ref ) );
        int width = imagefile.getWidth();
        int height = imagefile.getHeight();

        Vector vec = new Vector();
        vec.add( "#keep_aspect#" );
        vec.add( "checked" );
        vec.add( "#imgUrl#" );
        vec.add( image_url );
        vec.add( "#imgName#" );
        vec.add( "" );
        vec.add( "#imgRef#" );
        vec.add( image_ref );
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
        vec.add( "#getMetaId#" );
        vec.add( String.valueOf( meta_id ) );
        vec.add( "#img_no#" );
        vec.add( String.valueOf( img_no ) );

        vec.add( "#folders#" );
        vec.add( buff.toString() );

        String lang_prefix = user.getLangPrefix();
        String htmlStr = imcref.parseDoc( vec, "change_img.html", lang_prefix );
        out.print( htmlStr );

        return;
    }
}
