package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import com.imcode.imcms.mapping.jpa.SystemPropertyRepository;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
// todo: move to LanguageService
public class DocumentLanguageMapper {

    private final LanguageRepository languageRepository;
    private final SystemPropertyRepository systemRepository;
    private final LanguageService languageService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DocumentLanguageMapper(LanguageRepository languageRepository, SystemPropertyRepository systemRepository, LanguageService languageService) {
        this.languageRepository = languageRepository;
        this.systemRepository = systemRepository;
        this.languageService = languageService;
    }

    public List<DocumentLanguage> getAll() {
        return languageRepository.findAll().stream().map(this::toApiObject).collect(Collectors.toList());
    }

    public DocumentLanguage findByCode(String code) {
        return toApiObject(languageRepository.findByCode(code));
    }

    public void deleteByCode(String code) {
        LanguageJPA language = languageRepository.findByCode(code);
        if (language != null) languageRepository.delete(language);
    }

    public void setDefault(DocumentLanguage language) {
        setDefault(language.getCode());
    }

    public void save(DocumentLanguage language) {
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

    public boolean isDefault(DocumentLanguage language) {
        return Objects.equals(language, getDefault());
    }

    /**
     * @deprecated use {@link LanguageService#getDefaultLanguage()}
     */
    @Deprecated
    public DocumentLanguage getDefault() {
        final Language language = languageService.getDefaultLanguage();
        return toApiObject(language);
    }

    public void setDefault(String code) {
        LanguageJPA language = languageRepository.findByCode(code);

        if (language != null) {
            String propertyValue = String.valueOf(language.getId());
            SystemProperty property = systemRepository.findByName("DefaultLanguageId");
            if (property == null) {
                property = new SystemProperty(8, "DefaultLanguageId", propertyValue);
                systemRepository.save(property);
            } else {
                property.setValue(propertyValue);
            }
        }
    }

    public DocumentLanguage toApiObject(Language jpaLanguage) {
        return jpaLanguage == null
                ? null
                : DocumentLanguage.builder()
                .code(jpaLanguage.getCode())
                .name(jpaLanguage.getName())
                .nativeName(jpaLanguage.getNativeName())
                .build();
    }
}
