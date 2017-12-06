package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.TextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class TextService {

    private final TextRepository textRepository;
    private final LanguageService languageService;
    private final VersionService versionService;

    TextService(TextRepository textRepository, LanguageService languageService, VersionService versionService) {
        this.textRepository = textRepository;
        this.languageService = languageService;
        this.versionService = versionService;
    }

    public TextDTO getText(TextDTO textRequestData) {
        return getText(
                textRequestData.getDocId(),
                textRequestData.getIndex(),
                textRequestData.getLangCode(),
                textRequestData.getLoopEntryRef()
        );
    }

    public TextDTO getText(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRef) {
        return getText(docId, index, langCode, loopEntryRef, versionService::getDocumentWorkingVersion);
    }

    public TextDTO getPublicText(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRef) {
        return getText(docId, index, langCode, loopEntryRef, versionService::getLatestVersion);
    }

    public void save(TextDTO textDTO) {
        final Version version = versionService.getDocumentWorkingVersion(textDTO.getDocId());
        final LanguageJPA language = languageService.findEntityByCode(textDTO.getLangCode());
        final TextJPA text = new TextJPA(textDTO, version, language);
        final Integer textId = getTextId(textDTO, version, language);

        text.setId(textId);
        textRepository.save(text);
    }

    public void deleteByDocId(Integer docIdToDelete) {
        textRepository.deleteByDocId(docIdToDelete);
    }

    private TextDTO getText(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRefDTO,
                            Function<Integer, Version> versionReceiver) {

        final Version version = versionReceiver.apply(docId);
        final LanguageJPA language = languageService.findEntityByCode(langCode);
        final TextJPA text = getText(index, version, language, loopEntryRefDTO);

        return Optional.ofNullable(text)
                .map(text1 -> new TextDTO(text1, text1.getVersion(), text1.getLanguage()))
                .orElse(new TextDTO(index, docId, langCode, loopEntryRefDTO));
    }

    private TextJPA getText(int index, Version version, LanguageJPA language, LoopEntryRefDTO loopEntryRefDTO) {
        return Optional.ofNullable(loopEntryRefDTO)
                .map(LoopEntryRefJPA::new)
                .map(loopEntryRef -> textRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(
                        version, language, index, loopEntryRef
                ))
                .orElseGet(() -> textRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(
                        version, language, index
                ));
    }

    private Integer getTextId(TextDTO textDTO, Version version, LanguageJPA language) {
        final Integer index = textDTO.getIndex();
        final LoopEntryRefDTO loopEntryRefDTO = textDTO.getLoopEntryRef();
        final TextJPA text = getText(index, version, language, loopEntryRefDTO);

        if (text == null) {
            return null;
        }

        return text.getId();
    }
}
