package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class DocumentContentMapper {

    @Inject
    private CommonContentRepository commonContentRepository;

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private DocumentLanguageMapper languageMapper;

    public Map<DocumentLanguage, DocumentCommonContent> getCommonContents(int docId) {
        Map<DocumentLanguage, DocumentCommonContent> result = new HashMap<>();

        for (CommonContent commonContent : commonContentRepository.findByDocId(docId)) {
            result.put(
                    languageMapper.toApiObject(commonContent.getLanguage()),
                    toApiObject(commonContent)
            );
        }

        return result;
    }


    public DocumentCommonContent getCommonContent(DocRef docRef) {
        return toApiObject(commonContentRepository.findByDocIdAndLanguageCode(
                docRef.getDocId(), docRef.getDocLanguageCode()));
    }


    private DocumentCommonContent toApiObject(CommonContent jpaCommonContent) {
        return jpaCommonContent == null
                ? null
                : DocumentCommonContent.builder()
                .headline(jpaCommonContent.getHeadline())
                .menuImageURL(jpaCommonContent.getMenuImageURL())
                .menuText(jpaCommonContent.getMenuText())
                .build();
    }




}
