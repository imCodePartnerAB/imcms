package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.ImcmsConstants;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TextDocumentPermissionSetDomainObject;

public class ParserParameters implements Cloneable {

    private String template;	//used to store the template if not default is wanted
    private String param;		//used to store the parameter param
    private String externalParam; //used to store the param prodused from external class.
    private Integer editingMenuIndex;
    private DocumentRequest documentRequest;
    private int flags;
    private DocumentMapper documentMapper;

    public ParserParameters( DocumentRequest documentRequest, DocumentMapper documentMapper ) {
        this.documentMapper = documentMapper;
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
        return super.clone() ;
    }

    public boolean isMenuMode() {
        TextDocumentPermissionSetDomainObject permissionSet = (TextDocumentPermissionSetDomainObject)documentMapper.getDocumentPermissionSetForUser( documentRequest.getDocument(), documentRequest.getUser() );
        return ( flags & ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_MENUS ) != 0 && permissionSet.getEditMenus();
    }
}
