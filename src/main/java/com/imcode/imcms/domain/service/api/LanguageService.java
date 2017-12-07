package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    /**
     * Get language DTO by it's two-letter ISO-639-1 code
     *
     * @param code ISO-639-1 code
     * @return language DTO
     */
    public LanguageDTO findByCode(String code) {
        return new LanguageDTO(languageRepository.findByCode(code));
    }

    public List<LanguageDTO> getAll() {
        return languageRepository.findAll()
                .stream()
                .map(LanguageDTO::new)
                .collect(Collectors.toList());
    }

}
