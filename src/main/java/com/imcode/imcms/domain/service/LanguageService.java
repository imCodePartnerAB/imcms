package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Language;

import java.util.List;

public interface LanguageService {
    /**
     * Get language by it's two-letter ISO-639-1 code like "en" or "sv"
     *
     * @param code ISO-639-1 code
     */
    Language findByCode(String code);

    List<Language> getAll();

    List<Language> getAvailableLanguages();
}
