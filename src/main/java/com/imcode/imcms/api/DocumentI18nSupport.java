package com.imcode.imcms.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Document I18n support.
 *
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public class DocumentI18nSupport {

    private final DocumentLanguage defaultLanguage;

    private final Map<String, DocumentLanguage> languagesByCodes;

    private final Map<String, DocumentLanguage> languagesByHosts;

    private final List<DocumentLanguage> languages;

    public DocumentI18nSupport(Map<String, DocumentLanguage> languagesByCodes, Map<String, DocumentLanguage> languagesByHosts, DocumentLanguage defaultLanguage) {
        this.languagesByCodes = languagesByCodes;
        this.languagesByHosts = languagesByHosts;
        this.defaultLanguage = defaultLanguage;

        languages = new LinkedList<>();

        for (DocumentLanguage language: languagesByCodes.values()) {
            languages.add(language);
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
}