package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.exception.LanguageNotAvailableException;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.LanguageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service("languageService")
@Slf4j
class DefaultLanguageService implements LanguageService {

    @Value("#{'${AvailableLanguages}'.split(';')}")
    private List<String> availableLanguages;

    @Value("#{'${DefaultLanguage}'}")
    private String defaultLang;

    private final LanguageRepository languageRepository;

    DefaultLanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
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
        return new LanguageDTO(findByCode(defaultLang));
    }

    @Override
    public void deleteByCode(String code) {
        final LanguageJPA foundLanguage = languageRepository.findByCode(code);

        languageRepository.delete(foundLanguage);
    }

    @Override
    public void save(Language language) {
        LanguageJPA jpaLanguage = languageRepository.findByCode(language.getCode());

        if (jpaLanguage != null) {
            jpaLanguage.setName(language.getName());
            jpaLanguage.setNativeName(language.getNativeName());
        } else {
            languageRepository.save(
                    new LanguageJPA(language.getCode(), language.getName(), language.getNativeName())
            );
        }
    }
}
