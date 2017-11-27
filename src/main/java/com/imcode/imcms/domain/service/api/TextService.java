package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.service.core.VersionService;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.entity.LoopEntryRef;
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
    private final Function<LoopEntryRefDTO, LoopEntryRef> loopEntryRefDtoToLoopEntryRef;
    private final LanguageService languageService;
    private final VersionService versionService;
    private final Function<Text, TextDTO> textToTextDTO;
    private final TernaryFunction<TextDTO, Version, Language, Text> textDtoToText;

    TextService(TextRepository textRepository,
                Function<LoopEntryRefDTO, LoopEntryRef> loopEntryRefDtoToLoopEntryRef,
                LanguageService languageService,
                VersionService versionService,
                Function<Text, TextDTO> textToTextDTO,
                TernaryFunction<TextDTO, Version, Language, Text> textDtoToText) {

        this.textRepository = textRepository;
        this.loopEntryRefDtoToLoopEntryRef = loopEntryRefDtoToLoopEntryRef;
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
        final Language language = languageService.findEntityByCode(textDTO.getLangCode());
        final Text text = textDtoToText.apply(textDTO, version, language);
        final Integer textId = getTextId(textDTO, version, language);

        text.setId(textId);
        textRepository.save(text);
    }

    private TextDTO getText(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRefDTO,
                            Function<Integer, Version> versionReceiver) {

        final Version version = versionReceiver.apply(docId);
        final Language language = languageService.findEntityByCode(langCode);
        final Text text = getText(index, version, language, loopEntryRefDTO);

        return Optional.ofNullable(text)
                .map(textToTextDTO)
                .orElse(new TextDTO(index, docId, langCode, loopEntryRefDTO));
    }

    private Text getText(int index, Version version, Language language, LoopEntryRefDTO loopEntryRefDTO) {
        final LoopEntryRef loopEntryRef = loopEntryRefDtoToLoopEntryRef.apply(loopEntryRefDTO);

        return (loopEntryRef == null)
                ? textRepository.findByVersionAndLanguageAndIndexWhereLoopEntryRefIsNull(version, language, index)
                : textRepository.findByVersionAndLanguageAndIndexAndLoopEntryRef(version, language, index, loopEntryRef);
    }

    private Integer getTextId(TextDTO textDTO, Version version, Language language) {
        final Integer index = textDTO.getIndex();
        final LoopEntryRefDTO loopEntryRefDTO = textDTO.getLoopEntryRef();
        final Text text = getText(index, version, language, loopEntryRefDTO);

        if (text == null) {
            return null;
        }

        return text.getId();
    }
}
