package imcode.server.document;

import java.io.Serializable;

public class TextDocumentPermissionSetDomainObject extends DocumentPermissionSetDomainObject implements Serializable {

    private TemplateGroupDomainObject[] allowedTemplateGroups = new TemplateGroupDomainObject[0];
    private int[] allowedDocumentTypeIds = new int[0];
    private TemplateDomainObject defaultTemplate;

    public TextDocumentPermissionSetDomainObject( int typeId ) {
        super( typeId );
    }

    public boolean getEditTexts() {
        return hasPermission( TextDocumentPermission.EDIT_TEXTS ) ;
    }

    public void setEditTexts( boolean editTexts ) {
        setPermission( TextDocumentPermission.EDIT_TEXTS, editTexts );
    }

    public boolean getEditMenus() {
        return hasPermission( TextDocumentPermission.EDIT_MENUS );
    }

    public void setEditMenus( boolean editMenus ) {
        setPermission( TextDocumentPermission.EDIT_MENUS, editMenus);
    }

    public boolean getEditTemplates() {
        return hasPermission( TextDocumentPermission.EDIT_TEMPLATE );
    }

    public void setEditTemplates( boolean editTemplates ) {
        setPermission( TextDocumentPermission.EDIT_TEMPLATE, editTemplates );
    }

    public boolean getEditIncludes() {
        return hasPermission( TextDocumentPermission.EDIT_INCLUDES );
    }

    public void setEditIncludes( boolean editIncludes ) {
        setPermission( TextDocumentPermission.EDIT_INCLUDES, editIncludes );
    }

    public boolean getEditImages() {
        return hasPermission( TextDocumentPermission.EDIT_IMAGES );
    }

    public void setEditImages( boolean editImages ) {
        setPermission( TextDocumentPermission.EDIT_IMAGES, editImages );
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
