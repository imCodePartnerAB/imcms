package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.MultipartFormdataParser;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class TemplateAdd extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        ServletOutputStream out = res.getOutputStream();

        //**********************************************************************************************
        // Redirected here with bogus parameter, no-cache workaround
        //
        if ( req.getParameter( "action" ) != null ) {
            byte[] htmlStr;
            if ( req.getParameter( "action" ).equals( "noCacheImageView" ) ) {
                String template = req.getParameter( "template" );
                String mimeType;
                Object[] suffixAndStream = imcref.getDemoTemplate( Integer.parseInt( template ) );
                byte[] temp = (byte[])suffixAndStream[1];

                if ( temp == null || temp.length == 0 ) {
                    htmlStr = imcref.getAdminTemplate( "no_demotemplate.html", user, null ).getBytes( "8859_1" );
                    mimeType = "textdocument/html";
                } else {
                    mimeType = getServletContext().getMimeType( template + "." + suffixAndStream[0] );
                    htmlStr = temp;
                }
                System.out.println( "mimet: " + mimeType );
                res.setContentType( mimeType );
                out.write( htmlStr );
                return;
            } else if ( req.getParameter( "action" ).equals( "return" ) ) {
                Utility.setDefaultHtmlContentType( res );

                Vector vec = new Vector();
                vec.add( "#buttonName#" );
                vec.add( "return" );
                vec.add( "#formAction#" );
                vec.add( "TemplateAdmin" );
                vec.add( "#formTarget#" );
                vec.add( "_top" );
                htmlStr = imcref.getAdminTemplate( "back_button.html", user, vec ).getBytes( "8859_1" );
                out.write( htmlStr );
                return;
            }
        }

    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        int length = req.getContentLength();

        PrintWriter out = res.getWriter();

        ServletInputStream in = req.getInputStream();
        byte[] buffer = new byte[length];
        int bytes_read = 0;
        while ( bytes_read < length ) {
            bytes_read += in.read( buffer, bytes_read, length - bytes_read );
        }
        String contentType = req.getContentType();

        // Min klass tar emot datan och plockar ut det som är intressant...
        MultipartFormdataParser mp = new MultipartFormdataParser( buffer, contentType );

        if ( mp.getParameter( "cancel" ) != null ) {
            res.sendRedirect( "TemplateAdmin" );
            return;
        }

        // Plocka ut språket, så vi vet vilket vi editerar...
        String lang = mp.getParameter( "language" );

        boolean demo = mp.getParameter( "demo" ) != null;

        String template = null;
        String simple_name = null;
        if ( demo ) {
            template = mp.getParameter( "template" );
            if ( template == null || template.equals( "" ) ) {
                Vector vec = new Vector();
                vec.add( "#language#" );
                vec.add( lang );
                String htmlStr = imcref.getAdminTemplate( "templatedemo_upload_template_blank.html", user, vec );
                Utility.setDefaultHtmlContentType( res );
                out.print( htmlStr );
                return;
                // ************************* DELETE DEMO
            } else if ( mp.getParameter( "delete_demo" ) != null ) {
                imcref.deleteDemoTemplate( Integer.parseInt( template ) );
                String[] list;
                list = imcref.getDemoTemplateIds();
                String[] temp;
                temp = imcref.getDatabase().executeArrayQuery( "select template_id, simple_name from templates where lang_prefix = ? order by simple_name", new String[] {lang} );
                Vector vec = new Vector();
                vec.add( "#language#" );
                vec.add( lang );
                String htmlStr;
                if ( temp.length > 0 ) {
                    String temps = "";
                    for ( int i = 0; i < temp.length; i += 2 ) {
                        int tmp = Integer.parseInt( temp[i] );
                        for ( int j = 0; j < list.length; j++ ) {
                            if ( Integer.parseInt( list[j] ) == tmp ) {
                                temp[i + 1] = "*" + temp[i + 1];
                                break;
                            }
                        }
                        temps += "<option value=\"" + temp[i] + "\">" + temp[i + 1] + "</option>";
                    }
                    vec.add( "#templates#" );
                    vec.add( temps );
                    htmlStr = imcref.getAdminTemplate( "templatedemo_upload.html", user, vec );
                } else {
                    htmlStr = imcref.getAdminTemplate( "template_no_langtemplates.html", user, vec );
                }
                Utility.setDefaultHtmlContentType( res );
                out.print( htmlStr );
                return;
                // ************************** VIEW DEMO
                // Updated DefaultImcmsServices + interface, IMCServiceRMI : Now returns Object[] filesuffix, byteStream
            } else if ( mp.getParameter( "view_demo" ) != null ) {
                Object[] suffixAndStream = imcref.getDemoTemplate( Integer.parseInt( template ) );
                String htmlStr;
                Utility.setDefaultHtmlContentType( res );
                if ( suffixAndStream == null ) {
                    htmlStr = imcref.getAdminTemplate( "no_demotemplate.html", user, null );
                    out.print( htmlStr );
                    return;

                } else {
                    byte[] temp = (byte[])suffixAndStream[1];
                    if ( temp == null ) {
                        htmlStr = imcref.getAdminTemplate( "no_demotemplate.html", user, null );
                        out.print( htmlStr );
                        return;
                    } else {
                        htmlStr = new String( temp, "8859_1" );
                        //res.setContentType(mimeType) ;
                        String redirect = ( "TemplateAdd?action=noCacheImageView&template=" + template + "&bogus="
                                + (int)( 1000 * Math.random() ) );

                        // create frameset with topframe containing return-button
                        // and the main-frame doing a redirect
                        //FIXME: What The Fawk is this? Put it in a template, or suffer the consequences!
                        out.print(
                                "<html><head><title></title></head>"
                                + "<frameset rows=\"80,*\" frameborder=\"NO\" border=\"0\" framespacing=\"0\">"
                                + "<frame name=\"topFrame\" scrolling=\"NO\" noresize src=\"TemplateAdd?action=return\">"
                                + "<frame name=\"mainFrame\" src=\""
                                + redirect
                                + "\">"
                                + "</frameset>"
                                + "<noframes><body>"
                                + redirect
                                + "</body></noframes></html>" );
                    }

                }

            }
        } else {
            simple_name = mp.getParameter( "name" );
            if ( simple_name == null || simple_name.equals( "" ) ) {
                Vector vec = new Vector();
                vec.add( "#language#" );
                vec.add( lang );
                String htmlStr = imcref.getAdminTemplate( "template_upload_name_blank.html", user, vec );
                Utility.setDefaultHtmlContentType( res );
                out.print( htmlStr );
                return;
            }
        }

        String file = mp.getParameter( "file" );
        if ( file == null || file.length() == 0 ) {
            Vector vec = new Vector();
            vec.add( "#language#" );
            vec.add( lang );
            String htmlStr;
            if ( demo ) {
                htmlStr = imcref.getAdminTemplate( "templatedemo_upload_file_blank.html", user, vec );
            } else {
                htmlStr = imcref.getAdminTemplate( "template_upload_file_blank.html", user, vec );

            }
            Utility.setDefaultHtmlContentType( res );
            out.print( htmlStr );
            return;
        }

        log( "Filesize: " + file.length() );
        String filename = mp.getFilename( "file" );
        log( filename );
        File fn = new File( filename );
        filename = fn.getName();
        boolean overwrite = ( mp.getParameter( "overwrite" ) != null );
        String htmlStr;

        // ********************************** OK

        if ( demo ) {
            // get the suffix
            log( "*** TEMPLATE_ADD ***  FILENAME = " + filename + " | SUFFIX = " + filename.substring(
                    filename.lastIndexOf( '.' ) + 1 ) );
            String suffix = filename.substring( filename.lastIndexOf( '.' ) + 1 );

            if ( filename.lastIndexOf( "." ) == -1 ) {
                suffix = "";
            }
            Vector vec = new Vector();
            if ( !suffix.equals( "jpg" ) && !suffix.equals( "jpeg" ) && !suffix.equals( "png" ) && !suffix.equals(
                    "gif" ) && !suffix.equals( "htm" ) && !suffix.equals( "html" ) ) {
                vec.add( "#language#" );
                vec.add( lang );
                htmlStr = imcref.getAdminTemplate( "templatedemo_upload_done.html", user, vec );
            } else {

                vec.add( "#language#" );
                vec.add( lang );

                try {
                    imcref.saveDemoTemplate( Integer.parseInt( template ), file.getBytes( "8859_1" ), suffix );
                    htmlStr = imcref.getAdminTemplate( "templatedemo_upload_done.html", user, vec );
                } catch ( IOException ex ) {
                    htmlStr = imcref.getAdminTemplate( "templatedemo_upload_error.html", user, vec );
                }
            }
        } else {
            int result = imcref.saveTemplate( simple_name, filename, file.getBytes( "8859_1" ), overwrite, lang );

            if ( result == -2 ) {
                Vector vec = new Vector();
                vec.add( "#language#" );
                vec.add( lang );
                htmlStr = imcref.getAdminTemplate( "template_upload_error.html", user, vec );
            } else if ( result == -1 ) {
                Vector vec = new Vector();
                vec.add( "#language#" );
                vec.add( lang );
                htmlStr = imcref.getAdminTemplate( "template_upload_file_exists.html", user, vec );
            } else {
                String t_id = imcref.getDatabase().executeStringQuery( "select template_id from templates where simple_name = ?", new String[] {simple_name} );
                String[] temp = mp.getParameterValues( "templategroup" );
                if ( temp != null ) {
                    for ( int foo = 0; foo < temp.length; foo++ ) {
                        String sqlStr = "delete from templates_cref where group_id = ? and template_id = ?\n"
                                + "insert into templates_cref (group_id, template_id) values(?,?)\n";
                        imcref.getDatabase().executeUpdateQuery( sqlStr, new String[] {temp[foo],
                                                                                                    t_id, temp[foo],
                                                                                                    t_id} );
                    }
                }

                Vector vec = new Vector();
                vec.add( "#language#" );
                vec.add( lang );
                htmlStr = imcref.getAdminTemplate( "template_upload_done.html", user, vec );
            }
        }
        Utility.setDefaultHtmlContentType( res );
        out.print( htmlStr );
        return;
    }

}
