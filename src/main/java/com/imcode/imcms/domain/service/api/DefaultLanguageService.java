package com.imcode.imcms.domain.service.api;

import com.google.common.base.Strings;
import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.exception.LanguageNotAvailableException;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import com.imcode.imcms.mapping.jpa.SystemPropertyRepository;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("languageService")
class DefaultLanguageService implements LanguageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("#{'${AvailableLanguages}'.split(';')}")
    private List<String> availableLanguages;

    private final LanguageRepository languageRepository;
    private final SystemPropertyRepository systemRepository;

    DefaultLanguageService(LanguageRepository languageRepository, SystemPropertyRepository systemRepository) {
        this.languageRepository = languageRepository;
        this.systemRepository = systemRepository;
    }

    @Override
    public Language findByCode(String code) {
        if (!availableLanguages.contains(code)) {
            throw new LanguageNotAvailableException(code);
        }
        return new LanguageDTO(languageRepository.findByCode(code));
    }

    @Override
    public List<Language> getAll() {
        return languageRepository.findAll()
                .stream()
                .map(LanguageDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<Language> getAvailableLanguages() {
        return languageRepository.findAll()
                .stream()
                .filter(lang -> availableLanguages.contains(lang.getCode()))
                .map(LanguageDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Language getDefaultLanguage() {
        final SystemProperty property = systemRepository.findByName("DefaultLanguageId");

        if (property == null) {
            String message = "Configuration error. DefaultLanguageId property is not set.";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        final Integer languageId = Integer.parseInt(Strings.nullToEmpty(property.getValue()));

        if (languageId == null) {
            String message = "Configuration error. DefaultLanguageId property is not a valid id.";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        final LanguageJPA language = languageRepository.findOne(languageId);

        if (language == null) {
            String message = String.format("Configuration error. Default language (id: %d) can not be found.", languageId);
            logger.error(message);
            throw new IllegalStateException(message);
        }

        return language;
    }
}
