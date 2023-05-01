package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.model.Language;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
public class DocGetterCallback implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(DocGetterCallback.class);
    private static final long serialVersionUID = 2496394918087427549L;
    private volatile Language language;
    private UserDomainObject user;
    private Map<Integer, Callback> callbacks = new ConcurrentHashMap<>();
    private final Callback workingDocCallback = (docId, docMapper) -> {
        logger.trace("Working doc requested - user: {}, docId: {}, language: {}.", user, docId, language);
        return docMapper.getWorkingDocument(docId, language);
    };
    private final Callback uncheckedDefaultDocCallback = (docId, docMapper) -> {
        logger.trace("Default doc (unchecked) requested - user: {}, docId: {}, language: {}.", user, docId, language);
        return docMapper.getDefaultDocument(docId, language);
    };
    private final Callback defaultDocCallback = (docId, docMapper) -> {
        logger.trace("Default doc requested - user: {}, docId: {}, language: {}.", user, docId, language);

        DocumentDomainObject doc = docMapper.getDefaultDocument(docId, language);

        if (doc != null) {
            List<Language> docLanguages = Imcms.getServices()
                    .getDocumentMapper()
                    .getCommonContents(doc.getId(), doc.getVersionNo())
                    .entrySet()
                    .stream()
                    .filter(langToContent -> langToContent.getValue().getEnabled())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (docLanguages.isEmpty()) {
                docLanguages.add(doc.getLanguage());
            }

            if (!docLanguages.contains(language)) { // current language is disabled for current document
                doc = shouldDocBeShownWithDefaultLang(doc, docLanguages)
                        ? docMapper.getDefaultDocument(docId)
                        : null;
            }
        }

        return doc;
    };

    public DocGetterCallback(UserDomainObject user) {
        this.user = user;
    }

    private boolean shouldDocBeShownWithDefaultLang(DocumentDomainObject doc, List<Language> docLanguages) {
        return (doc.getDisabledLanguageShowMode() == DocumentMeta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE
                && docLanguages.contains(Imcms.getServices().getLanguageService().getDefaultLanguage()));
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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    private interface Callback extends Serializable {
        DocumentDomainObject getDoc(int docId, DocumentMapper docMapper);
    }
}
