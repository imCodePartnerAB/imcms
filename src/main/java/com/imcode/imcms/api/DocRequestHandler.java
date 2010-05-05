package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Document request handler - controls document version to return.
 *
 * DocumentMapper#getDocument uses DocRequestHandler getDoc method as a callback.
 *
 * DocRequestHandler is created per user's session and bound to thread local in Imcms singleton.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(Integer)
 */
public abstract class DocRequestHandler {

    protected I18nLanguage language;

    protected UserDomainObject user;

    public DocRequestHandler(I18nLanguage language, UserDomainObject user) {
        this.language = language;
        this.user = user;
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

    public abstract DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId);


    public static class DefaultDocVersionRequestHandler extends DocRequestHandler {
        
        public DefaultDocVersionRequestHandler(I18nLanguage language, UserDomainObject user) {
            super(language, user);
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



    public static class WorkingDocVersionRequestHandler extends DefaultDocVersionRequestHandler {

        private Integer docId;

        public WorkingDocVersionRequestHandler(Integer docId, I18nLanguage language, UserDomainObject user) {
            super(language, user);
            this.docId = docId;
        }

        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docId.equals(this.docId)
                ? docMapper.getWorkingDocument(docId, language)
                : super.getDoc(docMapper, docId);
        }
    }

    

    public static class CustomDocVersionRequestHandler extends DefaultDocVersionRequestHandler {

        private Integer docId;

        private Integer docVersionNo;                

        public CustomDocVersionRequestHandler(Integer docId, Integer docVersionNo, I18nLanguage language, UserDomainObject user) {
            super(language, user);
            this.docId = docId;
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
    }    

}