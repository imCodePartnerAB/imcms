package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.dto.TextHistoryDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.TextHistoryService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.*;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
import imcode.server.Config;
import imcode.server.Imcms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("textHistoryService")
@Transactional
public class DefaultTextHistoryService implements TextHistoryService {

    private final TextHistoryRepository textHistoryRepository;
    private final LanguageService languageService;
    private final UserService userService;
    private final Function<TextHistoryJPA, TextHistoryDTO> textHistoryJpaToTextHistoryDTO;
    private final int contentHistoryRecordsSize;

    public DefaultTextHistoryService(TextHistoryRepository textHistoryRepository,
                                     LanguageService languageService, UserService userService,
                                     Function<TextHistoryJPA, TextHistoryDTO> textHistoryJpaToTextHistoryDTO,
                                     Config config) {

        this.textHistoryRepository = textHistoryRepository;
        this.languageService = languageService;
        this.userService = userService;
        this.textHistoryJpaToTextHistoryDTO = textHistoryJpaToTextHistoryDTO;
        this.contentHistoryRecordsSize = config.getContentHistoryRecordsSize();
    }

    @Override
    public void save(Text text) {
        final Language language = languageService.findByCode(text.getLangCode());
        final User user = userService.getUser(Imcms.getUser().getId());
        final TextHistoryJPA textHistoryJPA = new TextHistoryJPA(text, language, user);

        final TextHistoryJPA save = textHistoryRepository.save(textHistoryJPA);
        clearHistoryIfLimitExceeded(save);
    }

    @Override
    public List<TextHistoryDTO> getAll(TextDTO textDTO) {
        final Integer docId = textDTO.getDocId();
        final Integer index = textDTO.getIndex();
        final LanguageJPA language = new LanguageJPA(languageService.findByCode(textDTO.getLangCode()));
        final Optional<LoopEntryRefJPA> oLoopEntry = Optional.ofNullable(textDTO.getLoopEntryRef())
                .map(LoopEntryRefJPA::new);

        final List<TextHistoryJPA> textHistory = (oLoopEntry.isPresent())
                ? textHistoryRepository.findTextHistoryInLoop(docId, language, oLoopEntry.get(), index)
                : textHistoryRepository.findTextHistoryNotInLoop(docId, language, index);

        return textHistory.stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(TextHistoryJPA::getModifiedDt)))
                .map(textHistoryJpaToTextHistoryDTO)
                .collect(Collectors.toList());
    }

    private void clearHistoryIfLimitExceeded(TextHistoryJPA save){
        final Integer loopIndex = Optional.ofNullable(save.getLoopEntryRef()).map(LoopEntryRefJPA::getLoopIndex).orElse(null);
        final Integer loopEntryIndex = Optional.ofNullable(save.getLoopEntryRef()).map(LoopEntryRefJPA::getLoopEntryIndex).orElse(null);
        textHistoryRepository.clearHistoryIfLimitExceeded(save.getDocId(), save.getIndex(), save.getLanguage().getId(),
                loopIndex, loopEntryIndex, contentHistoryRecordsSize);
    }
}
