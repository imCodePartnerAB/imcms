package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Language;

import java.util.List;

public interface LanguageService {

    Language findByCode(String code);

    List<Language> getAll();

    List<Language> getAvailableAdminLanguages();

    Language getDefaultAdminLanguage();

    List<String> getAllAdminLangCode();

    boolean isAdminAvailableLanguage(String code);

    boolean isAdminLanguage(String code);

	boolean isLanguageAvailableByCode(String code);

    List<Language> getAvailableLanguages();

    Language getDefaultLanguage();

    void deleteByCode(String code);

    void save(Language language);

    List<Language> getEnabledContentLanguagesByDocId(Integer docId);

}
