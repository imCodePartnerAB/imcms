package com.imcode.imcms.dao;

import java.util.List;

import com.imcode.imcms.api.I18nLanguage;

public interface LanguageDao {

    List<I18nLanguage> getAllLanguages();
    
    I18nLanguage getDefaultLanguage();
    
    I18nLanguage getByCode(String code);
    
    void setDefaultLanguage(I18nLanguage language);
}
