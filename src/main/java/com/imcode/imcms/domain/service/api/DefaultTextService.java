package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.TextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Transactional
@Service("textService")
class DefaultTextService implements TextService {

    private final TextRepository textRepository;
    private final LanguageService languageService;
    private final VersionService versionService;

    DefaultTextService(TextRepository textRepository, LanguageService languageService, VersionService versionService) {
        this.textRepository = textRepository;
        this.languageService = languageService;
        this.versionService = versionService;
    }

    @Override
    public Text getText(Text textRequestData) {
        return getText(
                textRequestData.getDocId(),
                textRequestData.getIndex(),
                textRequestData.getLangCode(),
                textRequestData.getLoopEntryRef()
        );
    }

    @Override
    public Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getText(docId, index, langCode, loopEntryRef, versionService::getDocumentWorkingVersion);
    }

    @Override
    public Text getPublicText(int docId, int index, String langCode, LoopEntryRef loopEntryRef) {
        return getText(docId, index, langCode, loopEntryRef, versionService::getLatestVersion);
    }

    @Override
    public void save(Text text) {
        final Version version = versionService.getDocumentWorkingVersion(text.getDocId());
        final LanguageJPA language = languageService.findEntityByCode(text.getLangCode());
        final TextJPA textJPA = new TextJPA(text, version, language);
        final Integer textId = getTextId(text, version, language);

        textJPA.setId(textId);
        textRepository.save(textJPA);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        textRepository.deleteByDocId(docIdToDelete);
    }

    private Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef,
                         Function<Integer, Version> versionReceiver) {

        final Version version = versionReceiver.apply(docId);
        final LanguageJPA language = languageService.findEntityByCode(langCode);
        final TextJPA text = getText(index, version, language, loopEntryRef);

        return Optional.ofNullable(text)
                .map(text1 -> new TextDTO(text1, text1.getVersion(), text1.getLanguage()))
                .orElse(new TextDTO(index, docId, langCode, loopEntryRef));
    }

    private TextJPA getText(int index, Version version, LanguageJPA language, LoopEntryRef loopEntryRef) {
        return Optional.ofNullable(loopEntryRef)
                .map(LoopEntryRefJPA::new)
                .map(loopEntryRefJPA -> textRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(
                        version, language, index, loopEntryRefJPA
                ))
                .orElseGet(() -> textRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(
                        version, language, index
                ));
    }

    private Integer getTextId(Text text, Version version, LanguageJPA language) {
        final Integer index = text.getIndex();
        final LoopEntryRef loopEntryRef = text.getLoopEntryRef();
        final TextJPA textJPA = getText(index, version, language, loopEntryRef);

        if (textJPA == null) {
            return null;
        }

        return textJPA.getId();
    }
}
