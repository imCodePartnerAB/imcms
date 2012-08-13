package com.imcode.imcms.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.I18nSupport;
import org.springframework.web.servlet.LocaleResolver;

import com.imcode.imcms.api.I18nLanguage;
import imcode.server.Imcms;

public class ImcmsLocaleResolver implements LocaleResolver {
    public Locale resolveLocale(HttpServletRequest request) {
        return new Locale(Imcms.getUser().getDocGetterCallback().selectedLanguage().getCode());
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        I18nSupport i18nSupport = Imcms.getServices().getI18nSupport();

        I18nLanguage language = i18nSupport.getByCode(locale.getLanguage());
        if (language == null) {
            language = i18nSupport.getDefaultLanguage();
        }

        //Imcms.getUser().getDocGetterCallback().setLanguage(language);
    }
}
