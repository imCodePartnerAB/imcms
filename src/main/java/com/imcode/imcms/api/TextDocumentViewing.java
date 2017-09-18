package com.imcode.imcms.api;

import imcode.server.parser.ParserParameters;

import javax.servlet.ServletRequest;

/**
 * An instance of this class is fetchable in JSPs included in text documents (with &lt;?imcms:include path="..."?&gt;)
 * via {@link #fromRequest(javax.servlet.ServletRequest)},
 * and in velocity code in text templates as <code>$viewing</code>.
 *
 * @since 2.0
 */
public class TextDocumentViewing {

    private static final String REQUEST_ATTRIBUTE__VIEWING = TextDocumentViewing.class.getName();
    private TextDocument textDocument;
    private ParserParameters parserParameters;

    public TextDocumentViewing(ParserParameters parserParameters) {
        this.parserParameters = parserParameters;
        textDocument = new TextDocument(parserParameters.getDocumentRequest().getDocument(), ContentManagementSystem.fromRequest(parserParameters.getDocumentRequest().getHttpServletRequest()));
    }

    public static TextDocumentViewing fromRequest(ServletRequest request) {
        return (TextDocumentViewing) request.getAttribute(REQUEST_ATTRIBUTE__VIEWING);
    }

    public static TextDocumentViewing putInRequest(TextDocumentViewing viewing) {
        ServletRequest servletRequest = viewing.parserParameters.getDocumentRequest().getHttpServletRequest();
        TextDocumentViewing previousViewing = fromRequest(servletRequest);
        servletRequest.setAttribute(REQUEST_ATTRIBUTE__VIEWING, viewing);
        return previousViewing;
    }

    public TextDocument getTextDocument() {
        return textDocument;
    }

    public boolean isEditing() {
        return parserParameters.isAnyMode();
    }

    public boolean isEditingTexts() {
        return parserParameters.isTextMode();
    }

    public boolean isEditingImages() {
        return parserParameters.isImageMode();
    }

    public boolean isEditingMenus() {
        return parserParameters.isMenuMode();
    }

    public boolean isEditingIncludes() {
        return parserParameters.isIncludesMode();
    }

    public boolean isEditingTemplate() {
        return parserParameters.isTemplateMode();
    }

    /**
     * @return The index of the menu currently being edited, or null if none.
     */
    public Integer getEditedMenuIndex() {
        return parserParameters.getEditingMenuIndex();
    }

}
