package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

// scala
public class WorkingDocGetterCallback implements DocGetterCallback {

    private final DocumentLanguages documentLanguages;
    private final int selectedDocId;

    public WorkingDocGetterCallback(DocumentLanguages documentLanguages, int selectedDocId) {
        this.documentLanguages = documentLanguages;
        this.selectedDocId = selectedDocId;
    }

    @Override
    public DocumentLanguages documentLanguages() {
        return documentLanguages;
    }

    @Override
    public <T extends DocumentDomainObject> T getDoc(int docId, UserDomainObject user, DocumentMapper docMapper) {
        return docId == selectedDocId
            ? docMapper.getWorkingDocument(docId, documentLanguages.getPreferred())
            : new DefaultDocGetterCallback(documentLanguages).getDoc(docId, user, docMapper);
    }

    @Override
    public WorkingDocGetterCallback copy(DocumentLanguages documentLanguages) {
        return new WorkingDocGetterCallback(documentLanguages, selectedDocId);
    }

    public DocumentLanguages getDocumentLanguages() {
        return documentLanguages;
    }

    public int getSelectedDocId() {
        return selectedDocId;
    }
}