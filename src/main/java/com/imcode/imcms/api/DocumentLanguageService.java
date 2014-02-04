package com.imcode.imcms.api;

import java.util.List;

// todo: add security
public interface DocumentLanguageService {

    List<DocumentLanguage> getAllLanguages();

    DocumentLanguage getByCode(String code);

    DocumentLanguage getDefault();

    boolean isDefault(DocumentLanguage language);
}
