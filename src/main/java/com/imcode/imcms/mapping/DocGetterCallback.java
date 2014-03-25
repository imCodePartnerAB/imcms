package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.container.DocRef;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Parametrized callback for DocumentMapper#getDocument method.
 * A callback is updated on each request and (re)assigned to a user.
 * <p>
 * Default doc callback always returns default version of any doc if it is present and the user has at least 'view' rights on it.
 * <p>
 * Working and Custom doc callback return working and custom version of a document with particular id;
 * for other doc ids they behave exactly as default doc callback.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsSetupFilter
 * @see com.imcode.imcms.mapping.DocumentGetter#getDocument(int)
 */
public class DocGetterCallback {

    private interface Callback {
        DocumentDomainObject getDoc(int docId, DocumentMapper docMapper);
    }

    private volatile DocumentLanguage language;

    private volatile boolean isDefaultLanguage;

    private UserDomainObject user;

    private Map<Integer, Callback> callbacks = new ConcurrentHashMap<>();

    private Callback workingDocCallback =
            (docId, docMapper) -> docMapper.getWorkingDocument(docId, language);

    private Callback defaultDocCallback = (docId, docMapper) -> {
        DocumentDomainObject doc = docMapper.getDefaultDocument(docId, language);

        if (doc != null && !isDefaultLanguage && user.isSuperAdmin()) {
            DocumentMeta meta = doc.getMeta();

            if (!meta.getEnabledLanguages().contains(language)) {
                doc = meta.getDisabledLanguageShowMode() == DocumentMeta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE
                        ? docMapper.getDefaultDocument(docId)
                        : null;
            }
        }

        return doc;
    };

    private final Callback c = new Callback() {
        @Override
        public DocumentDomainObject getDoc(int docId, DocumentMapper docMapper) {
            user.addRoleId(null);
            return null;
        }
    };

    public DocGetterCallback(UserDomainObject user) {
        this.user = user;
    }

    @SuppressWarnings("unchecked")
    public <T extends DocumentDomainObject> T getDoc(int docId, DocumentMapper docMapper) {
        return (T) callbacks.getOrDefault(docId, defaultDocCallback).getDoc(docId, docMapper);
    }

    public void setDefault(int docId) {
        callbacks.remove(docId);
    }

    public void setWorking(int docId) {
        callbacks.put(docId, workingDocCallback);
    }

    public void setCustom(int docId, int versionNo) {
        callbacks.put(docId, versionNo == DocumentVersion.WORKING_VERSION_NO ? workingDocCallback : createCustomDocCallback(versionNo));
    }

    private Callback createCustomDocCallback(int versionNo) {
        return (docId, docMapper) ->
                docMapper.getCustomDocument(DocRef.of(docId, versionNo, language.getCode()));
    }

    public DocumentLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocumentLanguage language, boolean isDefaultLanguage) {
        this.language = language;
        this.isDefaultLanguage = isDefaultLanguage;
    }
}



