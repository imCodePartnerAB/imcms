package imcode.server.document;

import java.io.Serializable;

public class TextDocumentPermissionSetDomainObject extends DocumentPermissionSetDomainObject implements Serializable {

    private TemplateGroupDomainObject[] allowedTemplateGroups = new TemplateGroupDomainObject[0];
    private int[] allowedDocumentTypeIds = new int[0];
    private TemplateDomainObject defaultTemplate;

    private static final String PERMISSION_NAME__EDIT_MENUS = "editMenus";
    private static final String PERMISSION_NAME__EDIT_TEMPLATES = "editTemplates";
    private static final String PERMISSION_NAME__EDIT_INCLUDES = "editIncludes";
    private static final String PERMISSION_NAME__EDIT_IMAGES = "editImages";

    public TextDocumentPermissionSetDomainObject( int typeId ) {
        super( typeId );
    }

    public boolean getEditTexts() {
        return getEdit() ;
    }

    public void setEditTexts( boolean editTexts ) {
        setEdit(editTexts) ;
    }

    public boolean getEditMenus() {
        return hasPermission( PERMISSION_NAME__EDIT_MENUS );
    }

    public void setEditMenus( boolean editMenus ) {
        setPermission( PERMISSION_NAME__EDIT_MENUS, editMenus);
    }

    public boolean getEditTemplates() {
        return hasPermission( PERMISSION_NAME__EDIT_TEMPLATES );
    }

    public void setEditTemplates( boolean editTemplates ) {
        setPermission( PERMISSION_NAME__EDIT_TEMPLATES, editTemplates );
    }

    public boolean getEditIncludes() {
        return hasPermission( PERMISSION_NAME__EDIT_INCLUDES );
    }

    public void setEditIncludes( boolean editIncludes ) {
        setPermission( PERMISSION_NAME__EDIT_INCLUDES, editIncludes );
    }

    public boolean getEditImages() {
        return hasPermission( PERMISSION_NAME__EDIT_IMAGES );
    }

    public void setEditImages( boolean editImages ) {
        setPermission( PERMISSION_NAME__EDIT_IMAGES, editImages );
    }

    public void setFromBits ( DocumentDomainObject document, DocumentPermissionSetMapper documentPermissionSetMapper,
                              int permissionBits, boolean forNewDocuments ) {
        documentPermissionSetMapper.setTextDocumentPermissionSetFromBits( document, this, permissionBits, forNewDocuments );
    }

    public void setAllowedTemplateGroups( TemplateGroupDomainObject[] allowedTemplateGroupNames ) {
        this.allowedTemplateGroups = allowedTemplateGroupNames;
    }

    public TemplateGroupDomainObject[] getAllowedTemplateGroups() {
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
