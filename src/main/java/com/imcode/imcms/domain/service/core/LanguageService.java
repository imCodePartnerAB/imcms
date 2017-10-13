package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.persistence.entity.Language;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public Language findByCode(String code) {
        return languageRepository.findByCode(code);
    }

    public Language getCurrentUserLanguage() {
        return getUserLanguage(Imcms.getUser());
    }

    public Language getUserLanguage(UserDomainObject user) {
        final String code = LanguageMapper.convert639_2to639_1(user.getLanguageIso639_2());
        return languageRepository.findByCode(code);
    }
}
