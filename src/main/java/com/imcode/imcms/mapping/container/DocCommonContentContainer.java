package com.imcode.imcms.mapping.container;

import com.imcode.imcms.mapping.DocumentCommonContent;

import java.util.Objects;

public class DocCommonContentContainer {

    public static DocCommonContentContainer of(DocVersionRef docVersionRef, DocumentCommonContent documentCommonContent) {
        return new DocCommonContentContainer(docVersionRef, documentCommonContent);
    }

    private final DocVersionRef docVersionRef;

    private final DocumentCommonContent documentCommonContent;

    public DocCommonContentContainer(DocVersionRef docVersionRef, DocumentCommonContent documentCommonContent) {
        Objects.requireNonNull(docVersionRef);
        Objects.requireNonNull(documentCommonContent);

        this.docVersionRef = docVersionRef;
        this.documentCommonContent = documentCommonContent;
    }

    public DocVersionRef getDocVersionRef() {
        return docVersionRef;
    }

    public DocumentCommonContent getCommonContent() {
        return documentCommonContent;
    }

    public int getDocId() {
        return docVersionRef.getDocId();
    }

    public int getDocVersionNo() {
        return docVersionRef.getDocVersionNo();
    }
}
