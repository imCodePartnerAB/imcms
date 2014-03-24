package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.container.DocRef;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

// scala
public class CustomDocGetterCallback implements DocGetterCallback {

    private final DocumentLanguages documentLanguages;
    private final int selectedDocId;
    private final int selectedDocVersionNo;

    public CustomDocGetterCallback(DocumentLanguages documentLanguages, int selectedDocId, int selectedDocVersionNo) {
        this.documentLanguages = documentLanguages;
        this.selectedDocId = selectedDocId;
        this.selectedDocVersionNo = selectedDocVersionNo;
    }

    @Override
    public DocumentLanguages documentLanguages() {
        return documentLanguages;
    }

    @Override
    public <T extends DocumentDomainObject> T getDoc(int docId, UserDomainObject user, DocumentMapper docMapper) {
        return docId == selectedDocId
                ? docMapper.getCustomDocument(DocRef.of(selectedDocId, selectedDocVersionNo, documentLanguages.getPreferred().getCode()))
                : new DefaultDocGetterCallback(documentLanguages).getDoc(docId, user, docMapper);

    }

    @Override
    public CustomDocGetterCallback copy(DocumentLanguages documentLanguages) {
        return new CustomDocGetterCallback(documentLanguages, selectedDocId, selectedDocVersionNo);
    }

    public DocumentLanguages getDocumentLanguages() {
        return documentLanguages;
    }

    public int getSelectedDocId() {
        return selectedDocId;
    }

    public int getSelectedDocVersionNo() {
        return selectedDocVersionNo;
    }
}