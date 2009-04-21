package com.imcode.imcms.addon.imagearchive.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;


public class CommonService {
    @Autowired
    private MessageSource messageSource;
    
    
    public String getMessage(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }
}
