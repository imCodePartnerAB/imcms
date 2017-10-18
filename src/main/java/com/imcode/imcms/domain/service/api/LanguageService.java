package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final Function<Language, LanguageDTO> languageToLanguageDTO;

    public LanguageService(LanguageRepository languageRepository,
                           Function<Language, LanguageDTO> languageToLanguageDTO) {
        this.languageRepository = languageRepository;
        this.languageToLanguageDTO = languageToLanguageDTO;
    }

    public Language findByCode(String code) {
        return languageRepository.findByCode(code);
    }

    public Language getCurrentUserLanguage() {
        return getUserLanguage(Imcms.getUser());
    }

    public Language getUserLanguage(UserDomainObject user) {
        final String code = LanguageMapper.convert639_2to639_1(user.getLanguageIso639_2());
        return languageRepository.findByCode(code);
    }

    public List<LanguageDTO> getAll() {
        return languageRepository.findAll().stream()
                .map(languageToLanguageDTO)
                .collect(Collectors.toList());
    }

}
