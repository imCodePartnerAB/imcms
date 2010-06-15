package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Parametrized callback for DocumentMapper#getDocument method.
 * 
 * A callback is created per a http request and bond to thread local in the Imcms singleton.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(Integer)
 */
public abstract class GetDocumentCallback {

    /**
     * A user associated with this callback.
     */
    protected UserDomainObject user;
    
    /**
     * Document language.
     */
    protected I18nLanguage language;

    public GetDocumentCallback(I18nLanguage language, UserDomainObject user) {
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


    public static class GetDocumentCallbackDefault extends GetDocumentCallback {
        
        public GetDocumentCallbackDefault(I18nLanguage language, UserDomainObject user) {
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

                if (!meta.getLanguages().contains(language)) {
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



    public static class GetDocumentCallbackWorking extends GetDocumentCallbackDefault {

        private Integer docId;

        public GetDocumentCallbackWorking(Integer docId, I18nLanguage language, UserDomainObject user) {
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

    

    public static class GetDocumentCallbackCustom extends GetDocumentCallbackDefault {

        private Integer docId;

        private Integer docVersionNo;                

        public GetDocumentCallbackCustom(Integer docId, Integer docVersionNo, I18nLanguage language, UserDomainObject user) {
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