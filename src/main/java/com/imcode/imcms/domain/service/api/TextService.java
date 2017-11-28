package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.Text;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.TextRepository;
import com.imcode.imcms.util.function.TernaryFunction;
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
    private final TernaryFunction<Text, Version, LanguageDTO, TextDTO> textToTextDTO;
    private final TernaryFunction<TextDTO, Version, LanguageJPA, Text> textDtoToText;

    TextService(TextRepository textRepository,
                LanguageService languageService,
                VersionService versionService,
                TernaryFunction<Text, Version, LanguageDTO, TextDTO> textToTextDTO,
                TernaryFunction<TextDTO, Version, LanguageJPA, Text> textDtoToText) {

        this.textRepository = textRepository;
        this.languageService = languageService;
        this.versionService = versionService;
        this.textToTextDTO = textToTextDTO;
        this.textDtoToText = textDtoToText;
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
        final Text text = textDtoToText.apply(textDTO, version, language);
        final Integer textId = getTextId(textDTO, version, language);

        text.setId(textId);
        textRepository.save(text);
    }

    private TextDTO getText(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRefDTO,
                            Function<Integer, Version> versionReceiver) {

        final Version version = versionReceiver.apply(docId);
        final LanguageJPA language = languageService.findEntityByCode(langCode);
        final Text text = getText(index, version, language, loopEntryRefDTO);

        return Optional.ofNullable(text)
                .map(text1 -> textToTextDTO.apply(text1, text1.getVersion(), new LanguageDTO(text1.getLanguage())))
                .orElse(new TextDTO(index, docId, langCode, loopEntryRefDTO));
    }

    private Text getText(int index, Version version, LanguageJPA language, LoopEntryRefDTO loopEntryRefDTO) {
        final Optional<LoopEntryRefJPA> oLoopEntryRef = Optional.ofNullable(loopEntryRefDTO).map(LoopEntryRefJPA::new);

        return (oLoopEntryRef.isPresent())
                ? textRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(version, language, index, oLoopEntryRef.get())
                : textRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index);
    }

    private Integer getTextId(TextDTO textDTO, Version version, LanguageJPA language) {
        final Integer index = textDTO.getIndex();
        final LoopEntryRefDTO loopEntryRefDTO = textDTO.getLoopEntryRef();
        final Text text = getText(index, version, language, loopEntryRefDTO);

        if (text == null) {
            return null;
        }

        return text.getId();
    }
}
