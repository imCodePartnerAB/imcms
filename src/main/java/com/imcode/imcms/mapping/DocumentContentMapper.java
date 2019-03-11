package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.container.DocRef;
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
import java.util.Map;

@Service
@Transactional
public class DocumentContentMapper {

    private final CommonContentRepository commonContentRepository;
    private final LanguageRepository languageRepository;
    private final DocumentLanguageMapper languageMapper;

    public DocumentContentMapper(CommonContentRepository commonContentRepository, LanguageRepository languageRepository,
                                 DocumentLanguageMapper languageMapper) {
        this.commonContentRepository = commonContentRepository;
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
    }

    /**
     * @deprecated use {@link #getCommonContents(int, int)}
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Deprecated
    public Map<DocumentLanguage, DocumentCommonContent> getCommonContents(int docId) {
        return getCommonContents(docId, DocumentVersion.WORKING_VERSION_NO);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Map<DocumentLanguage, DocumentCommonContent> getCommonContents(int docId, int versionNo) {
        Map<DocumentLanguage, DocumentCommonContent> result = new HashMap<>();

        for (CommonContentJPA commonContent : commonContentRepository.findByDocIdAndVersionNo(docId, versionNo)) {
            result.put(
                    languageMapper.toApiObject(commonContent.getLanguage()),
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
                .headline(commonContent.getHeadline())
                .menuText(commonContent.getMenuText())
                .enabled(commonContent.isEnabled())
                .versionNo(commonContent.getVersionNo())
                .build();
    }
}
