package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.dto.TextHistoryDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.TextHistoryService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.LoopEntryRefJPA;
import com.imcode.imcms.persistence.entity.TextHistoryJPA;
import com.imcode.imcms.persistence.entity.TextJPA;
import com.imcode.imcms.persistence.repository.TextHistoryRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service("textHistoryService")
public class DefaultTextHistoryService implements TextHistoryService {

    private final TextHistoryRepository textHistoryRepository;
    private final LanguageService languageService;
    private final UserService userService;
    private final Function<TextHistoryJPA, TextHistoryDTO> textHistoryJpaToTextHistoryDTO;

    @Autowired
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
        final TextJPA textJPA = new TextJPA(text, null, new LanguageJPA(language));

        final UserDomainObject userDomainObject = Imcms.getUser();
        final User user = userService.getUser(userDomainObject.getId());

        final TextHistoryJPA textHistoryJPA = new TextHistoryJPA(textJPA, user);

        textHistoryRepository.save(textHistoryJPA);
    }

    @Override
    public List<TextHistoryDTO> getAll(TextDTO textDTO) {
        final Integer index = textDTO.getIndex();
        final LoopEntryRefDTO loopEntryRef = textDTO.getLoopEntryRef();
        final Language language = languageService.findByCode(textDTO.getLangCode());

        return this.textHistoryRepository
                .findAllByLanguageAndLoopEntryRefAndNo(new LanguageJPA(language), new LoopEntryRefJPA(loopEntryRef), index)
                .stream()
                .map(textHistoryJpaToTextHistoryDTO)
                .collect(Collectors.toList());
    }
}
