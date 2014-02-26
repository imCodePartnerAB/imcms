package com.imcode.imcms.mapping;

import com.google.common.base.Optional;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.dao.DocCommonContentDao;
import com.imcode.imcms.mapping.dao.DocLanguageDao;
import com.imcode.imcms.mapping.orm.DocLanguage;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
public class DocMapperService {

    @Inject
    private DocCommonContentDao docCommonContentDao;

    @Inject
    private DocLanguageDao docLanguageDao;

    public Map<DocumentLanguage, Optional<DocumentCommonContent>> getCommonContents(int docId) {
        Map<DocumentLanguage, Optional<DocumentCommonContent>> commonContentMap = new HashMap<>();

        for (DocLanguage docLanguage : docLanguageDao.findAll()) {
            commonContentMap.put(
                    OrmToApi.toApi(docLanguage),
                    Optional.fromNullable(OrmToApi.toApi(docCommonContentDao.findByDocIdAndDocLanguage(docId, docLanguage)))
            );
        }

        return commonContentMap;
    }


    public DocumentCommonContent getCommonContents(DocRef docRef) {
        return OrmToApi.toApi(docCommonContentDao.findByDocIdAndDocLanguageCode(
                docRef.getDocId(), docRef.getDocLanguageCode()));
    }
}
