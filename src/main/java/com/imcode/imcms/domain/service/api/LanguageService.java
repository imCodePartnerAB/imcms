package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.repository.LanguageRepository;
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

    /**
     * Get language DTO by it's two-letter ISO-639-1 code
     *
     * @param code ISO-639-1 code
     * @return language DTO
     */
    public LanguageDTO findByCode(String code) {
        return languageToLanguageDTO.compose(languageRepository::findByCode).apply(code);
    }

    /**
     * @param code ISO-639-1 code
     * @return language entity
     * @deprecated use {@link com.imcode.imcms.domain.service.api.LanguageService#findByCode(java.lang.String)}
     */
    @Deprecated
    public Language findEntityByCode(String code) {
        return languageRepository.findByCode(code);
    }

    public List<LanguageDTO> getAll() {
        return languageRepository.findAll()
                .stream()
                .map(languageToLanguageDTO)
                .collect(Collectors.toList());
    }

}
