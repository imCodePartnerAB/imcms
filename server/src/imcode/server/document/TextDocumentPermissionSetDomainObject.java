package imcode.server.document;

import imcode.server.ImcmsConstants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TextDocumentPermissionSetDomainObject extends DocumentPermissionSetDomainObject {

    private HashSet allowedTemplateGroupIds = new HashSet();
    private HashSet allowedDocumentTypeIds = new HashSet();
    public static final DocumentPermission EDIT_TEXTS = new DocumentPermission( "editTexts" );
    public static final DocumentPermission EDIT_MENUS = new DocumentPermission( "editMenus" );
    public static final DocumentPermission EDIT_TEMPLATE = new DocumentPermission( "editTemplates" );
    public static final DocumentPermission EDIT_INCLUDES = new DocumentPermission( "editIncludes" ) ;
    public static final DocumentPermission EDIT_IMAGES = new DocumentPermission( "editImages" );
    public final static int EDIT_TEXT_DOCUMENT_TEXTS_PERMISSION_ID = EDIT_DOCUMENT_PERMISSION_ID;
    public final static int EDIT_TEXT_DOCUMENT_IMAGES_PERMISSION_ID = ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_IMAGES;
    public final static int EDIT_TEXT_DOCUMENT_MENUS_PERMISSION_ID = ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_MENUS;
    public final static int EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID = ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE;
    public final static int EDIT_TEXT_DOCUMENT_INCLUDES_PERMISSION_ID = ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_INCLUDES;

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

    public void setFromBits(int permissionBits) {
        setEditDocumentInformation(0 != ( permissionBits & EDIT_DOCINFO_PERMISSION_ID ));
        setEditPermissions(0 != ( permissionBits & EDIT_PERMISSIONS_PERMISSION_ID ));
        setEdit(0 != ( permissionBits & EDIT_DOCUMENT_PERMISSION_ID ));
        setEditTexts(0 != ( permissionBits & EDIT_TEXT_DOCUMENT_TEXTS_PERMISSION_ID ));
        setEditImages(0 != ( permissionBits & EDIT_TEXT_DOCUMENT_IMAGES_PERMISSION_ID ));
        setEditMenus(0 != ( permissionBits & EDIT_TEXT_DOCUMENT_MENUS_PERMISSION_ID ));
        setEditIncludes(0
                        != ( permissionBits & EDIT_TEXT_DOCUMENT_INCLUDES_PERMISSION_ID ));
        setEditTemplates(0
                         != ( permissionBits & EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID ));
    }

    public void setAllowedTemplateGroupIds( Set allowedTemplateGroupIds ) {
        this.allowedTemplateGroupIds = new HashSet(allowedTemplateGroupIds);
    }

    public Set getAllowedTemplateGroupIds() {
        return Collections.unmodifiableSet(allowedTemplateGroupIds);
    }

    /**
     * Sets the document type ids(file, text, url etc) allowed to be created based on the text document possessing this
     * permission set(parent document when creating new document).
     * @param allowedDocumentTypeIds allowed document type ids
     */
    public void setAllowedDocumentTypeIds( Set allowedDocumentTypeIds ) {
        this.allowedDocumentTypeIds = new HashSet(allowedDocumentTypeIds);
    }

    public Set getAllowedDocumentTypeIds() {
        return Collections.unmodifiableSet(allowedDocumentTypeIds);
    }

    public void addAllowedTemplateGroupId(int templateGroupId) {
        allowedTemplateGroupIds.add(new Integer(templateGroupId)) ;
    }

    public void addAllowedDocumentTypeId(int documentTypeId) {
        allowedDocumentTypeIds.add(new Integer(documentTypeId)) ;
    }

    protected Object clone() throws CloneNotSupportedException {
        TextDocumentPermissionSetDomainObject clone = (TextDocumentPermissionSetDomainObject) super.clone();
        clone.allowedDocumentTypeIds = (HashSet) allowedDocumentTypeIds.clone();
        clone.allowedTemplateGroupIds = (HashSet) allowedTemplateGroupIds.clone() ;
        return clone;
    }
}
