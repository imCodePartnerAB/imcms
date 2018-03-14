package com.imcode.imcms.api;

import javax.servlet.http.HttpServletRequest;

@Deprecated
public class TextDocumentViewing {

    private static final String REQUEST_ATTRIBUTE__VIEWING = TextDocumentViewing.class.getName();

    private TextDocument textDocument;

    @Deprecated
    public static TextDocumentViewing fromRequest(HttpServletRequest request) {
        return (TextDocumentViewing) request.getAttribute(REQUEST_ATTRIBUTE__VIEWING);
    }

    @Deprecated
    public TextDocument getTextDocument() {
        return this.textDocument;
    }
}