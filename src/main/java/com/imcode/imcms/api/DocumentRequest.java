package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Holds requested document language and  
 *
 * Document request bound to thread local.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(Integer) 
 */
public abstract class DocumentRequest {

    protected UserDomainObject user;

    protected I18nLanguage language;

    public DocumentRequest(UserDomainObject user, I18nLanguage language) {
        this.user = user;
        this.language = language;
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


    public static class WorkingDocRequest extends DocumentRequest {

        public WorkingDocRequest(UserDomainObject user, I18nLanguage language) {
            super(user, language);
        }

        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docMapper.getWorkingDocument(docId, language);
        }
    }
    
    
    public static class DefaultDocRequest extends DocumentRequest {

        public DefaultDocRequest(UserDomainObject user, I18nLanguage language) {
            super(user, language);
        }
        
        /**
         * @return default document.
         */
        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            DocumentDomainObject doc = docMapper.getDefaultDocument(docId, language);

            if (doc != null && !Imcms.getI18nSupport().isDefault(language) && !user.isSuperAdmin()) {
                Meta meta = doc.getMeta();

                if (!meta.getEnabledLanguages().contains(language)) {
                    if (meta.getDisabledLanguageShowSetting() != Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE) {
                        doc = null;
                    } else {
                        doc = docMapper.getDefaultDocument(docId);
                    }

                }
            }

            return doc;
        }
    }

    
    public static class CustomDocRequest extends DefaultDocRequest {

        private Integer docId;

        private Integer docVersionNo;
        
        public CustomDocRequest(UserDomainObject user, I18nLanguage language, Integer docId, Integer docVersionNo) {
            super(user, language);
            this.docId = docId;
            this.docVersionNo = docVersionNo;
        }

        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docId.equals(this.docId) && user.isSuperAdmin()
                ? docMapper.getCustomDocument(docId, docVersionNo, language)
                : super.getDoc(docMapper, docId);
        }

        public Integer getDocId() {
            return docId;
        }

        public Integer getDocVersionNo() {
            return docVersionNo;
        }
    }
}