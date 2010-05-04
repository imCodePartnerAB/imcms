package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Holds information about requested document language and version.  
 *
 * DocumentMapper#getDocument uses DocumentRequest getDoc method as a callback.
 *
 * DocumentRequest is created per user's session and bound to thread local in Imcms singleton.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(Integer) 
 */
public abstract class DocumentRequest {

    protected final Integer docId;

    // todo: make final
    protected I18nLanguage language;

    protected final UserDomainObject user;

    
    public DocumentRequest(Integer docId, I18nLanguage language, UserDomainObject user) {
        this.docId = docId;
        this.language = language;
        this.user = user;
    }


    public Integer getDocId() {
        return docId;
    }

    public UserDomainObject getUser() {
        return user;
    }

    public I18nLanguage getLanguage() {
        return language;
    }

    public void setLanguage(I18nLanguage language) {
        this.language = language;
    }
    
    /**
     * @return requested document.
     */
    public abstract DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId);


    public static class DefaultDocRequest extends DocumentRequest {

        public DefaultDocRequest(Integer docId, I18nLanguage language, UserDomainObject user) {
            super(docId, language, user);
        }

        /**
         * @return default version of a document.
         */
        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            DocumentDomainObject doc = docMapper.getDefaultDocument(docId, language);

            if (doc != null && !Imcms.getI18nSupport().isDefault(language) && !user.isSuperAdmin()) {
                Meta meta = doc.getMeta();

                if (!meta.getEnabledLanguages().contains(language)) {
                    if (meta.getDisabledLanguageShowSetting() == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE) {
                        doc = docMapper.getDefaultDocument(docId);
                    } else {
                        doc = null;
                    }

                }
            }

            return doc;
        }
    }

    
    public static class WorkingDocRequest extends DefaultDocRequest {

        public WorkingDocRequest(Integer docId, I18nLanguage language, UserDomainObject user) {
            super(docId, language, user);
        }

        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docId.equals(this.docId)
                ? docMapper.getWorkingDocument(docId, language)
                : super.getDoc(docMapper, docId);
        }
    }
    
    
    public static class CustomDocRequest extends DefaultDocRequest {

        private final Integer docVersionNo;

        public CustomDocRequest(Integer docId, Integer docVersionNo, I18nLanguage language, UserDomainObject user) {
            super(docId, language, user);
            this.docVersionNo = docVersionNo;
        }

        /**
         * Returns
         *
         * @param docMapper
         * @param docId requested document id.
         * @return
         */
        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docId.equals(this.docId)
                ? docMapper.getCustomDocument(docId, docVersionNo, language)
                : super.getDoc(docMapper, docId);
        }

        public Integer getDocVersionNo() {
            return docVersionNo;
        }
    }
}