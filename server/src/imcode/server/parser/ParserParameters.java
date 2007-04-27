package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.ImcmsConstants;
import imcode.server.document.*;

import javax.servlet.http.HttpServletRequest;

public class ParserParameters implements Cloneable {

    private String template;	//used to store the template if not default is wanted
    private String param;		//used to store the parameter param
    private Integer editingMenuIndex;
    private DocumentRequest documentRequest;
    private int flags;
    private boolean adminButtonsVisible = true ;
    private final static String ATTRIBUTE_NAME = ParserParameters.class.getName();
    private int includeLevel = 5;

    public ParserParameters( DocumentRequest documentRequest ) {
        this.documentRequest = documentRequest;
    }

    public void setTemplate( String template ) {
        this.template = template;
    }

    public void setParameter( String param ) {
        this.param = param;
    }

    public String getTemplateName() {
        return template;
    }

    public String getParameter() {
        return param == null ? "" : param;
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
        return isMode( ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEXTS, TextDocumentPermissionSetDomainObject.EDIT_TEXTS );
    }

    public boolean isMenuMode() {
        return isMode( ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_MENUS, TextDocumentPermissionSetDomainObject.EDIT_MENUS );
    }

    public boolean isImageMode() {
        return isMode( ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_IMAGES, TextDocumentPermissionSetDomainObject.EDIT_IMAGES );
    }

    public boolean isIncludesMode() {
        return isMode( ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_INCLUDES, TextDocumentPermissionSetDomainObject.EDIT_INCLUDES );
    }

    public boolean isTemplateMode() {
        return isMode( ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE, TextDocumentPermissionSetDomainObject.EDIT_TEMPLATE );
    }

    private boolean isMode( int flag,
                            DocumentPermission permission ) {
        return ( flags & flag ) != 0 && getPermissionSet().hasPermission(permission);
    }

    public boolean isAnyMode() {
        return isTextMode() || isImageMode() || isMenuMode() || isIncludesMode() || isTemplateMode() ;
    }

    public static ParserParameters putInRequest(ParserParameters parserParameters) {
        HttpServletRequest request = parserParameters.getDocumentRequest().getHttpServletRequest();
        Object attribute = request.getAttribute(ATTRIBUTE_NAME);
        request.setAttribute(ATTRIBUTE_NAME, parserParameters);
        return (ParserParameters) attribute ;
    }
    
    public static ParserParameters fromRequest(HttpServletRequest request) {
        return (ParserParameters) request.getAttribute(ATTRIBUTE_NAME) ;
    }

    public int getIncludeLevel() {
        return includeLevel;
    }

    public void setIncludeLevel(int includeLevel) {
        this.includeLevel = includeLevel;
    }
}
