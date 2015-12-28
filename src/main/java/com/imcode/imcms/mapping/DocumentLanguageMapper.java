package com.imcode.imcms.mapping;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import com.imcode.imcms.mapping.jpa.SystemPropertyRepository;
import com.imcode.imcms.mapping.jpa.doc.Language;
import com.imcode.imcms.mapping.jpa.doc.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DocumentLanguageMapper {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private SystemPropertyRepository systemRepository;

    public List<DocumentLanguage> getAll() {
        return Lists.transform(languageRepository.findAll(), this::toApiObject);
    }

    public DocumentLanguage findByCode(String code) {
        return toApiObject(languageRepository.findByCode(code));
    }

    public void deleteByCode(String code) {
        Language language = languageRepository.findByCode(code);
        if (language != null) languageRepository.delete(language);
    }

    public void setDefault(DocumentLanguage language) {
        setDefault(language.getCode());
    }

    public void save(DocumentLanguage language) {
        Language jpaLanguage = languageRepository.findByCode(language.getCode());

        if (jpaLanguage != null) {
            jpaLanguage.setName(language.getName());
            jpaLanguage.setNativeName(language.getNativeName());
        } else {
            languageRepository.save(
                new Language(language.getCode(), language.getName(), language.getNativeName())
            );
        }
    }

    public boolean isDefault(DocumentLanguage language) {
        return Objects.equals(language, getDefault());
    }

    public DocumentLanguage getDefault() {
        SystemProperty property = systemRepository.findByName("DefaultLanguageId");

        if (property == null) {
            String message = "Configuration error. DefaultLanguageId property is not set.";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        Integer languageId = Ints.tryParse(Strings.nullToEmpty(property.getValue()));

        if (languageId == null) {
            String message = "Configuration error. DefaultLanguageId property is not a valid id.";
            logger.error(message);
            throw new IllegalStateException(message);
        }

        Language language = languageRepository.findOne(languageId);

        if (language == null) {
            String message = String.format("Configuration error. Default language (id: %d) can not be found.", languageId);
            logger.error(message);
            throw new IllegalStateException(message);
        }

        return toApiObject(language);
    }

    public void setDefault(String code) {
        Language language = languageRepository.findByCode(code);

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
