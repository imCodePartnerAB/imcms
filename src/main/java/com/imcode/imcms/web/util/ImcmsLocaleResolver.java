package com.imcode.imcms.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.DocumentI18nSupport;
import com.imcode.imcms.mapping.orm.DocLanguage;
import org.springframework.web.servlet.LocaleResolver;

import imcode.server.Imcms;

public class ImcmsLocaleResolver implements LocaleResolver {
    public Locale resolveLocale(HttpServletRequest request) {
        return new Locale(Imcms.getUser().getDocGetterCallback().documentLanguages().preferred().getCode());
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        DocumentI18nSupport i18nContentSupport = Imcms.getServices().getDocumentI18nSupport();

        DocLanguage language = i18nContentSupport.getByCode(locale.getLanguage());
        if (language == null) {
            language = i18nContentSupport.getDefaultLanguage();
        }

        //Imcms.getUser().getDocGetterCallback().setLanguage(language);
    }
}
