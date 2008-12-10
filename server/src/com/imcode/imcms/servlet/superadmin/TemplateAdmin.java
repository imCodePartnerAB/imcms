package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.user.UserDomainObject;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.util.Parser;
import imcode.util.Utility;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TemplateAdmin extends HttpServlet {
    private static final String TEMPLATE_ADMIN = "template_admin.html";
    private static final String ADMIN_TEMPLATE_DELETE = "templategroup_delete.html";
    private static final String ADMIN_TEMPLATE_ADD = "templategroup_add.html";
    private static final String ADMIN_TEMPLATE_EDIT = "template_edit.html";
    private static final String ADMIN_TEMPLATE_RENAME = "template_rename.html";
    private static final String TEMPLATE_DELETE = "template_delete.html";
    private static final String TEMPLATE_DEMO_UPLOAD = "templatedemo_upload.html";
    private static final String TEMPLATE_UPLOAD = "template_upload.html";
    private static final String TEMPLATE_GROUP_RENAME = "templategroup_rename.html";
    private static final String TEMPLATE_ASSIGN = "template_assign.html";
    private static final String TEMPLATE_DOCS_ROW = "templates_docs_row.html";
    private static final String TEMPLATE_DELETE_WARNING = "template_delete_warning.html";
    private static final String TEMPLATE_GROUP_DELETE_WARNING = "templategroup_delete_warning.html";
    private static final String TEMPLATE_GROUP_DELETE_DOCUMENTS_ASSIGNED_WARNING = "templategroup_delete_documents_assigned_warning.html";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        Utility.setDefaultHtmlContentType( res );

        PrintWriter out = res.getWriter();

        String htmlStr = imcref.getAdminTemplate( TEMPLATE_ADMIN, user, null );
        out.println( htmlStr );

    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        Utility.setDefaultHtmlContentType( res );
        PrintWriter out = res.getWriter();

        String lang = req.getParameter( "language" );
        String htmlStr = null;
        TemplateMapper templateMapper = imcref.getTemplateMapper();
        if ( req.getParameter( "cancel" ) != null ) {
            res.sendRedirect( "AdminManager" );
            return;
        } else if ( req.getParameter( "add_template" ) != null ) {
            htmlStr = createUploadTemplateDialog( templateMapper, lang, imcref, user );
        } else if ( req.getParameter( "add_demotemplate" ) != null ) {
            htmlStr = createUploadDemoTemplateDialog( lang, templateMapper, imcref, user );
        } else if ( req.getParameter( "delete_template" ) != null ) {
            htmlStr = createDeleteTemplateDialog( templateMapper, user, lang, imcref );
        } else if ( req.getParameter( "rename_template" ) != null ) {
            htmlStr = createRenameTemplateDialog( lang, imcref, templateMapper, user, null);
        } else if ( req.getParameter( "get_template" ) != null ) {
            htmlStr = createDownloadTemplateDialog( lang, imcref, templateMapper, user );
        } else if ( req.getParameter( "edit_template" ) != null ) {
            htmlStr = createEditTemplateDialog( lang, imcref, templateMapper, user );
        } else if ( req.getParameter( "add_group" ) != null ) {
            htmlStr = createAddGroupDialog( imcref, user );
        } else if ( req.getParameter( "delete_group" ) != null ) {
            htmlStr = createDeleteTemplateGroupDialog( templateMapper, imcref, user );
        } else if ( req.getParameter( "rename_group" ) != null ) {
            htmlStr = createRenameTemplateGroupDialog( templateMapper, imcref, user );
        } else if ( req.getParameter( "assign_group" ) != null ) {
            htmlStr = createAssignTemplateGroupDialog( lang, templateMapper, user, imcref );
        } else if ( req.getParameter( "show_templates" ) != null ) {
            htmlStr = createListTemplatesDialog( templateMapper, user, lang, imcref );
        }
        out.print( htmlStr );
    }

    private String createListTemplatesDialog( TemplateMapper templateMapper, UserDomainObject user, String lang,
                                              ImcmsServices imcref ) {
        String templateList = templateMapper.createHtmlOptionListOfTemplatesWithDocumentCount(user);
        List vec = new ArrayList();
        vec.add( "#template_list#" );
        vec.add( templateList );
        vec.add( "#language#" );
        vec.add( lang );
        return imcref.getAdminTemplate( "template_list.html", user, vec );
    }

    private String createAssignTemplateGroupDialog( String lang, TemplateMapper templateMapper,
                                                    UserDomainObject user, ImcmsServices imcref ) throws IOException {
        return createAssignTemplatesToGroupDialog( templateMapper, null, lang, user, imcref );
    }

    static String createDeleteTemplateGroupDialog( TemplateMapper templateMapper, ImcmsServices imcref,
                                                   UserDomainObject user ) {
        String temps = templateMapper.createHtmlOptionListOfTemplateGroups( null );
        List vec = new ArrayList();
        vec.add( "#templategroups#" );
        vec.add( temps );
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_DELETE, user, vec);
    }

    static String createAddGroupDialog( ImcmsServices imcref, UserDomainObject user ) {
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_ADD, user, null);
    }

    private String createEditTemplateDialog( String lang, ImcmsServices imcref, TemplateMapper templateMapper,
                                             UserDomainObject user ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#language#" );
        vec.add( lang );
        List<TemplateDomainObject> templates = imcref.getTemplateMapper().getAllTemplates();
        String temps = templateMapper.createHtmlOptionListOfTemplates( templates, null );
        vec.add( "#templates#" );
        vec.add( temps );
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_EDIT, user, vec);
    }

    private String createDownloadTemplateDialog( String lang, ImcmsServices imcref,
                                                 TemplateMapper templateMapper, UserDomainObject user ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#language#" );
        vec.add( lang );
        List<TemplateDomainObject> templates = imcref.getTemplateMapper().getAllTemplates();
        String temps = templateMapper.createHtmlOptionListOfTemplates( templates, null );
        vec.add( "#templates#" );
        vec.add( temps );
        return imcref.getAdminTemplate("template_get.html", user, vec);
    }

    static String createRenameTemplateDialog(String lang, ImcmsServices imcref, TemplateMapper templateMapper,
                                             UserDomainObject user, LocalizedMessage error) throws IOException {
        List vec = new ArrayList();
        vec.add( "#language#" );
        vec.add( lang );
        vec.add( "#templates#" );
        vec.add( templateMapper.createHtmlOptionListOfTemplates( imcref.getTemplateMapper().getAllTemplates(), null ) );
        vec.add( "#error#" );
        vec.add( null == error ? "" : error.toLocalizedString(user)) ;
        return imcref.getAdminTemplate(ADMIN_TEMPLATE_RENAME, user, vec);
    }

    static String createDeleteTemplateDialog( TemplateMapper templateMapper, UserDomainObject user, String lang,
                                              ImcmsServices imcref ) {
        List vec = new ArrayList();
        String templatesList = templateMapper.createHtmlOptionListOfTemplatesWithDocumentCount(user);
        vec.add( "#templates#" );
        vec.add( templatesList );
        vec.add( "#language#" );
        vec.add( lang );
        return imcref.getAdminTemplate( TEMPLATE_DELETE, user, vec );
    }

    private String createUploadDemoTemplateDialog( String lang, TemplateMapper templateMapper,
                                                   ImcmsServices imcref, UserDomainObject user ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#language#" );
        vec.add( lang );
        List<TemplateDomainObject> templates = templateMapper.getAllTemplates();
        String temps = templateMapper.createHtmlOptionListOfTemplates( templates, null );
        vec.add( "#templates#" );
        vec.add( temps );
        return imcref.getAdminTemplate(TEMPLATE_DEMO_UPLOAD, user, vec);
    }

    private String createUploadTemplateDialog( TemplateMapper templateMapper, String lang, ImcmsServices imcref,
                                               UserDomainObject user ) {
        List vec = new ArrayList();
        String temps = templateMapper.createHtmlOptionListOfTemplateGroups( null );
        vec.add( "#templategroups#" );
        vec.add( temps );
        vec.add( "#language#" );
        vec.add( lang );
        return imcref.getAdminTemplate(TEMPLATE_UPLOAD, user, vec);
    }

    static String createRenameTemplateGroupDialog( TemplateMapper templateMapper, ImcmsServices imcref,
                                                   UserDomainObject user ) {
        String htmlOptionListOfTemplateGroups = templateMapper.createHtmlOptionListOfTemplateGroups( null );
        List vec = new ArrayList();
        vec.add( "#templategroups#" );
        vec.add( htmlOptionListOfTemplateGroups );
        return imcref.getAdminTemplate(TEMPLATE_GROUP_RENAME, user, vec);
    }

    static String createAssignTemplatesToGroupDialog( TemplateMapper templateMapper, TemplateGroupDomainObject currentTemplateGroup, String language,
                                                      UserDomainObject user, ImcmsServices imcref ) throws IOException {
        List vec = new ArrayList();

        String htmlOptionListOfTemplateGroups = templateMapper.createHtmlOptionListOfTemplateGroups(
                currentTemplateGroup );

        vec.add( "#templategroups#" );
        vec.add( htmlOptionListOfTemplateGroups );
        if ( currentTemplateGroup == null ) {
            vec.add( "#assigned#" );
            vec.add( "" );
            vec.add( "#unassigned#" );
            vec.add( "" );
            vec.add( "#group#" );
            vec.add( "" );
            vec.add( "#group_id#" );
            vec.add( "" );
        } else {
            List<TemplateDomainObject> templatesInGroup = templateMapper.getTemplatesInGroup( currentTemplateGroup );
            List<TemplateDomainObject> templatesNotInGroup = templateMapper.getTemplatesNotInGroup( currentTemplateGroup );
            String htmlOptionListOfTemplatesInSelectedGroup = templateMapper.createHtmlOptionListOfTemplates(
                    templatesInGroup, null );
            String htmlOptionListOfTemplatesNotInSelectedGroup = templateMapper.createHtmlOptionListOfTemplates(
                    templatesNotInGroup, null );
            vec.add( "#assigned#" );
            vec.add( htmlOptionListOfTemplatesInSelectedGroup );
            vec.add( "#unassigned#" );
            vec.add( htmlOptionListOfTemplatesNotInSelectedGroup );
            vec.add( "#group#" );
            vec.add( currentTemplateGroup.getName() );
            vec.add( "#group_id#" );
            vec.add( "" + currentTemplateGroup.getId() );
        }
        vec.add( "#language#" );
        vec.add( language );
        return imcref.getAdminTemplate( TEMPLATE_ASSIGN, user, vec );
    }

    static String createHtmlOptionListOfDocumentsUsingTemplate( ImcmsServices imcref, TemplateDomainObject template,
                                                                 UserDomainObject user ) {
        DocumentDomainObject[] documents = imcref.getTemplateMapper().getDocumentsUsingTemplate( template );
        StringBuffer htmlOptionList = new StringBuffer();
        for ( DocumentDomainObject document : documents ) {
            List vec = new ArrayList();
            vec.add("#meta_id#");
            vec.add("" + document.getId());
            vec.add("#meta_headline#");

            String[] pd = { "&", "&amp;", "<", "&lt;", ">", "&gt;", "\"", "&quot;" };
            String headline = document.getHeadline();
            headline = StringUtils.abbreviate(headline, 60) ;
            headline = Parser.parseDoc(headline, pd);
            vec.add(headline);
            htmlOptionList.append(imcref.getAdminTemplate(TEMPLATE_DOCS_ROW, user, vec));
        }
        return htmlOptionList.toString();
    }

    static String createDeleteTemplateInUseWarningDialog( String lang, ImcmsServices imcref,
                                                      TemplateDomainObject template, UserDomainObject user,
                                                      TemplateMapper templateMapper ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#language#" );
        vec.add( lang );
        vec.add( "#template#" );
        vec.add( template.getName() );
        vec.add( "#docs#" );
        vec.add( createHtmlOptionListOfDocumentsUsingTemplate( imcref, template, user ) );
        vec.add( "#templates#" );
        vec.add( templateMapper.createHtmlOptionListOfTemplates( templateMapper.getAllTemplatesExceptOne( template ), null ) );

        return imcref.getAdminTemplate(TEMPLATE_DELETE_WARNING, user, vec);
    }

    static String createDeleteNonEmptyTemplateGroupWarningDialog( Iterable<TemplateDomainObject> templatesInGroup,
                                                                   int templateGroupId, ImcmsServices imcref,
                                                                   UserDomainObject user ) {
        return createTemplateGroupWarningDialog(templatesInGroup, templateGroupId, imcref, TEMPLATE_GROUP_DELETE_WARNING, user);
    }

    static String createDocumentsAssignedToTemplateInTemplateGroupWarningDialog( Iterable<TemplateDomainObject> templatesInGroup,
                                                                   int templateGroupId, ImcmsServices imcref,
                                                                   UserDomainObject user) {

        return createTemplateGroupWarningDialog(templatesInGroup, templateGroupId, imcref, TEMPLATE_GROUP_DELETE_DOCUMENTS_ASSIGNED_WARNING, user);
    }

    private static String createTemplateGroupWarningDialog(Iterable<TemplateDomainObject> templatesInGroup, int templateGroupId,
                                                           ImcmsServices imcref, String template,
                                                           UserDomainObject user) {
        String commaSeparatedTemplateNames = StringUtils.join( new ArrayIterator( templatesInGroup ) {
            public Object next() {
                TemplateDomainObject template = (TemplateDomainObject) super.next();
                return template.getName() ;
            }
        }, ", " );
        List vec = new ArrayList();
        vec.add( "#templates#" );
        vec.add( commaSeparatedTemplateNames );
        vec.add( "#templategroup#" );
        vec.add( String.valueOf( templateGroupId ) );
        return imcref.getAdminTemplate(template, user, vec);
    }
}
