package com.imcode.imcms.mapping;

import com.google.common.base.Optional;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
public class DocContentMapper {

    @Inject
    private CommonContentRepository commonContentRepository;

    @Inject
    private LanguageRepository languageRepository;

    public Map<DocumentLanguage, Optional<CommonContentVO>> getCommonContents(int docId) {
        Map<DocumentLanguage, Optional<CommonContentVO>> commonContentMap = new HashMap<>();

        for (Language language : languageRepository.findAll()) {
            commonContentMap.put(
                    EntityConverter.toApi(language),
                    Optional.fromNullable(EntityConverter.toVO(commonContentRepository.findByDocIdAndDocLanguage(docId, language)))
            );
        }

        return commonContentMap;
    }


    public CommonContentVO getCommonContents(DocRef docRef) {
        return EntityConverter.toVO(commonContentRepository.findByDocIdAndDocLanguageCode(
                docRef.getDocId(), docRef.getDocLanguageCode()));
    }
}
