package com.imcode.imcms.api;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.parser.ParserParameters;

import javax.servlet.http.HttpServletRequest;

/**
 * Represents currently viewed TextDocument. Provides information if the current text document is in text, image, menu etc
 * edit mode.
 * An instance of this class is fetchable in JSPs included in text documents (with &lt;?imcms:include path="..."?&gt; or
 * jsp:include) via {@link #fromRequest(javax.servlet.http.HttpServletRequest)},
 * and in velocity code in text templates as <code>$viewing</code>.
 *
 * @since 2.0
 */
public class TextDocumentViewing {

    private TextDocument textDocument;
    private ParserParameters parserParameters;

    private static final String REQUEST_ATTRIBUTE__VIEWING = TextDocumentViewing.class.getName();

    public TextDocumentViewing( ParserParameters parserParameters ) {
        this.parserParameters = parserParameters;
        textDocument = new TextDocument( (TextDocumentDomainObject)parserParameters.getDocumentRequest().getDocument(), ContentManagementSystem.fromRequest( parserParameters.getDocumentRequest().getHttpServletRequest() ) );
    }

    /**
     * Returns TextDocumentViewing from the given request
     * @param request HttpServletRequest to get TextDocumentViewing from
     * @return TextDocumentViewing or null, if the request doesn't have any set
     */
    public static TextDocumentViewing fromRequest( HttpServletRequest request ) {
        return (TextDocumentViewing)request.getAttribute( REQUEST_ATTRIBUTE__VIEWING );
    }

    /**
     * Returns currently viewed TextDocument
     * @return currently viewed TextDocument
     */
    public TextDocument getTextDocument() {
        return textDocument;
    }

    /**
     * Tests if current TextDocument is in edit mode(be it text, images, menus etc)
     * @return true if current TextDocument is being edited, false otherwise
     */
    public boolean isEditing() {
        return parserParameters.isAnyMode() ;
    }

    /**
     * Tests if current TextDocument is in text edit mode
     * @return true if current TextDocument is in text edit mode, false otherwise
     */
    public boolean isEditingTexts() {
        return parserParameters.isTextMode();
    }

    /**
     * Tests if current TextDocument is in image edit mode
     * @return true if current TextDocument is in image edit mode, false otherwise
     */
    public boolean isEditingImages() {
        return parserParameters.isImageMode();
    }

    /**
     * Tests if current TextDocument is in menu edit mode
     * @return true if current TextDocument is in menu edit mode, false otherwise
     */
    public boolean isEditingMenus() {
        return parserParameters.isMenuMode();
    }

    /**
     * Tests if current TextDocument is in include edit mode
     * @return true if current TextDocument is in include edit mode, false otherwise
     */
    public boolean isEditingIncludes() {
        return parserParameters.isIncludesMode();
    }

    /**
     * Tests if current TextDocument is in template edit mode
     * @return true if current TextDocument is in template edit mode, false otherwise
     */
    public boolean isEditingTemplate() {
        return parserParameters.isTemplateMode();
    }

    /**
     * @return The index of the menu currently being edited, or null if none.
     */
    public Integer getEditedMenuIndex() {
        return parserParameters.getEditingMenuIndex();
    }

    /**
     * Puts the given TextDocumentViewing object into request attribute and requests the one previously set.
     * @param viewing TextDocumentViewing to put into request attribute.
     * @return TextDocumentViewing that was previously in request attribute or null if none was before.
     */
    public static TextDocumentViewing putInRequest( TextDocumentViewing viewing ) {
        HttpServletRequest httpServletRequest = viewing.parserParameters.getDocumentRequest().getHttpServletRequest();
        TextDocumentViewing previousViewing = fromRequest( httpServletRequest );
        httpServletRequest.setAttribute( REQUEST_ATTRIBUTE__VIEWING, viewing );
        return previousViewing ;
    }

}
