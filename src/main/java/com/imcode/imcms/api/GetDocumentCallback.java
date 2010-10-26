package com.imcode.imcms.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

/**
 * Parametrized callback for DocumentMapper#getDocument method.
 * 
 * A callback is created per a http request and bond to the thread local in the Imcms singleton.
 * Working doc callback is associated with a particular document id.
 * Custom doc callback is associated with a particular document id and version.
 * 
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(Integer)
 */
public abstract class GetDocumentCallback implements Cloneable {

    /** Common callback parameters. */
    public static class Params {

        /** An user associated with this callback. */
        public final UserDomainObject user;

        /** Document's language. */
        public final I18nLanguage language;

        /** Default language */
        public final I18nLanguage defaultLanguage;

        public final boolean languageIsDefault;

        public Params(UserDomainObject user, I18nLanguage language, I18nLanguage defaultLanguage) {
            this.user = user;
            this.language = language;
            this.defaultLanguage = defaultLanguage;
            this.languageIsDefault = language.equals(defaultLanguage);
        }
    }

    protected Params params;

    public Params getParams() { return params; }

    public GetDocumentCallback(Params params) {
        this.params = params;
    }

    public GetDocumentCallback copy(Params params) {
        GetDocumentCallback copy = clone();
        copy.params = params;

        return copy;
    }

    @Override
    public GetDocumentCallback clone() {
        try {
            return (GetDocumentCallback)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId);

    public static class GetDocumentCallbackDefault extends GetDocumentCallback {

        public GetDocumentCallbackDefault(Params params) {
            super(params);
        }

        /**
         * @return default version of a document.
         */
        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            DocumentDomainObject doc = docMapper.getDefaultDocument(docId, params.language);

            if (doc != null && !params.languageIsDefault && !params.user.isSuperAdmin()) {
                Meta meta = doc.getMeta();

                if (!meta.getLanguages().contains(params.language)) {
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

        public GetDocumentCallbackWorking(Params params, Integer docId) {
            super(params);
            this.docId = docId;
        }

        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docId.equals(this.docId)
                ? docMapper.getWorkingDocument(docId, params.language)
                : super.getDoc(docMapper, docId);
        }
    }

    

    public static class GetDocumentCallbackCustom extends GetDocumentCallbackDefault {

        private Integer docId;

        private Integer docVersionNo;                

        public GetDocumentCallbackCustom(Params params, Integer docId, Integer docVersionNo) {
            super(params);
            this.docId = docId;
            this.docVersionNo = docVersionNo;
        }

        /**
         * @param docMapper
         * @param docId requested document id.
         * @return
         */
        @Override
        public DocumentDomainObject getDoc(DocumentMapper docMapper, Integer docId) {
            return docId.equals(this.docId)
                ? docMapper.getCustomDocument(docId, docVersionNo, params.language)
                : super.getDoc(docMapper, docId);
        }
    }    

}