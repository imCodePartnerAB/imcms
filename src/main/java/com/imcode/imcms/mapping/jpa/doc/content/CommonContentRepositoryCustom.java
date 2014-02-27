package com.imcode.imcms.mapping.jpa.doc.content;

import com.imcode.imcms.mapping.jpa.doc.Language;

/**
 * Created by ajosua on 26/02/14.
 */
interface CommonContentRepositoryCustom {

    void deleteByDocIdAndDocLanguage(int docId, Language language);

    void deleteByDocIdAndDocLanguageCode(int docId, String code);

    void deleteByDocId(int docId);
}
