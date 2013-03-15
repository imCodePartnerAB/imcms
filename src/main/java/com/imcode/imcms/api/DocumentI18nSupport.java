package com.imcode.imcms.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Document I18n support.
 *
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public class DocumentI18nSupport {

    final private DocumentLanguage defaultLanguage;

    final private Map<String, DocumentLanguage> languagesByCodes;

    final private Map<String, DocumentLanguage> languagesByHosts;

    final private List<DocumentLanguage> languages;

    final private Map<Integer, DocumentLanguage> languagesByIds;

    public DocumentI18nSupport(Map<String, DocumentLanguage> languagesByCodes, Map<String, DocumentLanguage> languagesByHosts, DocumentLanguage defaultLanguage) {
        this.languagesByCodes = languagesByCodes;
        this.languagesByHosts = languagesByHosts;
        this.defaultLanguage = defaultLanguage;

        languages = Lists.newLinkedList();
        languagesByIds = Maps.newHashMap();


        for (DocumentLanguage language: languagesByCodes.values()) {
            languages.add(language);
            languagesByIds.put(language.getId(), language);
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

    public DocumentLanguage getById(Integer id) {
        return languagesByIds.get(id);
    }

    public boolean isDefault(DocumentLanguage language) {
        return defaultLanguage.equals(language);
    }

    public DocumentLanguage getForHost(String host) {
        return languagesByHosts.get(host.toLowerCase());
    }
}