package com.imcode.imcms.servlet.superadmin;

import com.imcode.util.MultipartHttpServletRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.db.DatabaseUtils;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TemplateAdd extends HttpServlet {

    private static final String REQUEST_PARAMETER__FILE = "file";
    private static final String REQUEST_PARAMETER__OVERWRITE = "overwrite";
    private static final String REQUEST_PARAMETER__NAME = "name";
    private static final String REQUEST_PARAMETER__ACTION = "action";
    private static final String REQUEST_PARAMETER__LANGUAGE = "language";
    private static final String REQUEST_PARAMETER__DEMO = "demo";
    private static final String REQUEST_PARAMETER__TEMPLATE = "template";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        ServletOutputStream out = res.getOutputStream();

        TemplateMapper templateMapper = imcref.getTemplateMapper();

        // Redirected here with bogus parameter, no-cache workaround
        if ( req.getParameter( REQUEST_PARAMETER__ACTION ) != null ) {
            byte[] htmlStr;
            if ( req.getParameter( REQUEST_PARAMETER__ACTION ).equals( "noCacheImageView" ) ) {
                String templateIdString = req.getParameter( REQUEST_PARAMETER__TEMPLATE );
                String mimeType;
                Object[] suffixAndStream = templateMapper.getDemoTemplate( Integer.parseInt( templateIdString ) );
                byte[] temp = (byte[])suffixAndStream[1];

                if ( temp == null || temp.length == 0 ) {
                    htmlStr = imcref.getAdminTemplate( "no_demotemplate.html", user, null ).getBytes( "8859_1" );
                    mimeType = "text/html";
                } else {
                    mimeType = getServletContext().getMimeType( templateIdString + "." + suffixAndStream[0] );
                    htmlStr = temp;
                }
                res.setContentType( mimeType );
                out.write( htmlStr );
            } else if ( req.getParameter( REQUEST_PARAMETER__ACTION ).equals( "return" ) ) {
                Utility.setDefaultHtmlContentType( res );

                List vec = new ArrayList();
                vec.add( "#buttonName#" );
                vec.add( "return" );
                vec.add( "#formAction#" );
                vec.add( "TemplateAdmin" );
                vec.add( "#formTarget#" );
                vec.add( "_top" );
                htmlStr = imcref.getAdminTemplate( "back_button.html", user, vec ).getBytes( "8859_1" );
                out.write( htmlStr );
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

        TemplateMapper templateMapper = imcref.getTemplateMapper();

        PrintWriter out = res.getWriter();

        MultipartHttpServletRequest request = new MultipartHttpServletRequest(req);

        if ( request.getParameter( "cancel" ) != null ) {
            res.sendRedirect( "TemplateAdmin" );
            return;
        }

        String language = request.getParameter( REQUEST_PARAMETER__LANGUAGE );

        boolean demo = request.getParameter( REQUEST_PARAMETER__DEMO ) != null;

        String templateIdString = null;
        String simpleName = null;
        if ( demo ) {
            templateIdString = request.getParameter( REQUEST_PARAMETER__TEMPLATE );
            if ( templateIdString == null || templateIdString.equals( "" ) ) {
                List vec = new ArrayList();
                vec.add( "#language#" );
                vec.add( language );
                String htmlStr = imcref.getAdminTemplate( "templatedemo_upload_template_blank.html", user, vec );
                Utility.setDefaultHtmlContentType( res );
                out.print( htmlStr );
                return;
                // ************************* DELETE DEMO
            } else if ( request.getParameter( "delete_demo" ) != null ) {
                templateMapper.deleteDemoTemplate( Integer.parseInt( templateIdString ) );
                String[] list;
                list = templateMapper.getDemoTemplateIds();
                String[] temp;
                final Object[] parameters = new String[] {language };
                temp = DatabaseUtils.executeStringArrayQuery(imcref.getDatabase(), "select template_id, simple_name from templates where lang_prefix = ? order by simple_name", parameters);
                List vec = new ArrayList();
                vec.add( "#language#" );
                vec.add( language );
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
            } else if ( request.getParameter( "view_demo" ) != null ) {
                Object[] suffixAndStream = templateMapper.getDemoTemplate( Integer.parseInt( templateIdString ) );
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
                        String redirect = "TemplateAdd?action=noCacheImageView&template=" + templateIdString + "&bogus="
                                          + (int) ( 1000 * Math.random() );

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
            simpleName = request.getParameter( REQUEST_PARAMETER__NAME );
            if ( simpleName == null || simpleName.equals( "" ) ) {
                List vec = new ArrayList();
                vec.add( "#language#" );
                vec.add( language );
                String htmlStr = imcref.getAdminTemplate( "template_upload_name_blank.html", user, vec );
                Utility.setDefaultHtmlContentType( res );
                out.print( htmlStr );
                return;
            }
        }

        FileItem file = request.getParameterFileItem( REQUEST_PARAMETER__FILE );
        if ( file == null || file.getSize() == 0 ) {
            List vec = new ArrayList();
            vec.add( "#language#" );
            vec.add( language );
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

        String filename = request.getParameterFileItem( REQUEST_PARAMETER__FILE ).getName();
        File fn = new File( filename );
        filename = fn.getName();
        boolean overwrite = request.getParameter(REQUEST_PARAMETER__OVERWRITE) != null;
        String htmlStr;

        if ( demo ) {
            // get the suffix
            String suffix = StringUtils.substringAfterLast(filename, ".").toLowerCase() ;

            List vec = new ArrayList();
            if ( !suffix.equals( "jpg" ) && !suffix.equals( "jpeg" ) && !suffix.equals( "png" ) && !suffix.equals(
                    "gif" ) && !suffix.equals( "htm" ) && !suffix.equals( "html" ) ) {
                vec.add( "#language#" );
                vec.add( language );
                htmlStr = imcref.getAdminTemplate( "templatedemo_upload_done.html", user, vec );
            } else {

                vec.add( "#language#" );
                vec.add( language );

                try {
                    imcref.getTemplateMapper().saveDemoTemplate( Integer.parseInt( templateIdString ), file.getInputStream(), suffix );
                    htmlStr = imcref.getAdminTemplate( "templatedemo_upload_done.html", user, vec );
                } catch ( IOException ex ) {
                    htmlStr = imcref.getAdminTemplate( "templatedemo_upload_error.html", user, vec );
                }
            }
        } else {
            int result = imcref.getTemplateMapper().saveTemplate( simpleName, filename, file.getInputStream(), overwrite, language );
            if ( result == -2 ) {
                List vec = new ArrayList();
                vec.add( "#language#" );
                vec.add( language );
                htmlStr = imcref.getAdminTemplate( "template_upload_error.html", user, vec );
            } else if ( result == -1 ) {
                List vec = new ArrayList();
                vec.add( "#language#" );
                vec.add( language );
                htmlStr = imcref.getAdminTemplate( "template_upload_file_exists.html", user, vec );
            } else {
                TemplateDomainObject template = templateMapper.getTemplateByName(simpleName);

                String[] templateGroupIdStrings = request.getParameterValues( "templategroup" );
                if ( templateGroupIdStrings != null ) {
                    for ( int i = 0; i < templateGroupIdStrings.length; i++ ) {
                        int templateGroupId = Integer.parseInt(templateGroupIdStrings[i]) ;
                        TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(templateGroupId) ;
                        templateMapper.removeTemplateFromGroup(template, templateGroup);
                        templateMapper.addTemplateToGroup(template, templateGroup);
                    }
                }

                List vec = new ArrayList();
                vec.add( "#language#" );
                vec.add( language );
                htmlStr = imcref.getAdminTemplate( "template_upload_done.html", user, vec );
            }
        }
        Utility.setDefaultHtmlContentType( res );
        out.print( htmlStr );
    }

}
