package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.exception.LanguageNotAvailableException;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import com.imcode.imcms.mapping.jpa.SystemPropertyRepository;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.LanguageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("languageService")
@Slf4j
class DefaultLanguageService implements LanguageService {

    @Value("#{'${AvailableLanguages}'.split(';')}")
    private List<String> availableLanguages;

    @Value("#{'${DefaultLanguage}'}")
    private String defaultLang;

    private final LanguageRepository languageRepository;
    private final SystemPropertyRepository systemRepository;

    DefaultLanguageService(LanguageRepository languageRepository, SystemPropertyRepository systemRepository) {
        this.languageRepository = languageRepository;
        this.systemRepository = systemRepository;
    }

    @Override
    public Language findByCode(String code) {
        if (LanguageMapper.existsIsoCode639_2(code)) {
            code = LanguageMapper.convert639_2to639_1(code);
        }

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
        final LanguageJPA langByProperty = new LanguageJPA(findByCode(defaultLang));

        if (langByProperty.getCode() == null) { // set default from db.
            log.error("In getDefaultLanguage: default language from properties not exists in db: {}", defaultLang);

            final SystemProperty property = systemRepository.findByName("DefaultLanguageId");

            if (property == null) {
                String message = "Configuration error. DefaultLanguageId property is not set.";
                log.error(message);
                throw new IllegalStateException(message);
            }

            final Integer languageId = Integer.parseInt(property.getValue());

            final LanguageJPA langBySysProp = languageRepository.findOne(languageId);

            if (langBySysProp == null) {
                String message = String.format("Configuration error. Default language (id: %d) can not be found.", languageId);
                log.error(message);
                throw new IllegalStateException(message);
            }

            return langBySysProp;
        }

        return langByProperty;
    }
}
