package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.imcode.imcms.servlet.superadmin.TemplateAdmin;
import com.imcode.imcms.servlet.superadmin.TemplateAdmin;

public class TemplateChange extends HttpServlet {

    public void service( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        Utility.setDefaultHtmlContentType( res );

        TemplateMapper templateMapper = imcref.getTemplateMapper();
        ServletOutputStream out = res.getOutputStream();
        String htmlStr = null;
        String lang = req.getParameter( "language" );
        if ( req.getParameter( "cancel" ) != null ) {
            res.sendRedirect( "TemplateAdmin" );
        } else if ( req.getParameter( "template_get" ) != null ) {
            downloadTemplate( req, imcref, res, out );
        } else if ( req.getParameter( "template_delete_cancel" ) != null ) {
            htmlStr = TemplateAdmin.createDeleteTemplateDialog( templateMapper, user, lang, imcref ) ;
        } else if ( req.getParameter( "template_delete" ) != null ) {
            deleteTemplate( req, imcref );
            htmlStr = TemplateAdmin.createDeleteTemplateDialog( templateMapper, user, lang, imcref ) ;
        } else if ( req.getParameter( "assign" ) != null ) {
            htmlStr = addTemplatesToGroup( req, templateMapper, lang, user, imcref );
        } else if ( req.getParameter( "deassign" ) != null ) {
            htmlStr = removeTemplatesFromGroup( req, templateMapper, lang, user, imcref );
        } else if ( req.getParameter( "show_assigned" ) != null ) {
            int templateGroupId = Integer.parseInt( req.getParameter( "templategroup" ) );
            TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById( templateGroupId );
            htmlStr =
                    TemplateAdmin.createAssignTemplatesToGroupDialog( templateMapper, templateGroup, lang, user,
                                                                      imcref );
        } else if ( req.getParameter( "template_rename" ) != null ) {
            htmlStr = renameTemplate( req, templateMapper, lang, imcref, user );
        } else if ( req.getParameter( "template_delete_check" ) != null ) {
            htmlStr = deleteTemplateAfterCheckingUsage( req, imcref, lang, user );
        } else if ( req.getParameter( "group_delete_check" ) != null ) {
            htmlStr = deleteTemplateGroupAfterCheckingUsage( req, imcref, user );
        } else if ( req.getParameter( "group_delete" ) != null ) {
            deleteTemplateGroup( req, imcref );
            htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog( templateMapper, imcref, user );
        } else if ( req.getParameter( "group_delete_cancel" ) != null ) {
            htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog( templateMapper, imcref, user );
        } else if ( req.getParameter( "group_add" ) != null ) {
            htmlStr = addTemplateGroup( req, imcref, user );
        } else if ( req.getParameter( "group_rename" ) != null ) {
            htmlStr = renameTemplateGroup( req, imcref, user, lang );
        } else if ( req.getParameter( "list_templates_docs" ) != null ) {
            htmlStr = listDocumentsUsingTemplate( req, imcref, lang, user );
        } else if ( req.getParameter( "show_doc" ) != null ) {
            htmlStr = showDocument( req, res, imcref, lang, htmlStr, user );
        }

        if ( null != htmlStr ) {
            out.print( htmlStr );
        }
    }

    private String addTemplateGroup( HttpServletRequest req, ImcmsServices imcref, UserDomainObject user ) {
        String htmlStr;
        String name = req.getParameter( "name" );
        if ( name == null || name.equals( "" ) ) {
            htmlStr = createAddNameEmptyErrorDialog( imcref, user );
        } else {
            TemplateGroupDomainObject templateGroup = imcref.getTemplateMapper().getTemplateGroupByName(name) ;
            if ( null != templateGroup ) {
                htmlStr = createTemplateGroupExistsErrorDialog( imcref, user );
            } else {
                imcref.getTemplateMapper().createTemplateGroup( name );
                htmlStr = TemplateAdmin.createAddGroupDialog( imcref, user );
            }
        }
        return htmlStr;
    }

    private String addTemplatesToGroup( HttpServletRequest req, TemplateMapper templateMapper, String lang,
                                        UserDomainObject user, ImcmsServices imcref ) {
        String htmlStr;
        int grp_id = Integer.parseInt( req.getParameter( "group_id" ) );
        String[] templatesToAssign = req.getParameterValues( "unassigned" );
        TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById( grp_id );
        for ( int i = 0; templatesToAssign != null && i < templatesToAssign.length; i++ ) {
            int templateId = Integer.parseInt( templatesToAssign[i] );
            TemplateDomainObject templateToAssign = templateMapper.getTemplateById( templateId );
            templateMapper.addTemplateToGroup( templateToAssign, templateGroup );
        }
        htmlStr =
                TemplateAdmin.createAssignTemplatesToGroupDialog( templateMapper, templateGroup, lang, user, imcref );
        return htmlStr;
    }

