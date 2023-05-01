package com.imcode.imcms.api;

import com.google.common.collect.Maps;
import com.imcode.imcms.api.exception.DocumentLanguageException;
import com.imcode.imcms.mapping.DocumentLanguageMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Document language support.
 */
@Deprecated
public class DocumentLanguages {

    public static final Logger LOG = LogManager.getLogger(DocumentLanguages.class);

    private final DocumentLanguage defaultLanguage;

    private final Map<String, DocumentLanguage> languagesByCodes;

    private final Map<String, DocumentLanguage> languagesByHosts;

    private final List<DocumentLanguage> languages;

    private DocumentLanguages(List<DocumentLanguage> languages, Map<String, DocumentLanguage> languagesByHosts,
                              DocumentLanguage defaultLanguage) {
        this.languagesByHosts = Collections.unmodifiableMap(languagesByHosts);
        this.defaultLanguage = defaultLanguage;

        this.languages = Collections.unmodifiableList(languages);
        this.languagesByCodes = new HashMap<>();

        for (DocumentLanguage language : languages) {
            languagesByCodes.put(language.getCode(), language);
        }
    }

    public static DocumentLanguages create(DocumentLanguageMapper languageMapper, Properties imcmsProperties) {
        LOG.info("Creating document languages support.");

        List<DocumentLanguage> languages = languageMapper.getAll();

        if (languages.size() == 0) {
            LOG.warn("No document languages defined. Adding new (default) language.");
            DocumentLanguage language = DocumentLanguage.builder()
                    .code("swe")
                    .name("Swedish")
                    .nativeName("Svenska")
                    .build();

            languageMapper.save(language);
            languageMapper.setDefault(language);
        } else {
            DocumentLanguage defaultLanguage = languageMapper.getDefault();
            if (defaultLanguage == null) {
                defaultLanguage = Optional.ofNullable(languageMapper.findByCode("swe")).orElseGet(() -> languages.get(0));

                LOG.warn("Default document language is not set. Setting it to " + defaultLanguage);

                languageMapper.setDefault(defaultLanguage);
            }
        }

        Map<String, DocumentLanguage> languagesByCodes = Maps.newHashMap();
        Map<String, DocumentLanguage> languagesByHosts = Maps.newHashMap();

        for (DocumentLanguage language : languages) {
            languagesByCodes.put(language.getCode(), language);
        }

        // Read "virtual" hosts mapped to languages.
        String prefix = "i18n.host.";
        int prefixLength = prefix.length();

        for (Map.Entry propertyEntry : imcmsProperties.entrySet()) {
            String propName = (String) propertyEntry.getKey();

            if (!propName.startsWith(prefix)) {
                continue;
            }

            String languageCode = propName.substring(prefixLength);
            String propertyVal = (String) propertyEntry.getValue();

            LOG.info("I18n configuration: language code [" + languageCode + "] mapped to host(s) [" + propertyVal + "].");

            DocumentLanguage language = languagesByCodes.get(languageCode);

            if (language == null) {
                String msg = "I18n configuration error. Language with code [" + languageCode + "] is not defined in the database.";
                LOG.error(msg);
                throw new DocumentLanguageException(msg);
            }

            String hosts[] = propertyVal.split("[ \\t]*,[ \\t]*");

            for (String host : hosts) {
                languagesByHosts.put(host.trim(), language);
            }
        }

        return new DocumentLanguages(languages, languagesByHosts, languageMapper.getDefault());
    }

    public DocumentLanguage getDefault() {
        return defaultLanguage;
    }

    public List<DocumentLanguage> getAll() {
        return languages;
    }

    public DocumentLanguage getByCode(String code) {
        return languagesByCodes.get(code.toLowerCase());
    }

    public boolean isDefault(DocumentLanguage language) {
        return defaultLanguage.equals(language);
    }

    public DocumentLanguage getForHost(String host) {
        return languagesByHosts.get(host.toLowerCase());
    }

    public Set<String> getCodes() {
        return languagesByCodes.keySet();
    }
}
