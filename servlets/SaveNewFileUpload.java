import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.server.*;

public class SaveNewFileUpload extends HttpServlet {

    public void init( ServletConfig config ) throws ServletException {
        super.init( config );
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();
        File file_path = Utility.getDomainPrefPath( "file_path" );

        imcode.server.user.UserDomainObject user;

        // Check if user logged on
        if ( ( user = Utility.getLoggedOnUserOrRedirect( req, res, start_url ) ) == null ) {
            return;
        }

        res.setContentType( "text/html" );
        res.setHeader( "Cache-Control", "no-cache; must-revalidate;" );
        res.setHeader( "Pragma", "no-cache;" );

        int length = req.getContentLength();

        ServletInputStream in = req.getInputStream();
        Writer out = res.getWriter();

        byte buffer[] = new byte[length];
        int bytes_read = 0;
        while ( bytes_read < length ) {
            bytes_read += in.read( buffer, bytes_read, length - bytes_read );
        }

        String contentType = req.getContentType();
        MultipartFormdataParser mp = new MultipartFormdataParser( new String( buffer, "8859_1" ), contentType );
        String meta_id = mp.getParameter( "new_meta_id" );
        String parent = mp.getParameter( "meta_id" );
        String mime = mp.getParameter( "mime" );
        String other = mp.getParameter( "other" );

        if ( mp.getParameter( "ok" ) != null ) {
            String file = mp.getParameter( "file" );
            String filename = mp.getFilename( "file" );

            filename = filename.substring( filename.lastIndexOf( '/' ) + 1 );
            filename = filename.substring( filename.lastIndexOf( '\\' ) + 1 );

            // Check if a mime-type was chosen
            if ( mime == null || "".equals( mime ) ) {

                // No mime-type chosen?
                // Set the mime-type to the value of the 'other'-field.
                mime = other;

                if ( mime == null || "".equals( mime ) ) {
                    // Nothing in the other-field?

                    // Auto-detect mime-type from filename.
                    if ( !"".equals( filename ) ) {
                        mime = getServletContext().getMimeType( filename );
                    }

                } else if ( mime.indexOf( '/' ) == -1 ) {
                    // The given mimetype does not contain '/',
                    // and is thus invalid.

                    // Assume it is a file-extension,
                    // and autodetect from that.
                    if ( mime.charAt( 0 ) == '.' ) {
                        mime = getServletContext().getMimeType( "_" + mime );
                    } else {
                        mime = getServletContext().getMimeType( "_." + mime );
                    }
                }

                // If we still don't have a mime-type,
                // use standard unknown binary mime-type.
                if ( mime == null || "".equals( mime ) ) {
                    mime = "application/octet-stream";
                }
            }

            if ( file.length() > 0 ) {
                File fn = new File( filename );
                filename = fn.getName();
                fn = new File( file_path, meta_id + "_se" );
                // FIXME: Move to SProc
                String sqlStr = "insert into fileupload_docs (meta_id,filename,mime) values (" + meta_id + ",'" + filename + "','" + mime + "')";
                imcref.sqlUpdateQuery( sqlStr );
                FileOutputStream fos = new FileOutputStream( fn );
                fos.write( file.getBytes( "8859_1" ) );
                fos.close();
                imcref.activateChild( Integer.parseInt( meta_id ), user );
            }
            String output = AdminDoc.adminDoc( Integer.parseInt( meta_id ), Integer.parseInt( meta_id ), user, req, res );
            if ( output != null ) {
                out.write( output );
            }
        } else {
            String output = AdminDoc.adminDoc( Integer.parseInt( parent ), Integer.parseInt( parent ), user, req, res );
            if ( output != null ) {
                out.write( output );
            }
        }
        return;
    }
}
