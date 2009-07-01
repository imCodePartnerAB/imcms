package com.imcode.imcms.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;

public class ImcmsLocaleResolver implements LocaleResolver {
	public Locale resolveLocale(HttpServletRequest request) {
		return new Locale(I18nSupport.getCurrentLanguage().getCode());
	}
	
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		I18nLanguage language = I18nSupport.getByCode(locale.getLanguage());
        if (language == null) {
            language = I18nSupport.getDefaultLanguage();
        }
        
        I18nSupport.setCurrentLanguage(language);
        request.getSession().setAttribute("lang", language);
	}
}
