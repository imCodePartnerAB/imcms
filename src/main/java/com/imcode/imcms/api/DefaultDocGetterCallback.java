package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentMeta;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

// scala
public class DefaultDocGetterCallback implements DocGetterCallback {

    private final DocumentLanguages documentLanguages;

    public DefaultDocGetterCallback(DocumentLanguages documentLanguages) {
        this.documentLanguages = documentLanguages;
    }

    @Override
    public DocumentLanguages documentLanguages() {
        return documentLanguages;
    }

    @Override
    public <T extends DocumentDomainObject> T getDoc(int docId, UserDomainObject user, DocumentMapper docMapper) {
        T doc = docMapper.getDefaultDocument(docId, documentLanguages.getPreferred());

        if (doc != null && !documentLanguages.preferredIsDefault() && user.isSuperAdmin()) {
            DocumentMeta meta = doc.getMeta();

            if (!meta.getEnabledLanguages().contains(documentLanguages.getPreferred())) {
                doc = meta.getDisabledLanguageShowMode() == DocumentMeta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE
                        ? docMapper.getDefaultDocument(docId, documentLanguages.getDefault())
                        : null;
            }
        }

        return doc;
    }

    @Override
    public DefaultDocGetterCallback copy(DocumentLanguages documentLanguages) {
        return new DefaultDocGetterCallback(documentLanguages);
    }
}