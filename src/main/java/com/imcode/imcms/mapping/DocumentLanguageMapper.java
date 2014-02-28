package com.imcode.imcms.mapping;

import com.google.common.base.Function;
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

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

// todo: add security checks
@Service
public class DocumentLanguageMapper {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private LanguageRepository languageRepository;

    @Inject
    private SystemPropertyRepository systemRepository;

    public List<DocumentLanguage> getAll() {
        return Lists.transform(languageRepository.findAll(), new Function<Language, DocumentLanguage>() {
            public DocumentLanguage apply(Language input) {
                return EntityConverter.fromEntity(input);
            }
        });
    }

    public DocumentLanguage getByCode(String code) {
        Language language = languageRepository.findByCode(code);

        return language == null ? null : EntityConverter.fromEntity(language);
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

        return EntityConverter.fromEntity(language);
    }
}
