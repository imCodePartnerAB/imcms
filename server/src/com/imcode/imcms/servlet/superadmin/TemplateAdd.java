package com.imcode.imcms.servlet.superadmin;

import com.imcode.util.MultipartHttpServletRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.fileupload.FileItem;

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

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        ServletOutputStream out = res.getOutputStream();

        // Redirected here with bogus parameter, no-cache workaround
        if ( req.getParameter( REQUEST_PARAMETER__ACTION ) != null ) {
            if ( req.getParameter( REQUEST_PARAMETER__ACTION ).equals( "return" ) ) {
                Utility.setDefaultHtmlContentType( res );

                List vec = new ArrayList();
                vec.add( "#buttonName#" );
                vec.add( "return" );
                vec.add( "#formAction#" );
                vec.add( "TemplateAdmin" );
                vec.add( "#formTarget#" );
                vec.add( "_top" );
                out.write( imcref.getAdminTemplate("back_button.html", user, vec).getBytes(Imcms.DEFAULT_ENCODING) );
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

        String simpleName = request.getParameter(REQUEST_PARAMETER__NAME);
        if ( simpleName == null || simpleName.equals( "" ) ) {
            List vec = new ArrayList();
            vec.add( "#language#" );
            vec.add( language );
            String htmlStr = imcref.getAdminTemplate( "template_upload_name_blank.html", user, vec );
            Utility.setDefaultHtmlContentType( res );
            out.print( htmlStr );
            return;
        }

        FileItem file = request.getParameterFileItem( REQUEST_PARAMETER__FILE );
        if ( file == null || file.getSize() == 0 ) {
            List vec = new ArrayList();
            vec.add( "#language#" );
            vec.add( language );
            String htmlStr = imcref.getAdminTemplate("template_upload_file_blank.html", user, vec);
            Utility.setDefaultHtmlContentType( res );
            out.print( htmlStr );
            return;
        }

        String filename = request.getParameterFileItem( REQUEST_PARAMETER__FILE ).getName();
        File fn = new File( filename );
        boolean overwrite = request.getParameter(REQUEST_PARAMETER__OVERWRITE) != null;
        String htmlStr;

        int result = imcref.getTemplateMapper().saveTemplate( simpleName, fn.getName(), file.getInputStream(), overwrite);
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
                for ( String templateGroupIdString : templateGroupIdStrings ) {
                    int templateGroupId = Integer.parseInt(templateGroupIdString);
                    TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById(templateGroupId);
                    templateMapper.removeTemplateFromGroup(template, templateGroup);
                    templateMapper.addTemplateToGroup(template, templateGroup);
                }
            }

            List vec = new ArrayList();
            vec.add( "#language#" );
            vec.add( language );
            htmlStr = imcref.getAdminTemplate( "template_upload_done.html", user, vec );
        }
        Utility.setDefaultHtmlContentType( res );
        out.print( htmlStr );
    }

}
