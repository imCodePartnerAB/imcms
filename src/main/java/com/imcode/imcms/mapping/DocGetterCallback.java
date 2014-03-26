package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.container.DocRef;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(DocGetterCallback.class);

    private volatile DocumentLanguage language;

    private volatile boolean isDefaultLanguage;

    private UserDomainObject user;

    private Map<Integer, Callback> callbacks = new ConcurrentHashMap<>();

    private Callback workingDocCallback = (docId, docMapper) -> {
        logger.trace("Working doc requested - user: {}, docId: {}, language: {}.", user, docId, language);
        return docMapper.getWorkingDocument(docId, language);
    };

    private Callback uncheckedDefaultDocCallback = (docId, docMapper) -> {
        logger.trace("Default doc (unchecked) requested - user: {}, docId: {}, language: {}.", user, docId, language);
        return docMapper.getDefaultDocument(docId, language);
    };

    private Callback defaultDocCallback = (docId, docMapper) -> {
        logger.trace("Default doc requested - user: {}, docId: {}, language: {}.", docId, language);

        DocumentDomainObject doc = docMapper.getDefaultDocument(docId, language);

        if (doc != null && !isDefaultLanguage) {
            DocumentMeta meta = doc.getMeta();

            if (!meta.getEnabledLanguages().contains(language)) {
                doc = meta.getDisabledLanguageShowMode() == DocumentMeta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE
                        ? docMapper.getDefaultDocument(docId)
                        : null;
            }
        }

        return doc;
    };

    public DocGetterCallback(UserDomainObject user) {
        this.user = user;
    }

    @SuppressWarnings("unchecked")
    public <T extends DocumentDomainObject> T getDoc(int docId, DocumentMapper docMapper) {
        Callback callback = user.isDefaultUser()
                ? defaultDocCallback
                : callbacks.getOrDefault(docId, uncheckedDefaultDocCallback);

        return (T) callback.getDoc(docId, docMapper);
    }

    public void setDefault(int docId) {
        callbacks.remove(docId);
    }

    public void setWorking(int docId) {
        if (!user.isDefaultUser()) {
            callbacks.put(docId, workingDocCallback);
        }
    }

    public void setCustom(int docId, int versionNo) {
        if (!user.isDefaultUser()) {
            callbacks.put(docId, versionNo == DocumentVersion.WORKING_VERSION_NO ? workingDocCallback : createCustomDocCallback(versionNo));
        }
    }

    private Callback createCustomDocCallback(int versionNo) {
        return (docId, docMapper) -> {
            logger.trace("Custom doc requested - user: {},  docId: {}, versionNo: {}, language: {}.", user, docId, versionNo, language);
            return docMapper.getCustomDocument(DocRef.of(docId, versionNo, language.getCode()));
        };
    }

    public DocumentLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocumentLanguage language, boolean isDefaultLanguage) {
        this.language = language;
        this.isDefaultLanguage = isDefaultLanguage;
    }
}