package imcode.server.document;

import com.imcode.imcms.mapping.DocumentPermissionSetMapper;
import imcode.server.ImcmsServices;

import java.io.Serializable;

public class TextDocumentPermissionSetDomainObject extends DocumentPermissionSetDomainObject implements Serializable {

    private TemplateGroupDomainObject[] allowedTemplateGroups = new TemplateGroupDomainObject[0];
    private int[] allowedDocumentTypeIds = new int[0];
    private TemplateDomainObject defaultTemplate;
    public static final DocumentPermission EDIT_TEXTS = new DocumentPermission( "editTexts" );
    public static final DocumentPermission EDIT_MENUS = new DocumentPermission( "editMenus" );
    public static final DocumentPermission EDIT_TEMPLATE = new DocumentPermission( "editTemplates" );
    public static final DocumentPermission EDIT_INCLUDES = new DocumentPermission( "editIncludes" ) ;
    public static final DocumentPermission EDIT_IMAGES = new DocumentPermission( "editImages" );

    public TextDocumentPermissionSetDomainObject( DocumentPermissionSetTypeDomainObject typeId ) {
        super( typeId );
    }

    public boolean getEditTexts() {
        return hasPermission( EDIT_TEXTS ) ;
    }

    public void setEditTexts( boolean editTexts ) {
        setPermission( EDIT_TEXTS, editTexts );
    }

    public boolean getEditMenus() {
        return hasPermission( EDIT_MENUS );
    }

    public void setEditMenus( boolean editMenus ) {
        setPermission( EDIT_MENUS, editMenus);
    }

    public boolean getEditTemplates() {
        return hasPermission( EDIT_TEMPLATE );
    }

    public void setEditTemplates( boolean editTemplates ) {
        setPermission( EDIT_TEMPLATE, editTemplates );
    }

    public boolean getEditIncludes() {
        return hasPermission( EDIT_INCLUDES );
    }

    public void setEditIncludes( boolean editIncludes ) {
        setPermission( EDIT_INCLUDES, editIncludes );
    }

    public boolean getEditImages() {
        return hasPermission( EDIT_IMAGES );
    }

    public void setEditImages( boolean editImages ) {
        setPermission( EDIT_IMAGES, editImages );
    }

    public void setFromBits ( DocumentDomainObject document, DocumentPermissionSetMapper documentPermissionSetMapper,
                              int permissionBits, boolean forNewDocuments ) {
        documentPermissionSetMapper.setTextDocumentPermissionSetFromBits( document, this, permissionBits, forNewDocuments );
    }

    public void setAllowedTemplateGroups( TemplateGroupDomainObject[] allowedTemplateGroupNames ) {
        this.allowedTemplateGroups = allowedTemplateGroupNames;
    }

    public TemplateGroupDomainObject[] getAllowedTemplateGroups( ImcmsServices services ) {
        return allowedTemplateGroups;
    }

    public void setAllowedDocumentTypeIds( int[] allowedDocumentTypeIds ) {
        this.allowedDocumentTypeIds = allowedDocumentTypeIds;
    }

    public int[] getAllowedDocumentTypeIds() {
        return allowedDocumentTypeIds;
    }

    public void setDefaultTemplate( TemplateDomainObject defaultTemplate ) {
        this.defaultTemplate = defaultTemplate;
    }

    public TemplateDomainObject getDefaultTemplate() {
        return defaultTemplate;
    }
}
