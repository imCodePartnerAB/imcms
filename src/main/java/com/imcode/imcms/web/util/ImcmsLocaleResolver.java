package com.imcode.imcms.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.DocumentLanguageSupport;
import com.imcode.imcms.api.DocumentLanguage;
import org.springframework.web.servlet.LocaleResolver;

import imcode.server.Imcms;

public class ImcmsLocaleResolver implements LocaleResolver {
    public Locale resolveLocale(HttpServletRequest request) {
        return new Locale(Imcms.getUser().getDocGetterCallback().documentLanguages().preferred().getCode());
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        DocumentLanguageSupport i18nContentSupport = Imcms.getServices().getDocumentLanguageSupport();

        DocumentLanguage language = i18nContentSupport.getByCode(locale.getLanguage());
        if (language == null) {
            language = i18nContentSupport.getDefault();
        }

        //Imcms.getUser().getDocGetterCallback().setLanguage(language);
    }
}
