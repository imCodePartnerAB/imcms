package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public Language findByCode(String code) {
        return languageRepository.findByCode(code);
    }
}
