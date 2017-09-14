package com.imcode.imcms.web.util;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentLanguages;
import imcode.server.Imcms;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

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
