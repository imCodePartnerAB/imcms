package com.imcode.imcms.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.api.DocumentLanguage;
import org.springframework.web.servlet.LocaleResolver;

import imcode.server.Imcms;

public class ImcmsLocaleResolver implements LocaleResolver {
    public Locale resolveLocale(HttpServletRequest request) {
        return new Locale(Imcms.getUser().getDocGetterCallback().getLanguage().getCode());
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        DocumentLanguages dls = Imcms.getServices().getDocumentLanguages();

        DocumentLanguage language = dls.getByCode(locale.getLanguage());
        if (language == null) {
            language = dls.getDefault();
        }

        Imcms.getUser().getDocGetterCallback().setLanguage(language, dls.isDefault(language));
    }
}
