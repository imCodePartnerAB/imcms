package com.imcode.imcms.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * I18n support.
 *
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public class I18nContentSupport {

    final private ContentLanguage defaultLanguage;

    final private Map<String, ContentLanguage> languagesByCodes;

    final private Map<String, ContentLanguage> languagesByHosts;

    final private List<ContentLanguage> languages;

    final private Map<Integer, ContentLanguage> languagesByIds;

    public I18nContentSupport(Map<String, ContentLanguage> languagesByCodes, Map<String, ContentLanguage> languagesByHosts, ContentLanguage defaultLanguage) {
        this.languagesByCodes = languagesByCodes;
        this.languagesByHosts = languagesByHosts;
        this.defaultLanguage = defaultLanguage;

        languages = Lists.newLinkedList();
        languagesByIds = Maps.newHashMap();


        for (ContentLanguage language: languagesByCodes.values()) {
            languages.add(language);
            languagesByIds.put(language.getId(), language);
        }
    }


    public ContentLanguage getDefaultLanguage() {
        return defaultLanguage;
    }

    public List<ContentLanguage> getLanguages() {
        return languages;
    }

    public ContentLanguage getByCode(String code) {
        return languagesByCodes.get(code);
    }

    public ContentLanguage getById(Integer id) {
        return languagesByIds.get(id);
    }

    public boolean isDefault(ContentLanguage language) {
        return defaultLanguage.equals(language);
    }

    public ContentLanguage getForHost(String host) {
        return languagesByHosts.get(host);
    }
}