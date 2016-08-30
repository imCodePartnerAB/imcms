package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermission;
import imcode.server.document.TextDocumentPermissionSetDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ParserParameters implements Cloneable {

    private final static String ATTRIBUTE_NAME = ParserParameters.class.getName();
    private String template;    //used to store the template if not default is wanted
    private String param;        //used to store the parameter param
    private Integer editingMenuIndex;
    private DocumentRequest documentRequest;
    private int flags;
    private boolean adminButtonsVisible = true;
    private int includeLevel = 5;

    /**
     * Constructor that encapsulates DocumentRequest instance
     *
     * @param document desired document
     * @param request request
     * @param response response
     */
    public ParserParameters(DocumentDomainObject document,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        this.documentRequest = new DocumentRequest(document, request, response);
    }

    public ParserParameters(DocumentRequest documentRequest) {
        this.documentRequest = documentRequest;
    }

    public static ParserParameters putInRequest(ParserParameters parserParameters) {
        HttpServletRequest request = parserParameters.getDocumentRequest().getHttpServletRequest();
        Object attribute = request.getAttribute(ATTRIBUTE_NAME);
        request.setAttribute(ATTRIBUTE_NAME, parserParameters);
        return (ParserParameters) attribute;
    }

    public static ParserParameters fromRequest(HttpServletRequest request) {
        return (ParserParameters) request.getAttribute(ATTRIBUTE_NAME);
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplateName() {
        return template;
    }

    public String getParameter() {
        return param == null ? "" : param;
    }

    public void setParameter(String param) {
        this.param = param;
    }

    public Integer getEditingMenuIndex() {
        return editingMenuIndex;
    }

    public void setEditingMenuIndex(Integer editingMenuIndex) {
        this.editingMenuIndex = editingMenuIndex;
    }

    public DocumentRequest getDocumentRequest() {
        return documentRequest;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Object clone() throws CloneNotSupportedException {
        ParserParameters clone = (ParserParameters) super.clone();
        clone.documentRequest = (DocumentRequest) documentRequest.clone();
        return clone;
    }

    public boolean isAdminButtonsVisible() {
        return adminButtonsVisible;
    }

    public void setAdminButtonsVisible(boolean adminButtonsVisible) {
        this.adminButtonsVisible = adminButtonsVisible;
    }

    private TextDocumentPermissionSetDomainObject getPermissionSet() {
        return (TextDocumentPermissionSetDomainObject) documentRequest.getUser().getPermissionSetFor(documentRequest.getDocument());
    }

    public boolean isTextMode() {
        return isMode(TextDocumentPermissionSetDomainObject.EDIT_TEXTS);
    }

    public boolean isMenuMode() {
        return isMode(TextDocumentPermissionSetDomainObject.EDIT_MENUS);
    }

    /**
     * There is no separate permissions for content loop editing.
     */
    public boolean isContentLoopMode() {
        return isMode(TextDocumentPermissionSetDomainObject.EDIT_LOOPS);
    }

    public boolean isImageMode() {
        return isMode(TextDocumentPermissionSetDomainObject.EDIT_IMAGES);
    }

    public boolean isIncludesMode() {
        return isMode(TextDocumentPermissionSetDomainObject.EDIT_INCLUDES);
    }

    public boolean isTemplateMode() {
        return isMode(TextDocumentPermissionSetDomainObject.EDIT_TEMPLATE);
    }

    public boolean isMode(DocumentPermission permission) {
        return (flags) != 0 && getPermissionSet().hasPermission(permission);
    }

    public boolean isAnyMode() {
        return isTextMode() || isImageMode() || isMenuMode() || isIncludesMode() || isTemplateMode() || isContentLoopMode();
    }

    public int getIncludeLevel() {
        return includeLevel;
    }

    public void setIncludeLevel(int includeLevel) {
        this.includeLevel = includeLevel;
    }
}
