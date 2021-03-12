package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Language;

import java.util.List;

public interface LanguageService {

    Language findByCode(String code);

    List<Language> getAll();

    List<Language> getAvailableLanguages();

    Language getDefaultLanguage();

    void deleteByCode(String code);

    void save(Language language);

    List<Language> getEnabledContentLanguagesByDocId(Integer docId);

}
