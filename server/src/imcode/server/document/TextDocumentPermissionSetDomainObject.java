package imcode.server.document;

public class TextDocumentPermissionSetDomainObject extends DocumentPermissionSetDomainObject {

    private boolean editMenus;
    private boolean editTemplates;
    private boolean editIncludes;
    private boolean editImages;
    private TemplateGroupDomainObject[] allowedTemplateGroups = new TemplateGroupDomainObject[0];
    private int[] allowedDocumentTypeIds = new int[0];

    public TextDocumentPermissionSetDomainObject( int permissionType ) {
        super( permissionType );
    }

    public boolean getEditTexts() {
        return getEdit() ;
    }

    public void setEditTexts( boolean editTexts ) {
        setEdit(editTexts) ;
    }

    public boolean getEditMenus() {
        return editMenus;
    }

    public void setEditMenus( boolean editMenus ) {
        this.editMenus = editMenus;
    }

    public boolean getEditTemplates() {
        return editTemplates;
    }

    public void setEditTemplates( boolean editTemplates ) {
        this.editTemplates = editTemplates;
    }

    public boolean getEditIncludes() {
        return editIncludes;
    }

    public void setEditIncludes( boolean editIncludes ) {
        this.editIncludes = editIncludes;
    }

    public boolean getEditImages() {
        return editImages;
    }

    public void setEditImages( boolean editImages ) {
        this.editImages = editImages;
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

}
