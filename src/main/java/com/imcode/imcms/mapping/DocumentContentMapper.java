package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.CommonContentJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentContentMapper {

    private final CommonContentRepository commonContentRepository;
    private final LanguageRepository languageRepository;
    private final CommonContentService commonContentService;

    public DocumentContentMapper(CommonContentRepository commonContentRepository,
                                 LanguageRepository languageRepository,
                                 CommonContentService commonContentService) {
        this.commonContentRepository = commonContentRepository;
        this.languageRepository = languageRepository;
        this.commonContentService = commonContentService;
    }

    /**
     * @deprecated use {@link #getCommonContents(int, int)}
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Deprecated
    public Map<Language, DocumentCommonContent> getCommonContents(int docId) {
        return getCommonContents(docId, DocumentVersion.WORKING_VERSION_NO);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Map<Language, DocumentCommonContent> getCommonContents(int docId, int versionNo) {
        Map<Language, DocumentCommonContent> result = new HashMap<>();

        final List<CommonContentJPA> receivedContents = commonContentService.getOrCreateCommonContents(docId, versionNo)
                .stream()
                .map(CommonContentJPA::new)
                .collect(Collectors.toList());

        for (CommonContentJPA commonContent : receivedContents) {
            result.put(
                    commonContent.getLanguage(),
                    toApiObject(commonContent)
            );
        }

        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public DocumentCommonContent getCommonContent(DocRef docRef) {
        final CommonContentJPA commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(
                docRef.getId(), docRef.getVersionNo(), docRef.getLanguageCode());
        return toApiObject(commonContent);
    }

    /**
     * Use {@link DocumentContentMapper#saveCommonContent(DocumentDomainObject)}
     */
    @Deprecated
    public void saveCommonContent(DocumentDomainObject doc, UserDomainObject user) {
        saveCommonContent(doc);
    }

    public void saveCommonContent(DocumentDomainObject doc) {
        LanguageJPA language = languageRepository.findByCode(doc.getLanguage().getCode());
        CommonContentJPA dcc = commonContentRepository.findByDocIdAndVersionNoAndLanguage(
                doc.getId(), doc.getVersionNo(), language);

        if (dcc == null) {
            dcc = new CommonContentJPA();
        }

        DocumentCommonContent dccDO = doc.getCommonContent();

        dcc.setDocId(doc.getId());
        dcc.setLanguage(language);
        dcc.setHeadline(dccDO.getHeadline());
        dcc.setMenuText(dccDO.getMenuText());
        dcc.setEnabled(dccDO.getEnabled());
        dcc.setVersionNo(doc.getVersionNo());

        commonContentRepository.save(dcc);
    }

    private DocumentCommonContent toApiObject(CommonContentJPA commonContent) {
        return commonContent == null
                ? null
                : DocumentCommonContent.builder()
		        .alias(commonContent.getAlias())
                .headline(commonContent.getHeadline())
                .menuText(commonContent.getMenuText())
                .enabled(commonContent.isEnabled())
                .versionNo(commonContent.getVersionNo())
                .build();
    }
}
