package com.imcode.imcms.api;

import java.util.*;

/**
 * Document language support.
 *
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public class DocumentLanguageSupport {

    private final DocumentLanguage defaultLanguage;

    private final Map<String, DocumentLanguage> languagesByCodes;

    private final Map<String, DocumentLanguage> languagesByHosts;

    private final List<DocumentLanguage> languages;

    public DocumentLanguageSupport(List<DocumentLanguage> languages, Map<String, DocumentLanguage> languagesByHosts, DocumentLanguage defaultLanguage) {
        this.languagesByHosts = Collections.unmodifiableMap(languagesByHosts);
        this.defaultLanguage = defaultLanguage;

        this.languages = Collections.unmodifiableList(languages);
        this.languagesByCodes = new HashMap<>();

        for (DocumentLanguage language: languages) {
            languagesByCodes.put(language.getCode(), language);
        }
    }


    public DocumentLanguage getDefaultLanguage() {
        return defaultLanguage;
    }

    public List<DocumentLanguage> getLanguages() {
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