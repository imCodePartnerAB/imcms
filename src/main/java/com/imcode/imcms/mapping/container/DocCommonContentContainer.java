package com.imcode.imcms.mapping.container;

import com.imcode.imcms.mapping.DocumentCommonContent;

import java.util.Objects;

public class DocCommonContentContainer extends TextDocVersionedContainer {

    public static DocCommonContentContainer of(VersionRef versionRef, DocumentCommonContent documentCommonContent) {
        return new DocCommonContentContainer(versionRef, documentCommonContent);
    }

    private final DocumentCommonContent documentCommonContent;

    public DocCommonContentContainer(VersionRef versionRef, DocumentCommonContent documentCommonContent) {
        super(versionRef);
        this.documentCommonContent = Objects.requireNonNull(documentCommonContent);
    }

    public DocumentCommonContent getCommonContent() {
        return documentCommonContent;
    }
}
