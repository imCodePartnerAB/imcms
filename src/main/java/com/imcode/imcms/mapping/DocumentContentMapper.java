package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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

    @Transactional(propagation = Propagation.SUPPORTS)
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


    @Transactional(propagation = Propagation.SUPPORTS)
    public DocumentCommonContent getCommonContent(DocRef docRef) {
        return toApiObject(commonContentRepository.findByDocIdAndLanguageCode(
                docRef.getId(), docRef.getLanguageCode()));
    }

    /**
     * Use {@link DocumentContentMapper#saveCommonContent(DocumentDomainObject)}
     */
    @Deprecated
    public void saveCommonContent(DocumentDomainObject doc, UserDomainObject user) {
        saveCommonContent(doc);
    }

    public void saveCommonContent(DocumentDomainObject doc) {
        Language language = languageRepository.findByCode(doc.getLanguage().getCode());
        CommonContent dcc = commonContentRepository.findByDocIdAndLanguage(doc.getId(), language);

        if (dcc == null) {
            dcc = new CommonContent();
        }

        DocumentCommonContent dccDO = doc.getCommonContent();

        dcc.setDocId(doc.getId());
        dcc.setLanguage(language);
        dcc.setHeadline(dccDO.getHeadline());
        dcc.setMenuText(dccDO.getMenuText());
        dcc.setMenuImageURL(dccDO.getMenuImageURL());
        dcc.setEnabled(dccDO.getEnabled());

        commonContentRepository.save(dcc);
    }

    private DocumentCommonContent toApiObject(CommonContent commonContent) {
        return commonContent == null
                ? null
                : DocumentCommonContent.builder()
                .headline(commonContent.getHeadline())
                .menuImageURL(commonContent.getMenuImageURL())
                .menuText(commonContent.getMenuText())
                .enabled(commonContent.getEnabled())
                .build();
    }
}
