package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.dto.TextHistoryDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.TextHistoryService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistoryJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
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

    public DefaultTextHistoryService(TextHistoryRepository textHistoryRepository,
                                     LanguageService languageService, UserService userService,
                                     Function<TextHistoryJPA, TextHistoryDTO> textHistoryJpaToTextHistoryDTO) {

        this.textHistoryRepository = textHistoryRepository;
        this.languageService = languageService;
        this.userService = userService;
        this.textHistoryJpaToTextHistoryDTO = textHistoryJpaToTextHistoryDTO;
    }

    @Override
    public void save(Text text) {
        final Language language = languageService.findByCode(text.getLangCode());
        final User user = userService.getUser(Imcms.getUser().getId());
        final TextHistoryJPA textHistoryJPA = new TextHistoryJPA(text, language, user);

        textHistoryRepository.save(textHistoryJPA);
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
}