    private String createAddNameEmptyErrorDialog( ImcmsServices imcref, UserDomainObject user ) {
        String htmlStr;
        htmlStr = imcref.getAdminTemplate( "templategroup_add_name_blank.html", user, null );
        return htmlStr;
    }

    private String createDocumentsUsingTemplateDialog( ImcmsServices imcref, UserDomainObject user,
                                                       TemplateDomainObject template, String lang ) {
        List vec2 = new ArrayList();
        vec2.add( "#template_list#" );
        vec2.add( imcref.getTemplateMapper().createHtmlOptionListOfTemplatesWithDocumentCount( user ) );
        if ( template != null ) {
            vec2.add( "#templates_docs#" );
            vec2.add( TemplateAdmin.createHtmlOptionListOfDocumentsUsingTemplate( imcref, template, user ) );
        }
        vec2.add( "#language#" );
        vec2.add( lang );
        String htmlStr = imcref.getAdminTemplate( "template_list.html", user, vec2 );
        return htmlStr;
    }

    private String createRenameNameEmptyErrorDialog( String lang, ImcmsServices imcref, UserDomainObject user ) {
        String htmlStr;
        List vec = new ArrayList();
        vec.add( "#language#" );
        vec.add( lang );
        htmlStr = imcref.getAdminTemplate( "template_rename_name_blank.html", user, vec );
        return htmlStr;
    }

    private String createRenameTemplateDialog( String lang, TemplateMapper templateMapper, ImcmsServices imcref,
                                               UserDomainObject user ) {
        String htmlStr;
        List vec = new ArrayList();
        vec.add( "#language#" );
        vec.add( lang );
        vec.add( "#templates#" );
        vec.add( templateMapper.createHtmlOptionListOfTemplates( templateMapper.getAllTemplates(), null ) );
        htmlStr = imcref.getAdminTemplate( "template_rename.html", user, vec );
        return htmlStr;
    }

    private String createTemplateGroupExistsErrorDialog( ImcmsServices imcref, UserDomainObject user ) {
        String htmlStr;
        htmlStr = imcref.getAdminTemplate( "templategroup_add_exists.html", user, null );
        return htmlStr;
    }

    private void deleteTemplate( HttpServletRequest req, ImcmsServices imcref ) {
        TemplateMapper templateMapper = imcref.getTemplateMapper() ;
        int new_temp_id = Integer.parseInt(req.getParameter( "new_template" ));
        TemplateDomainObject newTemplate = templateMapper.getTemplateById( new_temp_id ) ;
        int template_id = Integer.parseInt( req.getParameter( "template" ) );
        TemplateDomainObject template = templateMapper.getTemplateById( template_id ) ;

        templateMapper.replaceAllUsagesOfTemplate( template, newTemplate, imcref );
        templateMapper.deleteTemplate( template );
    }

    private String deleteTemplateAfterCheckingUsage( HttpServletRequest req, ImcmsServices imcref, String lang,
                                                     UserDomainObject user ) {
        String htmlStr;
        TemplateMapper templateMapper = imcref.getTemplateMapper() ;
        int template_id = Integer.parseInt( req.getParameter( "template" ) );
        TemplateDomainObject template = templateMapper.getTemplateById( template_id ) ;
        DocumentDomainObject[] documentsUsingTemplate = templateMapper.getDocumentsUsingTemplate(template) ;
        if ( documentsUsingTemplate.length > 0 ) {
            htmlStr = TemplateAdmin.createDeleteTemplateInUseWarningDialog( lang, imcref, template, user, templateMapper );
        } else {
            templateMapper.deleteTemplate( template );
            htmlStr = TemplateAdmin.createDeleteTemplateDialog( templateMapper, user, lang, imcref ) ;
        }
        return htmlStr;
    }

    private void deleteTemplateGroup( HttpServletRequest req, ImcmsServices imcref ) {
        int grp_id = Integer.parseInt( req.getParameter( "templategroup" ) );
        imcref.getTemplateMapper().deleteTemplateGroup( grp_id );
    }

