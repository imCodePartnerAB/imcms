package com.imcode.imcms.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

import com.imcode.imcms.api.I18nLanguage;
import imcode.server.Imcms;

public class ImcmsLocaleResolver implements LocaleResolver {
	public Locale resolveLocale(HttpServletRequest request) {
		return new Locale(Imcms.getDocumentRequest().getLanguage().getCode());
	}
	
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		I18nLanguage language = Imcms.getI18nSupport().getByCode(locale.getLanguage());
        if (language == null) {
            language = Imcms.getI18nSupport().getDefaultLanguage();
        }
        
        Imcms.getDocumentRequest().setLanguage(language);
	}
}
