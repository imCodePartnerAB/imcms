package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.ImcmsConstants;
import imcode.server.document.*;

public class ParserParameters implements Cloneable {

    private String template;	//used to store the template if not default is wanted
    private String param;		//used to store the parameter param
    private String externalParam; //used to store the param prodused from external class.
    private Integer editingMenuIndex;
    private DocumentRequest documentRequest;
    private int flags;
    private boolean adminButtonsVisible = true ;

    public ParserParameters( DocumentRequest documentRequest ) {
        this.documentRequest = documentRequest;
    }

    public void setTemplate( String template ) {
        this.template = template;
    }

    public void setParameter( String param ) {
        this.param = param;
    }

    public void setExternalParameter( String externalparam ) {
        this.externalParam = externalparam;
    }

    public String getTemplate() {
        return this.template;
    }

    public String getParameter() {
        return this.param == null ? "" : this.param;
    }

    public String getExternalParameter() {
        return this.externalParam == null ? "" : this.externalParam;
    }

    public Integer getEditingMenuIndex() {
        return editingMenuIndex ;
    }

    public void setEditingMenuIndex( Integer editingMenuIndex ) {
        this.editingMenuIndex = editingMenuIndex;
    }

    public DocumentRequest getDocumentRequest() {
        return documentRequest;
    }

    public void setDocumentRequest( DocumentRequest documentRequest ) {
        this.documentRequest = documentRequest;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags( int flags ) {
        this.flags = flags;
    }

    public Object clone() throws CloneNotSupportedException {
        ParserParameters clone = (ParserParameters)super.clone();
        clone.documentRequest = (DocumentRequest)documentRequest.clone() ;
        return clone ;
    }

    public void setAdminButtonsVisible( boolean adminButtonsVisible ) {
        this.adminButtonsVisible = adminButtonsVisible;
    }

    public boolean isAdminButtonsVisible() {
        return adminButtonsVisible;
    }

    private TextDocumentPermissionSetDomainObject getPermissionSet() {
        return (TextDocumentPermissionSetDomainObject)documentRequest.getUser().getPermissionSetFor( documentRequest.getDocument() );
    }

    public boolean isTextMode() {
        TextDocumentPermissionSetDomainObject permissionSet = getPermissionSet();
        return ( flags & ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS ) != 0 && permissionSet.getEditTexts();
    }

    public boolean isMenuMode() {
        TextDocumentPermissionSetDomainObject permissionSet = getPermissionSet();
        return ( flags & ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_MENUS ) != 0 && permissionSet.getEditMenus();
    }

    public boolean isImageMode() {
        TextDocumentPermissionSetDomainObject permissionSet = getPermissionSet();
        return ( flags & ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_IMAGES ) != 0 && permissionSet.getEditImages();
    }

    public boolean isIncludesMode() {
        TextDocumentPermissionSetDomainObject permissionSet = getPermissionSet();
        return ( flags & ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_INCLUDES ) != 0 && permissionSet.getEditIncludes();
    }

    public boolean isTemplateMode() {
        TextDocumentPermissionSetDomainObject permissionSet = getPermissionSet();
        return ( flags & ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE ) != 0 && permissionSet.getEditTemplates();
    }
}