    private String deleteTemplateGroupAfterCheckingUsage( HttpServletRequest req, ImcmsServices imcref,
                                                          UserDomainObject user ) {
        String htmlStr;
        int templateGroupId = Integer.parseInt( req.getParameter( "templategroup" ) );
        TemplateMapper templateMapper = imcref.getTemplateMapper();
        TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById( templateGroupId );
        TemplateDomainObject[] templatesInGroup = templateMapper.getTemplatesInGroup( templateGroup );

        if ( templatesInGroup.length > 0 ) {
            htmlStr = TemplateAdmin.createDeleteNonEmptyTemplateGroupWarningDialog( templatesInGroup, templateGroupId, imcref, user );
        } else {
            templateMapper.deleteTemplateGroup( templateGroupId );
            htmlStr = TemplateAdmin.createDeleteTemplateGroupDialog(templateMapper, imcref, user ) ;
        }
        return htmlStr;
    }

    private void downloadTemplate( HttpServletRequest req, ImcmsServices imcref, HttpServletResponse res,
                                   ServletOutputStream out ) throws IOException {
        int template_id = Integer.parseInt( req.getParameter( "template" ) );
        String filename = imcref.getTemplateMapper().getTemplateById( template_id ).getFileName();
        if ( filename == null ) {
            filename = "";
        }
        byte[] file = imcref.getTemplateData( template_id ).getBytes();
        res.setContentType( "application/octet-stream; name=\"" + filename + "\"" );
        res.setContentLength( file.length );
        res.setHeader( "Content-Disposition", "attachment; filename=\"" + filename + "\";" );
        out.write( file );
        out.flush();
    }

    private String listDocumentsUsingTemplate( HttpServletRequest req, ImcmsServices imcref, String lang,
                                               UserDomainObject user ) {
        int templateId = Integer.parseInt( req.getParameter( "template" ) );
        TemplateDomainObject template = imcref.getTemplateMapper().getTemplateById( templateId );
        return createDocumentsUsingTemplateDialog( imcref, user, template, lang );
    }

    private String removeTemplatesFromGroup( HttpServletRequest req, TemplateMapper templateMapper, String lang,
                                             UserDomainObject user, ImcmsServices imcref ) {
        String htmlStr;
        int grp_id = Integer.parseInt( req.getParameter( "group_id" ) );
        String[] templatesToUnassign = req.getParameterValues( "assigned" );
        TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById( grp_id );
        for ( int i = 0; templatesToUnassign != null && i < templatesToUnassign.length; i++ ) {
            int templateId = Integer.parseInt( templatesToUnassign[i] );
            TemplateDomainObject templateToUnassign = templateMapper.getTemplateById( templateId );
            templateMapper.removeTemplateFromGroup( templateToUnassign, templateGroup );
        }
        htmlStr =
                TemplateAdmin.createAssignTemplatesToGroupDialog( templateMapper, templateGroup, lang, user, imcref );
        return htmlStr;
    }

    private String renameTemplate( HttpServletRequest req, TemplateMapper templateMapper, String lang,
                                   ImcmsServices imcref, UserDomainObject user ) {
        String htmlStr;
        int template_id = Integer.parseInt( req.getParameter( "template" ) );
        TemplateDomainObject template = templateMapper.getTemplateById( template_id );
        String newNameForTemplate = req.getParameter( "name" );
        if ( newNameForTemplate == null || "".equals( newNameForTemplate ) ) {
            htmlStr = createRenameNameEmptyErrorDialog( lang, imcref, user );
        } else {
            templateMapper.renameTemplate( template, newNameForTemplate );
            htmlStr = createRenameTemplateDialog( lang, templateMapper, imcref, user );
        }
        return htmlStr;
    }

    private String renameTemplateGroup( HttpServletRequest req, ImcmsServices imcref, UserDomainObject user,
                                        String lang ) {
        String htmlStr;
        int grp_id = Integer.parseInt( req.getParameter( "templategroup" ) );
        String name = req.getParameter( "name" );
        if ( name == null || name.equals( "" ) ) {
            htmlStr = createRenameNameEmptyErrorDialog( lang, imcref, user );
        } else {
            TemplateMapper templateMapper = imcref.getTemplateMapper();
            TemplateGroupDomainObject templateGroup = templateMapper.getTemplateGroupById( grp_id );
            templateMapper.renameTemplateGroup( templateGroup, name );
            htmlStr = TemplateAdmin.createRenameTemplateGroupDialog( templateMapper, imcref, user );
        }
        return htmlStr;
    }

    private String showDocument( HttpServletRequest req, HttpServletResponse res, ImcmsServices imcref,
                                 String lang, String htmlStr, UserDomainObject user ) throws IOException {
        String meta_id = req.getParameter( "templates_doc" );
        if ( meta_id != null ) {
            res.sendRedirect( "AdminDoc?meta_id=" + meta_id );
        } else {
            htmlStr = createDocumentsUsingTemplateDialog( imcref, user, null, lang ) ;
        }
        return htmlStr;
    }

}
