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
public class DocumentContentMapper {

    @Inject
    private CommonContentRepository commonContentRepository;

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private EntityConverter entityConverter;

    public Map<DocumentLanguage, Optional<DocumentCommonContent>> getCommonContents(int docId) {
        Map<DocumentLanguage, Optional<DocumentCommonContent>> commonContentMap = new HashMap<>();

        for (Language language : languageRepository.findAll()) {
            commonContentMap.put(
                    entityConverter.fromEntity(language),
                    Optional.fromNullable(entityConverter.fromEntity(commonContentRepository.findByDocIdAndLanguage(docId, language)))
            );
        }

        return commonContentMap;
    }


    public DocumentCommonContent getCommonContents(DocRef docRef) {
        return entityConverter.fromEntity(commonContentRepository.findByDocIdAndLanguageCode(
                docRef.getDocId(), docRef.getDocLanguageCode()));
    }
}
