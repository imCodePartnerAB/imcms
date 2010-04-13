package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Document request bound to thread local.
 * 
 *
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(Integer) 
 */
public class DocumentRequest {

    protected UserDomainObject user;

    protected I18nLanguage language;

    public DocumentRequest(UserDomainObject user) {
        this.user = user;
    }

    /**
     * @param docMapper
     * @param docId
     * @return default document.
     */
    public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
        DocumentDomainObject doc = docMapper.getDefaultDocument(docId, language);

        if (doc != null && !language.isDefault() && !user.isSuperAdmin()) {
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

    
    public UserDomainObject getUser() {
        return user;
    }

    public I18nLanguage getLanguage() {
        return language;
    }

    public void setUser(UserDomainObject user) {
        this.user = user;
    }

    public void setLanguage(I18nLanguage language) {
        this.language = language;
    }

    
    public static class WorkingDocRequest extends DocumentRequest {

        public WorkingDocRequest(UserDomainObject user) {
            super(user);
        }


        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docMapper.getWorkingDocument(docId, language);
        }
    }


    public static class CustomDocRequest extends DocumentRequest {

        private Integer docId;

        private Integer docVersionNo;

        public CustomDocRequest(UserDomainObject user, Integer docId, Integer docVersionNo) {
            super(user);
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