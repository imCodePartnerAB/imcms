package com.imcode.imcms.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * I18n support.
 *
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public class I18nSupport {

    final private I18nLanguage defaultLanguage;

    final private Map<String, I18nLanguage> languagesByCodes;

    final private Map<String, I18nLanguage> languagesByHosts;

    final private List<I18nLanguage> languages;

    final private Map<Integer, I18nLanguage> languagesByIds;

    public I18nSupport(Map<String, I18nLanguage> languagesByCodes, Map<String, I18nLanguage> languagesByHosts, I18nLanguage defaultLanguage) {
        this.languagesByCodes = languagesByCodes;
        this.languagesByHosts = languagesByHosts;
        this.defaultLanguage = defaultLanguage;

        languages = Lists.newLinkedList();
        languagesByIds = Maps.newHashMap();


        for (I18nLanguage language: languagesByCodes.values()) {
            languages.add(language);
            languagesByIds.put(language.getId(), language);
        }
    }


    public I18nLanguage getDefaultLanguage() {
        return defaultLanguage;
    }

    public List<I18nLanguage> getLanguages() {
        return languages;
    }

    public I18nLanguage getByCode(String code) {
        return languagesByCodes.get(code);
    }

    public I18nLanguage getById(Integer id) {
        return languagesByIds.get(id);
    }

    public boolean isDefault(I18nLanguage language) {
        return defaultLanguage.equals(language);
    }

    public I18nLanguage getForHost(String host) {
        return languagesByHosts.get(host);
    }
}