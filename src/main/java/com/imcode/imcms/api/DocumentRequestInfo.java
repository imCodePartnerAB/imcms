package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Document request bound to thread local.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public abstract class DocumentRequestInfo {


    public static DocumentRequestInfo newWorkingDocRequestInstance(UserDomainObject user, I18nLanguage language, Integer docId) {
        return new WorkingDocRequest(user, language, docId);
    }

    public static DocumentRequestInfo newDefaultDocRequestInstance(UserDomainObject user, I18nLanguage language, Integer docId) {
        return new DefaultDocRequest(user, language, docId);
    }

    public static DocumentRequestInfo newCustomDocRequestInstance(UserDomainObject user, I18nLanguage language, Integer docId, Integer docVersionNo) {
        return new CustomDocRequest(user, language, docId, docVersionNo);
    }

    
    protected UserDomainObject user;

    protected I18nLanguage language;

    protected Integer docId;

    protected DocumentRequestInfo(UserDomainObject user, I18nLanguage language, Integer docId) {
        this.user = user;
        this.language = language;
        this.docId = docId;
    }

    /**
     * @param docMapper initialized instance of DocumentMapper.
     * @param docId id of document to return.
     * @return
    */
    public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {


        UserDomainObject user = documentRequestInfo.getUser();
        I18nLanguage language = documentRequestInfo.getLanguage();
        DocumentRequestInfo.DocVersionMode docVersionMode = documentRequestInfo.getDocVersionMode();

        if (user.isSuperAdmin()) {
            DocumentRequestInfo.CustomDoc customDoc = documentRequestInfo.getCustomDoc();

            if (customDoc != null && docId.equals(customDoc.id)) {
                return getCustomDocument(docId, customDoc.versionNo, language);
            }

            return docVersionMode == DocumentRequestInfo.DocVersionMode.WORKING
                ? getWorkingDocument(docId, language)
                : getDefaultDocument(docId, language);
        }


        if (!language.isDefault() && !meta.getLanguages().contains(language)) {
            if (meta.getDisabledLanguageShowSetting() == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE) {
                language = Imcms.getI18nSupport().getDefaultLanguage();
            } else {
                return null;
            }
        }

        return docVersionMode == DocumentRequestInfo.DocVersionMode.WORKING
            ? getWorkingDocument(docId, language)
            : getDefaultDocument(docId, language);
    }

    
    public UserDomainObject getUser() {
        return user;
    }

    public I18nLanguage getLanguage() {
        return language;
    }

    public Integer getDocId() {
        return docId;
    }


    public static final class WorkingDocRequest extends DocumentRequestInfo {

        private WorkingDocRequest(UserDomainObject user, I18nLanguage language, Integer docId) {
            super(user, language, docId); 
        }


        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docMapper.getWorkingDocument(docId, language);
        }
    }

    public static final class DefaultDocRequest extends DocumentRequestInfo {

        private DefaultDocRequest(UserDomainObject user, I18nLanguage language, Integer docId) {
            super(user, language, docId);
        }
        
        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docMapper.getDefaultDocument(docId, language);
        }
    }


    public static final class CustomDocRequest extends DocumentRequestInfo {

        private Integer docVersionNo;

        private CustomDocRequest(UserDomainObject user, I18nLanguage language, Integer docId, Integer docVersionNo) {
            super(user, language, docId);
            this.docVersionNo = docVersionNo;
        }

        public Integer getDocVersionNo() {
            return docVersionNo;
        }

        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return !docId.equals(this.docId)
                ? docMapper.getDefaultDocument(docId, language)
                : (user.isSuperAdmin())
                    ? docMapper.getCustomDocument(docId, docVersionNo, language)
                    : docMapper.getDefaultDocument(docId, language);
        }
    }
}
